---
platform: ios
title: Network.framework TLS Minimum Version Lowered via sec_protocol_options
code: [swift]
id: MASTG-DEMO-0x03
test: MASTG-TEST-0x03
kind: fail
status: draft
---

## Sample

The code sample uses `NWProtocolTLS.Options` with `sec_protocol_options_set_min_tls_protocol_version` to set the minimum TLS version to TLS 1.0 for a Network.framework connection. Since ATS does not apply to Network.framework, this configuration is not mitigated by any ATS policy.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for calls to `sec_protocol_options_set_min_tls_protocol_version` in the binary.

{{ nw_tls.r2 }}

{{ run.sh }}

## Observation

The output contains a reference to `sec_protocol_options_set_min_tls_protocol_version` and shows the TLS version value being loaded before the call:

{{ output.asm }}

## Evaluation

The test case fails because the app calls `sec_protocol_options_set_min_tls_protocol_version` with the value `0x301`, which corresponds to `tls_protocol_version_TLSv10` (TLS 1.0). This allows Network.framework connections to negotiate TLS 1.0, a deprecated protocol version.

The instruction at `0x100004914` loads `w1` with `0x301` immediately before the call to `sec_protocol_options_set_min_tls_protocol_version` at `0x100004920`. The value `0x0301` is the raw value of `tls_protocol_version_TLSv10`, as defined in the Security framework.

Because Network.framework operates entirely outside of ATS, this weak TLS configuration is not subject to any ATS enforcement, regardless of the app's `Info.plist` configuration.
