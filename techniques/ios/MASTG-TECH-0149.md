---
title: Validating ATS TLS Settings at Runtime Using nscurl
platform: ios
---

[`nscurl`](https://developer.apple.com/documentation/security/identifying-the-source-of-blocked-connections) is a macOS tool bundled with the Xcode Command Line Tools. It tests ATS behavior against a real endpoint by simulating the ATS policy evaluation that iOS applies. It runs a series of permutations of ATS settings against the target URL and reports which configurations succeed or fail, helping identify what `Info.plist` exceptions would be required for a given server.

Run it against the app's backend endpoints:

```bash
nscurl --ats-diagnostics --verbose https://example.com
```

The output shows the result for each tested combination, starting with the default ATS-secure configuration and progressing through increasingly relaxed variants. If the default ATS secure connection succeeds, the server is compatible with ATS in its default configuration and no exceptions are needed.

## Validating with Controlled Test Endpoints

To confirm the effective TLS behavior enforced for a specific domain, connect the app to controlled endpoints that expose only specific TLS configurations:

- A TLS 1.0-only endpoint: verifies whether the app accepts connections using TLS 1.0.
- A TLS 1.1-only endpoint: verifies whether the app accepts connections using TLS 1.1.
- A TLS 1.2 endpoint without perfect forward secrecy: verifies whether the app requires PFS.
- A TLS 1.3-only endpoint: confirms the app supports modern TLS.

This is especially important when validating the scope of `NSExceptionRequiresForwardSecrecy` exceptions found during static analysis, since those entries describe what is _configured_, not what is _actually negotiated_. See @MASTG-KNOW-0071 for details on ATS configuration and exceptions.

!!! note "nscurl is an ATS diagnostic helper, not a substitute for on-device testing"
    `nscurl --ats-diagnostics` is useful for identifying whether ATS policy would block a connection on the host macOS system, but results don't always match iOS behavior. On modern macOS versions network settings can be stricter than on iOS and rejecting handshakes that iOS still accepts under an ATS exception. A `FAIL` in `nscurl` does not necessarily mean the same configuration fails in the iOS app. Therefore always confirm with on-device testing.

## Examples

### TLS 1.0 Server: ATS Exception Applied, but Secure Transport on macOS Rejects the Handshake

!!! note
    Since [macOS Tahoe 26](https://developer.apple.com/documentation/macos-release-notes/macos-26-release-notes#Security) `nscurl` can no longer establish connections using TLS 1.0 or 1.1, as URLSession and the Network framework now enforce a minimum TLS version of 1.2. The same applies to iOS 26, where ATS exceptions configured to allow TLS 1.0 or 1.1 are no longer accepted by the operating system.

Running `nscurl --ats-diagnostics --verbose` on macOS 26 against a TLS 1.0-only endpoint (`tls-v1-0.badssl.com:1010`) shows that `nscurl` _does_ apply the ATS exception correctly:

```txt
ATS Dictionary:
{
    NSExceptionDomains = {
        "tls-v1-0.badssl.com" = {
            NSExceptionMinimumTLSVersion = "TLSv1.0";
        };
    };
}
```

However, every configuration still fails:

```txt
Default ATS Secure Connection
---
ATS Default Connection
Result : FAIL
---
...
Allowing Arbitrary Loads
---
Allow All Loads
Result : FAIL
---
...
Configuring TLS exceptions for tls-v1-0.badssl.com
---
TLSv1.0
Result : FAIL
---
...
TLSv1.0 with PFS disabled
Result : FAIL
---
```

The error is `NSURLErrorDomain Code=-1200` with `_kCFStreamErrorCodeKey=-9836`, a fatal Secure Transport error — not an ATS policy rejection. Because even `Allow All Loads` (which disables ATS entirely) fails with the same error, the problem is below ATS: the Apple URL Loading System on macOS 26 refuses the TLS 1.0 handshake regardless of ATS settings. But, as seen in the demo @MASTG-DEMO-0109, an app with `NSExceptionMinimumTLSVersion = "TLSv1.0"` would manage to connect properly (as of iOS 17).

### Server Blocked Only by the PFS Requirement

Running `nscurl --ats-diagnostics` against a server using static RSA key exchange (`static-rsa.badssl.com`):

```txt
Default ATS Secure Connection
---
ATS Default Connection
Result : FAIL
---
...
Configuring PFS exceptions for static-rsa.badssl.com
---
Disabling Perfect Forward Secrecy
Result : PASS
---
```

The default ATS connection fails, but disabling PFS succeeds. This means the server does not support ephemeral key exchange, so ATS blocks it by default under its PFS requirement. The minimal exception needed is `NSExceptionRequiresForwardSecrecy: false` for that domain. Apple recommends fixing the server to support ECDHE instead of adding this exception.
