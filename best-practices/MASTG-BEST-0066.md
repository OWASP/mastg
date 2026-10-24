---
title: Implementing Storage Integrity Checks on Android
alias: implementing-storage-integrity-checks-android
id: MASTG-BEST-0066
platform: android
knowledge: [MASTG-KNOW-0036]
---

Implement storage integrity checks in Android apps to detect unauthorized modifications to data stored on the device (for example, in `SharedPreferences`, files, or databases). These checks raise the cost for attackers who try to tamper with stored data, especially on rooted devices, through backups, or by directly manipulating the app's data directory.

## Storage Integrity

Compute an HMAC over any data you store on the device before writing it, and verify the HMAC before reading it back. Use a key that is generated and stored in the [Android Keystore](https://developer.android.com/privacy-and-security/keystore) so that it cannot be extracted from the app package or a backup.

```kotlin
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmac(data: ByteArray, key: ByteArray): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return mac.doFinal(data)
}

fun verify(data: ByteArray, tag: ByteArray, key: ByteArray): Boolean {
    return hmac(data, key).contentEquals(tag)
}
```

Alternatively, the Security framework provides `java.security.Signature` for asymmetric signing and verification of stored data when a public/private key pair is more appropriate than a shared secret.

If you also encrypt the data, follow the [Encrypt-then-MAC](https://web.archive.org/web/20210804035343/https://cseweb.ucsd.edu/~mihir/papers/oem.html) pattern: encrypt first, then compute the HMAC over the ciphertext.

!!! warning
    Storage integrity checks are bypassable if the attacker can extract the HMAC key (for example, if it is hardcoded in the app or recoverable on a rooted device) or intercept the verification logic at runtime. Treat these as a defense-in-depth control rather than a standalone guarantee. See @MASTG-KNOW-0036 for more information on `SharedPreferences` storage.
