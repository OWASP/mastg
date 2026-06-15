---
platform: ios
title: Missing Certificate Pinning in ATS
code: [swift, xml]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

## Sample

The sample below shows an app that makes HTTPS connections to three domains via `URLSession`: two domains that are pinned through ATS `NSPinnedDomains` (`sha256.badssl.com` and `rsa2048.badssl.com`) and the app's own backend (`example.com`), which is **not** pinned. Here `example.com` stands in for a first-party, developer-owned domain that should be pinned but isn't:

{{ MastgTest.swift # Info.plist }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to a JSON format (@MASTG-TECH-0138).
3. Search for the `NSPinnedDomains` entries in the ATS configuration.

{{ run.sh }}

## Observation

The output lists the domains configured under `NSPinnedDomains`:

{{ output.txt }}

## Evaluation

The test case fails because the app's own backend domain (`example.com`) is a relevant domain the app connects to, but it has no entry under `NSPinnedDomains`. Only `sha256.badssl.com` and `rsa2048.badssl.com` are pinned, so connections to `example.com` rely entirely on the system CA trust store and are not protected against a MITM attacker who controls a trusted CA.
