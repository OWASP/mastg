---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Storage Integrity Checks
best-practices: [MASTG-BEST-0065]
---

Apps can protect data they store on the device (for example in the Keychain, `UserDefaults`/`NSUserDefaults`, or a database) by computing an HMAC or cryptographic signature over it and verifying that value before each use. This lets the app detect whether stored data was modified, for example on a jailbroken device, through a backup, or by directly manipulating the app's data directory. For verifying the integrity of the app's own executable code, see @MASTG-KNOW-0140.

A common approach uses `CCHmac` from CommonCrypto with a key held in the Keychain:

```objectivec
// Generate HMAC
NSMutableData* actualData = [getData];
NSData* key = [getKey];  // key retrieved from the Keychain
NSMutableData* digestBuffer = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
CCHmac(kCCHmacAlgSHA256, [key bytes], (CC_LONG)[key length], [actualData bytes], (CC_LONG)[actualData length], [digestBuffer mutableBytes]);
[actualData appendData: digestBuffer];
```

Verification recomputes the HMAC and compares it to the stored value:

```objectivec
// Verify HMAC
NSData* hmac = [data subdataWithRange:NSMakeRange(data.length - CC_SHA256_DIGEST_LENGTH, CC_SHA256_DIGEST_LENGTH)];
NSData* actualData = [data subdataWithRange:NSMakeRange(0, (data.length - hmac.length))];
NSMutableData* digestBuffer = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
CCHmac(kCCHmacAlgSHA256, [key bytes], (CC_LONG)[key length], [actualData bytes], (CC_LONG)[actualData length], [digestBuffer mutableBytes]);
return [hmac isEqual: digestBuffer];
```

Alternatively, the [Security framework](https://developer.apple.com/documentation/security) provides `SecKeyCreateSignature` and `SecKeyVerifySignature` for asymmetric signing of stored data.

When data is both encrypted and MACed, the [Encrypt-then-MAC](https://web.archive.org/web/20210804035343/https://cseweb.ucsd.edu/~mihir/papers/oem.html "Authenticated Encryption: Relations among notions and analysis of the generic composition paradigm") ordering provides stronger integrity guarantees: the HMAC is computed over the ciphertext rather than the plaintext.

These checks can be circumvented on jailbroken devices by extracting the HMAC key from the Keychain or by intercepting the verification function at runtime.
