---
title: Use Hardware-Backed Key Attestation for Device and App Integrity
alias: android-hardware-backed-attestation
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0035, MASTG-KNOW-0044, MASTG-KNOW-0047, MASTG-KNOW-0119, MASTG-KNOW-0120]
---

Applications that perform business-critical operations, such as financial transactions, multi-factor authentication, or sensitive data handling, should verify the integrity of the device environment before trusting it as well as the integrity of the application binary.

For the majority of apps, **Google Play Integrity API** (@MASTG-KNOW-0035) is the recommended starting point. It provides a managed, server-backed attestation signal covering device integrity, app integrity, and licensing, without requiring apps to implement low-level certificate verification themselves.

**This best practice covers the manual attestation approach** using Android's hardware-backed Key Attestation (@MASTG-KNOW-0044). This approach should be used when Play Integrity limitations need to be addressed, or when the application requires more control. In this scenario, use Android's Key Attestation to cryptographically verify that the client's keys reside in hardware-backed storage and that the device has not been compromised.

## Implement Server-Driven Attestation with a Fresh Challenge

Always drive attestation from the server using the challenge-response flow described in @MASTG-KNOW-0044. Generate a unique, cryptographically random challenge (nonce) for each attestation request using a Cryptographically Secure Pseudorandom Number Generator (CSPRNG). Never reuse challenges across requests. Never implement attestation verification solely on the client side.

Since attestation reflects the state of the device and application at the time of key generation rather than at the time of use, require fresh key generation:

- At critical moments such as the first app launch, account binding, or sensitive operation.
- When the installed app version falls below the minimum acceptable threshold.

Enforce short-lived keys or periodic re-attestation policies to reduce the window of exposure between a device's state changing and that change being detected.

@MASTG-DEMO-0x03 shows a key generated with a challenge embedded in the attestation; @MASTG-DEMO-0x01 shows the same key generated without one.

## Verify the Attestation Certificate Chain

On the server side, verify the attestation certificate chain (@MASTG-KNOW-0044):

