---
masvs_category: MASVS-RESILIENCE
platform: android
title: Device Attestation via Hardware-Backed Key Attestation
---

Device attestation uses the `rootOfTrust` field in the `AuthorizationList` of a Key Attestation (@MASTG-KNOW-0044) certificate chain, which allows a verifier (usually a remote server) to verify the integrity of the device's software stack. For the full list of fields and their meanings, see @MASTG-KNOW-0044.

## Root of Trust Fields

The `rootOfTrust` object populated by the secure hardware (TEE or StrongBox) during key generation is placed in the `hardwareEnforced` `AuthorizationList`. Because it is hardware-enforced, these values cannot be tampered with by the Android OS. When the `attestationSecurityLevel` is `Software`, the `rootOfTrust` may instead appear in the `softwareEnforced` list, providing no hardware-backed guarantee.

The `rootOfTrust` object contains the following fields:

- **`verifiedBootState`**: Reflects the result of Android's [Verified Boot](https://source.android.com/docs/security/features/verifiedboot) process:
    - **`Verified`**: The entire boot chain, from bootloader to system partition, was verified against known-good OEM keys. This is the expected state for an unmodified production device.
    - **`SelfSigned`**: The device booted with a user-installed root of trust (e.g., a custom key). The bootloader is unlocked and a user-provided key was accepted.
    - **`Unverified`**: No verification was performed. The bootloader is unlocked and no custom key was set.
    - **`Failed`**: Verification was attempted but failed. The device should not have booted in this state under normal conditions.
- **`verifiedBootKey`**: The public key used to verify the boot image. On unmodified production devices, this matches the OEM's embedded root-of-trust key.
- **`deviceLocked`**: `true` if the bootloader is locked, preventing unauthorized modifications to the system partition.

## Low or No Device Integrity Signals

The following conditions indicate low or no device integrity:

- **`verifiedBootState` is not `Verified`**: The boot chain was not fully verified against OEM keys. A `SelfSigned` state means the device is running a custom ROM with a user-installed key; `Unverified` means no verification was performed at all; `Failed` means verification was attempted and failed.
- **`deviceLocked` is `false`**: The bootloader is unlocked, meaning the system partition can be modified without triggering a boot failure. This is a strong signal that the device may have been tampered with.
- **`attestationSecurityLevel` is `Software`**: The attestation was generated entirely in the Android OS with no hardware involvement. It can be trusted as long as the device is running an operating system that complies with the [Android Platform Security Model](https://arxiv.org/pdf/1904.05572) (that is, the `deviceLocked` is `true` and the `verifiedBootState` is `Verified`).

## Limitations

Device attestation via Key Attestation reflects the state of the device **at the time the key was generated**, not at the time of any subsequent API call or use. A key generated on a clean device retains its attestation even if the device is later rooted or its bootloader is unlocked after key generation.

Device attestation also cannot detect all forms of compromise. A device that was unlocked and re-locked, or that had its verified boot state manipulated at a hardware level (which is extremely difficult but theoretically possible), would still pass verification. It cannot replace a comprehensive device integrity strategy.