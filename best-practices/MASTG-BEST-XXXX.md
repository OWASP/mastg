---
title: Protect Locally Stored Data Integrity with AndroidKeyStore-Backed HMAC
id: MASTG-BEST-0x01
platform: android
---

When an Android app reads data from local storage and makes security-relevant decisions based on it — such as determining user roles, feature flags, or access tokens — integrity validation must protect the data against unauthorized modification. Without a tamper-detection mechanism, an attacker with local write access (via root, malware, or `adb shell run-as` on a debuggable build) can modify the stored data and the app will accept it.

## Recommended Approach

Store data alongside an HMAC computed with a key generated inside AndroidKeyStore. On read-back, recompute the HMAC and compare the result using a constant-time comparison function (`MessageDigest.isEqual`) before trusting the value.

### Generating the Key

Generate an HMAC key with `PURPOSE_SIGN | PURPOSE_VERIFY` so that Android enforces the key's usage scope:

```kotlin
val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")
kg.init(
    KeyGenParameterSpec.Builder(
        "my_integrity_key",
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    ).setDigests(KeyProperties.DIGEST_SHA256).build()
)
val key: SecretKey = kg.generateKey()
```

The key never leaves the Keystore in extractable form. Forging the HMAC requires a hardware-level bypass of the secure element, which is out of reach for the vast majority of attackers.

### Computing and Verifying the HMAC

```kotlin
fun computeHmac(data: ByteArray, key: SecretKey): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(key)
    return mac.doFinal(data)
}

fun verifyHmac(data: ByteArray, stored: ByteArray, key: SecretKey): Boolean =
    MessageDigest.isEqual(computeHmac(data, key), stored)
```

Use `MessageDigest.isEqual` rather than `Arrays.equals` or `String.equals` to prevent timing side-channel attacks that leak the HMAC value byte by byte.

## What Not to Do

Don't construct the HMAC key from a hardcoded byte array or string constant:

```kotlin
val key = SecretKeySpec("my-hardcoded-key".toByteArray(), "HmacSHA256")
```

Any constant embedded in the APK can be extracted with standard reverse-engineering tools (jadx, apktool). An attacker who recovers the constant can recompute a valid HMAC for any tampered payload, defeating the protection entirely.

## References

- Android Keystore system - <https://developer.android.com/privacy-and-security/keystore>
- KeyGenParameterSpec - <https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec>
- MessageDigest.isEqual - <https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html#isEqual-byte:A-byte:A->
- @MASTG-TEST-0x01
