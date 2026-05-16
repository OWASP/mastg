---
title: Enforce Strong TLS Settings When ATS Doesn't Apply
alias: enforce-strong-tls-settings-when-ats-doesnt-apply
id: MASTG-BEST-0043
platform: ios
knowledge: [MASTG-KNOW-0073]
---

App Transport Security (ATS) only protects connections made through the URL Loading System (`URLSession` and related Foundation APIs). When your app uses Network.framework, CFNetwork, BSD sockets, or a bundled third-party TLS library, ATS doesn't apply and you're responsible for configuring strong TLS settings explicitly. Apple's documentation states that "ATS doesn't apply to calls your app makes to lower-level networking interfaces like the `Network` framework or `CFNetwork`. In these cases, you take responsibility for ensuring the security of the connection." See [Preventing Insecure Network Connections](https://developer.apple.com/documentation/security/preventing-insecure-network-connections).

When possible, prefer `URLSession` and high-level Foundation APIs so that ATS protections apply automatically. See @MASTG-BEST-0042.

## Network.framework

When using [`NWConnection`](https://developer.apple.com/documentation/network/nwconnection) with TLS, configure `NWProtocolTLS.Options` and set the minimum TLS version to at least TLS 1.2 using [`sec_protocol_options_set_min_tls_protocol_version`](https://developer.apple.com/documentation/security/sec_protocol_options_set_min_tls_protocol_version(_:_:)):

```swift
let tlsOptions = NWProtocolTLS.Options()
sec_protocol_options_set_min_tls_protocol_version(
    tlsOptions.securityProtocolOptions,
    .TLSv12
)
```

Don't set the minimum or maximum TLS version to `tls_protocol_version_TLSv10` or `tls_protocol_version_TLSv11`. Unlike `URLSession`, there is no ATS enforcement to catch weak configurations at runtime.

## Embedded Third-Party TLS Libraries

Some apps bundle networking stacks such as OpenSSL, BoringSSL, mbedTLS, or curl that manage TLS independently from any Apple API. Configure them to require TLS 1.2 or later, use strong cipher suites, and enable full certificate chain validation. No OS-level mechanism will catch a misconfiguration in a bundled library. Regularly audit and update bundled libraries to incorporate security fixes.
