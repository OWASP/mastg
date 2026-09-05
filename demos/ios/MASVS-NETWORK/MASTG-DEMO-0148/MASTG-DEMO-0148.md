---
platform: ios
title: Missing Certificate Pinning in ATS
code: [swift, xml]
id: MASTG-DEMO-0148
test: MASTG-TEST-0385
kind: fail
---

## Sample

The sample below shows an app that makes HTTPS connections to three domains via `URLSession`.

- `sha256.badssl.com` and `rsa2048.badssl.com`, which are pinned through ATS `NSPinnedDomains`.
- `example.com`, which is not pinned.

In this demo, `example.com` represents the app's own backend. In a real app, this would be the actual first-party domain used for core functionality, such as `api.myapp.com`.

This distinction is central to the evaluation. Certificate pinning should be assessed for domains that belong to the app and are controlled by its developer, such as first-party backend or server-side API domains. Domains outside the developer's control should not be pinned, because their certificates and key rotation are managed by someone else.

{{ MastgTest.swift # Info.plist }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to a JSON format (@MASTG-TECH-0138).
3. Extract the `NSPinnedDomains` configuration from `NSAppTransportSecurity`.
4. Use @MASTG-TOOL-0129 to extract HTTP URLs from the app binary and compare them against the pinned domains.

{{ run.sh }}

## Observation

The output shows the whole `NSPinnedDomains` configuration:

{{ ats_pinned_domains.json }}

The output from @MASTG-TOOL-0129 shows that the app contains hardcoded URLs for all three domains:

{{ output.txt }}

## Evaluation

The test case fails because the app connects to its own backend, represented in this demo by `example.com`, but that domain has no entry under `NSPinnedDomains`.

Only `sha256.badssl.com` and `rsa2048.badssl.com` are pinned. As a result, connections to the app-controlled backend rely only on the system CA trust store and are not protected by ATS certificate pinning against a MITM attacker who can cause the device to trust a forged or misissued certificate.
