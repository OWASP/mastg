---
title: Encrypt Sensitive Data in Private Storage Locations
alias: encrypt-sensitive-data
id: MASTG-BEST-0025
platform: android
knowledge: [MASTG-KNOW-0036, MASTG-KNOW-0037, MASTG-KNOW-0038, MASTG-KNOW-0041]
---

Limit local storage of sensitive data to cases where it is strictly required. Android's app-private directories provide isolation but do not protect data on rooted devices, compromised devices, or during physical extraction. Therefore, sensitive data stored locally must always be **encrypted at rest**.

## App Private Storage

The following sandboxed locations (`/data/data/<package>/`) are private but not secure against privileged attackers:

- `filesDir` – Internal app files.
- `noBackupDir` – Internal files excluded from Auto Backup.
- `databases` – SQLite databases.
- `shared_prefs` – SharedPreferences.

Because sandbox isolation does not mitigate elevated access, **plaintext sensitive data must not be stored** in these locations.

## Use Platform Cryptography

Prefer platform-backed cryptography for local storage encryption.

### Deprecated Jetpack Security Components

**`EncryptedFile`** and **`EncryptedSharedPreferences`** (from `androidx.security:security-crypto`) are **deprecated** as of 1.1.0.

#### `EncryptedFile`

- **Deprecated:** API documentation states: "Use `java.io.File` instead."
- Uses a `MasterKey` stored in Android Keystore and enforces **AES256\_GCM\_HKDF\_4KB**.
- Must be **excluded from Auto Backup** (the restored file will not decrypt because the key will differ).
- A hidden keyset preferences file (`.../shared_prefs/__androidx_security_crypto_encrypted_file_pref__`) is auto-created and must also be **excluded from backups**.
- Decryption requires the identical master key and keyset; mismatch causes `GeneralSecurityException`.

#### `EncryptedSharedPreferences`

- **Deprecated:** API documentation states: "Use `android.content.SharedPreferences` instead."
- Encrypts keys (AES-SIV) and values (AES-GCM).
- Must be **excluded from Auto Backup.**
- Requires the same master key and keyset; mismatch leads to decryption failure.

### When Using Deprecated Classes

- Exclude encrypted files and all keyset preference files from Auto Backup.
- Reuse a single, consistent **`MasterKey`**.
- Prefer **hardware-backed Keystore keys** (TEE/StrongBox) when available.

### For New Codebases

Use maintained and recommended alternatives:

- Android Keystore + manual **AES-GCM** implementation.
- **Tink**, **SQLCipher**, or equivalent well-maintained cryptographic library.

## Envelope Encryption (Recommended)

For databases or large files, use **envelope encryption**:

- **DEK (Data Encryption Key)** — symmetric key (AES-GCM) used to encrypt data.
- **KEK (Key Encryption Key)** — stored securely in Android Keystore; used to encrypt the DEK.

This approach provides efficient symmetric encryption while protecting the key material using hardware-backed Keystore, when available.

### SQLite

- Use **SQLCipher** for full-database AES-256 encryption.
- Protect the SQLCipher passphrase using **envelope encryption**.
- Never store database keys or passphrases in plaintext `SharedPreferences`.

### Key–Value Data

- Use `EncryptedSharedPreferences` only for legacy/maintenance code.
- Prefer **DataStore** + manual **AES-GCM** or **Tink** for new implementations.
- Ensure **AES-GCM** operations use unique nonces.

## Storage Location Guidance

| Location | Recommendation |
| :--- | :--- |
| `filesDir` | Allowed only for **encrypted data**. Private, but unsecured on rooted devices or after physical extraction. |
| `noBackupFilesDir` | Allowed for **encrypted data that must not appear in backups**. Prevents key mismatch issues after system restore. |

### Locations to Avoid

- **External / Shared Storage:** Prohibited. No protection against access by other apps.
- **Plaintext `SharedPreferences`:** Prohibited. Data is easily accessible if the device is compromised.
- **Plaintext Cache:** Prohibited. Lacks strong protection for sensitive data.

> **Summary:** Use only `filesDir` and `noBackupFilesDir` for sensitive data, always encrypting it using platform cryptography or a well-maintained library.

---

## Resources

- [`EncryptedFile` (Android API Reference)](https://developer.android.com/reference/androidx/security/crypto/EncryptedFile)
- [`EncryptedSharedPreferences` (Android API Reference)](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
