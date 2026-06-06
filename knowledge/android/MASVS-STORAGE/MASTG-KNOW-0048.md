---
masvs_category: MASVS-STORAGE
platform: android
title: KeyChain
---

The [KeyChain](https://developer.android.com/reference/android/security/KeyChain) class is used to store and retrieve system-wide private keys and their corresponding certificate chains. The user will be prompted to set a lock screen PIN or password to protect the credential storage if something is being imported into the KeyChain for the first time.

Note that although the KeyChain is system-wide, applications do not have unrestricted access to its contents. Access to a given key is mediated either by user consent — typically via `KeyChain.choosePrivateKeyAlias()`, which presents a system UI for the user to select which credential the app may use — or by device policy, where a Device or Profile Owner grants access through [`DevicePolicyManager`](https://developer.android.com/reference/android/app/admin/DevicePolicyManager). Only after access has been granted can the app retrieve the key with `KeyChain.getPrivateKey()` and the certificate chain with `KeyChain.getCertificateChain()`. For hardware-backed keys, the raw key material is never exposed to the app (see the [Android Keystore system](https://developer.android.com/privacy-and-security/keystore)).

Note: `AndroidKeyStore` and `KeyChain` serve different purposes and should not be used interchangeably. The [`AndroidKeyStore`](https://developer.android.com/privacy-and-security/keystore) provider is used to store and use an application's _own_ cryptographic keys, which only that app can access. `KeyChain`, on the other hand, is designed for user-selected, system-wide shareable private keys and certificate chains. To protect an app's own keys and data, prefer `AndroidKeyStore` (see @MASTG-KNOW-0043).

Inspect the source code to determine which native Android mechanisms are used to handle sensitive information. Sensitive data should be encrypted, not stored in clear text. Specifically, private keys and certificates that must be stored on the device should be managed through the `KeyChain` class; to protect an app's own data and secrets, use the `AndroidKeyStore` (see @MASTG-KNOW-0043). Complete the following steps:

- Make sure that private keys and certificates are stored in and accessed through the KeyChain instead of being hardcoded or written to app-private files in clear text. Look for the patterns `import android.security.KeyChain`, `KeyChain.choosePrivateKeyAlias`, `KeyChain.getPrivateKey`, and `KeyChain.getCertificateChain`, and their corresponding usages.
- Make sure that the app verifies that the private key retrieved from the KeyChain is bound to secure hardware, so that it cannot be extracted even on a rooted device. Look for the patterns `KeyFactory`, [`KeyInfo`](https://developer.android.com/reference/android/security/keystore/KeyInfo), `getKeySpec`, `isInsideSecureHardware()` (deprecated in API level 31), and `getSecurityLevel()` (API level 31+), and their corresponding usages.
