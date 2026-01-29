---
masvs_category: MASVS-STORAGE
platform: android
title: Android KeyStore
available_since: 18
---

The [Android KeyStore](https://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/ "Use Android KeyStore") provides relatively secure credential storage. As of Android 4.3 (API level 18), it provides public APIs for storing and using app-private keys. An app can use a public key to generate a new private/public key pair to encrypt application secrets, and then decrypt them with the private key.

You can protect keys stored in the Android KeyStore using user authentication via a confirm credential flow. The user's lock screen credentials (pattern, PIN, password, or fingerprint) are used for authentication.

You can use stored keys in one of two modes:

1. Users are authorized to use keys for a limited time after authentication. In this mode, all keys can be used as soon as the user unlocks the device. You can customize the authorization period for each key. You can use this option only if the secure lock screen is enabled. If the user disables the secure lock screen, all stored keys will become permanently invalid.

2. Users are authorized to use a specific cryptographic operation associated with one key. In this mode, users must request separate authorization for each operation that involves the key. Currently, fingerprint authentication is the only way to request such authorization.

The security provided by the Android KeyStore depends on its implementation, which varies by device. Most modern devices offer a [hardware-backed KeyStore implementation](#hardware-backed-android-keystore): keys are generated and used in a Trusted Execution Environment (TEE) or a Secure Element (SE), and the operating system can't access them directly. As a result, the encryption keys can't be easily retrieved, even on a rooted device. You can verify hardware-backed keys with @MASTG-KNOW-0044. You can determine whether the keys are stored in secure hardware by checking the return value of the `isInsideSecureHardware` method, which is part of the [`KeyInfo` class](https://developer.android.com/reference/android/security/keystore/KeyInfo.html "Class KeyInfo").

!!! note
    The relevant KeyInfo indicates that secret and HMAC keys are insecurely stored on several devices, even though private keys are correctly stored on the secure hardware.

In a software-only implementation, the keys are encrypted with a [per-user encryption master key](https://nelenkov.blogspot.sg/2013/08/credential-storage-enhancements-android-43.html "Nikolay Elenkov - Credential storage enhancements in Android 4.3"). An attacker can access all keys stored on rooted devices that use this implementation in the `/data/misc/keystore/` folder. Because the user's lock screen PIN or password is used to generate the master key, the Android KeyStore is unavailable when the device is locked. For added security, Android 9 (API level 28) introduces the `unlockedDeviceRequired` flag. By passing `true` to the `setUnlockedDeviceRequired` method, the app prevents its keys stored in `AndroidKeystore` from being decrypted when the device is locked and requires the screen to be unlocked before allowing decryption.

## Hardware-backed Android KeyStore

The hardware-backed Android KeyStore adds another layer to Android's defense-in-depth security. The Keymaster Hardware Abstraction Layer (HAL) was introduced in Android 6 (API level 23). Applications can verify whether a key is stored in secure hardware by checking whether `KeyInfo.isInsideSecureHardware` returns `true`. Devices running Android 9 (API level 28) and higher can include a `StrongBox Keymaster` module, an implementation of the Keymaster HAL that resides in a hardware security module with its own CPU, secure storage, a true random number generator, and mechanisms to resist tampering. To use this feature, pass `true` to the `setIsStrongBoxBacked` method in either the `KeyGenParameterSpec.Builder` class or the `KeyProtection.Builder` class when generating or importing keys using `AndroidKeystore`. To ensure StrongBox is used at runtime, verify that `isInsideSecureHardware` returns `true` and that the system does not throw `StrongBoxUnavailableException`, which is thrown if the StrongBox Keymaster isn't available for the given algorithm and key size associated with a key. A description of the features of the hardware-based keystore can be found on the [AOSP pages](https://source.android.com/docs/security/features/keystore "AOSP Hardware-based KeyStore"). You can also check the [Android Device Security Database](https://www.android-device-security.org/database/ "Android Device Security Database") to see [which devices support StrongBox](https://www.android-device-security.org/database/?sortBy=COUNT%20Lab%20Strongbox%20True;COUNT%20Lab%20Strongbox%20False&order=-1&show=Strongbox&Strongbox=True) and other hardware security features.

Keymaster HAL is an interface to hardware-backed components, such as a Trusted Execution Environment (TEE) or a Secure Element (SE), that Android Keystore uses. An example of such a hardware-backed component is [Titan M](https://android-developers.googleblog.com/2018/10/building-titan-better-security-through.html "Building a Titan: Better security through a tiny chip").
