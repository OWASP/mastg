---
platform: ios
title: App Attest With a Server-Issued Challenge
id: MASTG-DEMO-0x06
code: [swift]
test: MASTG-TEST-0x02
kind: pass
---

## Sample

This sample performs the full App Attest flow: it checks `isSupported`, generates a key with `generateKey`, attests it with `attestKey`, and produces an assertion with `generateAssertion`.

Unlike @MASTG-DEMO-0x02, the `clientDataHash` passed to `attestKey` and `generateAssertion` is a SHA-256 digest over a nonce fetched from the server for that specific request. A separate challenge is requested for the assertion, so the assertion is bound to the request being made rather than to the earlier attestation.

The endpoint is a placeholder here to keep the demo self-contained. In a real implementation the server generates each nonce with a CSPRNG, stores it against the session, and rejects any attestation or assertion carrying a nonce it did not issue or has already accepted, as described in @MASTG-BEST-0x03.

{{ MastgTest.swift }}

!!! note "This sample cannot succeed on a simulator"
    App Attest requires the Secure Enclave, which the iOS simulator does not provide, so [`DCAppAttestService.shared.isSupported`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/issupported) returns `false` there and the sample returns early without attesting (@MASTG-KNOW-0x03). Exercising the full flow requires a physical device and an app configured with the App Attest capability.

    This does not affect the analysis below, which is static: the binary is disassembled, never executed. The binary shipped with this demo is an iOS device build and cannot be run on a simulator in any case.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the App Attest calls and the origin of the `clientDataHash`. Run the r2 script with the `-i` option.

{{ app_attest_server_challenge.r2 # run.sh }}

## Observation

The output shows the App Attest selectors present in the binary, the absence of any hardcoded challenge string, the challenge endpoint in the `__TEXT.__cstring` section, and the call sites that build the hash and invoke the App Attest APIs.

{{ output.txt }}

## Evaluation

The test case passes because the attestation and the assertions are bound to values the server issued, which makes them non-replayable.

The disassembly shows the following flow:

- The search for a hardcoded challenge string returns nothing, so there is no constant in the binary that could serve as a fixed `clientDataHash` input.
- At `0x10000be30`, the only challenge-related string is the endpoint `https://mastg.example.com/attest/challenge`, which is a location to fetch a nonce from rather than a nonce itself.
- Between `0x100004078` and `0x100004094`, that endpoint is loaded and converted into a `Foundation.URL` for the request.
- At `0x10000af24`, `dataTaskWithURL:completionHandler:` is invoked to retrieve the challenge from the server.
- At `0x10000411c`, the data returned by that request is passed through `CryptoKit.SHA256` to produce the digest that becomes the `clientDataHash`.
- At `0x10000aee4` and `0x10000af44`, `attestKey` and `generateAssertion` are invoked with hashes derived from those server-issued values.

Because the digest input arrives over the network at runtime, it differs on every run, and an attacker who captures one attestation object or assertion cannot replay it: the server only accepts a nonce it issued and has not yet seen used.

!!! note
    A passing static result only confirms that the challenge originates from the server rather than from a constant. The server must still perform Apple's verification steps, confirming the certificate chain, the App ID hash, the assertion counter, and the receipt risk metric before trusting the client. See @MASTG-BEST-0x03.
