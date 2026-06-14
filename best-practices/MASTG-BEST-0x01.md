---
title: Properly Configure App Transport Security
alias: properly-configure-app-transport-security
id: MASTG-BEST-0x01
platform: ios
available_since: 9
---

App Transport Security (ATS) is a security feature introduced in iOS 9 that enforces secure network connections. By default, ATS requires all HTTP connections to use HTTPS with TLS 1.2 or higher, and it validates that certificates are properly signed and that hostnames match.

## Always Rely on ATS for Network Security

ATS provides strong default security for apps using the URL Loading System (such as `URLSession`). You should:

- **Use `URLSession` for all HTTP/HTTPS connections** rather than lower-level APIs like CFStream or BSD Sockets, as these bypass ATS protections.
- **Let ATS handle certificate validation** by default rather than implementing custom validation logic, which is error-prone.
- **Avoid disabling ATS globally** using `NSAllowsArbitraryLoads`, as this removes security protections for all network connections.

## Minimize ATS Exceptions

If you must configure ATS exceptions (for example, to connect to a legacy server), follow these guidelines:

- **Use domain-specific exceptions** rather than global exceptions. Configure exceptions only for the specific domains that require them using `NSExceptionDomains`.
- **Document the business justification** for each exception. Apple requires justification for ATS exceptions during app review.
- **Set the most restrictive exceptions possible**. For example, if a server supports TLS 1.2 but not forward secrecy, disable only forward secrecy for that domain rather than disabling all ATS checks.
- **Plan to remove exceptions**. Work with backend teams to upgrade servers to meet ATS requirements, then remove the exceptions.

## Example of Proper Domain-Specific Configuration

If you must connect to a legacy server at `legacy.example.com` that supports TLS 1.2 but not forward secrecy, configure a minimal exception:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSExceptionDomains</key>
    <dict>
        <key>legacy.example.com</key>
        <dict>
            <key>NSExceptionRequiresForwardSecrecy</key>
            <false/>
        </dict>
    </dict>
</dict>
```

This configuration:

- Maintains all other ATS protections (TLS 1.2+, certificate validation, hostname verification).
- Only affects `legacy.example.com`, not other domains.
- Still requires HTTPS (not HTTP).

## What to Avoid

**Never disable ATS globally:**

```xml
<!-- INSECURE - Do not use -->
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

**Never allow insecure HTTP connections for production servers:**

```xml
<!-- INSECURE - Do not use for production -->
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSExceptionDomains</key>
    <dict>
        <key>api.example.com</key>
        <dict>
            <key>NSExceptionAllowsInsecureHTTPLoads</key>
            <true/>
        </dict>
    </dict>
</dict>
```

## References

- [Apple Developer Documentation: Preventing Insecure Network Connections](https://developer.apple.com/documentation/security/preventing_insecure_network_connections)
- [Apple Developer Documentation: NSAppTransportSecurity](https://developer.apple.com/documentation/bundleresources/information_property_list/nsapptransportsecurity)
- [Apple Security Updates: App Transport Security](https://developer.apple.com/news/?id=jxky8h89)