- Verify the chain of trust up to the Google Hardware Attestation Root Certificate. Keep the set of accepted root certificates up to date: Google rotates them, and a [new root certificate began signing attestation certificate chains on February 1, 2026](https://developer.android.com/privacy-and-security/security-key-attestation#root_certificate).
- Check every certificate in the chain against [Google's certificate revocation status list](https://developer.android.com/privacy-and-security/security-key-attestation#certificate_status) at `https://android.googleapis.com/attestation/status`, and reject the attestation on any `REVOKED` entry. This is what protects you against attestation keys that were extracted from a device and are being used to sign forged device states.
- Enforce the validity period of every certificate. Certificates issued through Remote Key Provisioning are deliberately short-lived so that compromises can be contained quickly; accepting an expired certificate discards that protection.
- Confirm that the embedded challenge matches the one the server originally issued.

Prefer Google's [attestation verification Kotlin library](https://github.com/android/keyattestation) over a custom verifier. Google notes that it covers edge cases custom verifiers often miss, and chain verification is easy to get subtly wrong.

!!! warning
    A valid chain proves that _some_ device with a trusted attestation key made the claim, not that _this_ device did. On devices using the legacy factory-provisioning mechanism, extracted attestation keys have been used to sign chains reporting a clean `verifiedBootState` and a locked bootloader on rooted devices. Revocation checking and validity-period enforcement are what limit this exposure, which is why neither step is optional. See @MASTG-KNOW-0120.

## Verify Device Integrity

Using the `rootOfTrust` fields described in @MASTG-KNOW-0120, confirm the device is in a trusted state:

- Require that `attestationSecurityLevel` is `TrustedEnvironment` or `StrongBox`, confirming the device integrity data is hardware-enforced and cannot be falsified by the Android OS.
- Verify `verifiedBootState` is `Verified`, indicating the full boot chain was validated against OEM keys.
- Verify `deviceLocked` is `true`, confirming the bootloader is locked and the partitions protected by Verified Boot (for example `boot`, `init_boot`, and `system`) cannot be modified without detection.

The following conditions indicate low or no device integrity:

- **`verifiedBootState` is not `Verified`**: The boot chain was not fully verified against OEM keys. A `SelfSigned` state means the boot chain was verified against a root of trust configured by the user, as on an aftermarket OS installed with a locked bootloader, so accept it only if `verifiedBootKey` is a key you explicitly trust; `Unverified` means the bootloader is unlocked and no chain of trust could be established; `Failed` means verification failed.
- **`deviceLocked` is `false`**: The bootloader is unlocked, meaning the partitions protected by Verified Boot can be modified without triggering a boot failure. This is a strong signal that the device may have been tampered with.
- **`attestationSecurityLevel` is `Software`**: The attestation was generated entirely in the Android OS with no hardware involvement, so `rootOfTrust` appears in the `softwareEnforced` list (@MASTG-KNOW-0120). The `deviceLocked` and `verifiedBootState` values are then asserted by the same software layer they are meant to vouch for, so they cannot corroborate the device's state: a compromised OS can report `deviceLocked` as `true` and `verifiedBootState` as `Verified` while running modified boot or system images. Do not treat a `Software` attestation as evidence of device integrity, even when those fields look correct. At most, record it as a weak signal to be combined with other controls, and rely on the guarantees of the [Android Platform Security Model](https://source.android.com/docs/security/features) only where they are enforced below the OS.

If the app requests device properties attestation with `setDevicePropertiesAttestationIncluded` (@MASTG-KNOW-0044), require the attested brand, device, manufacturer, model, and product values to be present in the attestation. The client decides whether to request them, so a server that also accepts attestations without them gains nothing from the check.

As an additional signal, compare the `verifiedBootHash` field of `rootOfTrust` with the value the device reports about itself (@MASTG-KNOW-0120). On devices that use Android Verified Boot, the same digest is available on the device as the `ro.boot.vbmeta.digest` system property. The app reads the property and sends it with the attestation, and the server compares the two values. A mismatch means the attestation does not describe the software the client is running, which happens when the client relays an attestation produced on another device or presents a forged chain.

!!! note
    This comparison can only raise suspicion, it cannot establish trust. The property is read and reported by the client, so a rooted device can report any value and a match proves nothing. The digest also identifies the booted software, not the device, so devices running the same build report the same value. Android has no public SDK API for reading system properties, so the app has to rely on non-SDK interfaces to obtain it.

## Align the Minimum Patch Level with the App's Risk

A device with a locked bootloader and a `Verified` boot state can still run software with publicly known vulnerabilities. The attestation records how up to date the device was when the key was generated, in the `osVersion`, `osPatchLevel`, `vendorPatchLevel`, and `bootPatchLevel` fields (@MASTG-KNOW-0120). Use them to match the security level you require from the device to the risk of the operations the app protects:

- Compare `osPatchLevel` against the oldest security patch level you are willing to accept. Google [recommends this field](https://source.android.com/docs/security/features/keystore/attestation#authorizationlist-fields) over `vendorPatchLevel` and `bootPatchLevel` for checking whether a device has been recently patched.
- For high-risk operations, require a recent patch level and deny access or ask for additional verification otherwise. For lower-risk operations, record an old patch level as a risk signal instead of blocking the user.
- Move the threshold forward as new security patches are released. A fixed value loses its meaning over time.

## Verify Application Integrity

Using the `attestationApplicationId` fields described in @MASTG-KNOW-0119, confirm the key was generated by the legitimate application. The Android OS supplies this field and the secure hardware only copies it into the attestation, so evaluate it only after the device integrity checks above have passed:

- Verify `packageName` matches the expected application identifier.
- Verify `signatureDigests` match the expected signing certificate digests pre-provisioned on the server (e.g., from the app's release certificate). A mismatch indicates the APK has been repackaged or signed with a different key.
- Verify `version` is within an acceptable range to reject outdated or known-vulnerable versions.

The following conditions indicate low or no application integrity:

- **`signatureDigests` do not match**: The SHA-256 digests of the app's signing certificates differ from the legitimate app's known digests at the time of release. This is the primary indicator that the APK has been repackaged or signed with a different key (e.g., a malicious clone or a patched version of the app).
- **`version` is below the minimum acceptable version**: The app version recorded at key generation time is too old, indicating a version with known vulnerabilities or one that predates a security-relevant update.

## Enforce Key Properties

Use the attestation extension data (@MASTG-KNOW-0044) to confirm that the attested key pair was generated with the expected properties:

- Restrict the key purpose to only the intended operations (e.g., signing, encryption).
- Require user authentication before key use when applicable (e.g., biometric binding via [`setUserAuthenticationRequired`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setuserauthenticationrequired)).

## Handle Attestation Failures Securely

If attestation verification fails, the server must not grant the client elevated trust or access to sensitive operations. Depending on the application's risk profile:

- Deny access to high-assurance features entirely.
- Fall back to alternative verification mechanisms with appropriate risk acceptance.
- Log the failure for monitoring and incident response.
