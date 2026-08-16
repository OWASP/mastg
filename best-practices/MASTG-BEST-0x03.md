---
title: Use App Attest for Device and App Integrity
alias: ios-use-app-attest
id: MASTG-BEST-0x03
platform: ios
available_since: 14
knowledge: [MASTG-KNOW-0x01, MASTG-KNOW-0x02, MASTG-KNOW-0x03, MASTG-KNOW-0072]
---
 
Applications that perform business-critical operations, such as financial transactions, multi-factor authentication, or sensitive data handling, should verify that requests reaching the backend originate from a genuine, unmodified instance of the app before trusting them.

On iOS, use **App Attest** (@MASTG-KNOW-0x03), which binds a Secure Enclave key to your App ID and the device hardware. Do not use DeviceCheck (@MASTG-KNOW-0x02) for this purpose: it persists per-device flags but provides no cryptographic proof of app identity or binary integrity. If you prefer a managed integration, **Firebase App Check** (@MASTG-KNOW-0x01) wraps App Attest and handles token issuance and validation for you.

For the equivalent Android guidance, see @MASTG-BEST-0x01.

## Check for Availability Before Attesting

Query [`DCAppAttestService.shared.isSupported`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/issupported) before starting the flow, as not all device types support the service. Treat an unsupported device as an unattested client and apply the same failure policy described below, rather than silently granting trust.

## Derive Every `clientDataHash` from a Server-Issued Challenge

Drive attestation from the server. For each attestation and each assertion, the server must generate a unique, cryptographically random challenge (nonce) using a CSPRNG and never reuse it. The client hashes the challenge (together with the request payload, where applicable) and passes the result as the `clientDataHash` argument to [`attestKey(_:clientDataHash:completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/attestkey%28_%3Aclientdatahash%3Acompletionhandler%3A%29) and [`generateAssertion(_:clientDataHash:completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generateassertion%28_%3Aclientdatahash%3Acompletionhandler%3A%29).

A `clientDataHash` derived from client-controlled or static data allows an attacker to replay a previously captured attestation object or assertion.

## Assert Every Sensitive Request, Not Only the Key

Attesting the key once with `attestKey` proves the app instance was genuine at attestation time. It says nothing about the requests that follow. Generate a fresh assertion with `generateAssertion` for each sensitive request so that the request payload itself is bound to the attested app instance.

Generate the key with [`generateKey(completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generatekey%28completionhandler%3A%29) once per app installation and persist the returned key identifier. Re-attest when the key is lost (for example, after a reinstall) or when your risk policy requires it.

## Verify Attestations and Assertions Server-Side

Client-side use of `DCAppAttestService` provides no security guarantee on its own. The server must perform Apple's [published verification steps](https://developer.apple.com/documentation/devicecheck/validating-apps-that-connect-to-your-server):

- Validate the attestation certificate chain up to the Apple App Attest root certificate.
- Confirm the embedded nonce matches the challenge the server issued.
- Confirm the App ID hash matches your team and bundle identifier.
- Verify the assertion signature against the attested public key and check that the assertion counter increases monotonically.
- Check receipt freshness and the risk metric Apple returns when the receipt is redeemed.

Transmit attestation objects and assertions over a pinned channel (@MASTG-KNOW-0072) so that a network-level attacker cannot observe or replay them.

## Handle Attestation Failures Securely

If verification fails, the server must not grant the client elevated trust or access to sensitive operations. Depending on the application's risk profile:

- Deny access to high-assurance features entirely.
- Fall back to alternative verification mechanisms with appropriate risk acceptance.
- Log the failure for monitoring and incident response.

!!! warning
    App Attest attests the app binary and the device hardware, but it is not jailbreak detection. A jailbroken device may still produce a valid attestation. Combine it with the resilience controls described in @MASTG-BEST-0029.
