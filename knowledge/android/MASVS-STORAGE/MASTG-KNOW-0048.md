---
masvs_category: MASVS-STORAGE
platform: android
title: KeyChain
---

The [KeyChain](https://developer.android.com/reference/android/security/KeyChain.html "Android KeyChain") class is used to store and retrieve _system-wide_ private keys and their corresponding certificates (chain). The user will be prompted to set a lock screen pin or password to protect the credential storage if something is being imported into the KeyChain for the first time.Note that although the KeyChain is system-wide, applications do not have unrestricted access to its contents. An app first requests permission for a specific key via `KeyChain.choosePrivateKeyAlias()`, and only after the user grants access can it retrieve the key with `getPrivateKey()` and the certificate chain with `getCertificateChain()`. For hardware-backed keys, the raw key material is never exposed to the app.

Inspect the source code to determine whether native Android mechanisms identify sensitive information. Sensitive information should be encrypted, not stored in clear text. For sensitive information that must be stored on the device, several API calls are available to protect the data via the `KeyChain` class. Complete the following steps:

- Make sure that private keys and certificates are stored in and accessed through the KeyChain instead of being hardcoded or written to app-private files in clear text. Look for the patterns `import android.security.KeyChain`, `KeyChain.choosePrivateKeyAlias`, `KeyChain.getPrivateKey`, and `KeyChain.getCertificateChain`, and corresponding usages.
- Make sure that the app verifies that the private key retrieved from the KeyChain is bound to secure hardware, so that it cannot be extracted even on a rooted device. Look for the patterns `KeyFactory`, `KeyInfo`, `getKeySpec`, `isInsideSecureHardware()` (deprecated in API level 31), and `getSecurityLevel()` (API level 31+), and corresponding usages.
