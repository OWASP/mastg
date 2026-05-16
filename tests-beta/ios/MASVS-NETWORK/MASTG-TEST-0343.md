---
platform: ios
title: URLSession TLS Protocol Configuration
id: MASTG-TEST-0343
type: [static]
weakness: MASWE-0050
profiles: [L1, L2]
best-practices: [MASTG-BEST-0042]
knowledge: [MASTG-KNOW-0071, MASTG-KNOW-0073]
---

## Overview

`URLSessionConfiguration` allows apps to customize TLS behavior for individual `URLSession` instances. The [`tlsMinimumSupportedProtocolVersion`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsminimumsupportedprotocolversion) property (or the deprecated [`tlsMinimumSupportedProtocol`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsminimumsupportedprotocol)) controls the minimum TLS version for a session.

Setting this property to `.TLSv10` or `.TLSv11` is a bad practice and should be flagged, even though ATS still applies to the URL Loading System and may block the connection at runtime unless a matching `Info.plist` exception is also present. Unlike `Network.framework`, `URLSession` does not bypass ATS.

Note that `tlsMinimumSupportedProtocol` is deprecated in favor of `tlsMinimumSupportedProtocolVersion`. Using either to set an insecure minimum TLS version weakens the intended TLS protection for that session.

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0066 to look for uses of `URLSessionConfiguration` properties that set TLS protocol versions (`tlsMinimumSupportedProtocol` and `tlsMinimumSupportedProtocolVersion`).
3. Use @MASTG-TECH-0076 to analyze the relevant code paths and determine the TLS version values being set.

## Observation

The output should contain the `URLSessionConfiguration` API calls that configure TLS protocol versions, if any.

## Evaluation

The test case fails if the app sets:

- `tlsMinimumSupportedProtocolVersion` to `tls_protocol_version_TLSv10` (value `0x0301`) or `tls_protocol_version_TLSv11` (value `0x0302`), or
- `tlsMinimumSupportedProtocol` (deprecated) to a value corresponding to TLS 1.0 (`kTLSProtocol1`) or TLS 1.1 (`kTLSProtocol11`).

!!! note "Note on ATS Interaction"
    ATS may still enforce minimum TLS version requirements for connections using the URL Loading System, depending on the ATS configuration in `Info.plist`. However, if the app has also configured broad ATS exceptions (see @MASTG-TEST-0342), the effective TLS minimum may be lower than expected for those domains.
