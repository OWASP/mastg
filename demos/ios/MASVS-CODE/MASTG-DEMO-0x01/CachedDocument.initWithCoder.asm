            ; CALL XREF from func.000055d0 @ 0x55f4(x) ; sym.MASTestApp.CachedDocument.coder.allocator.SgSo7NSCoderC_tcfC
            ; CALL XREF from func.00005ad4 @ 0x5b00(x) ; method.CachedDocument.initWithCoder:
┌ 1228: NSCoder.allocator -> ()...tcfc..partial.apply (void *arg1);
│ `- args(x0) vars(67:sp[0x8..0x248])
│           0x00005608      stp x20, x19, [var_20h_2]!                 ; MASTestApp.CachedDocument.coder.allocator.SgSo7NSCoderC_tcfc
│           0x0000560c      stp x29, x30, [var_10h]
│           0x00005610      add x29, sp, 0x10
│           0x00005614      sub sp, sp, 0x230
│           0x00005618      str x0, [instance]                         ; arg1
│           0x0000561c      mov w8, 8
│           0x00005620      mov x1, x8
│           0x00005624      stur xzr, [x29, -0x20]
│           0x00005628      stur xzr, [x29, -0x90]
│           0x0000562c      stur xzr, [x29, -0x88]
│           0x00005630      stur xzr, [x29, -0x100]
│           0x00005634      stur xzr, [x29, -0xf8]
│           0x00005638      stur x0, [x29, -0x20]                      ; arg1
│           0x0000563c      stur x20, [x29, -0x18]
│           0x00005640      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│           0x00005644      add x0, x0, 0x41d                          ; 0x1a41d ; "fileName"
│           0x00005648      mov w8, 1
│           0x0000564c      and w2, w8, 1
│           0x00005650      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│           0x00005654      str x1, [arg0]
│           0x00005658      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│           0x0000565c      mov x1, x0
│           0x00005660      ldr x0, [arg0]                             ; void *arg0
│           0x00005664      str x1, [var_d0h]
│           0x00005668      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│           0x0000566c      ldr x0, [instance]                         ; void *instance
│           0x00005670      ldr x2, [var_d0h]
│           0x00005674      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x00005678      ldr x1, [x8, 0x3e0]                        ; [0x283e0:8]=0x1a293 str.decodeObjectForKey: ; reloc.fixup.decodeObjectForKey: ; char *selector
│           0x0000567c      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│           0x00005680      mov x29, x29
│           0x00005684      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x00005688      mov x8, x0
│           0x0000568c      ldr x0, [var_d0h]
│           0x00005690      str x8, [var_d8h]
│           0x00005694      adrp x8, segment.__DATA_CONST              ; 0x24000
│           0x00005698      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│                                                                      ; reloc.objc_release
│           0x0000569c      blr x8
│           0x000056a0      ldr x0, [var_d8h]
│       ┌─< 0x000056a4      cbz x0, 0x56e4
│      ┌──< 0x000056a8      b 0x56ac
│      ││   ; CODE XREF from func.00005608 @ 0x56a8(x)
│      └──> 0x000056ac      ldr x8, [var_d8h]
│       │   0x000056b0      str x8, [var_b8h]
│      ┌──< 0x000056b4      b 0x56b8
│      ││   ; CODE XREF from func.00005608 @ 0x56b4(x)
│      └──> 0x000056b8      ldr x0, [var_b8h]
│       │   0x000056bc      str x0, [var_b0h]
│       │   0x000056c0      add x8, sp, 0xe0
│       │   0x000056c4      str x8, [var_a8h]
│       │   0x000056c8      bl sym.imp._bridgeAnyObjectTo...B0yypyXlSgF
│       │   0x000056cc      ldr x0, [var_a8h]                          ; int64_t arg1
│       │   0x000056d0      sub x1, x29, 0x70                          ; int64_t arg2
│       │   0x000056d4      bl sym....sypWOb                           ; func.000047a0
│       │   0x000056d8      ldr x0, [var_b0h]
│       │   0x000056dc      bl sym.imp.swift_unknownObjectRelease
│      ┌──< 0x000056e0      b 0x56f8
│      ││   ; CODE XREF from func.00005608 @ 0x56a4(x)
│      │└─> 0x000056e4      stur xzr, [x29, -0x70]
│      │    0x000056e8      stur xzr, [x29, -0x68]
│      │    0x000056ec      stur xzr, [x29, -0x60]
│      │    0x000056f0      stur xzr, [x29, -0x58]
│      │┌─< 0x000056f4      b 0x56f8
│      ││   ; CODE XREFS from func.00005608 @ 0x56e0(x), 0x56f4(x)
│      └└─> 0x000056f8      ldur q0, [x29, -0x70]
│           0x000056fc      stur q0, [x29, -0x50]
│           0x00005700      ldur q0, [x29, -0x60]
│           0x00005704      stur q0, [x29, -0x40]
│           0x00005708      ldur x8, [x29, -0x38]
│       ┌─< 0x0000570c      cbnz x8, 0x5730
│      ┌──< 0x00005710      b 0x5714
│      ││   ; CODE XREF from func.00005608 @ 0x5710(x)
│      └──> 0x00005714      sub x0, x29, 0x50                          ; int64_t arg1
│       │   0x00005718      bl sym....sypSgWOh                         ; func.00004708
│       │   0x0000571c      mov x8, 0
│       │   0x00005720      mov x9, x8
│       │   0x00005724      str x9, [var_98h]
│       │   0x00005728      str x8, [var_a0h]
│      ┌──< 0x0000572c      b 0x579c
│      ││   ; CODE XREF from func.00005608 @ 0x570c(x)
│      │└─> 0x00005730      add x0, sp, 0x100
│      │    0x00005734      sub x1, x29, 0x50
│      │    0x00005738      adrp x8, segment.__DATA_CONST              ; 0x24000
│      │    0x0000573c      ldr x8, [x8, 0x8b8]                        ; [0x248b8:8]=0
│      │                                                               ; reloc....ypN
│      │    0x00005740      add x2, x8, 8
│      │    0x00005744      adrp x3, segment.__DATA_CONST              ; 0x24000
│      │    0x00005748      ldr x3, [x3, 0x690]                        ; [0x24690:8]=0
│      │                                                               ; reloc....SSN
│      │    0x0000574c      mov w8, 6
│      │    0x00005750      mov x4, x8
│      │    0x00005754      bl sym.imp.swift_dynamicCast
│      │┌─< 0x00005758      tbz w0, 0, 0x5774
│     ┌───< 0x0000575c      b 0x5760
│     │││   ; CODE XREF from func.00005608 @ 0x575c(x)
│     └───> 0x00005760      ldr x9, [var_100h]
│      ││   0x00005764      ldr x8, [var_10h_28]
│      ││   0x00005768      str x9, [var_88h]
│      ││   0x0000576c      str x8, [var_90h]
│     ┌───< 0x00005770      b 0x5788
│     │││   ; CODE XREF from func.00005608 @ 0x5758(x)
│     ││└─> 0x00005774      mov x8, 0
│     ││    0x00005778      mov x9, x8
│     ││    0x0000577c      str x9, [var_88h]
│     ││    0x00005780      str x8, [var_90h]
│     ││┌─< 0x00005784      b 0x5788
│     │││   ; CODE XREFS from func.00005608 @ 0x5770(x), 0x5784(x)
│     └─└─> 0x00005788      ldr x9, [var_88h]
│      │    0x0000578c      ldr x8, [var_90h]
│      │    0x00005790      str x9, [var_98h]
│      │    0x00005794      str x8, [var_a0h]
│      │┌─< 0x00005798      b 0x579c
│      ││   ; CODE XREFS from func.00005608 @ 0x572c(x), 0x5798(x)
│      └└─> 0x0000579c      ldr x9, [var_98h]
│           0x000057a0      ldr x8, [var_a0h]
│           0x000057a4      stur x9, [x29, -0x80]
│           0x000057a8      stur x8, [x29, -0x78]
│           0x000057ac      ldur x8, [x29, -0x78]
│       ┌─< 0x000057b0      cbz x8, 0x57cc
│      ┌──< 0x000057b4      b 0x57b8
│      ││   ; CODE XREF from func.00005608 @ 0x57b4(x)
│      └──> 0x000057b8      ldur x9, [x29, -0x80]
│       │   0x000057bc      ldur x8, [x29, -0x78]
│       │   0x000057c0      stur x9, [x29, -0x30]
│       │   0x000057c4      stur x8, [x29, -0x28]
│      ┌──< 0x000057c8      b 0x57fc
│      ││   ; CODE XREF from func.00005608 @ 0x57b0(x)
│      │└─> 0x000057cc      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│      │    0x000057d0      add x0, x0, 0x430                          ; 0x1a430 ; "offline_cache.txt"
│      │    0x000057d4      mov w8, 0x11
│      │    0x000057d8      mov x1, x8
│      │    0x000057dc      mov w8, 1
│      │    0x000057e0      and w2, w8, 1
│      │    0x000057e4      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│      │    0x000057e8      stur x0, [x29, -0x30]
│      │    0x000057ec      stur x1, [x29, -0x28]
│      │    0x000057f0      ldur x8, [x29, -0x78]
│      │┌─< 0x000057f4      cbz x8, 0x5894
│     ┌───< 0x000057f8      b 0x5898
│     │││   ; CODE XREFS from func.00005608 @ 0x57c8(x), 0x5894(x), 0x58a0(x)
│   ┌┌─└──> 0x000057fc      ldur x9, [x29, -0x30]
│   ╎╎│ │   0x00005800      str x9, [var_60h]
│   ╎╎│ │   0x00005804      ldur x8, [x29, -0x28]
│   ╎╎│ │   0x00005808      str x8, [var_68h]
│   ╎╎│ │   0x0000580c      stur x9, [x29, -0x90]
│   ╎╎│ │   0x00005810      stur x8, [x29, -0x88]
│   ╎╎│ │   0x00005814      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│   ╎╎│ │   0x00005818      add x0, x0, 0x426                          ; 0x1a426 ; "contents"
│   ╎╎│ │   0x0000581c      mov w8, 8
│   ╎╎│ │   0x00005820      mov x1, x8
│   ╎╎│ │   0x00005824      mov w8, 1
│   ╎╎│ │   0x00005828      and w2, w8, 1
│   ╎╎│ │   0x0000582c      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│   ╎╎│ │   0x00005830      str x1, [var_70h]
│   ╎╎│ │   0x00005834      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│   ╎╎│ │   0x00005838      mov x1, x0
│   ╎╎│ │   0x0000583c      ldr x0, [var_70h]                          ; void *arg0
│   ╎╎│ │   0x00005840      str x1, [var_78h]
│   ╎╎│ │   0x00005844      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│   ╎╎│ │   0x00005848      ldr x0, [instance]                         ; void *instance
│   ╎╎│ │   0x0000584c      ldr x2, [var_78h]
│   ╎╎│ │   0x00005850      adrp x8, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│   ╎╎│ │   0x00005854      ldr x1, [x8, 0x3e0]                        ; [0x283e0:8]=0x1a293 str.decodeObjectForKey: ; reloc.fixup.decodeObjectForKey: ; char *selector
│   ╎╎│ │   0x00005858      bl sym.imp.objc_msgSend                    ; void *objc_msgSend(void *instance, char *selector)
│   ╎╎│ │   0x0000585c      mov x29, x29
│   ╎╎│ │   0x00005860      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│   ╎╎│ │   0x00005864      mov x8, x0
│   ╎╎│ │   0x00005868      ldr x0, [var_78h]
│   ╎╎│ │   0x0000586c      str x8, [var_80h]
│   ╎╎│ │   0x00005870      adrp x8, segment.__DATA_CONST              ; 0x24000
│   ╎╎│ │   0x00005874      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   ╎╎│ │                                                              ; reloc.objc_release
│   ╎╎│ │   0x00005878      blr x8
│   ╎╎│ │   0x0000587c      ldr x0, [var_80h]
│   ╎╎│┌──< 0x00005880      cbz x0, 0x58d0
│  ┌──────< 0x00005884      b 0x5888
│  │╎╎│││   ; CODE XREF from func.00005608 @ 0x5884(x)
│  └──────> 0x00005888      ldr x8, [var_80h]
│   ╎╎│││   0x0000588c      str x8, [var_58h]
│  ┌──────< 0x00005890      b 0x58a4
│  ││╎│││   ; CODE XREF from func.00005608 @ 0x57f4(x)
│  │└───└─> 0x00005894      b 0x57fc
│  │ ╎││    ; CODE XREF from func.00005608 @ 0x57f8(x)
│  │ ╎└───> 0x00005898      sub x0, x29, 0x80                          ; void *arg1
│  │ ╎ │    0x0000589c      bl sym....sSSSgWOh                         ; func.00008b3c
│  │ └────< 0x000058a0      b 0x57fc
│  │   │    ; CODE XREF from func.00005608 @ 0x5890(x)
│  └──────> 0x000058a4      ldr x0, [var_58h]
│      │    0x000058a8      str x0, [var_50h]
│      │    0x000058ac      add x8, sp, 0x110
│      │    0x000058b0      str x8, [var_48h]
│      │    0x000058b4      bl sym.imp._bridgeAnyObjectTo...B0yypyXlSgF
│      │    0x000058b8      ldr x0, [var_48h]                          ; int64_t arg1
│      │    0x000058bc      sub x1, x29, 0xe0                          ; int64_t arg2
│      │    0x000058c0      bl sym....sypWOb                           ; func.000047a0
│      │    0x000058c4      ldr x0, [var_50h]
│      │    0x000058c8      bl sym.imp.swift_unknownObjectRelease
│      │┌─< 0x000058cc      b 0x58e4
│      ││   ; CODE XREF from func.00005608 @ 0x5880(x)
│      └──> 0x000058d0      stur xzr, [x29, -0xe0]
│       │   0x000058d4      stur xzr, [x29, -0xd8]
│       │   0x000058d8      stur xzr, [x29, -0xd0]
│       │   0x000058dc      stur xzr, [x29, -0xc8]
│      ┌──< 0x000058e0      b 0x58e4
│      ││   ; CODE XREFS from func.00005608 @ 0x58cc(x), 0x58e0(x)
│      └└─> 0x000058e4      ldur q0, [x29, -0xe0]
│           0x000058e8      stur q0, [x29, -0xc0]
│           0x000058ec      ldur q0, [x29, -0xd0]
│           0x000058f0      stur q0, [x29, -0xb0]
│           0x000058f4      ldur x8, [x29, -0xa8]
│       ┌─< 0x000058f8      cbnz x8, 0x591c
│      ┌──< 0x000058fc      b 0x5900
│      ││   ; CODE XREF from func.00005608 @ 0x58fc(x)
│      └──> 0x00005900      sub x0, x29, 0xc0                          ; int64_t arg1
│       │   0x00005904      bl sym....sypSgWOh                         ; func.00004708
│       │   0x00005908      mov x8, 0
│       │   0x0000590c      mov x9, x8
│       │   0x00005910      str x9, [var_38h]
│       │   0x00005914      str x8, [var_40h]
│      ┌──< 0x00005918      b 0x5988
│      ││   ; CODE XREF from func.00005608 @ 0x58f8(x)
│      │└─> 0x0000591c      add x0, sp, 0x130
│      │    0x00005920      sub x1, x29, 0xc0
│      │    0x00005924      adrp x8, segment.__DATA_CONST              ; 0x24000
│      │    0x00005928      ldr x8, [x8, 0x8b8]                        ; [0x248b8:8]=0
│      │                                                               ; reloc....ypN
│      │    0x0000592c      add x2, x8, 8
│      │    0x00005930      adrp x3, segment.__DATA_CONST              ; 0x24000
│      │    0x00005934      ldr x3, [x3, 0x690]                        ; [0x24690:8]=0
│      │                                                               ; reloc....SSN
│      │    0x00005938      mov w8, 6
│      │    0x0000593c      mov x4, x8
│      │    0x00005940      bl sym.imp.swift_dynamicCast
│      │┌─< 0x00005944      tbz w0, 0, 0x5960
│     ┌───< 0x00005948      b 0x594c
│     │││   ; CODE XREF from func.00005608 @ 0x5948(x)
│     └───> 0x0000594c      ldr x9, [var_130h]
│      ││   0x00005950      ldr x8, [var_138h]
│      ││   0x00005954      str x9, [var_28h]
│      ││   0x00005958      str x8, [var_30h]
│     ┌───< 0x0000595c      b 0x5974
│     │││   ; CODE XREF from func.00005608 @ 0x5944(x)
│     ││└─> 0x00005960      mov x8, 0
│     ││    0x00005964      mov x9, x8
│     ││    0x00005968      str x9, [var_28h]
│     ││    0x0000596c      str x8, [var_30h]
│     ││┌─< 0x00005970      b 0x5974
│     │││   ; CODE XREFS from func.00005608 @ 0x595c(x), 0x5970(x)
│     └─└─> 0x00005974      ldr x9, [var_28h]
│      │    0x00005978      ldr x8, [var_30h]
│      │    0x0000597c      str x9, [var_38h]
│      │    0x00005980      str x8, [var_40h]
│      │┌─< 0x00005984      b 0x5988
│      ││   ; CODE XREFS from func.00005608 @ 0x5918(x), 0x5984(x)
│      └└─> 0x00005988      ldr x9, [var_38h]
│           0x0000598c      ldr x8, [var_40h]
│           0x00005990      stur x9, [x29, -0xf0]
│           0x00005994      stur x8, [x29, -0xe8]
│           0x00005998      ldur x8, [x29, -0xe8]
│       ┌─< 0x0000599c      cbz x8, 0x59b8
│      ┌──< 0x000059a0      b 0x59a4
│      ││   ; CODE XREF from func.00005608 @ 0x59a0(x)
│      └──> 0x000059a4      ldur x9, [x29, -0xf0]
│       │   0x000059a8      ldur x8, [x29, -0xe8]
│       │   0x000059ac      stur x9, [x29, -0xa0]
│       │   0x000059b0      stur x8, [x29, -0x98]
│      ┌──< 0x000059b4      b 0x59e4
│      ││   ; CODE XREF from func.00005608 @ 0x599c(x)
│      │└─> 0x000059b8      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│      │    0x000059bc      add x0, x0, 0x920                          ; "__objc_classrefs__DATA_CONST"
│      │    0x000059c0      mov x1, 0
│      │    0x000059c4      mov w8, 1
│      │    0x000059c8      and w2, w8, 1
│      │    0x000059cc      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│      │    0x000059d0      stur x0, [x29, -0xa0]
│      │    0x000059d4      stur x1, [x29, -0x98]
│      │    0x000059d8      ldur x8, [x29, -0xe8]
│      │┌─< 0x000059dc      cbz x8, 0x5ac4
│     ┌───< 0x000059e0      b 0x5ac8
│     │││   ; CODE XREFS from func.00005608 @ 0x59b4(x), 0x5ac4(x), 0x5ad0(x)
│   ┌┌─└──> 0x000059e4      ldur x9, [x29, -0xa0]
│   ╎╎│ │   0x000059e8      str x9, [var_8h]
│   ╎╎│ │   0x000059ec      ldur x8, [x29, -0x98]
│   ╎╎│ │   0x000059f0      str x8, [var_18h]
│   ╎╎│ │   0x000059f4      stur x9, [x29, -0x100]
│   ╎╎│ │   0x000059f8      stur x8, [x29, -0xf8]
│   ╎╎│ │   0x000059fc      ldur x0, [x29, -0x18]
│   ╎╎│ │   0x00005a00      bl sym.imp.swift_getObjectType
│   ╎╎│ │   0x00005a04      mov x20, x0
│   ╎╎│ │   0x00005a08      ldr x0, [var_68h]                          ; void *arg0
│   ╎╎│ │   0x00005a0c      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│   ╎╎│ │   0x00005a10      ldr x0, [var_18h]                          ; void *arg0
│   ╎╎│ │   0x00005a14      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│   ╎╎│ │   0x00005a18      ldr x0, [var_60h]
│   ╎╎│ │   0x00005a1c      ldr x2, [var_8h]
│   ╎╎│ │   0x00005a20      ldr x3, [var_18h]
│   ╎╎│ │   0x00005a24      ldr x1, [var_68h]
│   ╎╎│ │   0x00005a28      ldr x8, [x20, 0x60]
│   ╎╎│ │   0x00005a2c      blr x8
│   ╎╎│ │   0x00005a30      str x0, [var_20h]
│   ╎╎│ │   0x00005a34      ldur x8, [x29, -0x18]
│   ╎╎│ │   0x00005a38      str x8, [var_10h_2]
│   ╎╎│ │   0x00005a3c      adrp x8, segment.__DATA_CONST              ; 0x24000
│   ╎╎│ │   0x00005a40      ldr x8, [x8, 0x1a0]                        ; [0x241a0:8]=0
│   ╎╎│ │                                                              ; reloc.objc_retain
│   ╎╎│ │   0x00005a44      blr x8
│   ╎╎│ │   0x00005a48      ldr x8, [var_20h]
│   ╎╎│ │   0x00005a4c      ldr x0, [var_10h_2]
│   ╎╎│ │   0x00005a50      stur x8, [x29, -0x18]
│   ╎╎│ │   0x00005a54      bl sym.imp.swift_getObjectType
│   ╎╎│ │   0x00005a58      mov x1, x0
│   ╎╎│ │   0x00005a5c      ldr x0, [var_10h_2]
│   ╎╎│ │   0x00005a60      mov w8, 0x28                               ; '('
│   ╎╎│ │   0x00005a64      mov x2, x8
│   ╎╎│ │   0x00005a68      mov w8, 7
│   ╎╎│ │   0x00005a6c      mov x3, x8
│   ╎╎│ │   0x00005a70      bl sym.imp.swift_deallocPartialClassInstance
│   ╎╎│ │   0x00005a74      ldur x20, [x29, -0x18]
│   ╎╎│ │   0x00005a78      bl sym.MASTestApp.CachedDocument.restoreToDisk.allocator_...E8AB2C58CE173A727EF27CB85DF8CD8LLyyF_ ; func.00005b14
│   ╎╎│ │   0x00005a7c      ldr x0, [var_18h]                          ; void *arg0
│   ╎╎│ │   0x00005a80      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│   ╎╎│ │   0x00005a84      ldr x0, [var_68h]                          ; void *arg0
│   ╎╎│ │   0x00005a88      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│   ╎╎│ │   0x00005a8c      ldr x0, [instance]
│   ╎╎│ │   0x00005a90      adrp x8, segment.__DATA_CONST              ; 0x24000
│   ╎╎│ │   0x00005a94      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   ╎╎│ │                                                              ; reloc.objc_release
│   ╎╎│ │   0x00005a98      blr x8
│   ╎╎│ │   0x00005a9c      ldur x0, [x29, -0x18]
│   ╎╎│ │   0x00005aa0      adrp x8, segment.__DATA_CONST              ; 0x24000
│   ╎╎│ │   0x00005aa4      ldr x8, [x8, 0x198]                        ; [0x24198:8]=0
│   ╎╎│ │                                                              ; reloc.objc_release
│   ╎╎│ │   0x00005aa8      blr x8
│   ╎╎│ │   0x00005aac      ldr x0, [var_20h]
│   ╎╎│ │   0x00005ab0      nop
│   ╎╎│ │   0x00005ab4      add sp, sp, 0x230
│   ╎╎│ │   0x00005ab8      ldp x29, x30, [var_10h]
│   ╎╎│ │   0x00005abc      ldp x20, x19, [sp], 0x20
│   ╎╎│ │   0x00005ac0      ret
│   │╎│ │   ; CODE XREF from func.00005608 @ 0x59dc(x)
│   └───└─> 0x00005ac4      b 0x59e4
│    ╎│     ; CODE XREF from func.00005608 @ 0x59e0(x)
│    ╎└───> 0x00005ac8      sub x0, x29, 0xf0                          ; void *arg1
│    ╎      0x00005acc      bl sym....sSSSgWOh                         ; func.00008b3c
└    └────< 0x00005ad0      b 0x59e4
