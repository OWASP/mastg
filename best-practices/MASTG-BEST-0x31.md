---
title: Store Data Encrypted in App Sandbox Directory
alias: store-data-encrypted-in-the-app-sandbox-directory
id: MASTG-BEST-0x31
platform: android
knowledge: [MASTG-KNOW-0036]
---

Store sensitive data in `SharedPreferences` only after encrypting it. Standard `SharedPreferences` stores values in XML files inside the app's private data directory, so values such as credentials, authentication tokens, API keys, private keys, or personally identifiable information (PII) should not be stored in cleartext.

For apps that use `SharedPreferences`, use [`EncryptedSharedPreferences`](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) or an equivalent mechanism that encrypts preference keys and values before they are written to disk.

`EncryptedSharedPreferences` wraps `SharedPreferences`, uses key material protected by the Android Keystore, and encrypts preference keys and values before storing them.

!!! note
    `EncryptedSharedPreferences` is part of the Jetpack Security Crypto library, which has been [deprecated](https://developer.android.com/privacy-and-security/cryptography#jetpack_security_crypto_library). Until Android provides an official replacement, it remains a practical mitigation for apps that need to keep using `SharedPreferences` for sensitive data. Monitor Android's cryptography guidance and plan migrations when a supported replacement becomes available.

As Android recommends [`DataStore`](https://developer.android.com/topic/libraries/architecture/datastore) as a modern replacement for `SharedPreferences`, but `DataStore` doesn't encrypt data by default. If you migrate sensitive data to `DataStore`, apply an appropriate encryption layer before writing the data.
