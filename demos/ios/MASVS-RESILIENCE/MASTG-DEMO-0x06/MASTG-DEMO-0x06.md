---
platform: ios
title: App Attest with a Server-Issued Challenge
id: MASTG-DEMO-0x06
code: [swift]
test: MASTG-TEST-0x02
kind: pass
---

## Sample

This sample performs the full App Attest flow: it checks `isSupported`, generates a key with `generateKey`, attests it with `attestKey`, and signs a request payload with `generateAssertion`.

Unlike @MASTG-DEMO-0x02, every `clientDataHash` is derived from a challenge fetched from the server. For the attestation, the hash is a SHA-256 digest over the fetched challenge. For the assertion, a separate challenge is fetched and placed in the JSON request payload, and the hash is a SHA-256 digest over the payload, so the signature covers both the request and a value the server issued for it.

The endpoint is a placeholder here to keep the demo self-contained. In a real implementation the server generates each challenge with a CSPRNG, stores it against the session, and rejects any attestation or assertion carrying a challenge it did not issue or has already accepted, as described in @MASTG-BEST-0x03.

{{ MastgTest.swift }}

!!! note "This sample cannot succeed on a simulator"
    App Attest requires the Secure Enclave, which the iOS simulator does not provide, so [`DCAppAttestService.shared.isSupported`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/issupported) returns `false` there and the sample returns early without attesting (@MASTG-KNOW-0x03). Exercising the full flow requires a physical device and an app configured with the App Attest capability.

    This does not affect the analysis below, which is static: the binary is disassembled, never executed. The binary shipped with this demo is an iOS device build and cannot be run on a simulator in any case.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the App Attest calls and the origin of the `clientDataHash`. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ app_attest_server_challenge.r2 # run.sh }}

## Observation

The output shows the App Attest selectors present in the binary and where each one is called, the strings that mention a challenge, how the challenge endpoint is used, and the calls that build each hash.

{{ output.txt }}

## Evaluation

The test case passes because the attestation and the assertion are built from challenges the app fetches from the server, not from a value that ships inside the app.

The output shows the following flow:

- The app calls `isSupported` at `0x100008884` in `sym.func.1000087b8`, `generateKey` at `0x100004704` in `sym.func.10000442c`, `attestKey` at `0x10000486c` in `sym.func.100004760`, and `generateAssertion` at `0x100004ebc` in `sym.func.100004ae0`.
- The only strings that mention a challenge are the endpoint `https://mastg.example.com/attest/challenge` at `0x10000cfd0`, the Swift field name `challenge`, and messages shown to the user. None of them is a challenge value.
- The endpoint is referenced once, at `0x10000407c`, where it is converted into a `Foundation.URL`. `sym.func.100006afc` uses that URL and calls `dataTaskWithURL:completionHandler:` at `0x100006c38`.
- `sym.func.100006afc` is called twice: at `0x1000088cc`, right after the `isSupported` check, and at `0x100004a58` in `sym.func.100004944`.
- In `sym.func.10000442c`, a SHA-256 digest is computed between `0x1000045c8` and `0x1000045e8`, before `generateKey` is called. The only string the function references is an error message, so the digest input is not a constant.
- In `sym.func.100004ae0`, the request payload is encoded with `JSONEncoder.encode` at `0x100004c5c` and a SHA-256 digest is computed between `0x100004d14` and `0x100004d34`, before `generateAssertion` is called. Here too, the only strings referenced are error messages.

Because the challenges arrive over the network at runtime, both `clientDataHash` values differ on every run, and the server can reject an attestation object or assertion that does not carry a challenge it issued and has not yet seen used.

!!! note
    A passing static result only confirms that the challenge originates from the server rather than from a constant. The server must still perform Apple's verification steps, confirming the certificate chain, the App ID hash, the assertion counter, and the receipt risk metric before trusting the client. See @MASTG-BEST-0x03.
