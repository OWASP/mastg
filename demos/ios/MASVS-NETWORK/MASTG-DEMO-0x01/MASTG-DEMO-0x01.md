---
platform: ios
title: ATS TLS Policy Exceptions in Info.plist
code: [xml, swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

## Sample

The code below shows an insecure ATS configuration in an `Info.plist` file that lowers the minimum TLS version to TLS 1.0 for `tls-v1-0.badssl.com` (and its subdomains) via `NSExceptionMinimumTLSVersion`.

{{ Info.plist # Info_reversed.plist # MastgTest.swift }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to pretty-printed JSON (@MASTG-TECH-0138).
3. Extract any `NSExceptionMinimumTLSVersion` or `NSTemporaryExceptionMinimumTLSVersion` keys from the `NSAppTransportSecurity` configuration. In this case we use `gron` to transform the JSON into a greppable format and `egrep` to search for those keys.

{{ run.sh }}

## Observation

The output shows the TLS version exception found in `Info_reversed.plist`:

{{ output.txt }}

## Evaluation

The test case fails because a TLS version exception is configured:

- `NSExceptionMinimumTLSVersion = "TLSv1.0"` for `tls-v1-0.badssl.com` allows connections using the deprecated TLS 1.0 protocol. Because `NSIncludesSubdomains = true`, the exception also applies to all subdomains of `tls-v1-0.badssl.com`.
