---
platform: ios
title: Network.framework TLS Minimum Version Lowered via sec_protocol_options
code: [swift]
id: MASTG-DEMO-0111
test: MASTG-TEST-0344
kind: fail
---

## Sample

The code sample uses `NWProtocolTLS.Options` with `sec_protocol_options_set_min_tls_protocol_version` to set the minimum TLS version to TLS 1.0 for a Network.framework connection. It also sets the maximum TLS version to TLS 1.0, constraining the connection to TLS 1.0 only. Because ATS doesn't apply to Network.framework, this configuration is not mitigated by any ATS policy and the connection will succeed against a TLS 1.0 server.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for calls to `sec_protocol_options_set_min_tls_protocol_version` in the binary.

{{ nw_tls.r2 }}

The r2 script works in three stages:

- First, it looks up the symbol and cross-references for `sec_protocol_options_set_min_tls_protocol_version` to confirm the function is imported and to find where it is called.
- Second, it disassembles the import stub to show the indirect call structure.
- Third, it searches the binary for ARM64 `mov w1` instructions that load each of the known TLS protocol constants immediately before the function call. On ARM64, `w1` holds the second argument of a C function call (the first being the `sec_protocol_options_t` handle in `x0`). The TLS constants are `0x0301` (TLS 1.0), `0x0302` (TLS 1.1), `0x0303` (TLS 1.2), and `0x0304` (TLS 1.3). Each produces a fixed 4-byte encoding, so the script searches for those byte sequences directly, then disassembles the surrounding instructions.

{{ run.sh }}

## Observation

The output shows the imported symbol, its cross-references, the import stub, and the byte-pattern search results:

{{ output.asm }}

## Evaluation

The test case fails because the binary calls both `sec_protocol_options_set_min_tls_protocol_version` and `sec_protocol_options_set_max_tls_protocol_version` with `0x0301` loaded in `w1`, which corresponds to TLS 1.0.

The relevant sequence in the output is:

```asm
0x1000041cc      bl sym.imp.Network.NWProtocolTLS.Options.allocator.securityProtocol_...vgTj_
0x1000041d0      mov x20, x0                ; sec_protocol_options_t handle

0x1000041d4      mov w1, 0x301              ; second argument: TLS 1.0
0x1000041d8      bl sym.imp.sec_protocol_options_set_min_tls_protocol_version

0x1000041e8      bl sym.imp.Network.NWProtocolTLS.Options.allocator.securityProtocol_...vgTj_
0x1000041ec      mov x20, x0                ; sec_protocol_options_t handle

0x1000041f0      mov w1, 0x301              ; second argument: TLS 1.0
0x1000041f4      bl sym.imp.sec_protocol_options_set_max_tls_protocol_version
```

On ARM64, C function arguments are passed in `x0`, `x1`, `x2`, ... (or their 32-bit aliases `w0`, `w1`, `w2`, ...). Here `x0` carries the `sec_protocol_options_t` handle retrieved from `NWProtocolTLS.Options.securityProtocolOptions`, and `w1` carries the TLS version constant. The value `0x0301` corresponds to `tls_protocol_version_TLSv10` (TLS 1.0).

The two calls are equivalent to:

```swift
sec_protocol_options_set_min_tls_protocol_version(tlsOptions.securityProtocolOptions, .TLSv10)
sec_protocol_options_set_max_tls_protocol_version(tlsOptions.securityProtocolOptions, .TLSv10)
```

This pins the connection to TLS 1.0 only. Because Network.framework operates entirely outside of ATS, this configuration is not subject to any ATS enforcement and the connection will succeed against a TLS 1.0 server.
