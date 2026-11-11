---
masvs_category: MASVS-RESILIENCE
platform: android
title: Device Attestation via Hardware-Backed Key Attestation
best-practices: [MASTG-BEST-0x01]
---

Device attestation uses the `rootOfTrust` field in the `AuthorizationList` of a Key Attestation (@MASTG-KNOW-0044) certificate chain, which allows a verifier (usually a remote server) to verify the integrity of the device's software stack. For the full list of fields and their meanings, see @MASTG-KNOW-0044.

## Root of Trust Fields

The `rootOfTrust` object populated by the secure hardware (TEE or StrongBox) during key generation is placed in the `hardwareEnforced` `AuthorizationList`. Because it is hardware-enforced, these values cannot be tampered with by the Android OS. When the `attestationSecurityLevel` is `Software`, the `rootOfTrust` may instead appear in the `softwareEnforced` list, providing no hardware-backed guarantee.

The `rootOfTrust` object contains the following fields:

- **`verifiedBootState`**: Reflects the result of Android's [Verified Boot](https://source.android.com/docs/security/features/verifiedboot) process:
    - **`Verified`**: A full chain of trust extends from a hardware-protected root of trust to the bootloader and all partitions verified by Verified Boot. This is the expected state for an unmodified production device.
    - **`SelfSigned`**: The same as `Verified`, except that the verification was done against a root of trust configured by the user (e.g., a custom key) instead of the one embedded by the OEM. This is the state of an aftermarket OS installed with a locked bootloader. GrapheneOS, for example, [publishes the verified boot key fingerprints](https://grapheneos.org/articles/attestation-compatibility-guide) of its official releases, which appear in `verifiedBootKey`.
    - **`Unverified`**: The bootloader is unlocked, so a chain of trust cannot be established.
    - **`Failed`**: Verification was attempted but failed. The device should not have booted in this state under normal conditions.
- **`verifiedBootKey`**: The public key used to verify the boot image. On unmodified production devices, this matches the OEM's embedded root-of-trust key.
- **`deviceLocked`**: `true` if the bootloader is locked. A locked bootloader only boots images that pass Verified Boot, so the partitions protected by Verified Boot (for example `boot`, `init_boot`, and `system`) cannot be modified without detection.
- **`verifiedBootHash`**: A digest of all data protected by Verified Boot. On devices that use the Android Verified Boot reference implementation, it contains the [VBMeta digest](https://android.googlesource.com/platform/external/avb/+/refs/heads/main/README.md#the-vbmeta-digest), which the bootloader also passes to Android on the kernel command line as `androidboot.vbmeta.digest`, where it is available as the `ro.boot.vbmeta.digest` system property.

## OS Version and Patch Level Fields

Next to `rootOfTrust`, the [`AuthorizationList`](https://source.android.com/docs/security/features/keystore/attestation#authorizationlist-fields) reports how up to date the device software was when the key was generated:

- **`osVersion`**: The Android version as a six-digit integer (for example, `080100` for version 8.1.0).
- **`osPatchLevel`**: The month and year of the security patch level as a six-digit integer (for example, `201808` for August 2018).
- **`vendorPatchLevel`**: The security patch level of the vendor image as an eight-digit integer (for example, `20180805`).
- **`bootPatchLevel`**: The security patch level of the kernel image, in the same format as `vendorPatchLevel`.

## Limitations

Device attestation via Key Attestation reflects the state of the device **at the time the key was generated**, not at the time of any subsequent API call or use. A key generated on a clean device retains its attestation even if the device is later rooted or its bootloader is unlocked after key generation.

Device attestation also cannot detect all forms of compromise. A device that was unlocked and re-locked, or that had its verified boot state manipulated at a hardware level (which is extremely difficult but theoretically possible), would still pass verification. It cannot replace a comprehensive device integrity strategy.

The `rootOfTrust` values are only as trustworthy as the attestation key that signed the chain. On devices using the legacy factory-provisioning mechanism, an attestation key extracted from a device can be used to sign a chain reporting `verifiedBootState` as `Verified` and `deviceLocked` as `true` for a device that is neither, and tooling exists that performs this on rooted devices using leaked key material. Google revokes such keys through the certificate revocation status list once a leak is discovered, and keys certified through Remote Key Provisioning are not affected by this class of leak. See @MASTG-KNOW-0044 for how attestation keys are provisioned and revoked.
