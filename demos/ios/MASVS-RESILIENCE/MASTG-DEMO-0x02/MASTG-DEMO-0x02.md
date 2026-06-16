---
platform: ios
title: Storing Data Without File Storage Integrity Checks
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: fail
---

<!-- Placeholder: the rest of this demo lives on the MASTG-TEST-0090 branch.
     Only the v2.1 spec sections (Exploitation, Fix) are staged here. -->

### Exploitation

You can confirm the missing integrity check at runtime by tampering with the stored file:

1. Use @MASTG-TECH-0056 to install the app and tap **Start** so it writes `user_profile.json` and prints its path.
2. Use @MASTG-TECH-0059 to access the app's Documents directory and modify the stored file, for example to grant premium access:

    ```sh
    echo '{"username":"alice","role":"admin","premium":true}' > /var/mobile/Containers/Data/Application/<APP-UUID>/Documents/user_profile.json
    ```

3. Tap **Start** again and observe that the app reads back and trusts the modified values without any verification failure.

## Fix

Protect stored data by computing a cryptographic authentication tag over it and verifying that tag before use. See @MASTG-BEST-0x01 for full guidance.

**Option 1: compute and verify an `HMAC<SHA256>` with a Keychain-bound key (recommended)**

Store an HMAC alongside the data and verify it on read. Keep the HMAC key in the [Keychain](https://developer.apple.com/documentation/security/keychain-services) with a strict accessibility class (for example, `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`) so it cannot be extracted from a backup or transferred to another device:

```swift
import CryptoKit

// On write: append an HMAC computed with a Keychain-held key
let mac = HMAC<SHA256>.authenticationCode(for: sensitiveData, using: key)
try (sensitiveData + Data(mac)).write(to: fileURL)

// On read: recompute and compare before trusting the data
let stored = try Data(contentsOf: fileURL)
let payload = stored.prefix(stored.count - SHA256.byteCount)
let tag = stored.suffix(SHA256.byteCount)
guard HMAC<SHA256>.isValidAuthenticationCode(tag, authenticating: payload, using: key) else {
    // Reject the tampered data
    return
}
```

After applying this fix, modifying the file on disk makes verification fail, so the app rejects the tampered data. For Objective-C or mixed codebases, `CCHmac` from CommonCrypto provides the same capability.

**Option 2: Use a digital signature with `SecKeyCreateSignature` for asymmetric scenarios**

When the signer and verifier are different parties (for example, server-signed resources delivered to the app), sign the data with a private key and verify it in the app with [`SecKeyVerifySignature`](https://developer.apple.com/documentation/security/seckeyverifysignature(_:_:_:_:_:)) using the embedded public key.

**Why file-system protection alone is not enough:**

iOS [Data Protection](https://support.apple.com/guide/security/data-protection-overview-secf6276da8a/web) encrypts files at rest, but it protects confidentiality, not integrity, and provides no protection on a jailbroken device where an attacker can read and rewrite the app's container. Only an authentication tag that the app verifies lets it detect tampering.