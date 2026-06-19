            ; CALL XREF from func.00005608 @ 0x5a78(x) ; sym.MASTestApp.CachedDocument.coder.allocator.SgSo7NSCoderC_tcfc
┌ 980: AB2C58CE.A727EF27CB85DF8CD8LLyyF_..partial.apply ();
│ afv: vars(38:sp[0x8..0x130])
; MASTestApp.CachedDocument.restoreToDisk.allocator(...E8AB2C58CE173A7
; 27EF27CB85DF8CD8LLyyF)
│           0x00005b14      stp x22, x21, [var_30h]!
│           0x00005b18      stp x20, x19, [var_10h]
│           0x00005b1c      stp x29, x30, [var_20h]
│           0x00005b20      add x29, sp, 0x20
│           0x00005b24      sub sp, sp, 0x100
│           0x00005b28      stur x20, [x29, -0xe8]
│           0x00005b2c      stur xzr, [x29, -0x28]
│           0x00005b30      stur xzr, [x29, -0x30]
│           0x00005b34      stur xzr, [x29, -0x38]
│           0x00005b38      mov x0, 0
│           0x00005b3c      stur x0, [x29, -0xe0]
│           0x00005b40      mov x0, 0
│           0x00005b44      stur x0, [x29, -0xb8]
│           0x00005b48      bl sym.imp.Foundation.Encoding...VMa
│           0x00005b4c      stur x0, [x29, -0xd8]
│           0x00005b50      ldur x8, [x0, -8]
│           0x00005b54      stur x8, [x29, -0xd0]
│           0x00005b58      ldr x8, [x8, 0x40]
│           0x00005b5c      lsr x8, x8, 0
│           0x00005b60      add x8, x8, 0xf
│           0x00005b64      and x9, x8, 0xfffffffffffffff0
│           0x00005b68      stur x9, [x29, -0xc8]
│           0x00005b6c      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x00005b70      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x00005b74      blr x16
│           0x00005b78      ldur x9, [x29, -0xc8]
│           0x00005b7c      mov x8, sp
│           0x00005b80      subs x0, x8, x9
│           0x00005b84      stur x0, [x29, -0xc0]
│           0x00005b88      mov sp, x0
│           0x00005b8c      adrp x0, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x00005b90      add x0, x0, 0x790                          ; "__const" ; int64_t arg1
│           0x00005b94      adrp x1, 0x1b000
│           0x00005b98      add x1, x1, 0x990                          ; int64_t arg2
│           0x00005b9c      bl sym.___swift_instantiateConcreteTypeFromMangledNameV2
│           0x00005ba0      mov x8, x0
│           0x00005ba4      ldur x0, [x29, -0xb8]
│           0x00005ba8      ldur x8, [x8, -8]                          ; [0x28788:8]=24
│                                                                      ; field.class.CachedDocument.var.contents
│                                                                      MASTestApp.CachedDocument.contents.allocator__Swift.String__String: allocator, contents__String: allocator, S.pWvd
│           0x00005bac      ldr x8, [x8, 0x40]
│           0x00005bb0      lsr x8, x8, 0
│           0x00005bb4      add x8, x8, 0xf
│           0x00005bb8      and x9, x8, 0xfffffffffffffff0
│           0x00005bbc      stur x9, [x29, -0xb0]
│           0x00005bc0      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x00005bc4      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x00005bc8      blr x16
│           0x00005bcc      ldur x9, [x29, -0xb0]
│           0x00005bd0      mov x8, sp
│           0x00005bd4      subs x1, x8, x9
│           0x00005bd8      stur x1, [x29, -0x60]
│           0x00005bdc      mov sp, x1
│           0x00005be0      bl sym.imp.Foundation.URL...VMa
│           0x00005be4      stur x0, [x29, -0x58]
│           0x00005be8      ldur x8, [x0, -8]
│           0x00005bec      stur x8, [x29, -0x68]
│           0x00005bf0      ldr x8, [x8, 0x40]
│           0x00005bf4      lsr x9, x8, 0
│           0x00005bf8      add x9, x9, 0xf
│           0x00005bfc      and x9, x9, 0xfffffffffffffff0
│           0x00005c00      stur x9, [x29, -0xa8]
│           0x00005c04      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x00005c08      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x00005c0c      blr x16
│           0x00005c10      ldur x10, [x29, -0xa8]
│           0x00005c14      mov x9, sp
│           0x00005c18      subs x9, x9, x10
│           0x00005c1c      stur x9, [x29, -0xa0]
│           0x00005c20      mov sp, x9
│           0x00005c24      stur x9, [x29, -0x28]
│           0x00005c28      lsr x8, x8, 0
│           0x00005c2c      add x8, x8, 0xf
│           0x00005c30      and x9, x8, 0xfffffffffffffff0
│           0x00005c34      stur x9, [x29, -0x98]
│           0x00005c38      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x00005c3c      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x00005c40      blr x16
│           0x00005c44      ldur x9, [x29, -0x98]
│           0x00005c48      mov x8, sp
│           0x00005c4c      subs x8, x8, x9
│           0x00005c50      stur x8, [x29, -0x90]
│           0x00005c54      mov sp, x8
│           0x00005c58      stur x8, [x29, -0x30]
│           0x00005c5c      stur x20, [x29, -0x38]
│           0x00005c60      adrp x8, 0x25000
│           0x00005c64      ldr x0, [x8, 0x368]                        ; [0x25368:8]=0
│                                                                      ; reloc.NSFileManager ; void *arg0
│           0x00005c68      bl sym.imp.objc_opt_self                   ; void *objc_opt_self(void *arg0)
│           0x00005c6c      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x00005c70      ldr x1, [x8, 0x3f8]                        ; [0x283f8:8]=0x1a2a7 str.defaultManager ; reloc.fixup.defaultManager ; char *selector
│           0x00005c74      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│           0x00005c78      mov x29, x29
│           0x00005c7c      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x00005c80      stur x0, [x29, -0x88]
│           0x00005c84      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x00005c88      ldr x1, [x8, 0x400]                        ; [0x28400:8]=0x1a213 str.URLsForDirectory:inDomains: ; reloc.fixup.URLsForDirectory:inDomains: ; char *selector
│           0x00005c8c      mov w8, 9
│           0x00005c90      mov x2, x8
│           0x00005c94      mov w8, 1
│           0x00005c98      mov x3, x8
│           0x00005c9c      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│           0x00005ca0      mov x29, x29
│           0x00005ca4      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x00005ca8      mov x8, x0
│           0x00005cac      ldur x0, [x29, -0x88]
│           0x00005cb0      stur x8, [x29, -0x80]
│           0x00005cb4      adrp x8, segment.__DATA_CONST              ; 0x24000
│           0x00005cb8      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│                                                                      ; reloc.objc_release
│           0x00005cbc      blr x8
│           0x00005cc0      ldur x0, [x29, -0x80]
│           0x00005cc4      ldur x1, [x29, -0x58]
│           0x00005cc8      bl sym.imp.Foundation_...nconditionallyBridgeFromObjectiveCySayxGSo7NSArrayCSgFZ_ ; Foundation(...nconditionallyBridgeFromObjectiveCySayxGSo7NSArrayCSgFZ)
│           0x00005ccc      stur x0, [x29, -0x78]
│           0x00005cd0      sub x20, x29, 0x40
│           0x00005cd4      stur x0, [x29, -0x40]
│           0x00005cd8      adrp x0, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x00005cdc      add x0, x0, 0x798                          ; int64_t arg1
│           0x00005ce0      adrp x1, 0x1b000
│           0x00005ce4      add x1, x1, 0x998                          ; int64_t arg2
│           0x00005ce8      bl sym.___swift_instantiateConcreteTypeFromMangledNameV2
│           0x00005cec      stur x0, [x29, -0x70]
│           0x00005cf0      bl sym....sSay10Foundation3URLVGSayxGSlsWl ; func.00008bcc
│           0x00005cf4      ldur x8, [x29, -0x60]
│           0x00005cf8      mov x1, x0
│           0x00005cfc      ldur x0, [x29, -0x70]
│           0x00005d00      bl sym.imp.first.Element_...zSgvg_         ; first.Element(...zSgvg)
│           0x00005d04      ldur x8, [x29, -0x68]
│           0x00005d08      ldur x0, [x29, -0x60]
│           0x00005d0c      ldur x2, [x29, -0x58]
│           0x00005d10      ldr x8, [x8, 0x30]
│           0x00005d14      mov w1, 1
│           0x00005d18      blr x8
│           0x00005d1c      subs w8, w0, 1
│       ┌─< 0x00005d20      b.eq 0x5e78
│      ┌──< 0x00005d24      b 0x5d28
│      ││   ; CODE XREF from func.00005b14 @ 0x5d24(x)
│      └──> 0x00005d28      ldur x21, [x29, -0xe0]
│       │   0x00005d2c      ldur x20, [x29, -0x90]
│       │   0x00005d30      ldur x2, [x29, -0x58]
│       │   0x00005d34      ldur x1, [x29, -0x60]
│       │   0x00005d38      ldur x8, [x29, -0x68]
│       │   0x00005d3c      ldr x8, [x8, 0x20]
│       │   0x00005d40      mov x0, x20
│       │   0x00005d44      blr x8
│       │   0x00005d48      ldur x0, [x29, -0x78]                      ; void *arg0
│       │   0x00005d4c      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│       │   0x00005d50      ldur x0, [x29, -0x80]
│       │   0x00005d54      adrp x8, segment.__DATA_CONST              ; 0x24000
│       │   0x00005d58      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│       │                                                              ; reloc.objc_release
│       │   0x00005d5c      blr x8
│       │   0x00005d60      ldur x8, [x29, -0xe8]
│       │   0x00005d64      adrp x9, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│       │   0x00005d68      ldr x9, [x9, 0x780]                        ; [0x28780:8]=8
│       │                                                              ; field.class.CachedDocument.var.fileName
│       │                                                              MASTestApp.CachedDocument.fileName.allocator__Swift.String__String: allocator, fileName__String: allocator, S.pWvd
│       │   0x00005d6c      add x8, x8, x9
│       │   0x00005d70      ldr x9, [x8]
│       │   0x00005d74      sub x10, x29, 0x10
│       │   0x00005d78      stur x9, [x10, -0x100]
│       │   0x00005d7c      ldr x0, [x8, 8]                            ; void *arg0
│       │   0x00005d80      sub x8, x29, 8
│       │   0x00005d84      stur x0, [x8, -0x100]
│       │   0x00005d88      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│       │   0x00005d8c      sub x8, x29, 0x10
│       │   0x00005d90      ldur x0, [x8, -0x100]
│       │   0x00005d94      sub x8, x29, 8
│       │   0x00005d98      ldur x1, [x8, -0x100]
│       │   0x00005d9c      ldur x8, [x29, -0xa0]
│       │   0x00005da0      bl sym.imp.Foundation.URL.appendingPathComponent_...CSSF_ ; Foundation.URL.appendingPathComponent(...CSSF)
│       │   0x00005da4      sub x8, x29, 8
│       │   0x00005da8      ldur x0, [x8, -0x100]                      ; void *arg0
│       │   0x00005dac      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│       │   0x00005db0      ldur x8, [x29, -0xe8]
│       │   0x00005db4      adrp x9, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│       │   0x00005db8      ldr x9, [x9, 0x788]                        ; [0x28788:8]=24
│       │                                                              ; field.class.CachedDocument.var.contents
│       │                                                              MASTestApp.CachedDocument.contents.allocator__Swift.String__String: allocator, contents__String: allocator, S.pWvd
│       │   0x00005dbc      add x8, x8, x9
│       │   0x00005dc0      ldr x9, [x8]
│       │   0x00005dc4      stur x9, [x29, -0x100]
│       │   0x00005dc8      ldr x0, [x8, 8]                            ; void *arg0
│       │   0x00005dcc      stur x0, [x29, -0xf8]
│       │   0x00005dd0      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│       │   0x00005dd4      ldur x10, [x29, -0x100]
│       │   0x00005dd8      ldur x9, [x29, -0xf8]
│       │   0x00005ddc      ldur x8, [x29, -0xc0]
│       │   0x00005de0      sub x20, x29, 0x50
│       │   0x00005de4      stur x10, [x29, -0x50]
│       │   0x00005de8      stur x9, [x29, -0x48]
│       │   0x00005dec      bl sym.imp.Foundation.Encoding.utf8_...vgZ_ ; Foundation.Encoding.utf8(...vgZ)
│       │   0x00005df0      bl sym....sS2SSysWl                        ; func.00008d18
│       │   0x00005df4      ldur x2, [x29, -0xc0]
│       │   0x00005df8      mov x4, x0
│       │   0x00005dfc      ldur x0, [x29, -0xa0]
│       │   0x00005e00      mov w8, 1
│       │   0x00005e04      and w1, w8, 1
│       │   0x00005e08      adrp x3, segment.__DATA_CONST              ; 0x24000
│       │   0x00005e0c      ldr x3, [x3, 0x690]                        ; [0x24690:8]=0
│       │                                                              ; reloc....SSN
│       │   0x00005e10      bl sym.imp.Foundation_...bSSAAE8EncodingVtKF_ ; Foundation(...bSSAAE8EncodingVtKF)
│       │   0x00005e14      mov x8, x21
│       │   0x00005e18      stur x8, [x29, -0xf0]
│      ┌──< 0x00005e1c      cbnz x21, 0x5eb0
│     ┌───< 0x00005e20      b 0x5e24
│     │││   ; CODE XREF from func.00005b14 @ 0x5e20(x)
│     └───> 0x00005e24      ldur x1, [x29, -0xd8]                      ; int64_t arg_20h
│      ││   0x00005e28      ldur x0, [x29, -0xc0]
│      ││   0x00005e2c      ldur x8, [x29, -0xd0]
│      ││   0x00005e30      ldr x8, [x8, 8]
│      ││   0x00005e34      blr x8
│      ││   0x00005e38      sub x0, x29, 0x50                          ; int64_t arg1
│      ││   0x00005e3c      bl sym....sSSWOh                           ; func.00008a08
│     ┌───< 0x00005e40      b 0x5e44
│     │││   ; CODE XREFS from func.00005b14 @ 0x5e40(x), 0x5ee4(x)
│    ┌└───> 0x00005e44      ldur x1, [x29, -0x58]
│    ╎ ││   0x00005e48      ldur x0, [x29, -0xa0]
│    ╎ ││   0x00005e4c      ldur x8, [x29, -0x68]
│    ╎ ││   0x00005e50      ldr x8, [x8, 8]
│    ╎ ││   0x00005e54      sub x9, x29, 0x18
│    ╎ ││   0x00005e58      stur x8, [x9, -0x100]
│    ╎ ││   0x00005e5c      blr x8
│    ╎ ││   0x00005e60      ldur x0, [x29, -0x90]
│    ╎ ││   0x00005e64      ldur x1, [x29, -0x58]
│    ╎ ││   0x00005e68      sub x8, x29, 0x18
│    ╎ ││   0x00005e6c      ldur x8, [x8, -0x100]
│    ╎ ││   0x00005e70      blr x8
│    ╎┌───< 0x00005e74      b 0x5e9c
│    ╎│││   ; CODE XREF from func.00005b14 @ 0x5d20(x)
│    ╎││└─> 0x00005e78      ldur x0, [x29, -0x60]                      ; int64_t arg1
│    ╎││    0x00005e7c      bl sym.Foundation.URL:_GenericAccessorW.bool____GenericAccessor ; func.00008cb0
│    ╎││    0x00005e80      ldur x0, [x29, -0x78]                      ; void *arg0
│    ╎││    0x00005e84      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│    ╎││    0x00005e88      ldur x0, [x29, -0x80]
│    ╎││    0x00005e8c      adrp x8, segment.__DATA_CONST              ; 0x24000
│    ╎││    0x00005e90      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│    ╎││                                                               ; reloc.objc_release
│    ╎││    0x00005e94      blr x8
│    ╎││┌─< 0x00005e98      b 0x5e9c
│    ╎│││   ; CODE XREFS from func.00005b14 @ 0x5e74(x), 0x5e98(x)
│    ╎└─└─> 0x00005e9c      sub sp, x29, 0x20
│    ╎ │    0x00005ea0      ldp x29, x30, [var_20h]
│    ╎ │    0x00005ea4      ldp x20, x19, [var_10h]
│    ╎ │    0x00005ea8      ldp x22, x21, [sp], 0x30
│    ╎ │    0x00005eac      ret
│    ╎ │    ; CODE XREF from func.00005b14 @ 0x5e1c(x)
│    ╎ └──> 0x00005eb0      ldur x1, [x29, -0xd8]                      ; int64_t arg_20h
│    ╎      0x00005eb4      ldur x0, [x29, -0xc0]
│    ╎      0x00005eb8      ldur x8, [x29, -0xd0]
│    ╎      0x00005ebc      ldur x9, [x29, -0xf0]
│    ╎      0x00005ec0      sub x10, x29, 0x20
│    ╎      0x00005ec4      stur x9, [x10, -0x100]
│    ╎      0x00005ec8      ldr x8, [x8, 8]
│    ╎      0x00005ecc      blr x8
│    ╎      0x00005ed0      sub x0, x29, 0x50                          ; int64_t arg1
│    ╎      0x00005ed4      bl sym....sSSWOh                           ; func.00008a08
│    ╎      0x00005ed8      sub x8, x29, 0x20
│    ╎      0x00005edc      ldur x0, [x8, -0x100]
│    ╎      0x00005ee0      bl sym.imp.swift_errorRelease
└    └────< 0x00005ee4      b 0x5e44
