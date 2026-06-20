            ; CALL XREF from func.00006644 @ 0x6d20(x) ; sym.MASTestApp.MastgTest.importSharedSession.from.Foundation.URL...V_tFZ
┌ 1980: E8AB2C.CE173A727EF27CB85DF8CD8.Foundation.Data...VFZ..partial.apply (int64_t arg1, int64_t arg2, void *arg_10h, int64_t arg_18h, int64_t arg_24h, void *arg_28h, int64_t arg_30h, int64_t arg_38h, int64_t arg_40h, int64_t arg_50h, int64_t arg_58h, int64_t arg_78h, void *arg_90h, int64_t arg_a0h, int64_t arg_a8h, int64_t arg_b0h, int64_t arg_b8h, int64_t arg_c0h, int64_t arg_108h, int64_t arg_110h, int64_t arg_178h, int64_t arg_180h, int64_t arg_198h, int64_t arg_1a8h, int64_t arg_278h, int64_t arg_280h, int64_t arg_2a0h, int64_t arg_2b8h);
│ `- args(x0, x1, sp[0x10..0x2b8]) vars(92:sp[0x8..0x3c8])
; MASTestApp.MastgTest.decodeInsecurely._6E8AB2C58CE173A727EF27CB85DF8
; CD8.Foundation.Data...VFZ
│           0x00007040      stp x22, x21, [var_30h]!
│           0x00007044      stp x20, x19, [var_10h]
│           0x00007048      stp x29, x30, [var_20h]
│           0x0000704c      add x29, sp, 0x20
│           0x00007050      sub sp, sp, 0x3a0
│           0x00007054      str x0, [var_1f0h]                         ; arg1
│           0x00007058      str x1, [var_1f8h]                         ; arg2
│           0x0000705c      stur xzr, [x29, -0x30]
│           0x00007060      stur xzr, [x29, -0x28]
│           0x00007064      mov x21, 0
│           0x00007068      stur xzr, [x29, -0x38]
│           0x0000706c      stur xzr, [x29, -0x80]
│           0x00007070      str xzr, [var_260h]
│           0x00007074      str xzr, [var_258h]
│           0x00007078      stur x0, [x29, -0x30]                      ; arg1
│           0x0000707c      stur x1, [x29, -0x28]                      ; arg2
│           0x00007080      mov x0, 0                                  ; int64_t arg_20h
│           0x00007084      bl sym....sSo17NSKeyedUnarchiverCMa        ; func.0000923c
│           0x00007088      ldr x1, [var_1f8h]                         ; int64_t arg2
│           0x0000708c      mov x20, x0
│           0x00007090      ldr x0, [var_1f0h]                         ; int64_t arg1
│           0x00007094      bl sym.Foundation.Data._Representation_...Oy_ ; func.0000929c
│           0x00007098      ldr x0, [var_1f0h]                         ; int64_t arg1
│           0x0000709c      ldr x1, [var_1f8h]                         ; int64_t arg2
│           0x000070a0      bl sym.__C.NSKeyedUnarchiver               ; func.00007aa8
│           0x000070a4      str x0, [instance]
│           0x000070a8      mov x8, x21
│           0x000070ac      str x8, [var_208h]
│       ┌─< 0x000070b0      cbnz x21, 0x76b0
│      ┌──< 0x000070b4      b 0x70b8
│      ││   ; CODE XREF from func.00007040 @ 0x70b4(x)
│      └──> 0x000070b8      ldr x0, [instance]                         ; void *instance
│       │   0x000070bc      str x0, [var_1d8h]
│       │   0x000070c0      stur x0, [x29, -0x80]
│       │   0x000070c4      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│       │   0x000070c8      ldr x1, [x8, 0x408]                        ; [0x28408:8]=0x1a36e str.setRequiresSecureCoding: ; "n\xa3\x01" ; char *selector
│       │   0x000070cc      mov w8, 0
│       │   0x000070d0      and w2, w8, 1
│       │   0x000070d4      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│       │   0x000070d8      adrp x8, segment.__DATA_CONST              ; 0x24000
│       │   0x000070dc      ldr x8, [x8, 0x170]                        ; [0x24170:8]=0
│       │                                                              ; reloc.NSKeyedArchiveRootObjectKey
│       │   0x000070e0      ldr x0, [x8]
│       │   0x000070e4      str x0, [var_1d0h]
│       │   0x000070e8      adrp x8, segment.__DATA_CONST              ; 0x24000
│       │   0x000070ec      ldr x8, [x8, 0x1a0]                        ; [0x241a0:8]=0
│       │                                                              ; reloc.objc_retain
│       │   0x000070f0      blr x8
│       │   0x000070f4      ldr x0, [var_1d0h]
│       │   0x000070f8      bl sym.imp.Foundation_...nconditionallyBridgeFromObjectiveCySSSo8NSStringCSgFZ_ ; Foundation(...nconditionallyBridgeFromObjectiveCySSSo8NSStringCSgFZ)
│       │   0x000070fc      str x1, [arg0]
│       │   0x00007100      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│       │   0x00007104      mov x1, x0
│       │   0x00007108      ldr x0, [arg0]                             ; void *arg0
│       │   0x0000710c      str x1, [var_1e0h]
│       │   0x00007110      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│       │   0x00007114      ldr x0, [var_1d0h]
│       │   0x00007118      adrp x8, segment.__DATA_CONST              ; 0x24000
│       │   0x0000711c      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│       │                                                              ; reloc.objc_release
│       │   0x00007120      blr x8
│       │   0x00007124      ldr x0, [var_1d8h]                         ; void *instance
│       │   0x00007128      ldr x2, [var_1e0h]
│       │   0x0000712c      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│       │   0x00007130      ldr x1, [x8, 0x3e0]                        ; [0x283e0:8]=0x1a293 str.decodeObjectForKey: ; reloc.fixup.decodeObjectForKey: ; char *selector
│       │   0x00007134      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│       │   0x00007138      mov x29, x29
│       │   0x0000713c      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│       │   0x00007140      mov x8, x0
│       │   0x00007144      ldr x0, [var_1e0h]
│       │   0x00007148      str x8, [var_1e8h]
│       │   0x0000714c      adrp x8, segment.__DATA_CONST              ; 0x24000
│       │   0x00007150      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│       │                                                              ; reloc.objc_release
│       │   0x00007154      blr x8
│       │   0x00007158      ldr x0, [var_1e8h]
│      ┌──< 0x0000715c      cbz x0, 0x719c
│     ┌───< 0x00007160      b 0x7164
│     │││   ; CODE XREF from func.00007040 @ 0x7160(x)
│     └───> 0x00007164      ldr x8, [var_1e8h]
│      ││   0x00007168      str x8, [var_1c0h]
│     ┌───< 0x0000716c      b 0x7170
│     │││   ; CODE XREF from func.00007040 @ 0x716c(x)
│     └───> 0x00007170      ldr x0, [var_1c0h]
│      ││   0x00007174      str x0, [var_1b8h]
│      ││   0x00007178      add x8, sp, 0x210
│      ││   0x0000717c      str x8, [var_1b0h]
│      ││   0x00007180      bl sym.imp._bridgeAnyObjectTo...B0yypyXlSgF
│      ││   0x00007184      ldr x0, [var_1b0h]                         ; int64_t arg1
│      ││   0x00007188      sub x1, x29, 0xc0                          ; int64_t arg2
│      ││   0x0000718c      bl sym....sypWOb                           ; func.000047a0
│      ││   0x00007190      ldr x0, [var_1b8h]
│      ││   0x00007194      bl sym.imp.swift_unknownObjectRelease
│     ┌───< 0x00007198      b 0x71b0
│     │││   ; CODE XREF from func.00007040 @ 0x715c(x)
│     │└──> 0x0000719c      stur xzr, [x29, -0xc0]
│     │ │   0x000071a0      stur xzr, [x29, -0xb8]
│     │ │   0x000071a4      stur xzr, [x29, -0xb0]
│     │ │   0x000071a8      stur xzr, [x29, -0xa8]
│     │┌──< 0x000071ac      b 0x71b0
│     │││   ; CODE XREFS from func.00007040 @ 0x7198(x), 0x71ac(x)
│     └└──> 0x000071b0      ldr x0, [var_1d8h]                         ; void *instance
│       │   0x000071b4      ldur q0, [x29, -0xc0]
│       │   0x000071b8      sub x8, x29, 0xa0
│       │   0x000071bc      str x8, [arg_1a8h]
│       │   0x000071c0      stur q0, [x29, -0xa0]
│       │   0x000071c4      ldur q0, [x29, -0xb0]
│       │   0x000071c8      stur q0, [x29, -0x90]
│       │   0x000071cc      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│       │   0x000071d0      ldr x1, [x8, 0x410]                        ; [0x28410:8]=0x1a2ef str.finishDecoding ; reloc.fixup.finishDecoding ; char *selector
│       │   0x000071d4      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│       │   0x000071d8      ldr x0, [arg_1a8h]                         ; int64_t arg1
│       │   0x000071dc      sub x1, x29, 0xe0                          ; int64_t arg2
│       │   0x000071e0      bl sym....sypSgWOc                         ; func.00009314
│       │   0x000071e4      ldur x8, [x29, -0xc8]
│      ┌──< 0x000071e8      cbz x8, 0x73c8
│     ┌───< 0x000071ec      b 0x71f0
│     │││   ; CODE XREF from func.00007040 @ 0x71ec(x)
│     └───> 0x000071f0      mov x0, 0
│      ││   0x000071f4      bl method.SecureUserSession.allocator_...a_ ; func.00008b18
│      ││   0x000071f8      mov x3, x0
│      ││   0x000071fc      add x0, sp, 0x230
│      ││   0x00007200      sub x1, x29, 0xe0
│      ││   0x00007204      adrp x8, segment.__DATA_CONST              ; 0x24000
│      ││   0x00007208      ldr x8, [x8, 0x8b8]                        ; [0x248b8:8]=0
│      ││                                                              ; reloc....ypN
│      ││   0x0000720c      add x2, x8, 8
│      ││   0x00007210      mov w8, 6
│      ││   0x00007214      mov x4, x8
│      ││   0x00007218      bl sym.imp.swift_dynamicCast
│     ┌───< 0x0000721c      tbz w0, 0, 0x7230
│    ┌────< 0x00007220      b 0x7224
│    ││││   ; CODE XREF from func.00007040 @ 0x7220(x)
│    └────> 0x00007224      ldr x8, [var_230h]
│     │││   0x00007228      str x8, [var_1a0h]
│    ┌────< 0x0000722c      b 0x723c
│    ││││   ; CODE XREF from func.00007040 @ 0x721c(x)
│    │└───> 0x00007230      mov x8, 0
│    │ ││   0x00007234      str x8, [var_1a0h]
│    │┌───< 0x00007238      b 0x723c
│    ││││   ; CODE XREFS from func.00007040 @ 0x722c(x), 0x7238(x)
│    └└───> 0x0000723c      ldr x8, [var_1a0h]
│      ││   0x00007240      str x8, [var_198h]
│     ┌───< 0x00007244      b 0x7248
│     │││   ; CODE XREFS from func.00007040 @ 0x7244(x), 0x73d8(x)
│    ┌└───> 0x00007248      ldr x8, [var_198h]
│    ╎ ││   0x0000724c      str x8, [var_190h]
│    ╎┌───< 0x00007250      cbz x8, 0x73dc
│   ┌─────< 0x00007254      b 0x7258
│   │╎│││   ; CODE XREF from func.00007040 @ 0x7254(x)
│   └─────> 0x00007258      ldr x8, [var_190h]
│    ╎│││   0x0000725c      str x8, [var_188h]
│   ┌─────< 0x00007260      b 0x7264
│   │╎│││   ; CODE XREF from func.00007040 @ 0x7260(x)
│   └─────> 0x00007264      ldr x8, [var_188h]
│    ╎│││   0x00007268      str x8, [var_160h]
│    ╎│││   0x0000726c      str x8, [var_258h]
│    ╎│││   0x00007270      mov w8, 0x62                               ; 'b'
│    ╎│││   0x00007274      mov x0, x8
│    ╎│││   0x00007278      mov w8, 1
│    ╎│││   0x0000727c      mov x1, x8
│    ╎│││   0x00007280      bl sym.imp.DefaultStringInterpolation.literalCapacity.interpolationCount_...itcfC_ ; DefaultStringInterpolation.literalCapacity.interpolationCount(...itcfC)
│    ╎│││   0x00007284      add x20, sp, 0x248                         ; "__const"
│    ╎│││   0x00007288      str x20, [var_148h]
│    ╎│││   0x0000728c      str x0, [var_248h]
│    ╎│││   0x00007290      str x1, [arg_180h]
│    ╎│││   0x00007294      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│    ╎│││   0x00007298      add x0, x0, 0x610                          ; 0x1a610 ; "Decoding instantiated CachedDocument and ran its initializer, which wrote '"
│    ╎│││   0x0000729c      mov w8, 0x4b                               ; 'K'
│    ╎│││   0x000072a0      mov x1, x8
│    ╎│││   0x000072a4      mov w8, 1
│    ╎│││   0x000072a8      str w8, [var_13ch]
│    ╎│││   0x000072ac      and w2, w8, 1
│    ╎│││   0x000072b0      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│    ╎│││   0x000072b4      str x1, [arg_2b8h]
│    ╎│││   0x000072b8      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│    ╎│││   0x000072bc      ldr x20, [var_148h]
│    ╎│││   0x000072c0      ldr x0, [arg_2b8h]                         ; void *arg0
│    ╎│││   0x000072c4      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│    ╎│││   0x000072c8      ldr x8, [var_160h]
│    ╎│││   0x000072cc      adrp x9, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│    ╎│││   0x000072d0      ldr x9, [x9, 0x780]                        ; [0x28780:8]=8
│    ╎│││                                                              ; field.class.CachedDocument.var.fileName
│    ╎│││                                                              MASTestApp.CachedDocument.fileName.allocator__Swift.String__String: allocator, fileName__String: allocator, S.pWvd
│    ╎│││   0x000072d4      add x8, x8, x9
│    ╎│││   0x000072d8      ldr x9, [x8]
│    ╎│││   0x000072dc      str x9, [var_120h]
│    ╎│││   0x000072e0      ldr x0, [x8, 8]                            ; void *arg0
│    ╎│││   0x000072e4      str x0, [var_128h]
│    ╎│││   0x000072e8      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│    ╎│││   0x000072ec      ldr x9, [var_120h]
│    ╎│││   ; DATA XREF from func.00014528 @ 0x14830(r)
│    ╎│││   0x000072f0      ldr x8, [var_128h]
│    ╎│││   0x000072f4      add x0, sp, 0x238
│    ╎│││   0x000072f8      str x0, [arg_2a0h]
│    ╎│││   0x000072fc      str x9, [arg_198h]
│    ╎│││   0x00007300      str x8, [var_240h]
│    ╎│││   0x00007304      adrp x1, segment.__DATA_CONST              ; 0x24000
│    ╎│││   0x00007308      ldr x1, [x1, 0x690]                        ; [0x24690:8]=0
│    ╎│││                                                              ; reloc....SSN
│    ╎│││   0x0000730c      adrp x2, segment.__DATA_CONST              ; 0x24000
│    ╎│││   0x00007310      ldr x2, [x2, 0x6b8]
│    ╎│││   0x00007314      adrp x3, segment.__DATA_CONST              ; 0x24000
│    ╎│││   0x00007318      ldr x3, [x3, 0x6b0]                        ; [0x246b0:8]=0
│    ╎│││                                                              ; reloc.TextOutputStreamable.setter_...P_
│    ╎│││   0x0000731c      bl sym.imp.DefaultStringInterpolation.append...C0yyxs06CustomB11ConvertibleRzs20TextOutputStreamableRzlF
│    ╎│││   0x00007320      ldr x20, [var_148h]
│    ╎│││   0x00007324      ldr x0, [arg_2a0h]                         ; int64_t arg1
│    ╎│││   0x00007328      bl sym....sSSWOh                           ; func.00008a08
│    ╎│││   0x0000732c      ldr w8, [var_13ch]
│    ╎│││   0x00007330      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│    ╎│││   0x00007334      add x0, x0, 0x660                          ; 0x1a660 ; "' into the app sandbox."
│    ╎│││   0x00007338      mov w9, 0x17
│    ╎│││   0x0000733c      mov x1, x9
│    ╎│││   0x00007340      and w2, w8, 1
│    ╎│││   0x00007344      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│    ╎│││   0x00007348      str x1, [var_140h]
│    ╎│││   0x0000734c      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│    ╎│││   0x00007350      ldr x0, [var_140h]                         ; void *arg0
│    ╎│││   0x00007354      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│    ╎│││   0x00007358      ldr x8, [var_248h]                         ; [0x248:8]=0x74736e6f635f5f ; "__const"
│    ╎│││   0x0000735c      str x8, [arg_278h]
│    ╎│││   0x00007360      ldr x0, [arg_180h]                         ; void *arg0
│    ╎│││   0x00007364      str x0, [arg_280h]
│    ╎│││   0x00007368      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│    ╎│││   0x0000736c      ldr x0, [var_148h]                         ; void *arg1
│    ╎│││   0x00007370      bl sym....ss26DefaultStringInterpolationVWOh ; func.00009098
│    ╎│││   0x00007374      ldr x1, [arg_280h]
│    ╎│││   0x00007378      ldr x0, [arg_278h]
│    ╎│││   0x0000737c      bl sym.imp.stringInterpolation__String_...cfC_ ; stringInterpolation__String(...cfC)
│    ╎│││   0x00007380      mov x2, x0
│    ╎│││   0x00007384      ldr x0, [var_160h]
│    ╎│││   0x00007388      str x2, [var_168h]
│    ╎│││   0x0000738c      str x1, [var_170h]
│    ╎│││   0x00007390      adrp x8, segment.__DATA_CONST              ; 0x24000
│    ╎│││   0x00007394      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│    ╎│││                                                              ; reloc.objc_release
│    ╎│││   0x00007398      blr x8
│    ╎│││   0x0000739c      sub x0, x29, 0xa0                          ; int64_t arg1
│    ╎│││   0x000073a0      bl sym....sypSgWOh                         ; func.00004708
│    ╎│││   0x000073a4      ldr x0, [var_1d8h]
│    ╎│││   0x000073a8      adrp x8, segment.__DATA_CONST              ; 0x24000
│    ╎│││   0x000073ac      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│    ╎│││                                                              ; reloc.objc_release
│    ╎│││   0x000073b0      blr x8
│    ╎│││   0x000073b4      ldr x0, [var_168h]
│    ╎│││   0x000073b8      ldr x1, [var_170h]
│    ╎│││   0x000073bc      str x0, [arg_178h]
│    ╎│││   0x000073c0      str x1, [arg_180h]
│   ┌─────< 0x000073c4      b 0x7694
│   │╎│││   ; CODE XREF from func.00007040 @ 0x71e8(x)
│   │╎│└──> 0x000073c8      sub x0, x29, 0xe0                          ; int64_t arg1
│   │╎│ │   0x000073cc      bl sym....sypSgWOh                         ; func.00004708
│   │╎│ │   0x000073d0      mov x8, 0
│   │╎│ │   0x000073d4      str x8, [arg_198h]
│   │└────< 0x000073d8      b 0x7248
│   │ │ │   ; CODE XREF from func.00007040 @ 0x7250(x)
│   │ └───> 0x000073dc      sub x0, x29, 0xa0                          ; int64_t arg1
│   │   │   0x000073e0      add x1, sp, 0x2a0                          ; int64_t arg2
│   │   │   0x000073e4      bl sym....sypSgWOc                         ; func.00009314
│   │   │   0x000073e8      ldr x8, [arg_2b8h]
│   │  ┌──< 0x000073ec      cbz x8, 0x763c
│   │ ┌───< 0x000073f0      b 0x73f4
│   │ │││   ; CODE XREF from func.00007040 @ 0x73f0(x)
│   │ └───> 0x000073f4      add x0, sp, 0x2a0                          ; int64_t arg1
│   │  ││   0x000073f8      sub x1, x29, 0x100                         ; int64_t arg2
│   │  ││   0x000073fc      str x1, [arg_108h]
│   │  ││   0x00007400      bl sym....sypWOb                           ; func.000047a0
│   │  ││   0x00007404      ldr x0, [arg_108h]                         ; int64_t arg1
│   │  ││   0x00007408      add x1, sp, 0x280                          ; int64_t arg2
│   │  ││   0x0000740c      str x1, [arg_110h]
│   │  ││   0x00007410      bl sym....sypWOc                           ; func.00009384
│   │  ││   0x00007414      mov x0, 0
│   │  ││   0x00007418      bl sym.MASTestApp.InsecureUserSession.allocator_...a_ ; func.000041bc
│   │  ││   0x0000741c      ldr x1, [arg_110h]
│   │  ││   0x00007420      mov x3, x0
│   │  ││   0x00007424      add x0, sp, 0x278
│   │  ││   0x00007428      adrp x8, segment.__DATA_CONST              ; 0x24000
│   │  ││   0x0000742c      ldr x8, [x8, 0x8b8]                        ; [0x248b8:8]=0
│   │  ││                                                              ; reloc....ypN
│   │  ││   0x00007430      add x2, x8, 8
│   │  ││   0x00007434      mov w8, 6
│   │  ││   0x00007438      mov x4, x8
│   │  ││   0x0000743c      bl sym.imp.swift_dynamicCast
│   │ ┌───< 0x00007440      tbz w0, 0, 0x7454
│   │┌────< 0x00007444      b 0x7448
│   │││││   ; CODE XREF from func.00007040 @ 0x7444(x)
│   │└────> 0x00007448      ldr x8, [var_278h]
│   │ │││   0x0000744c      str x8, [var_100h]
│   │┌────< 0x00007450      b 0x7460
│   │││││   ; CODE XREF from func.00007040 @ 0x7440(x)
│   ││└───> 0x00007454      mov x8, 0
│   ││ ││   0x00007458      str x8, [var_100h]
│   ││┌───< 0x0000745c      b 0x7460
│   │││││   ; CODE XREFS from func.00007040 @ 0x7450(x), 0x745c(x)
│   │└└───> 0x00007460      ldr x8, [var_100h]
│   │  ││   0x00007464      str x8, [var_f8h]
│   │ ┌───< 0x00007468      cbz x8, 0x7524
│   │┌────< 0x0000746c      b 0x7470
│   │││││   ; CODE XREF from func.00007040 @ 0x746c(x)
│   │└────> 0x00007470      ldr x8, [var_f8h]
│   │ │││   0x00007474      str x8, [var_f0h]
│   │┌────< 0x00007478      b 0x747c
│   │││││   ; CODE XREF from func.00007040 @ 0x7478(x)
│   │└────> 0x0000747c      ldr x8, [var_f0h]
│   │ │││   0x00007480      str x8, [var_d8h]
│   │ │││   0x00007484      str x8, [var_260h]
│   │ │││   0x00007488      adrp x9, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│   │ │││   0x0000748c      ldr x9, [x9, 0x750]                        ; [0x28750:8]=8
│   │ │││                                                              ; field.class.MASTestApp.InsecureUserSession.var.userID
│   │ │││                                                              [31] -rw- section size 1656 named 31.__DATA.__data
│   │ │││   0x00007490      add x8, x8, x9
│   │ │││   0x00007494      ldr x9, [x8]
│   │ │││   0x00007498      str x9, [var_c8h]
│   │ │││   0x0000749c      ldr x0, [x8, 8]                            ; void *arg0
│   │ │││   0x000074a0      str x0, [var_d0h]
│   │ │││   0x000074a4      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│   │ │││   0x000074a8      ldr x0, [var_c8h]                          ; int64_t arg1
│   │ │││   0x000074ac      ldr x1, [var_d0h]                          ; int64_t arg2
│   │ │││   0x000074b0      ldr x8, [var_d8h]
│   │ │││   0x000074b4      adrp x9, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│   │ │││   0x000074b8      ldr x9, [x9, 0x758]                        ; [0x28758:8]=24
│   │ │││                                                              ; field.class.MASTestApp.InsecureUserSession.var.isAdmin
│   │ │││                                                              MASTestApp.InsecureUserSession.isAdmin.allocator__Bool: allocator, isAdmin: allocator, Sbool -> allocator
│   │ │││   0x000074bc      add x8, x8, x9
│   │ │││   0x000074c0      ldrb w8, [x8]
│   │ │││   0x000074c4      and w2, w8, 1
│   │ │││   0x000074c8      bl sym.MASTestApp.MastgTest.accessDecision._6E8AB2C58CE173A727EF27CB85DF8CD8.userID.isAdmin.S__...tFZ_ ; func.00007b60
│   │ │││   0x000074cc      mov x2, x0
│   │ │││   0x000074d0      ldr x0, [var_d0h]                          ; void *arg0
│   │ │││   0x000074d4      str x2, [var_e0h]
│   │ │││   0x000074d8      str x1, [var_e8h]
│   │ │││   0x000074dc      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│   │ │││   0x000074e0      ldr x0, [var_d8h]
│   │ │││   0x000074e4      adrp x8, segment.__DATA_CONST              ; 0x24000
│   │ │││   0x000074e8      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   │ │││                                                              ; reloc.objc_release
│   │ │││   0x000074ec      blr x8
│   │ │││   0x000074f0      sub x0, x29, 0x100                         ; int64_t arg1
│   │ │││   0x000074f4      bl sym.___swift_destroy_boxed_opaque_existential_0
│   │ │││   0x000074f8      sub x0, x29, 0xa0                          ; int64_t arg1
│   │ │││   0x000074fc      bl sym....sypSgWOh                         ; func.00004708
│   │ │││   0x00007500      ldr x0, [var_1d8h]
│   │ │││   0x00007504      adrp x8, segment.__DATA_CONST              ; 0x24000
│   │ │││   0x00007508      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   │ │││                                                              ; reloc.objc_release
│   │ │││   0x0000750c      blr x8
│   │ │││   0x00007510      ldr x0, [var_e0h]
│   │ │││   0x00007514      ldr x1, [var_e8h]
│   │ │││   0x00007518      str x0, [arg_178h]
│   │ │││   0x0000751c      str x1, [arg_180h]
│   │┌────< 0x00007520      b 0x7694
│   │││││   ; CODE XREF from func.00007040 @ 0x7468(x)
│   ││└───> 0x00007524      mov w8, 0x1d
│   ││ ││   0x00007528      mov x0, x8
│   ││ ││   0x0000752c      mov w8, 1
│   ││ ││   0x00007530      mov x1, x8
│   ││ ││   0x00007534      str x1, [var_80h]
│   ││ ││   0x00007538      bl sym.imp.DefaultStringInterpolation.literalCapacity.interpolationCount_...itcfC_ ; DefaultStringInterpolation.literalCapacity.interpolationCount(...itcfC)
│   ││ ││   0x0000753c      add x20, sp, 0x268
│   ││ ││   0x00007540      str x20, [var_98h]
│   ││ ││   0x00007544      str x0, [var_268h]
│   ││ ││   0x00007548      str x1, [var_270h]
│   ││ ││   0x0000754c      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│   ││ ││   0x00007550      add x0, x0, 0x5f0                          ; 0x1a5f0 ; "Decoded an unexpected type:"
│   ││ ││   0x00007554      mov w8, 0x1c
│   ││ ││   0x00007558      mov x1, x8
│   ││ ││   0x0000755c      mov w8, 1
│   ││ ││   0x00007560      str w8, [var_8ch]
│   ││ ││   0x00007564      and w2, w8, 1
│   ││ ││   0x00007568      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│   ││ ││   0x0000756c      str x1, [var_70h]
│   ││ ││   0x00007570      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│   ││ ││   0x00007574      ldr x20, [var_98h]
│   ││ ││   ; DATA XREF from func.00014b6c @ 0x14cd4(r)
│   ││ ││   0x00007578      ldr x0, [var_70h]                          ; void *arg0
│   ││ ││   0x0000757c      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│   ││ ││   0x00007580      sub x0, x29, 0x100                         ; int64_t arg1
│   ││ ││   0x00007584      str x0, [arg_b0h]
│   ││ ││   0x00007588      ldur x1, [x29, -0xe8]                      ; int64_t arg2
│   ││ ││   0x0000758c      str x1, [arg_78h]
│   ││ ││   0x00007590      bl sym.___swift_project_boxed_opaque_existential_0
│   ││ ││   0x00007594      ldr x1, [arg_78h]
│   ││ ││   0x00007598      ldr w8, [var_8ch]
│   ││ ││   0x0000759c      and w2, w8, 1
│   ││ ││   0x000075a0      bl sym.imp.swift_getDynamicType
│   ││ ││   0x000075a4      bl sym....ss26DefaultStringInterpolationV06appendC0yyypXpF ; func.00007b2c
│   ││ ││   0x000075a8      ldr x20, [var_98h]
│   ││ ││   0x000075ac      ldr x1, [var_80h]
│   ││ ││   0x000075b0      ldr w8, [var_8ch]
│   ││ ││   0x000075b4      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│   ││ ││   0x000075b8      add x0, x0, 0x60d
│   ││ ││   0x000075bc      and w2, w8, 1
│   ││ ││   0x000075c0      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│   ││ ││   0x000075c4      str x1, [arg_90h]
│   ││ ││   0x000075c8      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│   ││ ││   0x000075cc      ldr x0, [arg_90h]                          ; void *arg0
│   ││ ││   0x000075d0      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│   ││ ││   0x000075d4      ldr x8, [var_268h]
│   ││ ││   0x000075d8      str x8, [arg_a8h]
│   ││ ││   0x000075dc      ldr x0, [var_270h]                         ; void *arg0
│   ││ ││   0x000075e0      str x0, [arg_a0h]
│   ││ ││   0x000075e4      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│   ││ ││   0x000075e8      ldr x0, [var_98h]                          ; void *arg1
│   ││ ││   0x000075ec      bl sym....ss26DefaultStringInterpolationVWOh ; func.00009098
│   ││ ││   0x000075f0      ldr x1, [arg_a0h]
│   ││ ││   0x000075f4      ldr x0, [arg_a8h]
│   ││ ││   0x000075f8      bl sym.imp.stringInterpolation__String_...cfC_ ; stringInterpolation__String(...cfC)
│   ││ ││   0x000075fc      mov x2, x0
│   ││ ││   0x00007600      ldr x0, [arg_b0h]                          ; int64_t arg1
│   ││ ││   0x00007604      str x2, [arg_b8h]
│   ││ ││   0x00007608      str x1, [arg_c0h]
│   ││ ││   0x0000760c      bl sym.___swift_destroy_boxed_opaque_existential_0
│   ││ ││   0x00007610      sub x0, x29, 0xa0                          ; int64_t arg1
│   ││ ││   0x00007614      bl sym....sypSgWOh                         ; func.00004708
│   ││ ││   0x00007618      ldr x0, [var_1d8h]
│   ││ ││   0x0000761c      adrp x8, segment.__DATA_CONST              ; 0x24000
│   ││ ││   0x00007620      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   ││ ││                                                              ; reloc.objc_release
│   ││ ││   0x00007624      blr x8
│   ││ ││   0x00007628      ldr x0, [arg_b8h]
│   ││ ││   0x0000762c      ldr x1, [arg_c0h]
│   ││ ││   0x00007630      str x0, [arg_178h]
│   ││ ││   0x00007634      str x1, [arg_180h]
│   ││┌───< 0x00007638      b 0x7694
│   │││││   ; CODE XREF from func.00007040 @ 0x73ec(x)
│   │││└──> 0x0000763c      add x0, sp, 0x2a0                          ; int64_t arg1
│   │││ │   0x00007640      bl sym....sypSgWOh                         ; func.00004708
│   │││ │   0x00007644      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│   │││ │   0x00007648      add x0, x0, 0x5d0                          ; 0x1a5d0 ; "Decoded no root object."
│   │││ │   0x0000764c      mov w8, 0x17
│   │││ │   0x00007650      mov x1, x8
│   │││ │   0x00007654      mov w8, 1
│   │││ │   0x00007658      and w2, w8, 1
│   │││ │   0x0000765c      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│   │││ │   0x00007660      str x0, [var_60h]
│   │││ │   0x00007664      str x1, [var_68h]
│   │││ │   0x00007668      sub x0, x29, 0xa0                          ; int64_t arg1
│   │││ │   0x0000766c      bl sym....sypSgWOh                         ; func.00004708
│   │││ │   0x00007670      ldr x0, [var_1d8h]
│   │││ │   0x00007674      adrp x8, segment.__DATA_CONST              ; 0x24000
│   │││ │   0x00007678      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   │││ │                                                              ; reloc.objc_release
│   │││ │   0x0000767c      blr x8
│   │││ │   0x00007680      ldr x0, [var_60h]
│   │││ │   0x00007684      ldr x1, [var_68h]
│   │││ │   0x00007688      str x0, [arg_178h]
│   │││ │   0x0000768c      str x1, [arg_180h]
│   │││┌──< 0x00007690      b 0x7694
│   │││││   ; CODE XREFS from func.00007040 @ 0x73c4(x), 0x7520(x), 0x7638(x), 0x7690(x), 0x77f8(x)
│  ┌└└└└──> 0x00007694      ldr x0, [var_178h]
│  ╎    │   0x00007698      ldr x1, [var_180h]
│  ╎    │   0x0000769c      add sp, sp, 0x3a0
│  ╎    │   0x000076a0      ldp x29, x30, [var_20h]
│  ╎    │   0x000076a4      ldp x20, x19, [var_10h]
│  ╎    │   0x000076a8      ldp x22, x21, [sp], 0x30
│  ╎    │   0x000076ac      ret
│  ╎    │   ; CODE XREF from func.00007040 @ 0x70b0(x)
│  ╎    └─> 0x000076b0      ldr x0, [var_208h]
│  ╎        0x000076b4      str x0, [var_48h]
│  ╎        0x000076b8      bl sym.imp.swift_errorRetain
│  ╎        0x000076bc      ldr x8, [var_48h]
│  ╎        0x000076c0      stur x8, [x29, -0x38]
│  ╎        0x000076c4      mov w8, 0x11
│  ╎        0x000076c8      mov x0, x8
│  ╎        0x000076cc      str x0, [var_8h]
│  ╎        0x000076d0      mov w8, 1
│  ╎        0x000076d4      mov x1, x8
│  ╎        0x000076d8      bl sym.imp.DefaultStringInterpolation.literalCapacity.interpolationCount_...itcfC_ ; DefaultStringInterpolation.literalCapacity.interpolationCount(...itcfC)
│  ╎        0x000076dc      mov x8, x1
│  ╎        0x000076e0      ldr x1, [var_8h]
│  ╎        0x000076e4      sub x20, x29, 0x48
│  ╎        0x000076e8      str x20, [arg_30h]
│  ╎        0x000076ec      stur x0, [x29, -0x48]
│  ╎        0x000076f0      stur x8, [x29, -0x40]
│  ╎        0x000076f4      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│  ╎        0x000076f8      add x0, x0, 0x5b0                          ; 0x1a5b0 ; "Decoding failed:"
│  ╎        0x000076fc      mov w8, 1
│  ╎        0x00007700      str w8, [arg_24h]
│  ╎        0x00007704      and w2, w8, 1
│  ╎        0x00007708      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│  ╎        0x0000770c      str x1, [arg_10h]
│  ╎        0x00007710      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│  ╎        0x00007714      ldr x0, [arg_10h]                          ; void *arg0
│  ╎        0x00007718      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│  ╎        0x0000771c      ldr x0, [var_48h]
│  ╎        0x00007720      sub x1, x29, 0x50
│  ╎        0x00007724      sub x2, x29, 0x68
│  ╎        0x00007728      bl sym.imp.swift_getErrorValue
│  ╎        0x0000772c      ldur x20, [x29, -0x68]
│  ╎        0x00007730      ldur x0, [x29, -0x60]
│  ╎        0x00007734      ldur x1, [x29, -0x58]
│  ╎        0x00007738      bl sym.imp.Error.Foundation.localizedDescription_...vg_ ; Error.Foundation.localizedDescription(...vg)
│  ╎        0x0000773c      ldr x20, [arg_30h]
│  ╎        0x00007740      mov x8, x0
│  ╎        0x00007744      sub x0, x29, 0x78
│  ╎        0x00007748      str x0, [arg_18h]
│  ╎        0x0000774c      stur x8, [x29, -0x78]
│  ╎        0x00007750      stur x1, [x29, -0x70]
│  ╎        0x00007754      adrp x1, segment.__DATA_CONST              ; 0x24000
│  ╎        0x00007758      ldr x1, [x1, 0x690]                        ; [0x24690:8]=0
│  ╎                                                                   ; reloc....SSN
│  ╎        0x0000775c      adrp x2, segment.__DATA_CONST              ; 0x24000
│  ╎        0x00007760      ldr x2, [x2, 0x6b8]
│  ╎        0x00007764      adrp x3, segment.__DATA_CONST              ; 0x24000
│  ╎        0x00007768      ldr x3, [x3, 0x6b0]                        ; [0x246b0:8]=0
│  ╎                                                                   ; reloc.TextOutputStreamable.setter_...P_
│  ╎        0x0000776c      bl sym.imp.DefaultStringInterpolation.append...C0yyxs06CustomB11ConvertibleRzs20TextOutputStreamableRzlF
│  ╎        0x00007770      ldr x20, [arg_30h]
│  ╎        0x00007774      ldr x0, [arg_18h]                          ; int64_t arg1
│  ╎        0x00007778      bl sym....sSSWOh                           ; func.00008a08
│  ╎        0x0000777c      ldr w8, [arg_24h]
│  ╎        0x00007780      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│  ╎        0x00007784      add x0, x0, 0x920                          ; "__objc_classrefs__DATA_CONST"
│  ╎        0x00007788      mov x1, 0
│  ╎        0x0000778c      and w2, w8, 1
│  ╎        0x00007790      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│  ╎        0x00007794      str x1, [arg_28h]
│  ╎        0x00007798      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│  ╎        0x0000779c      ldr x0, [arg_28h]                          ; void *arg0
│  ╎        0x000077a0      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│  ╎        0x000077a4      ldur x8, [x29, -0x48]
│  ╎        0x000077a8      str x8, [arg_40h]
│  ╎        0x000077ac      ldur x0, [x29, -0x40]                      ; void *arg0
│  ╎        0x000077b0      str x0, [arg_38h]
│  ╎        0x000077b4      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│  ╎        0x000077b8      ldr x0, [arg_30h]                          ; void *arg1
│  ╎        0x000077bc      bl sym....ss26DefaultStringInterpolationVWOh ; func.00009098
│  ╎        0x000077c0      ldr x1, [arg_38h]
│  ╎        0x000077c4      ldr x0, [arg_40h]
│  ╎        0x000077c8      bl sym.imp.stringInterpolation__String_...cfC_ ; stringInterpolation__String(...cfC)
│  ╎        0x000077cc      mov x2, x0
│  ╎        0x000077d0      ldr x0, [var_48h]
│  ╎        0x000077d4      str x2, [arg_50h]
│  ╎        0x000077d8      str x1, [arg_58h]
│  ╎        0x000077dc      bl sym.imp.swift_errorRelease
│  ╎        0x000077e0      ldr x0, [var_48h]
│  ╎        0x000077e4      bl sym.imp.swift_errorRelease
│  ╎        0x000077e8      ldr x0, [arg_50h]
│  ╎        0x000077ec      ldr x1, [arg_58h]
│  ╎        0x000077f0      str x0, [arg_178h]
│  ╎        0x000077f4      str x1, [arg_180h]
└  └──────< 0x000077f8      b 0x7694
