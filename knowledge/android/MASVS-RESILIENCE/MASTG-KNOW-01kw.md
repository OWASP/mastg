---
masvs_category: MASVS-RESILIENCE
platform: android
title: Device Attestation via Hardware-Backed Key Attestation
---

The [attestation extension data](https://developer.android.com/training/articles/security-key-attestation#certificate_schema) embedded in the leaf certificate of a Key Attestation (@MASTG-KNOW-0044) certificate chain contains detailed information about both the device's integrity state and the properties of the attested key pair. This data is what enables a remote server to make trust decisions about the client environment.

## Device Integrity Signals

The attestation extension data includes the following device integrity fields:

- **Verified boot state**: Indicates whether the device's boot chain has been verified as unmodified. A `Verified` state means the bootloader confirmed the integrity of all boot partitions.
- **Verified boot key**: The public key used to verify the boot image. On unmodified devices, this matches the OEM's embedded key.
- **Bootloader lock status**: A locked bootloader prevents flashing unsigned images. An unlocked bootloader is a strong indicator that the device has been modified.

## Key Properties

The attestation extension data also describes the configuration of the attested key pair:

- **Purpose**: The authorized operations for the key (e.g., signing, encryption).
- **Authentication requirements**: Whether user authentication (e.g., biometric via [`setUserAuthenticationRequired`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setuserauthenticationrequired)) is required before key use.
- **Origin**: Whether the key was generated on the device (`KeyOrigin.GENERATED`) or imported. A device-generated key has never existed outside the hardware.
- **Security level**: Whether the key is protected by `Software`, `TrustedEnvironment` (Trusted Execution Environment), or `StrongBox` (dedicated secure element). Only `TrustedEnvironment` and `StrongBox` indicate hardware-backed protection. See @MASTG-KNOW-0047 for the full key storage hierarchy.
