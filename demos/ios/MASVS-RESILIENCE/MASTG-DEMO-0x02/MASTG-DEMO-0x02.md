---
platform: ios
title: App Attest Without a Server-Issued Challenge
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
kind: fail
---

## Sample

This sample performs the full App Attest flow: it checks `isSupported`, generates a key with `generateKey`, attests it with `attestKey`, and signs a request payload with `generateAssertion`.

However, the challenge behind both `clientDataHash` values is a constant compiled into the app, rather than a one-time challenge fetched from the server. For the attestation, the hash is a SHA-256 digest over that constant. For the assertion, the constant is placed in the JSON request payload and the hash is a SHA-256 digest over the payload. The same two values are therefore produced on every run and on every install.

{{ MastgTest.swift }}

!!! note "This sample cannot succeed on a simulator"
    App Attest requires the Secure Enclave, which the iOS simulator does not provide, so [`DCAppAttestService.shared.isSupported`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/issupported) returns `false` there and the sample returns early without attesting (@MASTG-KNOW-0x03). Exercising the full flow requires a physical device and an app configured with the App Attest capability.

    This does not affect the analysis below, which is static: the binary is disassembled, never executed. The binary shipped with this demo is an iOS device build and cannot be run on a simulator in any case.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the App Attest calls and the origin of the `clientDataHash`. Run the r2 script with the `-i` option.

{{ app_attest_challenge.r2 # run.sh }}

## Observation

The output shows the App Attest selectors present in the binary and where each one is called, the hardcoded challenge string in the `__TEXT.__cstring` section and its cross references, and the calls that build each hash in the two functions that reference the string.

{{ output.txt }}

## Evaluation

The test case fails because the attestation and the assertion are built from a challenge that ships inside the app, so neither is bound to a value the server issued.

The output shows the following flow:

- The app calls `isSupported` at `0x100004404` and `generateKey` at `0x100004620`, both in `sym.func.100004348`. It calls `attestKey` at `0x100004764` in `sym.func.10000465c` and `generateAssertion` at `0x100004cd8` in `sym.func.10000483c`.
- The string `mastg-app-attest-challenge` at `0x10000c2a0` is referenced from two functions only: `sym.func.100004348` and `sym.func.10000483c`.
- In `sym.func.100004348`, the constant is loaded at `0x100004414` and a SHA-256 digest is computed between `0x1000044ec` and `0x10000450c`, before `generateKey` is called. This digest is the `clientDataHash` for the attestation.
- In `sym.func.10000483c`, the constant is loaded again at `0x100004930`, the request payload is encoded with `JSONEncoder.encode` at `0x1000049c8`, and a SHA-256 digest is computed between `0x100004afc` and `0x100004b1c`, before `generateAssertion` is called. This digest is the `clientDataHash` for the assertion.

The assertion does cover the request payload, so the request cannot be changed without invalidating the signature. But the challenge inside the payload is the constant, and so is the input to the attestation hash. The server has no value of its own to compare against, so its challenge check accepts a recorded attestation object or assertion the same way it accepts a new one.

See @MASTG-BEST-0x03 for the expected flow, in which the server generates a unique random challenge for each attestation and each assertion, and rejects any response that does not carry a challenge it issued.
