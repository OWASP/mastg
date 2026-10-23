---
masvs_category: MASVS-RESILIENCE
platform: android
title: Key Attestation
available_since: 24
---

Android provides the [Key Attestation](https://developer.android.com/training/articles/security-key-attestation) feature, which allows a verifier (usually a remote server) to cryptographically verify the security properties of asymmetric keys managed through the Android KeyStore (@MASTG-KNOW-0043). By inspecting and validating the signed certificate chain of the generated (public) key, a third party can establish trust in the integrity of both the device environment (see @MASTG-KNOW-0120) and the identity of the calling application (see @MASTG-KNOW-0119). Starting with Android 8.0 (API level 26), key attestation became mandatory for all new devices that require device certification for Google apps. These devices use attestation keys signed by the [Google Hardware Attestation Root Certificate](https://developer.android.com/training/articles/security-key-attestation#root_certificate).

@MASTG-KNOW-0035 builds on the same hardware-backed primitives to attest device and app integrity, but returns a managed verdict from Google's servers instead of a certificate chain the verifier parses itself.

## Chain of Trust

A chain of trust is only as strong as its weakest link, and key attestation plays a fundamental role in establishing it. For a remote server to trust a client, it must first trust the platform the client runs on, and then trust the application running on that platform.

A typical chain of trust for a mobile application is built from the bottom up:

- **Device integrity** is the foundation: if the device runs a modified OS, has an unlocked bootloader, or is rooted, an attacker controls the execution environment and can bypass, spoof, or intercept any application-level control.
- **Application integrity** builds on top: even on a trusted device, a repackaged or patched APK may have had its security controls removed or its behavior altered.
- **In-app security controls** are the final layer: only when both the device and the binary are trusted can controls (such as TLS certificate pinning) be meaningfully enforced.

If any link in this chain is broken, the security guarantees of all higher layers are void. Tools like @MASTG-TOOL-0001 rely on exactly this: they run on devices or apps where the lower layers are already under the attacker's control, so any in-app control can be observed or disabled.

## Attestation Certificate Chain

During an asymmetric key pair generation in the Android KeyStore using [`KeyGenParameterSpec.Builder`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder), the keys can be generated and remain in hardware-backed security modules by enabling [`setIsStrongBoxBacked`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setisstrongboxbacked) (available since API level 28) if the device allows it. This uses the [StrongBox Keymaster](https://developer.android.com/training/articles/keystore#HardwareSecurityModule) for stronger isolation. If StrongBox is not available on the device, Trusted Execution Environment (TEE) would be used as secure hardware. If no secure hardware is available, the key would be generated in software.

After key generation, the public key certificate (and its chain) can be requested and inspected as described in [Reading the X.509 Certificate](#reading-the-x509-certificate). A challenge (nonce) can be included to provide freshness, proving the attestation was generated in response to a specific request. In this scenario, the verifier (usually a server) generates a random challenge, and the client passes it to [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) during key generation. When the challenge is set and if secure hardware supports attestation, this certificate will be signed by a chain of certificates rooted at a trustworthy CA key. Otherwise (such as when the public key was created in software), the chain will be rooted at an untrusted certificate. If the attestation challenge was not set, the public key certificate will be self-signed. Therefore, to obtain a certificate chain rooted at a trustworthy CA key, both hardware support and the attestation challenge are required.

If the chain's root certificate is the [Google Hardware Attestation Root Certificate](https://developer.android.com/training/articles/security-key-attestation#root_certificate) and the hardware-backed storage checks are satisfied, this provides assurance that the device supports hardware-level key attestation and that the key is stored in a keystore that Google considers secure. If the certificate or the attestation chain has any other root certificate, Google does not make any claims about the security of the hardware. This does not mean the key or the device is compromised. It just indicates the key is not confirmed to be in secure hardware by Google.

From API level 31, if an attestation challenge is set, [`setDevicePropertiesAttestationIncluded`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setdevicepropertiesattestationincluded) can also be enabled to include device properties (brand, device, manufacturer, model, product) in the attestation.

The resulting certificate chain is then returned to the verifier for inspection:

- The certificate chain up to the root, including validity, integrity, and [revocation status](https://developer.android.com/training/articles/security-key-attestation#certificate_status).
- The attestation extension data in the leaf certificate, which follows an [ASN.1 schema](https://source.android.com/docs/security/features/keystore/attestation#schema), including:
    - `attestationChallenge`: the challenge value matches the server-issued nonce.
    - `attestationSecurityLevel`: the Keymaster security level (`Software`, `TrustedEnvironment`, or `StrongBox`).
    - `softwareEnforced` / `hardwareEnforced`: the key pair attributes (see [Key Properties](#key-properties) below).
    - `rootOfTrust`: device integrity signals including `verifiedBootState`, `verifiedBootKey`, and `deviceLocked` (see @MASTG-KNOW-0120).
    - `attestationApplicationId`: the application identity including package name and signing certificate digests (see @MASTG-KNOW-0119).

The `attestationSecurityLevel` determines the overall trust of the attestation. The higher the security level, the more confident the verifier can be that the reported device and application properties reflect reality and cannot be falsified by the Android OS. For device integrity signals and how to interpret them (e.g., unlocked bootloader), see @MASTG-KNOW-0120. For application identity signals and how to interpret them (e.g., repackaged or tampered app), see @MASTG-KNOW-0119.

**Example:**

The following example shows how to configure a `KeyGenParameterSpec` for key attestation using an EC key pair on the `secp256r1` (P-256) curve with StrongBox, an attestation challenge, and device properties attestation:

```kotlin
val keyGenParameterSpec = KeyGenParameterSpec
    .Builder(
        keyAlias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    )
    .setDigests(KeyProperties.DIGEST_SHA256)
    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
    .setIsStrongBoxBacked(true)
    .setAttestationChallenge(attestationChallenge)
    .setDevicePropertiesAttestationIncluded(true)
    .build()
```

For a reference implementation, see [Dionysis Lorentzos' Android-Security sample](https://github.com/Diolor/Android-Security/blob/main/app/src/main/java/dio/security/crypto/KeyManager.kt#L45-L70).

## Attestation Key Provisioning

The trustworthiness of an attestation rests on the attestation key that signs the chain. Android provisions those keys in two ways:

- **Factory provisioning (legacy)**: The attestation key is programmed into the device during manufacturing and remains on it for the device's lifetime. Because the key material is present on the device, it can be extracted, and once extracted it can be used to sign attestations describing a device state that does not exist. Google states that ["attestation keys can be revoked for a number of reasons, including mishandling or suspected extraction by an attacker"](https://developer.android.com/privacy-and-security/security-key-attestation#certificate_status), and that leaked keys are discovered through analysis of attestation data in the wild, public disclosure, and researcher reports. Revocation typically follows within several days of discovery.
- **[Remote Key Provisioning (RKP)](https://source.android.com/docs/core/ota/modular-system/remote-key-provisioning)**: Present in AOSP since Android 12, RKP issues attestation certificates on demand from Google's backend instead of programming keys onto the device. Devices that launch with Android 16 support only RKP. Google states that RKP "prevents key leakage because the system does not program keys directly onto the device", that keys cannot be deleted from the device, and that revocation can target a single device. Certificates issued through RKP deliberately have shorter expiration periods, so that a compromise can be responded to more quickly.

Google's [certificate revocation status list](https://developer.android.com/privacy-and-security/security-key-attestation#certificate_status) is published at `https://android.googleapis.com/attestation/status` and records a `status` and a `reason` (`KEY_COMPROMISE`, `CA_COMPROMISE`, `SUPERSEDED`, `SOFTWARE_FLAW`, or `UNSPECIFIED`) for each revoked certificate. Attestation key leaks affect only the legacy factory-provisioning mechanism and do not apply to keys certified through RKP.

Google publishes an [attestation verification Kotlin library](https://github.com/android/keyattestation) for verifying certificate chains, noting that it covers edge cases that custom verifiers often miss.

## Reading the X.509 Certificate

The returned [X509Certificate](https://developer.android.com/reference/kotlin/java/security/cert/X509Certificate) chain from [`KeyStore.getCertificateChain(alias)`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getcertificatechain) can be inspected to determine the key properties. X.509 certificates are described by [ASN.1 format](https://source.android.com/docs/security/features/keystore/attestation#tbscertificate-sequence) and the Android-specific extensions (certificate's payload) can be requested with OID `1.3.6.1.4.1.11129.2.1.17`. This attestation extension content is described by the [ASN.1 schema KeyDescription](https://source.android.com/docs/security/features/keystore/attestation#schema).

For a sample decoding functionality of X.509 certificate's payload, you may consult [Dionysis Lorentzos' - Simple attestation converter](https://github.com/Diolor/Android-Security/blob/main/app/src/main/java/dio/security/crypto/attestation/Attestation.kt#L34-L64).

The extension content contains the `attestationSecurityLevel` and `keyMintSecurityLevel` values, which indicate the methodology used to generate the key pair, and their respective `attestationVersion` and `keyMintVersion` values, which determine the schema used to encode the attestation extension. There are also two [`AuthorizationList`](https://source.android.com/docs/security/features/keystore/attestation#authorizationlist-fields) fields that describe the configuration of the attested key pair: `softwareEnforced` and `hardwareEnforced` for properties enforced by the Android OS or by the TEE or StrongBox respectively.

### Key Properties

Some of the key properties are described below:

- The `attestationSecurityLevel` and `keyMintSecurityLevel` contain the **`SecurityLevel`** indicating how the key was generated. You can refer to its [schema](https://source.android.com/docs/security/features/keystore/attestation#securitylevel-values) and [Hardware versus software enforcement](https://source.android.com/docs/security/features/keystore/features#hardware_vs_software_enforcement) for more information.
    - **`Software`**: Attestation was performed in the Android system, with no hardware-backed guarantee. This is usually what emulators use.
    - **`TrustedEnvironment`**: Attestation was performed by the Trusted Execution Environment (TEE), providing hardware-enforced isolation via a dedicated CPU execution environment.
    - **`StrongBox`**: Attestation was performed by a dedicated secure element (StrongBox), offering the highest level of hardware protection.
- **`attestationChallenge`**: The nonce provided by the server and passed to `setAttestationChallenge()` during key generation. The server checks this value to confirm the attestation was produced in response to its specific request to prevent replay attacks.
- The **`softwareEnforced`** or **`hardwareEnforced`** `AuthorizationList` contains the following fields:
    - **`rootOfTrust`**: Device integrity signals used for device attestation (see @MASTG-KNOW-0120), including:
        - **`verifiedBootState`**: Whether the device's boot chain was verified as unmodified (`Verified`, `SelfSigned`, `Unverified`, or `Failed`).
        - **`verifiedBootKey`**: The public key used to verify the boot image. On unmodified devices this matches the OEM's embedded key.
        - **`deviceLocked`**: Whether the bootloader is locked. An unlocked bootloader indicates the device may have been modified.
    - **`attestationApplicationId`**: Application identity fields used for application attestation (see @MASTG-KNOW-0119), including:
        - **`packageInfos`**: A set of entries each containing the app's `packageName` and `version` code.
        - **`signatureDigests`**: SHA-256 digests of the app's signing certificates, allowing the server to verify the app has not been repackaged.
    - **Authentication requirements**: Whether user authentication (e.g., biometric via [`setUserAuthenticationRequired`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setuserauthenticationrequired)) is required before key use, indicated by fields such as `noAuthRequired` and `userAuthType`.
