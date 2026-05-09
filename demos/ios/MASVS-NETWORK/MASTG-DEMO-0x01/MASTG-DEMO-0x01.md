---
platform: ios
title: ATS TLS Policy Exceptions in Info.plist
code: [xml, swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

## Sample

The code below shows an insecure ATS configuration in an `Info.plist` file that weakens TLS enforcement in two ways:

- For `example.com` (and its subdomains): lowers the minimum TLS version to TLS 1.1 via `NSExceptionMinimumTLSVersion`.
- For `legacy.example.com`: disables the forward secrecy requirement via `NSExceptionRequiresForwardSecrecy = false`.

{{ Info.plist # Info_reversed.plist # MastgTest.swift }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to pretty-printed JSON (@MASTG-TECH-0138).
3. Extract the relevant TLS exception keys and values from the `NSAppTransportSecurity` configuration. In this case we use `gron` to transform the JSON into a greppable format and `egrep` to search for specific regex patterns.

{{ run.sh }}

## Observation

The output shows the TLS policy exception settings found in `Info_reversed.plist`:

{{ output.txt }}

## Evaluation

The test case fails because two TLS policy exceptions are configured:

- `NSExceptionMinimumTLSVersion = "TLSv1.1"` for `example.com` allows connections using the deprecated TLS 1.1 protocol. Because `NSIncludesSubdomains = true`, the exception also applies to all subdomains of `example.com`.
- `NSExceptionRequiresForwardSecrecy = false` for `legacy.example.com` disables the ATS requirement for Perfect Forward Secrecy (PFS). This allows TLS connections that do not use ECDHE key exchange, meaning past sessions can be decrypted if the server's private key is later compromised.
