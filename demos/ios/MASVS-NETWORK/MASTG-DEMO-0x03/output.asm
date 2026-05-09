References to sec_protocol_options_set_min_tls_protocol_version:
0x100006e80 12 sym.imp.sec_protocol_options_set_min_tls_protocol_version

xrefs to sec_protocol_options_set_min_tls_protocol_version:
sym.func.mastgTest 0x100004920 [CALL:--x] bl sym.imp.sec_protocol_options_set_min_tls_protocol_version

Code calling sec_protocol_options_set_min_tls_protocol_version:
│           0x100004908      ldr x0, [x19, 0x10]
│           0x10000490c      bl sym.imp.Network.NWProtocolTLS.Options.securityProtocolOptions.getter
│           0x100004910      mov x8, x0
│           0x100004914      mov w1, 0x301                             ; tls_protocol_version_TLSv10
│           0x100004918      mov x0, x8
│           0x10000491c      mov w1, 0x301
│           0x100004920      bl sym.imp.sec_protocol_options_set_min_tls_protocol_version
│           0x100004924      ldr x8, [x19, 0x18]
│           0x100004928      str x8, [sp, 0x20]
