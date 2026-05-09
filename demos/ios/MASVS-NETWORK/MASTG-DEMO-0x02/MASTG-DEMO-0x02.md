---
platform: ios
title: URLSession Minimum TLS Version Lowered in Code
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: fail
status: draft
---

## Sample

The code sample sets the `tlsMinimumSupportedProtocolVersion` of a `URLSessionConfiguration` to `0x0302` (`tls_protocol_version_TLSv11`), which allows `URLSession` connections to use TLS 1.1.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for calls to the `tlsMinimumSupportedProtocolVersion` setter in the binary.

{{ urlsession_tls.r2 }}

{{ run.sh }}

## Observation

The output contains a reference to the `tlsMinimumSupportedProtocolVersion` setter and shows the value being loaded into the register before the call:

{{ output.asm }}

## Evaluation

The test case fails because the app calls `Foundation.URLSessionConfiguration.tlsMinimumSupportedProtocolVersion.setter` with the value `0x302`, which corresponds to `tls_protocol_version_TLSv11` (TLS 1.1). This allows `URLSession` connections to negotiate TLS 1.1, a deprecated protocol version, instead of requiring at least TLS 1.2.

The instruction at `0x10000483c` loads `w1` with `0x302` immediately before the setter call at `0x100004840`. The value `0x0302` is the raw value of `tls_protocol_version_TLSv11`, as defined in the Security framework.
