References to sec_protocol_options_set_min_tls_protocol_version:
0x1000099e4 12 sym.imp.sec_protocol_options_set_min_tls_protocol_version
0x10000c240 8 reloc.sec_protocol_options_set_min_tls_protocol_version

xrefs to sec_protocol_options_set_min_tls_protocol_version:
sym.imp.sec_protocol_options_set_min_tls_protocol_version 0x1000099e8 [DATA:r--] ldr x16, reloc.sec_protocol_options_set_min_tls_protocol_version

Import stub for sec_protocol_options_set_min_tls_protocol_version:
            ; CALL XREF from sym.func.100005b50 @ 0x100005d78(x)
┌ 12: void sym.imp.objc_retainAutoreleasedReturnValue (void *instance);
│           0x1000099c0      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000099c4      ldr x16, [x16, 0xb0]                      ; [0x10000c0b0:4]=20
│                                                                      ; reloc.objc_retainAutoreleasedReturnValue
└           0x1000099c8      br x16
            ; CALL XREF from sym.func.100009068 @ 0x1000091f0(x)
┌ 12: void sym.imp.rewind (FILE *stream);
│           0x1000099cc      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000099d0      ldr x16, [x16, 0x138]                     ; [0x10000c138:4]=37 ; "%"
└           0x1000099d4      br x16
            ; CALL XREF from sym.func.100004000 @ 0x1000041f4(x)
┌ 12: sym.imp.sec_protocol_options_set_max_tls_protocol_version ();
│           0x1000099d8      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000099dc      ldr x16, [x16, 0x238]                     ; [0x10000c238:4]=69 ; "E"
└           0x1000099e0      br x16
            ; CALL XREF from sym.func.100004000 @ 0x1000041d8(x)
┌ 12: sym.imp.sec_protocol_options_set_min_tls_protocol_version ();
│           0x1000099e4      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000099e8      ldr x16, [x16, 0x240]                     ; [0x10000c240:4]=70 ; "F"
└           0x1000099ec      br x16
            ; CALL XREF from sym.func.100004000 @ 0x100004214(x)
┌ 12: sym.imp.sec_protocol_options_set_tls_server_name ();
│           0x1000099f0      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000099f4      ldr x16, [x16, 0x248]                     ; [0x10000c248:4]=71 ; "G"
└           0x1000099f8      br x16
            ; CALL XREF from sym.func.100009068 @ 0x100009314(x)
┌ 12: int sym.imp.sscanf (const char *s, const char *format,   ...);
│           0x1000099fc      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x100009a00      ldr x16, [x16, 0x140]                     ; [0x10000c140:4]=38 ; "&"
└           0x100009a04      br x16
            ; XREFS: CALL 0x100004120  CALL 0x1000041b8  CALL 0x10000423c  CALL 0x1000042c8  CALL 0x1000042e4  
            ; XREFS: CALL 0x100004564  CALL 0x100004a30  CALL 0x100004d10  CALL 0x100004d30  CALL 0x100004d4c  
            ; XREFS: CALL 0x100005444  CALL 0x100005460  CALL 0x1000061c8  CALL 0x100006240  CALL 0x100006548  
            ; XREFS: CALL 0x10000673c  
┌ 12: sym.imp.swift_allocObject ();
│           0x100009a08      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x100009a0c      ldr x16, [x16, 0x608]                     ; [0x10000c608:4]=191
│                                                                      ; reloc.swift_allocObject

Search for ARM64 instructions that load TLS constants into w1:
0x0301, TLS 1.0
0x0302, TLS 1.1
0x0303, TLS 1.2
0x0304, TLS 1.3
0x1000041d4 hit4_0 21608052
0x1000041f0 hit4_1 21608052

Print the surrounding instructions for the mov w1, 0x301 instruction that sets TLS 1.0:
│           0x1000041ac      bl sym.imp.Network.NWProtocolTLS.Options.allocator..metadata.accessor_...a_ ; Network.NWProtocolTLS.Options.allocator..metadata.accessor(...a)
│           0x1000041b0      ldr w1, [x0, 0x30]
│           0x1000041b4      ldrh w2, [x0, 0x34]
│           0x1000041b8      bl sym.imp.swift_allocObject
│           0x1000041bc      mov x20, x0
│           0x1000041c0      bl sym.imp.Network.NWProtocolTLS.Options.allocator.bool:_allocator__Options.bool:_allocator__C._...cfc_ ; Network.NWProtocolTLS.Options.allocator.bool: allocator, Options.bool: allocator, C.(...cfc)
│           0x1000041c4      mov x26, x0
│           0x1000041c8      mov x20, x0
│           0x1000041cc      bl sym.imp.Network.NWProtocolTLS.Options.allocator.securityProtocol_...vgTj_ ; Network.NWProtocolTLS.Options.allocator.securityProtocol(...vgTj)
│           0x1000041d0      mov x20, x0
│           ;-- _0:
│           0x1000041d4      mov w1, 0x301
│           0x1000041d8      bl sym.imp.sec_protocol_options_set_min_tls_protocol_version
│           0x1000041dc      mov x0, x20
│           0x1000041e0      bl sym.imp.swift_unknownObjectRelease
│           0x1000041e4      mov x20, x26
│           0x1000041e8      bl sym.imp.Network.NWProtocolTLS.Options.allocator.securityProtocol_...vgTj_ ; Network.NWProtocolTLS.Options.allocator.securityProtocol(...vgTj)
│           0x1000041ec      mov x20, x0
│           ;-- _1:
│           0x1000041f0      mov w1, 0x301
│           0x1000041f4      bl sym.imp.sec_protocol_options_set_max_tls_protocol_version
│           0x1000041f8      mov x0, x20
