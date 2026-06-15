---
platform: ios
title: Embedded or Third-party TLS Stack Configuration
id: MASTG-TEST-0345
type: [static, code, manual]
weakness: MASWE-0050
profiles: [L1, L2]
best-practices: [MASTG-BEST-0043]
knowledge: [MASTG-KNOW-0073]
---

## Overview

Some apps embed networking stacks that manage TLS independently from Apple's ATS-enforced URL Loading System. Examples include OpenSSL, BoringSSL, mbedTLS, curl, and gRPC. Since ATS doesn't apply to these libraries, any weak TLS configuration in them is not protected by ATS or `URLSession` settings.

Such libraries often expose their own API calls to set the minimum TLS version, maximum TLS version, cipher suite list, certificate verification mode, or custom trust store (for example, `SSL_CTX_set_min_proto_version` for OpenSSL/BoringSSL, `mbedtls_ssl_conf_min_version` for mbedTLS, or `curl_easy_setopt` for libcurl). If these settings permit TLS below 1.2, allow deprecated cipher suites, or disable certificate verification, they introduce vulnerabilities that are entirely independent of the ATS configuration.

Apple's documentation states that "ATS doesn't apply to calls your app makes to lower-level networking interfaces like the `Network` framework or `CFNetwork`. In these cases, you take responsibility for ensuring the security of the connection." (See [Preventing Insecure Network Connections](https://developer.apple.com/documentation/security/preventing-insecure-network-connections).) The same principle applies to entirely embedded TLS libraries.

## Steps

1. Use @MASTG-TECH-0082 to identify all bundled libraries.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of any identified third-party TLS library symbols and their locations in the app binary.

## Evaluation

The test case fails if any embedded TLS library is configured to:

- Allow TLS versions below 1.2.
- Use weak or deprecated cipher suites.
- Disable certificate verification or use a custom trust store without proper validation.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine the TLS configuration settings in use.
