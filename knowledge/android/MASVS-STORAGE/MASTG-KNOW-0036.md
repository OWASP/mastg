---
masvs_category: MASVS-STORAGE
platform: android
title: Shared Preferences
---

The [`SharedPreferences`](https://developer.android.com/training/data-storage/shared-preferences "Shared Preferences") API is commonly used to permanently save small collections of key-value pairs in the app's sandbox storage.

Since Android 4.2 (API level 17) the `SharedPreferences` object can only be declared to be private (and not world-readable, i.e. accessible to all apps).

This is made by interacting with `SharedPreferences` by calling `getSharedPreferences` with the `Context.MODE_PRIVATE` flag (see ["Use SharedPreferences in private mode"](https://developer.android.com/privacy-and-security/security-best-practices#sharedpreferences).

When using such flag, the XML file containing the key-value data is stored with permissions that restrict access to the app's own Linux user ID. Under the normal Android app sandbox, other apps cannot read this file directly.

However, this flag does not provide encryption of the data, as the data is still written in plain-text in the XML file.

Consider the following example:

```kotlin
var sharedPref = getSharedPreferences("key", Context.MODE_PRIVATE)
var editor = sharedPref.edit()
editor.putString("username", "administrator")
editor.putString("password", "supersecret")
editor.commit()
```

Once the activity has been called, the file key.xml will be created with the provided data. This code violates several best practices.

- The username and password are stored in clear text in `/data/data/<package-name>/shared_prefs/key.xml`.

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
  <string name="username">administrator</string>
  <string name="password">supersecret</string>
</map>
```


> Other insecure modes exist, such as `MODE_WORLD_READABLE` and `MODE_WORLD_WRITEABLE`, but they have been deprecated since Android 4.2 (API level 17) and [removed in Android 7.0 (API Level 24)](https://developer.android.com/reference/android/os/Build.VERSION_CODES#N). Therefore, only apps running on an older OS version (`android:minSdkVersion` less than 17) will be affected. Otherwise, Android will throw a [`SecurityException`](https://developer.android.com/reference/java/lang/SecurityException). If an app needs to share private files with other apps, it is best to use a [`FileProvider`](https://developer.android.com/reference/androidx/core/content/FileProvider) with the [`FLAG_GRANT_READ_URI_PERMISSION`](https://developer.android.com/reference/android/content/Intent#FLAG_GRANT_READ_URI_PERMISSION). See ["Sharing Files"](https://developer.android.com/training/secure-file-sharing) for more details.

[`EncryptedSharedPreferences`](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) is a `SharedPreferences` wrapper from the Jetpack Security Crypto library. It encrypts preference keys with `AES256_SIV`, a deterministic Authenticated Encryption with Associated Data (AEAD) scheme, and preference values with `AES256_GCM`, an AEAD scheme, before writing them to disk. See the [`EncryptedSharedPreferences` source code](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:security/security-crypto/src/main/java/androidx/security/crypto/EncryptedSharedPreferences.java) for implementation details.

!!! Warning

    The **Jetpack security crypto library**, including the `EncryptedFile` and  `EncryptedSharedPreferences` classes, has been [deprecated](https://developer.android.com/privacy-and-security/cryptography#jetpack_security_crypto_library). However, since an official replacement has not yet been released, we recommend using these classes until one is available.
The following example details storing data using `EncryptedSharedPreferences`, which encrypts the preference keys and values before writing them to disk:

```kotlin
var masterKey: MasterKey? = null
masterKey = Builder(this)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
    this,
    "secret_shared_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

val editor = sharedPreferences.edit()
editor.putString("username", "administrator")
editor.putString("password", "supersecret")
editor.commit()
```
