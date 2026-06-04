---
title: Store Data Encrypted in App Sandbox Directory
alias: store-data-encrypted-in-the-app-sandbox-directory
id: MASTG-BEST-0050
platform: android
knowledge: [MASTG-KNOW-0036]
---

Store sensitive data in `SharedPreferences` only after encrypting it. Standard `SharedPreferences` stores values in XML files inside the app's private data directory, so values such as credentials, authentication tokens, private keys, or personally identifiable information (PII) should not be stored in cleartext.

For apps that use `SharedPreferences`, use [`EncryptedSharedPreferences`](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) or an equivalent mechanism that encrypts preference keys and values before they are written to disk. An equivalent mechanism should use authenticated encryption, protect encryption keys with the Android Keystore or another appropriate key management system, and avoid custom cryptography.

!!! note
    `EncryptedSharedPreferences` is part of the Jetpack Security Crypto library. All APIs in that library were [deprecated](https://developer.android.com/privacy-and-security/cryptography#security-crypto-jetpack-deprecated) in version `1.1.0`, and Android states that there will be no subsequent releases. It may still be a practical mitigation for existing apps that must keep using `SharedPreferences`, but it should not be treated as a long term storage strategy. Monitor Android's cryptography and DataStore guidance, and plan a migration to a supported encryption approach when available.

Android recommends [`DataStore`](https://developer.android.com/topic/libraries/architecture/datastore) as a modern replacement for `SharedPreferences`, but `DataStore` does not encrypt data by default. If you migrate sensitive data to `DataStore`, use an appropriate encryption layer before writing the data. AndroidX introduced the `androidx.datastore:datastore-tink` artifact for DataStore encryption support in version `1.3.0-alpha07`. This provides encryption using the Tink library and [`AeadSerializer`](https://developer.android.com/reference/kotlin/androidx/datastore/tink/AeadSerializer), which wraps an existing DataStore serializer and performs authenticated encryption and decryption. See the [DataStore release notes](https://developer.android.com/jetpack/androidx/releases/datastore#1.3.0-alpha07) for more information and an example of use.
