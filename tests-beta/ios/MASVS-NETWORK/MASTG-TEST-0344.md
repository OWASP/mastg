---
platform: ios
title: Network.framework TLS Protocol Configuration
id: MASTG-TEST-0344
type: [static, code, manual]
weakness: MASWE-0050
profiles: [L1, L2]
best-practices: [MASTG-BEST-0043]
knowledge: [MASTG-KNOW-0073]
---

## Overview

The [Network framework](https://developer.apple.com/documentation/network) operates entirely outside of ATS. Apps using `NWConnection` with `NWProtocolTLS.Options` can configure TLS settings directly via the Security framework, including minimum and maximum supported TLS versions through [`sec_protocol_options_set_min_tls_protocol_version`](https://developer.apple.com/documentation/security/sec_protocol_options_set_min_tls_protocol_version(_:_:)) and [`sec_protocol_options_set_max_tls_protocol_version`](https://developer.apple.com/documentation/security/sec_protocol_options_set_max_tls_protocol_version(_:_:)).

Since ATS does not apply to Network.framework connections, any weak TLS configuration here is not mitigated by ATS. Setting a minimum TLS version below 1.2 creates the risk of connection downgrade attacks without any ATS-level safety net. Apple's documentation notes that "ATS doesn't apply to calls your app makes to lower-level networking interfaces like the `Network` framework or `CFNetwork`. In these cases, you take responsibility for ensuring the security of the connection." See [Preventing Insecure Network Connections](https://developer.apple.com/documentation/security/preventing-insecure-network-connections).

For more information on iOS network APIs and when ATS applies, see @MASTG-KNOW-0071 and @MASTG-KNOW-0073.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain any calls to TLS protocol version configuration functions in the Network.framework, if found.

## Evaluation

The test case fails if the app calls:

- `sec_protocol_options_set_min_tls_protocol_version` with a value of `tls_protocol_version_TLSv10` (`0x0301`) or `tls_protocol_version_TLSv11` (`0x0302`), or
- `sec_protocol_options_set_max_tls_protocol_version` with a value of `tls_protocol_version_TLSv10` (`0x0301`) or `tls_protocol_version_TLSv11` (`0x0302`).

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine the TLS version values passed to those functions.

Because Network.framework operates entirely outside of ATS, a connection configured this way will succeed against a server that supports the deprecated TLS version, bypassing ATS.
