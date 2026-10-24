---
platform: ios
title: URLSession Minimum TLS Version Lowered in Code
code: [swift]
id: MASTG-DEMO-0110
test: MASTG-TEST-0343
kind: fail
---

## Sample

The code sample sets `tlsMinimumSupportedProtocolVersion` on a `URLSessionConfiguration` to `.TLSv10`, which requests that `URLSession` connections accept TLS 1.0. Even though ATS would block such a connection at runtime unless an explicit `Info.plist` exception is also present, using this API with a deprecated TLS version is itself a bad practice and should be flagged.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for calls to the `tlsMinimumSupportedProtocolVersion` setter in the binary.

{{ urlsession_tls.r2 }}

The r2 script works in three stages:

- First, it looks up the symbol name and cross-references for `setTLSMinimumSupportedProtocolVersion:` to confirm the setter is present and to find the stub function used to dispatch it.
- Second, it disassembles the stub (`fcn.1000091a0`) to show the Objective-C message dispatch pattern.
- Third, it searches the binary for ARM64 `mov w2` instructions that load each of the known TLS protocol constants immediately before the setter call. On ARM64, `w2` carries the first argument in an Objective-C message send. The TLS constants are `0x0301` (TLS 1.0), `0x0302` (TLS 1.1), `0x0303` (TLS 1.2), and `0x0304` (TLS 1.3). Because each constant produces a fixed 4-byte instruction encoding (for example, `mov w2, 0x301` encodes as `22 60 80 52` in little-endian), the script searches for those byte sequences directly and then disassembles the surrounding instructions.

{{ run.sh }}

## Observation

The output shows the setter symbol, its cross-reference, the stub, and the byte-pattern search results:

{{ output.asm }}

## Evaluation

The test case fails because the binary calls `setTLSMinimumSupportedProtocolVersion:` with the value `0x0301`, which corresponds to TLS 1.0.

The relevant sequence in the output is:

```asm
0x10000417c      ldr x0, [x8, 0xb28]        ; NSURLSessionConfiguration class
0x100004180      bl sym.imp.objc_opt_self
0x100004184      bl fcn.100009120            ; ephemeral configuration factory
0x10000418c      bl sym.imp.objc_retainAutoreleasedReturnValue
0x100004190      mov x24, x0                 ; retain configuration object

0x100004194      mov w2, 0x301              ; first argument: TLS 1.0
0x100004198      bl fcn.1000091a0           ; setTLSMinimumSupportedProtocolVersion:

0x1000041a8      mov x2, x24               ; pass configuration object
0x1000041ac      bl fcn.100009180           ; URLSession factory
```

On ARM64, Objective-C message sends follow the convention `x0` = receiver, `x1` = selector, `x2` = first argument. `fcn.1000091a0` loads the `setTLSMinimumSupportedProtocolVersion:` selector into `x1` and jumps to `objc_msgSend`. The instruction at `0x100004194` loads `w2` with `0x301` immediately before that call, making this equivalent to:

```swift
configuration.tlsMinimumSupportedProtocolVersion = .TLSv10
```

The subsequent call at `0x1000041ac` passes the configured `NSURLSessionConfiguration` object to a `NSURLSession` factory method, confirming the session is created with this TLS setting.

Although ATS would block the connection at runtime unless a matching `Info.plist` exception is also present, explicitly setting `tlsMinimumSupportedProtocolVersion` to a deprecated TLS version is a bad practice and must be flagged regardless of whether the connection would succeed.
