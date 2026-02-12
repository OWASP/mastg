---
masvs_category: MASVS-RESILIENCE
platform: android
title: Device Attestation via Hardware-Backed Key Attestation
---

The attestation extension data embedded in the leaf certificate of a Key Attestation (@MASTG-KNOW-0044) certificate chain follows an [ASN.1 schema](https://source.android.com/docs/security/features/keystore/attestation#schema) and contains detailed information about both the device's integrity state and the properties of the attested key pair. This data is what enables a remote server to make trust decisions about the client environment. Bellow is a summary of the most important fields and how to interpret them.

## Attestation Security Level

The [KeyDescription's](https://source.android.com/docs/security/features/keystore/attestation#keydescription-fields) `attestationSecurityLevel` field indicates the security environment that performed the attestation:

- **`Software`**: Attestation was performed in the Android system, with no hardware-backed guarantee. This is usually what emulators use.
- **`TrustedEnvironment`**: Attestation was performed by the Trusted Execution Environment (TEE), providing software-level isolation.
- **`StrongBox`**: Attestation was performed by a dedicated secure element (StrongBox), offering the highest level of hardware protection.

Only `StrongBox` provide hardware-backed assurance.

## Software vs. Hardware Enforcement

The [KeyDescription](https://source.android.com/docs/security/features/keystore/attestation#keydescription-fields) contains two authorization lists (`AuthorizationList`) that describe where key properties are enforced:

- **`softwareEnforced`**: Properties enforced by the Android operating system. These can potentially be bypassed if the OS is compromised (e.g., on a rooted device).
- **`hardwareEnforced`**: Properties enforced by the Trusted Execution Environment or StrongBox hardware. These cannot be modified by the OS, even if it is compromised.

## Device Integrity Signals

Each `AuthorizationList` has a `rootOfTrust` field and includes the following device integrity signals:

- **`verifiedBootState`**: Indicates whether the device's boot chain has been verified as unmodified. A `Verified` state means the bootloader confirmed the integrity of all boot partitions.
- **`verifiedBootKey`**: The public key used to verify the boot image. On unmodified devices, this matches the OEM's embedded key.
- **`deviceLocked`**: Whether the bootloader is locked. A locked bootloader prevents flashing unsigned images. An unlocked bootloader is a strong indicator that the device has been modified.

## Server-Side Verification

A server can verify the device integrity by validating the attestation certificate chain and inspecting these fields. This includes checking the certificate's validity (expiration, signature chain up to the [Google Hardware Attestation Root Certificate](https://developer.android.com/training/articles/security-key-attestation#root_certificate), and [revocation status](https://developer.android.com/training/articles/security-key-attestation#certificate_status)) as well as confirming that `attestationSecurityLevel` is `TrustedEnvironment` or `StrongBox`, `verifiedBootState` is `Verified`, and `deviceLocked` is `true`.

Combined with application attestation (@MASTG-KNOW-02kw), which verifies the identity and integrity of the calling application, these checks allow a server to establish that a request originates from a legitimate application running on a device with a verified, unmodified software stack.

For the full list of key pair properties attested in the authorization lists, see @MASTG-KNOW-0044.
