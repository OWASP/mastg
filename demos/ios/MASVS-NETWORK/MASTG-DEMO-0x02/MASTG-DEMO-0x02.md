---
platform: ios
title: Expired Certificate Pins in ATS
code: [swift, xml]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: fail
---

## Sample

The sample below shows an app that makes HTTPS connections to `api.example.com`. It uses [TrustKit](https://github.com/datatheorem/TrustKit) for certificate pinning, configured via the `TSKConfiguration` key in `Info.plist`. However, the `TSKExpirationDate` for the pinned domain is set to `2020-01-01`, which is in the past. After this date, TrustKit stops enforcing certificate pinning for the domain and falls back to the system CA trust store:

{{ MastgTest.swift # Info.plist }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to a JSON format (@MASTG-TECH-0138).
3. Search for `TSKExpirationDate` values in the TrustKit configuration.

{{ run.sh }}

## Observation

The output shows the `TSKExpirationDate` value for the pinned domain:

{{ output.txt }}

## Evaluation

The test fails because the `TSKExpirationDate` for `api.example.com` is `2020-01-01`, which is in the past. TrustKit has already stopped enforcing certificate pinning for this domain.
