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

## When EncryptedFile Is Not an Option

If you can't use `EncryptedFile` (for example, because of library constraints, minimum API level requirements, or native code), encrypt the data manually before writing it to disk.

The recommended approach on Android is to:

1. Generate or retrieve an AES key stored in the Android KeyStore (see @MASTG-KNOW-0043).
2. Encrypt the plaintext with `AES/GCM/NoPadding` using the [`Cipher`](https://developer.android.com/reference/javax/crypto/Cipher) API.
3. Prepend the initialization vector (IV) to the ciphertext and write the combined bytes to the file using any of the standard File APIs (see @MASTG-KNOW-0x01).
4. On read, extract the IV and decrypt with the same key.

```kotlin
// Generate or load a key from the Android KeyStore
val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
keyGenerator.init(
    KeyGenParameterSpec.Builder("my_key_alias",
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()
)
val secretKey = keyGenerator.generateKey()

// Encrypt
val cipher = Cipher.getInstance("AES/GCM/NoPadding")
cipher.init(Cipher.ENCRYPT_MODE, secretKey)
val iv = cipher.iv
val ciphertext = cipher.doFinal("sensitive content".toByteArray())

// Write IV + ciphertext
val file = File(context.filesDir, "sensitive_data.bin")
FileOutputStream(file).use { fos ->
    fos.write(iv)
    fos.write(ciphertext)
}
```

Always use an authenticated encryption mode such as GCM.

## Native, NDK, and JNI Code

When file writes happen from native code, encrypt the data before passing it to the native layer, or use a well-audited native encryption library. Do not implement custom cryptographic primitives.

**Preferred - Encrypt in Java or Kotlin, pass ciphertext to native code:**

Encrypt the data on the Java or Kotlin side using Android KeyStore backed keys and `Cipher`, then pass only the resulting ciphertext byte array to the JNI layer for writing. This keeps key generation, storage, and access control within the Android security framework.

**Alternative - Use a native encryption library:**

If encryption must occur in native code, use a well-established library such as [Tink C++](https://developers.google.com/tink/tinkcrypto), [BoringSSL](https://boringssl.googlesource.com/boringssl/), OpenSSL, or a trusted encrypted storage library such as SQLCipher. Keys used by native code should still be generated, wrapped, or derived using Android KeyStore. They must not be hardcoded or stored in plaintext alongside the encrypted data.

**Real world example, Signal Android.**

Signal Android uses [SQLCipher for Android](https://github.com/signalapp/sqlcipher-android), a native encrypted SQLite implementation, for local database encryption. Signal's Java code generates a random 32 byte database secret, protects it with Android KeyStore through `KeyStoreHelper.seal(...)`, stores only the wrapped value in `SharedPreferences`, and later unwraps it with `KeyStoreHelper.unseal(...)` when opening the database. This behavior can be seen in Signal's [`DatabaseSecretProvider`](https://github.com/signalapp/Signal-Android/blob/main/app/src/main/java/org/thoughtcrime/securesms/crypto/DatabaseSecretProvider.java).

This is a production example of keeping key management in the Android framework while relying on a well-established native encryption layer for file backed storage. The important pattern is that the encryption secret is randomly generated and KeyStore protected, rather than being hardcoded or stored in plaintext beside the encrypted database.

Molly, a Signal fork, also [documents this design](https://github.com/mollyim/mollyim-android/wiki/Data-Encryption-At-Rest), noting that Signal stores contacts, chat history, and attachments in an SQLCipher database and wraps the database encryption key with Android KeyStore before storing it in `SharedPreferences`.
