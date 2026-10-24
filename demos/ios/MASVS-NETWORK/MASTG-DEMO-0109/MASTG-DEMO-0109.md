---
platform: ios
title: ATS TLS Policy Exceptions in Info.plist
code: [xml, swift]
id: MASTG-DEMO-0109
test: MASTG-TEST-0342
kind: fail
---

## Sample

The code below shows an insecure ATS configuration in an `Info.plist` file. `NSAllowsArbitraryLoads` is explicitly set to `false` to keep the focus on the two per-domain TLS policy exceptions:

- `tls-v1-0.badssl.com`: lowers the minimum TLS version to TLS 1.0 via `NSExceptionMinimumTLSVersion`, applying to all subdomains via `NSIncludesSubdomains`.
- `static-rsa.badssl.com`: disables the forward secrecy requirement via `NSExceptionRequiresForwardSecrecy = false`, allowing cipher suites without ephemeral key exchange (such as RSA key exchange).

Note that if `NSAllowsArbitraryLoads` were set to `true`, ATS would be disabled for all domains not explicitly listed in `NSExceptionDomains`, allowing all connections to those domains regardless of TLS version or cipher suite. Domains listed in `NSExceptionDomains` would still have their per-domain settings applied.

{{ Info.plist # Info_reversed.plist # MastgTest.swift }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the `Info.plist` to pretty-printed JSON (@MASTG-TECH-0138).
3. Extract `NSAllowsArbitraryLoads` and any `NSExceptionMinimumTLSVersion`, `NSTemporaryExceptionMinimumTLSVersion`, or `NSExceptionRequiresForwardSecrecy` keys from the `NSAppTransportSecurity` configuration. In this case we use `gron` to transform the JSON into a greppable format and `egrep` to search for those keys.

{{ run.sh }}

## Observation

The output shows the `NSAllowsArbitraryLoads` setting and the TLS policy exceptions found in `Info_reversed.plist`:

{{ output.txt }}

## Evaluation

The test case fails because two per-domain TLS policy exceptions are configured:

- `NSExceptionMinimumTLSVersion = "TLSv1.0"` for `tls-v1-0.badssl.com` allows connections using the deprecated TLS 1.0 protocol. Because `NSIncludesSubdomains = true`, the exception also applies to all subdomains of `tls-v1-0.badssl.com`.
- `NSExceptionRequiresForwardSecrecy = false` for `static-rsa.badssl.com` disables the ATS requirement for Perfect Forward Secrecy (PFS), allowing cipher suites such as RSA key exchange that don't provide forward secrecy. Past sessions can be decrypted if the server's private key is later compromised.
Note that `NSAllowsArbitraryLoads` is set to `false`, so we don't consider it as a failure condition here. If it were `true`, the test would fail on that basis alone, because ATS would be disabled for all connections to domains not listed in `NSExceptionDomains`. Domains listed in `NSExceptionDomains` would still have their per-domain settings applied even in that case.
