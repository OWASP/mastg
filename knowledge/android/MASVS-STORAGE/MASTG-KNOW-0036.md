---
masvs_category: MASVS-STORAGE
platform: android
title: Shared Preferences
---

!!! warning

    Android recommends [`DataStore`](https://developer.android.com/topic/libraries/architecture/datastore) as a modern replacement for `SharedPreferences`. See @MASTG-BEST-0050 for more information.

The [`SharedPreferences`](https://developer.android.com/training/data-storage/shared-preferences "Shared Preferences") API is commonly used to persist small collections of key-value pairs in the app's sandbox storage.

Since Android 4.2 (API level 17), the `MODE_WORLD_READABLE` and `MODE_WORLD_WRITEABLE` flags for `SharedPreferences` have been deprecated because they allow other apps to access the created file. Starting with Android 7.0 (API level 24), using either flag throws a [`SecurityException`](https://developer.android.com/reference/java/lang/SecurityException).

Use `SharedPreferences` in private mode by calling `getSharedPreferences` with `Context.MODE_PRIVATE`. See ["Use SharedPreferences in private mode"](https://developer.android.com/privacy-and-security/security-best-practices#sharedpreferences).

When private mode is used, the XML file containing the key-value data is stored with permissions that restrict access to the app's own Linux user ID. Under the normal Android app sandbox, other apps cannot read this file directly.

However, private mode does not encrypt the data. The values are still written in plaintext in the XML file.

Consider the following example:

```kotlin
var sharedPref = getSharedPreferences("key", Context.MODE_PRIVATE)
var editor = sharedPref.edit()
editor.putString("username", "administrator")
editor.putString("password", "supersecret")
editor.commit()
```

Once the activity has been called, the file `key.xml` will be created, meaning that the username and password are stored in cleartext in `/data/data/<package-name>/shared_prefs/key.xml`:

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
  <string name="username">administrator</string>
  <string name="password">supersecret</string>
</map>
```

[`EncryptedSharedPreferences`](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) is a `SharedPreferences` wrapper from the Jetpack Security Crypto library. It encrypts preference keys with `AES256_SIV`, a deterministic Authenticated Encryption with Associated Data (AEAD) scheme, and preference values with `AES256_GCM`, an AEAD scheme, before writing them to disk. See the [`EncryptedSharedPreferences` source code](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:security/security-crypto/src/main/java/androidx/security/crypto/EncryptedSharedPreferences.java) for implementation details.

!!! warning

    The **Jetpack Security Crypto library**, including the `EncryptedFile` and `EncryptedSharedPreferences` classes, has been [deprecated](https://developer.android.com/privacy-and-security/cryptography#jetpack_security_crypto_library). All APIs in the library were deprecated in stable version `1.1.0`, and Android states that there will be no subsequent releases. For existing apps that must continue using `SharedPreferences` for sensitive data, `EncryptedSharedPreferences` may still be a practical mitigation, but it should not be treated as a long term storage strategy. Monitor Android's cryptography and DataStore guidance, and plan a migration to a supported encryption approach when available.

When using `EncryptedSharedPreferences`, exclude the encrypted preference file from Auto Backup. Android's API reference warns that restoring the file may fail because the key used to encrypt it might no longer be available.
