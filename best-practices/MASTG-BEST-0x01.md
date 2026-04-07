---
title: Implement Certificate Pinning on iOS
alias: ios-implement-certificate-pinning
id: MASTG-BEST-0032
platform: ios
knowledge: [MASTG-KNOW-0072]
---

## Overview

Certificate pinning lets an iOS app reject TLS connections to servers that don't present a specific expected certificate or public key, even if the server's certificate is signed by a CA trusted by the OS. This protects against MITM attacks by rogue or compromised certificate authorities.

## Recommendation

Use Apple's built-in [Identity Pinning via `NSPinnedDomains`](https://developer.apple.com/news/?id=g9ejcf8y) in `Info.plist` as the primary pinning mechanism. This is the simplest and most maintainable approach, as it requires no code changes and is automatically enforced by the system for all connections made through the URL Loading System.

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSPinnedDomains</key>
    <dict>
        <key>example.com</key>
        <dict>
            <key>NSIncludesSubdomains</key>
            <true/>
            <key>NSPinnedCAIdentities</key>
            <array>
                <dict>
                    <key>SPKI-SHA256-BASE64</key>
                    <string>+[BASE64-ENCODED SHA-256 HASH OF SUBJECT PUBLIC KEY INFO]</string>
                </dict>
            </array>
        </dict>
    </dict>
</dict>
```

If you need more control, implement pinning in the [`URLSessionDelegate`](https://developer.apple.com/documentation/foundation/urlsessiondelegate) method [`urlSession(_:didReceive:completionHandler:)`](https://developer.apple.com/documentation/foundation/urlsessiondelegate/1409308-urlsession) and perform [manual server trust authentication](https://developer.apple.com/documentation/foundation/url_loading_system/handling_an_authentication_challenge/performing_manual_server_trust_authentication).

## Caveats and Considerations

- **Pin to a CA public key, not a leaf certificate** when possible. Leaf certificates rotate frequently; CA public keys are more stable, avoiding forced app updates on every certificate renewal.
- **Always include a backup pin** (a second CA or leaf key) to ensure connectivity if the primary certificate is replaced unexpectedly. Without a backup, a certificate rotation could render the app unable to connect.
- **Manage pin rotation carefully.** Unlike Android's Network Security Configuration, `NSPinnedDomains` doesn't support expiration dates. Plan certificate rotation before pins become stale.
- **Pinning doesn't replace proper TLS configuration.** Ensure the server is using strong cipher suites, up-to-date TLS versions, and valid certificates from trusted CAs.
- **Pinning can be bypassed** on jailbroken devices or via tools such as [SSL Kill Switch 2](https://github.com/nabla-c0d3/ssl-kill-switch2), and by reverse-engineering and repackaging the app. Complement pinning with other controls such as jailbreak detection and app integrity checks for high-risk scenarios.
- **Cross-platform and third-party frameworks** may use their own network stacks that bypass ATS and `NSPinnedDomains` entirely. Verify that pinning is enforced at the framework level (for example, Dart's `HttpClient` for Flutter).

## References

- Apple Developer Documentation: [Identity Pinning: How to configure server certificates for your app](https://developer.apple.com/news/?id=g9ejcf8y)
- Apple Developer Documentation: [NSPinnedDomains](https://developer.apple.com/documentation/bundleresources/information-property-list/nsapptransportsecurity/nspinneddomains)
- Apple Developer Documentation: [Performing Manual Server Trust Authentication](https://developer.apple.com/documentation/foundation/url_loading_system/handling_an_authentication_challenge/performing_manual_server_trust_authentication)
- Apple Developer Documentation: [Preventing Insecure Network Connections](https://developer.apple.com/documentation/security/preventing_insecure_network_connections)
