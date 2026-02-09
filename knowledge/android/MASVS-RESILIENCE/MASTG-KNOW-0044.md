---
masvs_category: MASVS-RESILIENCE
platform: android
title: Key Attestation
available_since: 24
---

Android provides the [Key Attestation](https://developer.android.com/training/articles/security-key-attestation) feature, which allows verification of the security properties of cryptographic keys managed through the Android KeyStore (@MASTG-KNOW-0043). Starting with Android 8.0 (API level 26), key attestation became mandatory for all new devices (Android 7.0 or higher) that require device certification for Google apps. These devices use attestation keys signed by the [Google Hardware Attestation Root Certificate](https://developer.android.com/training/articles/security-key-attestation#root_certificate).

## Attestation Certificate Chain

During key attestation, an asymmetric key pair is generated in the Android KeyStore using [`KeyGenParameterSpec.Builder`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder) and a certificate chain is returned. The key can be stored in hardware-backed security modules by enabling [`setIsStrongBoxBacked`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setisstrongboxbacked) (available since API level 28), which uses the [StrongBox Keymaster](https://developer.android.com/training/articles/keystore#HardwareSecurityModule) for stronger isolation.

If the chain's root certificate is the [Google Hardware Attestation Root Certificate](https://developer.android.com/training/articles/security-key-attestation#root_certificate) and the hardware-backed storage checks are satisfied, this provides assurance that the device supports hardware-level key attestation and that the key is stored in a keystore that Google considers secure. If the attestation chain has any other root certificate, Google does not make any claims about the security of the hardware.

A challenge (nonce) can be included to provide freshness, proving the attestation was generated in response to a specific request and preventing replay attacks. The server generates a random challenge, and the client passes it to [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) during key generation. From API level 31, if an attestation challenge is set, [`setDevicePropertiesAttestationIncluded`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setdevicepropertiesattestationincluded) can also be enabled to include device properties (brand, device, manufacturer, model, product) in the attestation. The resulting certificate chain is then returned to the server for verification. The server then verifies:

- The certificate chain up to the root, including validity, integrity, and [revocation status](https://developer.android.com/training/articles/security-key-attestation#certificate_status).
- That the root certificate is the Google Hardware Attestation Root Certificate.
- The attestation extension data in the leaf certificate, which follows an [ASN.1 schema](https://source.android.com/docs/security/features/keystore/attestation#schema), including:
    - `attestationChallenge`: the challenge value matches the server-issued nonce.
    - `attestationSecurityLevel`: the Keymaster security level (`Software`, `TrustedEnvironment`, or `StrongBox`).
    - `softwareEnforced` / `teeEnforced`: the key pair attributes such as `purpose`, `origin`, `algorithm`, and authentication requirements (see @MASTG-KNOW-01kw).
    - `rootOfTrust`: device integrity signals including `verifiedBootState`, `verifiedBootKey`, and `deviceLocked` (see @MASTG-KNOW-01kw).

The following example shows how to configure a `KeyGenParameterSpec` for key attestation using an EC key pair on the `secp256r1` (P-256) curve with StrongBox, an attestation challenge, and device properties attestation:

```kotlin
val keyGenParameterSpec = KeyGenParameterSpec
    .Builder(
        keyName,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    )
    .setDigests(KeyProperties.DIGEST_SHA256)
    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
    .setIsStrongBoxBacked(hasStrongBox)
    .setAttestationChallenge(attestationChallenge)
    .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setDevicePropertiesAttestationIncluded(true)
        }
    }
    .build()
```

!!! note
    If attestation fails, it indicates the key is not confirmed to be in secure hardware. This does not mean the key is compromised.

For a reference implementation, see [Dionysis Lorentzos' Android-Security sample](https://github.com/Diolor/Android-Security/blob/main/app/src/main/java/dio/security/crypto/KeyManager.kt).

## Attestation Response Format

A typical Android KeyStore attestation response:

```json
{
    "fmt": "android-key",
    "authData": "9569088f1ecee3232954035dbd10d7cae391305a2751b559bb8fd7cbb229bd...",
    "attStmt": {
        "alg": -7,
        "sig": "304402202ca7a8cfb6299c4a073e7e022c57082a46c657e9e53...",
        "x5c": [
            "308202ca30820270a003020102020101300a06082a8648ce3d040302308188310b30090603550406130...",
            "308202783082021ea00302010202021001300a06082a8648ce3d040302308198310b300906035504061...",
            "3082028b30820232a003020102020900a2059ed10e435b57300a06082a8648ce3d040302308198310b3..."
        ]
    }
}
```

- `fmt`: Attestation statement format identifier
- `authData`: Authenticator data for the attestation
- `alg`: Algorithm used for the signature
- `sig`: Signature, generated by concatenating `authData` and `clientDataHash` (the server's challenge) and signing with the credential's private key
- `x5c`: Attestation certificate chain

The signature is verified on the server side using the public key in the first certificate.

For a reference implementation, see [Google's Key Attestation Sample Code](https://github.com/google/android-key-attestation/blob/master/src/main/java/com/android/example/KeyAttestationExample.java).
