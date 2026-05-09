---
platform: ios
title: URLSession TLS Protocol Configuration
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0050
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0071, MASTG-KNOW-0073]
---

## Overview

`URLSessionConfiguration` allows apps to customize TLS behavior for individual `URLSession` instances. The [`tlsMinimumSupportedProtocolVersion`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsminimumsupportedprotocolversion) property (or the deprecated [`tlsMinimumSupportedProtocol`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsminimumsupportedprotocol)) controls the minimum TLS version for a session. Setting it to `tls_protocol_version_TLSv10` or `tls_protocol_version_TLSv11` allows connections using deprecated TLS versions, even when ATS would otherwise enforce TLS 1.2 as the minimum.

If an app overrides these settings with a weaker value, it lowers the effective TLS protection for all connections using that session configuration, regardless of the ATS policy configured in `Info.plist`.

Note that `tlsMinimumSupportedProtocol` is deprecated in favor of `tlsMinimumSupportedProtocolVersion`. Regardless, using either deprecated or newer API to set an insecure minimum TLS version weakens the connection security.

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0072 to look for uses of `URLSessionConfiguration` properties that set TLS protocol versions (`tlsMinimumSupportedProtocol` and `tlsMinimumSupportedProtocolVersion`).
3. Use @MASTG-TECH-0076 to analyze the relevant code paths and determine the TLS version values being set.

## Observation

The output should contain the `URLSessionConfiguration` API calls that configure TLS protocol versions, if any.

## Evaluation

The test case fails if the app sets:

- `tlsMinimumSupportedProtocolVersion` to `tls_protocol_version_TLSv10` (value `0x0301`) or `tls_protocol_version_TLSv11` (value `0x0302`), or
- `tlsMinimumSupportedProtocol` (deprecated) to a value corresponding to TLS 1.0 (`kTLSProtocol1`) or TLS 1.1 (`kTLSProtocol11`).

!!! note Note on ATS Interaction
    ATS may still enforce minimum TLS version requirements for connections using the URL Loading System, depending on the ATS configuration in `Info.plist`. However, if the app has also configured broad ATS exceptions (see @MASTG-TEST-0x01), the effective TLS minimum may be lower than expected for those domains.
