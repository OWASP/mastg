---
platform: ios
title: Missing Certificate Pinning in ATS
code: [swift, xml]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

## Sample

The sample below shows an app that makes HTTPS connections to `api.example.com` via `URLSession`. While the app uses HTTPS, the `Info.plist` file contains an `NSAppTransportSecurity` section with no `NSPinnedDomains` key, meaning no certificate pinning is configured via ATS:

{{ MastgTest.swift # Info.plist }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to a JSON format (@MASTG-TECH-0138).
3. Search for `NSPinnedDomains` in the ATS configuration.

{{ run.sh }}

## Observation

The output is empty, indicating that `NSPinnedDomains` is not present in the `NSAppTransportSecurity` section of the `Info.plist`:

{{ output.txt }}

## Evaluation

The test fails because the `Info.plist` does not contain a `NSPinnedDomains` key inside `NSAppTransportSecurity`. As a result, the app doesn't enforce certificate pinning via ATS and relies entirely on the system CA trust store.
