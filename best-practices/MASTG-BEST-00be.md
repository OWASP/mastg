---
title: Use Hardware-Backed Key Attestation for Device and App Integrity
alias: android-hardware-backed-attestation
id: MASTG-BEST-00be
platform: android
knowledge: [MASTG-KNOW-0044, MASTG-KNOW-0047, MASTG-KNOW-01kw]
---

Applications that perform business-critical operations, such as financial transactions, multi-factor authentication, or sensitive data handling, should verify the integrity of the device environment before trusting it. Use Android's Key Attestation (@MASTG-KNOW-0044) to cryptographically verify that the client's keys reside in hardware-backed storage and that the device has not been compromised.

## Implement Server-Driven Attestation with a Fresh Challenge

Always drive attestation from the server using the challenge-response flow described in @MASTG-KNOW-0044. Generate a unique, cryptographically random challenge (nonce) for each attestation request using a CSPRNG. Never reuse challenges across requests. Never implement attestation verification solely on the client side.

## Verify the Attestation Certificate Chain

On the server side, verify the attestation certificate chain (@MASTG-KNOW-0044):

- Verify the chain of trust up to the Google Hardware Attestation Root Certificate.
- Check each certificate against Google's Certificate Revocation Status List.
- Confirm that the embedded challenge matches the one the server originally issued.
- Require that the Keymaster security level is `TrustedEnvironment` or `StrongBox`.
- Check the device integrity signals (@MASTG-KNOW-01kw): verified boot state, verified boot key, and bootloader lock status.

## Enforce Key Properties

Use the attestation extension data (@MASTG-KNOW-01kw) to confirm that the attested key pair was generated with the expected properties:

- Restrict the key purpose to only the intended operations (e.g., signing, encryption).
- Require user authentication before key use when applicable (e.g., biometric binding via [`setUserAuthenticationRequired`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setuserauthenticationrequired)).
- Require that the key origin is `KeyOrigin.GENERATED` to ensure the private key has never existed outside the hardware.

## Handle Attestation Failures Securely

If attestation verification fails, the server must not grant the client elevated trust or access to sensitive operations. Depending on the application's risk profile:

- Deny access to high-assurance features entirely.
- Fall back to alternative verification mechanisms with appropriate risk acceptance.
- Log the failure for monitoring and incident response.
