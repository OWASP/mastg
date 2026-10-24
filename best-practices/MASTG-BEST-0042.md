---
title: Use Strong TLS Settings in ATS Configuration
alias: use-strong-tls-settings-in-ats-configuration
id: MASTG-BEST-0042
platform: ios
knowledge: [MASTG-KNOW-0071]
---

App Transport Security (ATS) enforces strong TLS defaults for `URLSession` connections on iOS 9 and later. Avoid weakening these defaults through ATS exceptions in `Info.plist`, and ensure any custom TLS configuration in code is equally strong.

## Avoid Lowering the Minimum TLS Version

!!! note
    Since [iOS 26](https://developer.apple.com/documentation/macos-release-notes/macos-26-release-notes#Security), `URLSession` and the Network framework now enforce a minimum TLS version of 1.2. ATS exceptions configured to allow TLS 1.0 or 1.1 are no longer accepted by the operating system.

Do not set `NSExceptionMinimumTLSVersion` to `TLSv1.0` or `TLSv1.1` for any domain. TLS 1.0 and TLS 1.1 are deprecated and have known weaknesses. Apple requires a justification for this exception when submitting to the App Store and recommends fixing the server rather than weakening the client configuration.

If a third-party service requires TLS 1.0 or 1.1, [contact the service provider](https://developer.apple.com/documentation/security/preventing-insecure-network-connections#Configure-Exceptions-Only-When-Needed-and-Prefer-Server-Fixes) to request an upgrade. Treat any such exception as temporary and remove it once the server is updated.

When configuring `URLSessionConfiguration` in code, do not set `tlsMinimumSupportedProtocolVersion` or the deprecated `tlsMinimumSupportedProtocol` to values corresponding to TLS 1.0 or 1.1. Prefer leaving the default, which inherits ATS minimum requirements.

## Avoid Disabling Forward Secrecy

Do not set `NSExceptionRequiresForwardSecrecy` to `false`. ATS enforces [Perfect Forward Secrecy (PFS)](https://developer.apple.com/documentation/security/preventing-insecure-network-connections) by default using ECDHE cipher suites. Disabling PFS means that past sessions can be decrypted if the server's private key is later compromised.

## Prefer the URL Loading System for HTTPS Requests

Always prefer [`URLSession`](https://developer.apple.com/documentation/foundation/urlsession) or other high-level Foundation APIs for HTTPS requests so that ATS protections apply automatically. Avoid using Network.framework, CFNetwork, or BSD sockets for application-level HTTPS unless you explicitly configure and enforce strong TLS settings in code, because ATS does not protect those connection paths. See @MASTG-BEST-0043 for guidance on those APIs.

## Apply Narrow Exceptions When Required

When an ATS exception is unavoidable, apply it as narrowly as possible:

- Target only the specific domain that requires the exception.
- Avoid setting `NSIncludesSubdomains = true` unless all subdomains require the exception.
- Do not set `NSAllowsArbitraryLoads` to `true`. This disables ATS for all connections to domains not listed in `NSExceptionDomains`, removing TLS version enforcement, certificate validation, and forward secrecy requirements for those domains. Per-domain exceptions in `NSExceptionDomains` still apply to their listed domains, but all unlisted domains have no ATS protection.
- Provide a justification in the app's App Store submission as [required by Apple](https://developer.apple.com/documentation/security/preventing-insecure-network-connections#Provide-Justification-for-Exceptions).
