---
platform: android
title: Runtime Use of Asymmetric Key Pairs Used For Multiple Purposes With Frida
id: MASTG-DEMO-0072
code: [kotlin]
test: MASTG-TEST-0308
---

### Sample

This sample uses the code from @MASTG-DEMO-0071 and takes a dynamic approach to intercept the cryptographic operations at runtime (including encryption, decryption, signing, and verification) to demonstrate the misuse of an asymmetric key pair for multiple purposes.

{{ ../MASTG-DEMO-0071/MastgTest.kt }}

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ script.js # run.sh }}

### Observation

The output shows all usages of cryptographic operations.

{{ output.txt }}

Note all `WARNING` messages in the output.

### Evaluation

The test fails because the same asymmetric key pair is used across different groups of cryptographic operations.

A single RSA key pair is performing both encryption and decryption, as well as signing and verification, which violates the requirement that an asymmetric key be restricted to one purpose class.

In the sample output:

- the private key instance (`android.security.keystore2.AndroidKeyStoreRSAPrivateKey`) is consistently identified by the instance ID `@3818961`
- the matching public key instance (`OpenSSLRSAPublicKey`) is identified by its modulus beginning with `a41226cf3ca5b...`

These two objects form one key pair in the Android Keystore. Their appearances across different operations show the misuse.

Following the private key reference `@3818961`, the first usage appears during decryption:

```bash
🔒 *** Cipher.init(Key) HOOKED ***
  encryption/decryption with key: "android.security.keystore2.AndroidKeyStoreRSAPrivateKey@3818961"
  Stack Trace:
    javax.crypto.Cipher.init(Native Method)
    org.owasp.mastestapp.MastgTest.decrypt(MastgTest.kt:145)
```

Later in the output, the same private key instance is used for signing:

```bash
✍️ *** Signature.initSign(PrivateKey) HOOKED ***
  sign/verify with key: "android.security.keystore2.AndroidKeyStoreRSAPrivateKey@3818961"
!!! WARNING: This key is used for multiple, conflicting purposes: encryption/decryption and sign/verify
```

The matching public key, identified by its modulus (`a41226cf3ca5b...`), appears in both encryption:

```bash
🔒 *** Cipher.init(Key) HOOKED ***
  encryption/decryption with key: "OpenSSLRSAPublicKey{modulus=a41226cf3ca5b...
```

And verification:

```bash
✅ *** Signature.initVerify(PublicKey) HOOKED ***
  sign/verify with key: "OpenSSLRSAPublicKey{modulus=a41226cf3ca5b...
```
