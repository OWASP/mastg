---
title: Use EncryptedFile for Sensitive Data in Internal Storage
alias: use-encrypted-file-for-sensitive-data-in-internal-storage
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0x01, MASTG-KNOW-0041]
---

Use [`EncryptedFile`](https://developer.android.com/reference/androidx/security/crypto/EncryptedFile) from the [Jetpack Security library](https://developer.android.com/topic/security/data) when writing sensitive data to internal storage. `EncryptedFile` transparently encrypts file contents using [AES-256-GCM-HKDF-4KB](https://developers.google.com/tink/streaming-aead/aes_gcm_hkdf_streaming) before writing them to disk, providing transparent protection at rest.

```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedFile = EncryptedFile.Builder(
    context,
    File(context.filesDir, "sensitive_data.bin"),
    masterKey,
    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
).build()

encryptedFile.openFileOutput().use { output ->
    output.write("sensitive content".toByteArray())
}
```

The encryption key is generated and stored in the Android KeyStore, providing hardware-backed protection on supported devices. The resulting file is unreadable without the key, so even if an attacker gains access to the app's sandbox (for example, on a rooted device), the plaintext cannot be recovered without the key.

!!! warning

    The **Jetpack Security crypto library**, including `EncryptedFile` and `EncryptedSharedPreferences`, has been [deprecated](https://developer.android.com/privacy-and-security/cryptography#jetpack_security_crypto_library). However, since an official replacement has not yet been released, we recommend using these classes until one is available.
