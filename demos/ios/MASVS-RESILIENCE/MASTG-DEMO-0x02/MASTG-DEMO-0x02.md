---
platform: ios
title: App Attest Without a Server-Issued Challenge
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
kind: fail
---

## Sample

This sample performs the full App Attest flow: it checks `isSupported`, generates a key with `generateKey`, attests it with `attestKey`, and produces an assertion with `generateAssertion`.

However, the `clientDataHash` passed to both `attestKey` and `generateAssertion` is a SHA-256 digest over a constant compiled into the app, rather than over a one-time challenge fetched from the server. The same value is therefore produced on every run and on every install.

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the App Attest calls and the origin of the `clientDataHash`. Run the r2 script with the `-i` option.

{{ app_attest_challenge.r2 # run.sh }}

## Observation

The output shows the App Attest selectors present in the binary, the hardcoded challenge string in the `__TEXT.__cstring` section, the single cross reference to it, and disassembly of the three call sites.

{{ output.txt }}

## Evaluation

The test case fails because the attestation and the assertions are not bound to any server-issued value, which makes them replayable.

The disassembly shows the following flow:

- At `0x100006398`, the app calls `isSupported` and branches away if App Attest is unavailable.
- At `0x1000063a4`, it loads the constant string `mastg-app-attest-challenge` from `0x10000b0b0`. The cross reference list confirms this is the only place the string is used, so no other value ever feeds the hash.
- Between `0x1000063c8` and `0x10000643c`, that constant is passed through `CryptoKit.SHA256` to produce the digest that becomes the `clientDataHash`.
- At `0x100004110`, the `attestKey:clientDataHash:completionHandler:` selector is invoked with that digest.
- At `0x10000436c`, `generateAssertion:clientDataHash:completionHandler:` is invoked with the same digest.

Nothing in the binary retrieves a nonce from a server before building the hash. There is no network call between the `isSupported` check and the hash construction, and the only input to the digest is a string literal shipped inside the app.

An attacker who captures one attestation object or assertion can therefore replay it against any later request, because the server has no value of its own to match against. The server cannot distinguish a fresh assertion from a recorded one.

See @MASTG-BEST-0x03 for the expected flow, in which the server generates a unique random challenge for each attestation and each assertion, and rejects any response whose embedded nonce it did not issue.
