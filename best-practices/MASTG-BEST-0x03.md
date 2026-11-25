---
title: Use App Attest for Device and App Integrity
alias: ios-use-app-attest
id: MASTG-BEST-0x03
platform: ios
available_since: 14
knowledge: [MASTG-KNOW-0x01, MASTG-KNOW-0x02, MASTG-KNOW-0x03, MASTG-KNOW-0072]
---

Applications that perform business-critical operations, such as financial transactions, multi-factor authentication, or sensitive data handling, should verify that requests reaching the backend originate from a genuine, unmodified instance of the app before trusting them.

On iOS, use **App Attest** (@MASTG-KNOW-0x03), which binds a Secure Enclave key to your App ID (your team identifier and bundle identifier) and the device hardware. Do not use DeviceCheck (@MASTG-KNOW-0x02) for this purpose: it persists per-device flags but provides no cryptographic proof of app identity and integrity. If you prefer a managed integration, **Firebase App Check** (@MASTG-KNOW-0x01) wraps App Attest and handles token issuance and validation for you.

## Check for Availability Before Attesting

Query [`DCAppAttestService.shared.isSupported`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/issupported) before starting the flow, as not all device types support the service. Treat an unsupported device as an unattested client and apply the same failure policy described below, rather than silently granting trust.

## Derive Every `clientDataHash` from a Server-Issued Challenge

Drive attestation from the server. For each attestation and each assertion, the server must generate a unique, cryptographically random challenge (nonce) using a Cryptographically Secure Pseudorandom Number Generator (CSPRNG) and never reuse it. The client hashes the challenge (together with the request payload, where applicable) and passes the result as the `clientDataHash` argument to [`attestKey(_:clientDataHash:completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/attestkey%28_%3Aclientdatahash%3Acompletionhandler%3A%29) and [`generateAssertion(_:clientDataHash:completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generateassertion%28_%3Aclientdatahash%3Acompletionhandler%3A%29).

A `clientDataHash` derived from client-controlled or static data allows an attacker to replay a previously captured attestation object or assertion.

@MASTG-DEMO-0x06 shows both hashes derived from a challenge fetched from the server; @MASTG-DEMO-0x02 shows them derived from a constant in the app.

## Assert Every Sensitive Request, Not Only the Key

Attesting the key once with `attestKey` proves the app instance was genuine at attestation time. It says nothing about the requests that follow. Generate a fresh assertion with `generateAssertion` for each sensitive request so that the request payload itself is bound to the attested app instance.

Generate a key with [`generateKey(completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generatekey%28completionhandler%3A%29) for each user account on each device, as [Apple recommends](https://developer.apple.com/documentation/devicecheck/establishing-your-app-s-integrity), and persist the returned key identifier. Don't reuse one key for several users on a device. Generate a new key only when the old one is lost (for example, after a reinstall, a device migration, or a restore from a backup) or when your risk policy requires it, because the number of keys attested per device is itself a fraud signal.

## Verify Attestations and Assertions Server-Side

Client-side use of `DCAppAttestService` provides no security guarantee on its own. The server must perform Apple's [published verification steps](https://developer.apple.com/documentation/devicecheck/validating-apps-that-connect-to-your-server):

- Validate the attestation certificate chain up to the Apple App Attest root certificate.
- Confirm the embedded nonce matches the challenge the server issued.
- Confirm the App ID hash matches your team and bundle identifier.
- Verify the assertion signature against the attested public key and check that the assertion counter increases monotonically.
- Use the receipt to request Apple's [fraud risk metric](https://developer.apple.com/documentation/devicecheck/assessing-fraud-risk), an approximate count of the keys attested for your app on that device over the past 30 days. A count that is higher than expected can indicate one compromised device serving many instances of the app. The count also grows when the app is reinstalled or the device is restored, so treat it as a risk signal and tune the threshold to the numbers you normally see.

Send attestation objects and assertions over a connection with properly validated server trust (@MASTG-KNOW-0072), so that a network-level attacker cannot observe or alter them. The server-issued challenge is what prevents replay, not the channel.

## Handle Attestation Failures Securely

If verification fails, the server must not grant the client elevated trust or access to sensitive operations. Depending on the application's risk profile:

- Deny access to high-assurance features entirely.
- Fall back to alternative verification mechanisms with appropriate risk acceptance.
- Log the failure for monitoring and incident response.
- Don't tell the client which check failed. Return a generic error, so that the response doesn't help an attacker adapt.

!!! warning
    App Attest attests the app's identity and the device hardware, but it is not jailbreak detection. A jailbroken device may still produce a valid attestation. Combine it with the resilience controls described in @MASTG-BEST-0029.
