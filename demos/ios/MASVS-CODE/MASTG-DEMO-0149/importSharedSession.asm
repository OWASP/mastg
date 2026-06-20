            ; CALL XREF from func.000063d4 @ 0x65ac(x) ; sym.MASTestApp.MastgTest.mastg.completion_...FZ_
┌ 2360: sym.MASTestApp.MastgTest.importSharedSession.from.Foundation.URL...V_tFZ (int64_t arg1, int64_t arg_10h, int64_t arg_20h);
│ `- args(x0, sp[0x10..0x20]) vars(86:sp[0x8..0x2a0])
│           0x00006644      stp x22, x21, [var_30h]!                   ; MASTestApp.MastgTest.importSharedSession.from.Foundation.URL...V_tFZ
│           0x00006648      stp x20, x19, [var_10h]
│           0x0000664c      stp x29, x30, [var_20h]
│           0x00006650      add x29, sp, 0x20
│           0x00006654      sub sp, sp, 0x270
│           0x00006658      sub x8, x29, 0x20
│           0x0000665c      stur x0, [x8, -0x100]                      ; arg1
│           0x00006660      stur xzr, [x29, -0x28]
│           0x00006664      stur xzr, [x29, -0x30]
│           0x00006668      stur xzr, [x29, -0x40]
│           0x0000666c      stur xzr, [x29, -0x38]
│           0x00006670      stur xzr, [x29, -0x50]
│           0x00006674      stur xzr, [x29, -0x48]
│           0x00006678      mov x0, 0
│           0x0000667c      sub x8, x29, 0x40
│           0x00006680      stur x0, [x8, -0x100]
│           0x00006684      adrp x0, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x00006688      add x0, x0, 0x7b0                          ; int64_t arg1
│           0x0000668c      adrp x1, 0x1b000
│           0x00006690      add x1, x1, 0x9a0                          ; int64_t arg2
│           0x00006694      bl sym.___swift_instantiateConcreteTypeFromMangledNameV2
│           0x00006698      ldur x8, [x0, -8]                          ; [0x287a8:8]=0
│                                                                      ; sym....sS2SSysWL
│                                                                      ...sS2SSysWL
│           0x0000669c      ldr x8, [x8, 0x40]
│           0x000066a0      lsr x8, x8, 0
│           0x000066a4      add x8, x8, 0xf
│           0x000066a8      and x9, x8, 0xfffffffffffffff0
│           0x000066ac      sub x8, x29, 0x38
│           0x000066b0      stur x9, [x8, -0x100]
│           0x000066b4      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x000066b8      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x000066bc      blr x16
│           0x000066c0      sub x8, x29, 0x38
│           0x000066c4      ldur x9, [x8, -0x100]
│           0x000066c8      mov x8, sp
│           0x000066cc      subs x0, x8, x9
│           0x000066d0      sub x8, x29, 0x30
│           0x000066d4      stur x0, [x8, -0x100]
│           0x000066d8      mov sp, x0
│           0x000066dc      adrp x0, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│           0x000066e0      add x0, x0, 0x7b8                          ; int64_t arg1
│           0x000066e4      adrp x1, 0x1b000
│           0x000066e8      add x1, x1, 0x9a8                          ; int64_t arg2
│           0x000066ec      bl sym.___swift_instantiateConcreteTypeFromMangledNameV2
│           0x000066f0      ldur x8, [x0, -8]                          ; [0x287b0:8]=0
│                                                                      ; sym.Foundation.URLQueryItem:_GenericAccessorM__1
│                                                                      Foundation.URLQueryItem: GenericAccessorM
│           0x000066f4      ldr x8, [x8, 0x40]
│           0x000066f8      lsr x8, x8, 0
│           0x000066fc      add x8, x8, 0xf
│           0x00006700      and x9, x8, 0xfffffffffffffff0
│           0x00006704      sub x8, x29, 0x28
│           0x00006708      stur x9, [x8, -0x100]
│           0x0000670c      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x00006710      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x00006714      blr x16
│           0x00006718      sub x8, x29, 0x28
│           0x0000671c      ldur x9, [x8, -0x100]
│           0x00006720      mov x8, sp
│           0x00006724      subs x0, x8, x9
│           0x00006728      stur x0, [x29, -0x100]
│           0x0000672c      mov sp, x0
│           0x00006730      mov x0, 0
│           0x00006734      bl sym.imp.Foundation.URLComponents...VMa
│           0x00006738      ldur x8, [x29, -0x100]
│           0x0000673c      mov x9, x0
│           0x00006740      sub x10, x29, 0x20
│           0x00006744      ldur x0, [x10, -0x100]
│           0x00006748      stur x9, [x29, -0xf0]
│           0x0000674c      ldur x9, [x9, -8]
│           0x00006750      sub x10, x29, 8
│           0x00006754      stur x9, [x10, -0x100]
│           0x00006758      ldr x9, [x9, 0x40]
│           0x0000675c      lsr x9, x9, 0
│           0x00006760      add x9, x9, 0xf
│           0x00006764      and x9, x9, 0xfffffffffffffff0
│           0x00006768      sub x10, x29, 0x18
│           0x0000676c      stur x9, [x10, -0x100]
│           0x00006770      adrp x16, segment.__DATA_CONST             ; 0x24000
│           0x00006774      ldr x16, [x16, 0x1b0]                      ; [0x241b0:8]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x00006778      blr x16
│           0x0000677c      sub x9, x29, 0x18
│           0x00006780      ldur x10, [x9, -0x100]
│           0x00006784      mov x9, sp
│           0x00006788      subs x9, x9, x10
│           0x0000678c      sub x10, x29, 0x10
│           0x00006790      stur x9, [x10, -0x100]
│           0x00006794      mov sp, x9
│           0x00006798      stur x9, [x29, -0x28]
│           0x0000679c      mov x9, x0
│           0x000067a0      stur x9, [x29, -0x30]
│           0x000067a4      mov w9, 0
│           0x000067a8      mov w10, 1
│           0x000067ac      stur w10, [x29, -0xf4]
│           0x000067b0      and w1, w9, 1
│           0x000067b4      bl sym.imp.Foundation.URLComponents.url.resolvingAgainstBaseURL...A0G0Vh_SbtcfC
│           0x000067b8      sub x8, x29, 8
│           0x000067bc      ldur x8, [x8, -0x100]
│           0x000067c0      ldur x0, [x29, -0x100]
│           0x000067c4      ldur w1, [x29, -0xf4]
│           0x000067c8      ldur x2, [x29, -0xf0]
│           0x000067cc      ldr x8, [x8, 0x30]
│           0x000067d0      blr x8
│           0x000067d4      subs w8, w0, 1
│       ┌─< 0x000067d8      b.ne 0x67ec
│      ┌──< 0x000067dc      b 0x67e0
│      ││   ; CODE XREF from func.00006644 @ 0x67dc(x)
│      └──> 0x000067e0      ldur x0, [x29, -0x100]                     ; int64_t arg1
│       │   0x000067e4      bl sym.Foundation.URLComponents:_GenericAccessorW.bool____GenericAccessor ; func.00009030
│      ┌──< 0x000067e8      b 0x6f24
│      ││   ; CODE XREF from func.00006644 @ 0x67d8(x)
│      │└─> 0x000067ec      sub x8, x29, 0x10
│      │    0x000067f0      ldur x20, [x8, -0x100]
│      │    0x000067f4      ldur x2, [x29, -0xf0]
│      │    0x000067f8      ldur x1, [x29, -0x100]
│      │    0x000067fc      sub x8, x29, 8
│      │    0x00006800      ldur x8, [x8, -0x100]
│      │    0x00006804      ldr x8, [x8, 0x20]
│      │    0x00006808      mov x0, x20
│      │    0x0000680c      blr x8
│      │    0x00006810      bl sym.imp.Foundation.URLComponents.queryItems.URLQueryItem...VGSgvg
│      │    0x00006814      sub x8, x29, 0x48
│      │    0x00006818      stur x0, [x8, -0x100]
│      │┌─< 0x0000681c      cbz x0, 0x6838
│     ┌───< 0x00006820      b 0x6824
│     │││   ; CODE XREF from func.00006644 @ 0x6820(x)
│     └───> 0x00006824      sub x8, x29, 0x48
│      ││   0x00006828      ldur x8, [x8, -0x100]
│      ││   0x0000682c      sub x9, x29, 0x50
│      ││   0x00006830      stur x8, [x9, -0x100]
│     ┌───< 0x00006834      b 0x683c
│     │││   ; CODE XREF from func.00006644 @ 0x681c(x)
│    ┌──└─> 0x00006838      b 0x6f08
│    │││    ; CODE XREF from func.00006644 @ 0x6834(x)
│    │└───> 0x0000683c      sub x8, x29, 0x40
│    │ │    0x00006840      ldur x21, [x8, -0x100]
│    │ │    0x00006844      sub x8, x29, 0x50
│    │ │    0x00006848      ldur x8, [x8, -0x100]
│    │ │    0x0000684c      sub x20, x29, 0xe8
│    │ │    0x00006850      stur x8, [x29, -0xe8]
│    │ │    0x00006854      adrp x0, sym.__PROTOCOLS__TtC10MASTestApp19InsecureUserSession ; 0x28000
│    │ │    0x00006858      add x0, x0, 0x7c0                          ; int64_t arg1
│    │ │    0x0000685c      adrp x1, 0x1b000
│    │ │    0x00006860      add x1, x1, 0x9b0                          ; int64_t arg2
│    │ │    0x00006864      bl sym.___swift_instantiateConcreteTypeFromMangledNameV2
│    │ │    0x00006868      sub x8, x29, 0x58
│    │ │    0x0000686c      stur x0, [x8, -0x100]
│    │ │    0x00006870      bl sym....sSay10Foundation12URLQueryItemVGSayxGSTsWl ; func.00009138
│    │ │    0x00006874      sub x8, x29, 0x30
│    │ │    0x00006878      ldur x8, [x8, -0x100]
│    │ │    0x0000687c      sub x9, x29, 0x58
│    │ │    0x00006880      ldur x2, [x9, -0x100]
│    │ │    0x00006884      mov x3, x0
│    │ │    0x00006888      adrp x0, 0x6000
│    │ │    0x0000688c      add x0, x0, 0xf7c
│    │ │    0x00006890      mov x1, 0
│    │ │    0x00006894      bl sym.imp.first.where.Element_...KF_      ; first.where.Element(...KF)
│    │ │┌─< 0x00006898      cbnz x21, 0x6f78
│    │┌───< 0x0000689c      b 0x68a0
│    ││││   ; CODE XREF from func.00006644 @ 0x689c(x)
│    │└───> 0x000068a0      sub x0, x29, 0xe8                          ; void *arg1
│    │ ││   0x000068a4      bl sym....sSay10Foundation12URLQueryItemVGWOh ; func.000091ac
│    │ ││   0x000068a8      mov x0, 0
│    │ ││   0x000068ac      bl sym.imp.Foundation.URLQueryItem...VMa
│    │ ││   0x000068b0      mov x2, x0
│    │ ││   0x000068b4      sub x8, x29, 0x30
│    │ ││   0x000068b8      ldur x0, [x8, -0x100]
│    │ ││   0x000068bc      sub x8, x29, 0x68
│    │ ││   0x000068c0      stur x2, [x8, -0x100]
│    │ ││   0x000068c4      ldur x8, [x2, -8]
│    │ ││   0x000068c8      sub x9, x29, 0x60
│    │ ││   0x000068cc      stur x8, [x9, -0x100]
│    │ ││   0x000068d0      ldr x8, [x8, 0x30]
│    │ ││   0x000068d4      mov w1, 1
│    │ ││   0x000068d8      blr x8
│    │ ││   0x000068dc      subs w8, w0, 1
│    │┌───< 0x000068e0      b.ne 0x68f8
│   ┌─────< 0x000068e4      b 0x68e8
│   │││││   ; CODE XREF from func.00006644 @ 0x68e4(x)
│   └─────> 0x000068e8      sub x8, x29, 0x30
│    ││││   0x000068ec      ldur x0, [x8, -0x100]                      ; int64_t arg1
│    ││││   0x000068f0      bl sym.Foundation.URLQueryItem:_GenericAccessorW.bool____GenericAccessor ; func.000091d4
│   ┌─────< 0x000068f4      b 0x6f08
│   │││││   ; CODE XREF from func.00006644 @ 0x68e0(x)
│   ││└───> 0x000068f8      sub x8, x29, 0x30
│   ││ ││   0x000068fc      ldur x20, [x8, -0x100]
│   ││ ││   0x00006900      bl sym.imp.Foundation.URLQueryItem.value_...Sgvg_ ; Foundation.URLQueryItem.value(...Sgvg)
│   ││ ││   0x00006904      sub x8, x29, 0x60
│   ││ ││   0x00006908      ldur x8, [x8, -0x100]
│   ││ ││   0x0000690c      mov x2, x0
│   ││ ││   0x00006910      sub x9, x29, 0x30
│   ││ ││   0x00006914      ldur x0, [x9, -0x100]
│   ││ ││   0x00006918      sub x9, x29, 0x88
│   ││ ││   0x0000691c      stur x2, [x9, -0x100]
│   ││ ││   0x00006920      mov x2, x1
│   ││ ││   0x00006924      sub x9, x29, 0x68
│   ││ ││   0x00006928      ldur x1, [x9, -0x100]
│   ││ ││   0x0000692c      sub x9, x29, 0x80
│   ││ ││   0x00006930      stur x2, [x9, -0x100]
│   ││ ││   0x00006934      ldr x8, [x8, 8]
│   ││ ││   0x00006938      blr x8
│   ││ ││   0x0000693c      sub x8, x29, 0x88
│   ││ ││   0x00006940      ldur x9, [x8, -0x100]
│   ││ ││   0x00006944      sub x8, x29, 0x80
│   ││ ││   0x00006948      ldur x8, [x8, -0x100]
│   ││ ││   0x0000694c      sub x10, x29, 0x78
│   ││ ││   0x00006950      stur x9, [x10, -0x100]
│   ││ ││   0x00006954      sub x9, x29, 0x70
│   ││ ││   0x00006958      stur x8, [x9, -0x100]
│   ││┌───< 0x0000695c      b 0x6960
│   │││││   ; CODE XREFS from func.00006644 @ 0x695c(x), 0x6f20(x)
│  ┌──└───> 0x00006960      sub x8, x29, 0x78
│  ╎││ ││   0x00006964      ldur x9, [x8, -0x100]
│  ╎││ ││   0x00006968      sub x8, x29, 0x70
│  ╎││ ││   0x0000696c      ldur x8, [x8, -0x100]
│  ╎││ ││   0x00006970      sub x10, x29, 0x98
│  ╎││ ││   0x00006974      stur x8, [x10, -0x100]
│  ╎││ ││   0x00006978      sub x10, x29, 0x90
│  ╎││ ││   0x0000697c      stur x9, [x10, -0x100]
│  ╎││┌───< 0x00006980      cbz x8, 0x69ac
│ ┌───────< 0x00006984      b 0x6988
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6984(x)
│ └───────> 0x00006988      sub x8, x29, 0x90
│  ╎│││││   0x0000698c      ldur x9, [x8, -0x100]
│  ╎│││││   0x00006990      sub x8, x29, 0x98
│  ╎│││││   0x00006994      ldur x8, [x8, -0x100]
│  ╎│││││   0x00006998      sub x10, x29, 0xa8
│  ╎│││││   0x0000699c      stur x9, [x10, -0x100]
│  ╎│││││   0x000069a0      sub x9, x29, 0xa0
│  ╎│││││   0x000069a4      stur x8, [x9, -0x100]
│ ┌───────< 0x000069a8      b 0x69cc
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6980(x)
│ │╎││└───> 0x000069ac      ldur x1, [x29, -0xf0]
│ │╎││ ││   0x000069b0      sub x8, x29, 0x10
│ │╎││ ││   0x000069b4      ldur x0, [x8, -0x100]
│ │╎││ ││   0x000069b8      sub x8, x29, 8
│ │╎││ ││   0x000069bc      ldur x8, [x8, -0x100]
│ │╎││ ││   0x000069c0      ldr x8, [x8, 8]
│ │╎││ ││   0x000069c4      blr x8
│ │╎││┌───< 0x000069c8      b 0x6f24
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x69a8(x)
│ └───────> 0x000069cc      sub x8, x29, 0xa8
│  ╎│││││   0x000069d0      ldur x9, [x8, -0x100]
│  ╎│││││   0x000069d4      sub x8, x29, 0xa0
│  ╎│││││   0x000069d8      ldur x8, [x8, -0x100]
│  ╎│││││   0x000069dc      sub x10, x29, 0xc8
│  ╎│││││   0x000069e0      stur x8, [x10, -0x100]
│  ╎│││││   0x000069e4      sub x10, x29, 0xc0
│  ╎│││││   0x000069e8      stur x9, [x10, -0x100]
│  ╎│││││   0x000069ec      stur x9, [x29, -0x40]
│  ╎│││││   0x000069f0      stur x8, [x29, -0x38]
│  ╎│││││   0x000069f4      bl sym.Foundation.Data.base64Encoded.options.NSDataBase64DecodingOptions...A0_ ; func.00007014
│  ╎│││││   0x000069f8      sub x8, x29, 0xc8
│  ╎│││││   0x000069fc      ldur x1, [x8, -0x100]
│  ╎│││││   0x00006a00      mov x2, x0
│  ╎│││││   0x00006a04      sub x8, x29, 0xc0
│  ╎│││││   0x00006a08      ldur x0, [x8, -0x100]
│  ╎│││││   0x00006a0c      bl sym.imp.Foundation.Data.base64Encoded.options.NSDataBase64DecodingOptions...VtcfC
│  ╎│││││   0x00006a10      sub x8, x29, 0xb8
│  ╎│││││   0x00006a14      stur x0, [x8, -0x100]
│  ╎│││││   0x00006a18      sub x8, x29, 0xb0
│  ╎│││││   0x00006a1c      stur x1, [x8, -0x100]
│  ╎│││││   0x00006a20      mov x9, -0x1000000000000000
│  ╎│││││   0x00006a24      and x8, x1, 0xf000000000000000
│  ╎│││││   0x00006a28      subs x8, x8, x9
│ ┌───────< 0x00006a2c      b.eq 0x6a58
│ ────────< 0x00006a30      b 0x6a34
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6a30(x)
│ ────────> 0x00006a34      sub x8, x29, 0xb0
│ │╎│││││   0x00006a38      ldur x8, [x8, -0x100]
│ │╎│││││   0x00006a3c      sub x9, x29, 0xb8
│ │╎│││││   0x00006a40      ldur x9, [x9, -0x100]
│ │╎│││││   0x00006a44      sub x10, x29, 0xd8
│ │╎│││││   0x00006a48      stur x9, [x10, -0x100]
│ │╎│││││   0x00006a4c      sub x9, x29, 0xd0
│ │╎│││││   0x00006a50      stur x8, [x9, -0x100]
│ ────────< 0x00006a54      b 0x6a84
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6a2c(x)
│ └───────> 0x00006a58      sub x8, x29, 0xc8
│  ╎│││││   0x00006a5c      ldur x0, [x8, -0x100]                      ; void *arg0
│  ╎│││││   0x00006a60      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│  ╎│││││   0x00006a64      sub x8, x29, 8
│  ╎│││││   0x00006a68      ldur x8, [x8, -0x100]
│  ╎│││││   0x00006a6c      sub x9, x29, 0x10
│  ╎│││││   0x00006a70      ldur x0, [x9, -0x100]
│  ╎│││││   0x00006a74      ldur x1, [x29, -0xf0]
│  ╎│││││   0x00006a78      ldr x8, [x8, 8]
│  ╎│││││   0x00006a7c      blr x8
│ ┌───────< 0x00006a80      b 0x6f24
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6a54(x)
│ ────────> 0x00006a84      sub x8, x29, 0xd8
│ │╎│││││   0x00006a88      ldur x9, [x8, -0x100]
│ │╎│││││   0x00006a8c      sub x8, x29, 0xd0
│ │╎│││││   0x00006a90      ldur x8, [x8, -0x100]
│ │╎│││││   0x00006a94      sub x10, x29, 0xf0
│ │╎│││││   0x00006a98      stur x8, [x10, -0x100]
│ │╎│││││   0x00006a9c      sub x10, x29, 0xe8
│ │╎│││││   0x00006aa0      stur x9, [x10, -0x100]
│ │╎│││││   0x00006aa4      stur x9, [x29, -0x50]
│ │╎│││││   0x00006aa8      stur x8, [x29, -0x48]
│ │╎│││││   0x00006aac      mov w8, 0x5c                               ; '\\'
│ │╎│││││   0x00006ab0      mov x0, x8
│ │╎│││││   0x00006ab4      mov w8, 4
│ │╎│││││   0x00006ab8      mov x1, x8
│ │╎│││││   0x00006abc      bl sym.imp.DefaultStringInterpolation.literalCapacity.interpolationCount_...itcfC_ ; DefaultStringInterpolation.literalCapacity.interpolationCount(...itcfC)
│ │╎│││││   0x00006ac0      sub x20, x29, 0x60
│ │╎│││││   0x00006ac4      stur x0, [x29, -0x60]
│ │╎│││││   0x00006ac8      stur x1, [x29, -0x58]
│ │╎│││││   0x00006acc      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006ad0      add x0, x0, 0x540                          ; 0x1a540 ; "Imported a session from a deep link ("
│ │╎│││││   0x00006ad4      mov w8, 0x25                               ; '%'
│ │╎│││││   0x00006ad8      mov x1, x8
│ │╎│││││   0x00006adc      mov w8, 1
│ │╎│││││   0x00006ae0      and w2, w8, 1
│ │╎│││││   0x00006ae4      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006ae8      sub x8, x29, 0xe0
│ │╎│││││   0x00006aec      stur x1, [x8, -0x100]
│ │╎│││││   0x00006af0      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│ │╎│││││   0x00006af4      sub x8, x29, 0x20
│ │╎│││││   0x00006af8      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006afc      sub x8, x29, 0xe0
│ │╎│││││   0x00006b00      ldur x0, [x8, -0x100]                      ; void *arg0
│ │╎│││││   0x00006b04      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│ │╎│││││   0x00006b08      bl sym.imp.Foundation.URL.scheme_...Sgvg_  ; Foundation.URL.scheme(...Sgvg)
│ │╎│││││   0x00006b0c      stur x0, [x29, -0x80]
│ │╎│││││   0x00006b10      stur x1, [x29, -0x78]
│ │╎│││││   0x00006b14      ldur x8, [x29, -0x78]
│ ────────< 0x00006b18      cbz x8, 0x6b34
│ ────────< 0x00006b1c      b 0x6b20
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6b1c(x)
│ ────────> 0x00006b20      ldur x9, [x29, -0x80]
│ │╎│││││   0x00006b24      ldur x8, [x29, -0x78]
│ │╎│││││   0x00006b28      stur x9, [x29, -0x70]
│ │╎│││││   0x00006b2c      stur x8, [x29, -0x68]
│ ────────< 0x00006b30      b 0x6b60
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6b18(x)
│ ────────> 0x00006b34      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006b38      add x0, x0, 0x920                          ; "__objc_classrefs__DATA_CONST"
│ │╎│││││   0x00006b3c      mov x1, 0
│ │╎│││││   0x00006b40      mov w8, 1
│ │╎│││││   0x00006b44      and w2, w8, 1
│ │╎│││││   0x00006b48      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006b4c      stur x0, [x29, -0x70]
│ │╎│││││   0x00006b50      stur x1, [x29, -0x68]
│ │╎│││││   0x00006b54      ldur x8, [x29, -0x78]
│ ────────< 0x00006b58      cbz x8, 0x6c0c
│ ────────< 0x00006b5c      b 0x6c10
│ │╎│││││   ; CODE XREFS from func.00006644 @ 0x6b30(x), 0x6c0c(x), 0x6c18(x)
│ ────────> 0x00006b60      ldur x9, [x29, -0x70]
│ │╎│││││   0x00006b64      ldur x8, [x29, -0x68]
│ │╎│││││   0x00006b68      sub x0, x29, 0x90
│ │╎│││││   0x00006b6c      sub x10, x29, 0x100
│ │╎│││││   0x00006b70      stur x0, [x10, -0x100]
│ │╎│││││   0x00006b74      stur x9, [x29, -0x90]
│ │╎│││││   0x00006b78      stur x8, [x29, -0x88]
│ │╎│││││   0x00006b7c      adrp x1, segment.__DATA_CONST              ; 0x24000
│ │╎│││││   0x00006b80      ldr x1, [x1, 0x690]                        ; [0x24690:8]=0
│ │╎│││││                                                              ; reloc....SSN
│ │╎│││││   0x00006b84      adrp x2, segment.__DATA_CONST              ; 0x24000
│ │╎│││││   0x00006b88      ldr x2, [x2, 0x6b8]
│ │╎│││││   0x00006b8c      adrp x3, segment.__DATA_CONST              ; 0x24000
│ │╎│││││   0x00006b90      ldr x3, [x3, 0x6b0]                        ; [0x246b0:8]=0
│ │╎│││││                                                              ; reloc.TextOutputStreamable.setter_...P_
│ │╎│││││   0x00006b94      sub x20, x29, 0x60
│ │╎│││││   0x00006b98      sub x8, x29, 0x108
│ │╎│││││   0x00006b9c      stur x20, [x8, -0x100]
│ │╎│││││   0x00006ba0      bl sym.imp.DefaultStringInterpolation.append...C0yyxs06CustomB11ConvertibleRzs20TextOutputStreamableRzlF
│ │╎│││││   0x00006ba4      sub x8, x29, 0x108
│ │╎│││││   0x00006ba8      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006bac      sub x8, x29, 0x100
│ │╎│││││   0x00006bb0      ldur x0, [x8, -0x100]                      ; int64_t arg1
│ │╎│││││   0x00006bb4      bl sym....sSSWOh                           ; func.00008a08
│ │╎│││││   0x00006bb8      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006bbc      add x0, x0, 0x566
│ │╎│││││   0x00006bc0      mov w8, 3
│ │╎│││││   0x00006bc4      mov x1, x8
│ │╎│││││   0x00006bc8      mov w8, 1
│ │╎│││││   0x00006bcc      and w2, w8, 1
│ │╎│││││   0x00006bd0      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006bd4      sub x8, x29, 0xf8
│ │╎│││││   0x00006bd8      stur x1, [x8, -0x100]
│ │╎│││││   0x00006bdc      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│ │╎│││││   0x00006be0      sub x8, x29, 0x20
│ │╎│││││   0x00006be4      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006be8      sub x8, x29, 0xf8
│ │╎│││││   0x00006bec      ldur x0, [x8, -0x100]                      ; void *arg0
│ │╎│││││   0x00006bf0      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│ │╎│││││   0x00006bf4      bl sym.imp.Foundation.URL.host_...Sgvg_    ; Foundation.URL.host(...Sgvg)
│ │╎│││││   0x00006bf8      stur x0, [x29, -0xb0]
│ │╎│││││   0x00006bfc      stur x1, [x29, -0xa8]
│ │╎│││││   0x00006c00      ldur x8, [x29, -0xa8]
│ ────────< 0x00006c04      cbz x8, 0x6c30
│ ────────< 0x00006c08      b 0x6c1c
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6b58(x)
│ ────────> 0x00006c0c      b 0x6b60
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6b5c(x)
│ ────────> 0x00006c10      sub x0, x29, 0x80                          ; void *arg1
│ │╎│││││   0x00006c14      bl sym....sSSSgWOh                         ; func.00008b3c
│ ────────< 0x00006c18      b 0x6b60
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6c08(x)
│ ────────> 0x00006c1c      ldur x9, [x29, -0xb0]
│ │╎│││││   0x00006c20      ldur x8, [x29, -0xa8]
│ │╎│││││   0x00006c24      stur x9, [x29, -0xa0]
│ │╎│││││   0x00006c28      stur x8, [x29, -0x98]
│ ────────< 0x00006c2c      b 0x6c5c
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6c04(x)
│ ────────> 0x00006c30      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006c34      add x0, x0, 0x920                          ; "__objc_classrefs__DATA_CONST"
│ │╎│││││   0x00006c38      mov x1, 0
│ │╎│││││   0x00006c3c      mov w8, 1
│ │╎│││││   0x00006c40      and w2, w8, 1
│ │╎│││││   0x00006c44      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006c48      stur x0, [x29, -0xa0]
│ │╎│││││   0x00006c4c      stur x1, [x29, -0x98]
│ │╎│││││   0x00006c50      ldur x8, [x29, -0xa8]
│ ────────< 0x00006c54      cbz x8, 0x6ef8
│ ────────< 0x00006c58      b 0x6efc
│ │╎│││││   ; CODE XREFS from func.00006644 @ 0x6c2c(x), 0x6ef8(x), 0x6f04(x)
│ ────────> 0x00006c5c      ldur x9, [x29, -0xa0]
│ │╎│││││   0x00006c60      ldur x8, [x29, -0x98]
│ │╎│││││   0x00006c64      sub x0, x29, 0xc0
│ │╎│││││   0x00006c68      sub x10, x29, 0x190
│ │╎│││││   0x00006c6c      stur x0, [x10, -0x100]
│ │╎│││││   0x00006c70      stur x9, [x29, -0xc0]
│ │╎│││││   0x00006c74      stur x8, [x29, -0xb8]
│ │╎│││││   0x00006c78      adrp x1, segment.__DATA_CONST              ; 0x24000
│ │╎│││││   0x00006c7c      ldr x1, [x1, 0x690]                        ; [0x24690:8]=0
│ │╎│││││                                                              ; reloc....SSN
│ │╎│││││   0x00006c80      sub x8, x29, 0x160
│ │╎│││││   0x00006c84      stur x1, [x8, -0x100]
│ │╎│││││   0x00006c88      adrp x2, segment.__DATA_CONST              ; 0x24000
│ │╎│││││   0x00006c8c      ldr x2, [x2, 0x6b8]
│ │╎│││││   0x00006c90      sub x8, x29, 0x170
│ │╎│││││   0x00006c94      stur x2, [x8, -0x100]
│ │╎│││││   0x00006c98      adrp x3, segment.__DATA_CONST              ; 0x24000
│ │╎│││││   0x00006c9c      ldr x3, [x3, 0x6b0]                        ; [0x246b0:8]=0
│ │╎│││││                                                              ; reloc.TextOutputStreamable.setter_...P_
│ │╎│││││   0x00006ca0      sub x8, x29, 0x168
│ │╎│││││   0x00006ca4      stur x3, [x8, -0x100]
│ │╎│││││   0x00006ca8      sub x20, x29, 0x60
│ │╎│││││   0x00006cac      sub x8, x29, 0x140
│ │╎│││││   0x00006cb0      stur x20, [x8, -0x100]
│ │╎│││││   0x00006cb4      bl sym.imp.DefaultStringInterpolation.append...C0yyxs06CustomB11ConvertibleRzs20TextOutputStreamableRzlF
│ │╎│││││   0x00006cb8      sub x8, x29, 0x140
│ │╎│││││   0x00006cbc      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006cc0      sub x8, x29, 0x190
│ │╎│││││   0x00006cc4      ldur x0, [x8, -0x100]                      ; int64_t arg1
│ │╎│││││   0x00006cc8      bl sym....sSSWOh                           ; func.00008a08
│ │╎│││││   0x00006ccc      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006cd0      add x0, x0, 0x570                          ; 0x1a570 ; ").\n\nINSECURE (NSCoding):\n"
│ │╎│││││   0x00006cd4      mov w8, 0x19
│ │╎│││││   0x00006cd8      mov x1, x8
│ │╎│││││   0x00006cdc      mov w8, 1
│ │╎│││││   0x00006ce0      sub x9, x29, 0x14c
│ │╎│││││   0x00006ce4      stur w8, [x9, -0x100]
│ │╎│││││   0x00006ce8      and w2, w8, 1
│ │╎│││││   0x00006cec      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006cf0      sub x8, x29, 0x188
│ │╎│││││   0x00006cf4      stur x1, [x8, -0x100]
│ │╎│││││   0x00006cf8      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│ │╎│││││   0x00006cfc      sub x8, x29, 0x140
│ │╎│││││   0x00006d00      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006d04      sub x8, x29, 0x188
│ │╎│││││   0x00006d08      ldur x0, [x8, -0x100]                      ; void *arg0
│ │╎│││││   0x00006d0c      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│ │╎│││││   0x00006d10      sub x8, x29, 0xe8
│ │╎│││││   0x00006d14      ldur x0, [x8, -0x100]                      ; int64_t arg1
│ │╎│││││   0x00006d18      sub x8, x29, 0xf0
│ │╎│││││   0x00006d1c      ldur x1, [x8, -0x100]                      ; int64_t arg2
│ │╎│││││   0x00006d20      bl sym.MASTestApp.MastgTest.decodeInsecurely._6E8AB2C58CE173A727EF27CB85DF8CD8.Foundation.Data...VFZ ; func.00007040
│ │╎│││││   0x00006d24      sub x8, x29, 0x170
│ │╎│││││   0x00006d28      ldur x2, [x8, -0x100]
│ │╎│││││   0x00006d2c      sub x8, x29, 0x168
│ │╎│││││   0x00006d30      ldur x3, [x8, -0x100]
│ │╎│││││   0x00006d34      mov x9, x0
│ │╎│││││   0x00006d38      mov x8, x1
│ │╎│││││   0x00006d3c      sub x10, x29, 0x160
│ │╎│││││   0x00006d40      ldur x1, [x10, -0x100]
│ │╎│││││   0x00006d44      sub x0, x29, 0xd0
│ │╎│││││   0x00006d48      sub x10, x29, 0x180
│ │╎│││││   0x00006d4c      stur x0, [x10, -0x100]
│ │╎│││││   0x00006d50      stur x9, [x29, -0xd0]
│ │╎│││││   0x00006d54      stur x8, [x29, -0xc8]
│ │╎│││││   0x00006d58      bl sym.imp.DefaultStringInterpolation.append...C0yyxs06CustomB11ConvertibleRzs20TextOutputStreamableRzlF
│ │╎│││││   0x00006d5c      sub x8, x29, 0x140
│ │╎│││││   0x00006d60      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006d64      sub x8, x29, 0x180
│ │╎│││││   0x00006d68      ldur x0, [x8, -0x100]                      ; int64_t arg1
│ │╎│││││   0x00006d6c      bl sym....sSSWOh                           ; func.00008a08
│ │╎│││││   0x00006d70      sub x8, x29, 0x14c
│ │╎│││││   0x00006d74      ldur w8, [x8, -0x100]
│ │╎│││││   0x00006d78      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006d7c      add x0, x0, 0x590                          ; 0x1a590 ; "\n\nSECURE (NSSecureCoding):\n"
│ │╎│││││   0x00006d80      mov w9, 0x1b
│ │╎│││││   0x00006d84      mov x1, x9
│ │╎│││││   0x00006d88      and w2, w8, 1
│ │╎│││││   0x00006d8c      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006d90      sub x8, x29, 0x178
│ │╎│││││   0x00006d94      stur x1, [x8, -0x100]
│ │╎│││││   0x00006d98      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│ │╎│││││   0x00006d9c      sub x8, x29, 0x140
│ │╎│││││   0x00006da0      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006da4      sub x8, x29, 0x178
│ │╎│││││   0x00006da8      ldur x0, [x8, -0x100]                      ; void *arg0
│ │╎│││││   0x00006dac      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│ │╎│││││   0x00006db0      sub x8, x29, 0xe8
│ │╎│││││   0x00006db4      ldur x0, [x8, -0x100]                      ; int64_t arg1
│ │╎│││││   0x00006db8      sub x8, x29, 0xf0
│ │╎│││││   0x00006dbc      ldur x1, [x8, -0x100]                      ; int64_t arg2
│ │╎│││││   0x00006dc0      bl sym.MASTestApp.MastgTest.decodeSecurely._6E8AB2C58CE173A727EF27CB85DF8CD8.Foundation.Data...VFZ ; func.000077fc
│ │╎│││││   0x00006dc4      sub x8, x29, 0x170
│ │╎│││││   0x00006dc8      ldur x2, [x8, -0x100]
│ │╎│││││   0x00006dcc      sub x8, x29, 0x168
│ │╎│││││   0x00006dd0      ldur x3, [x8, -0x100]
│ │╎│││││   0x00006dd4      mov x9, x0
│ │╎│││││   0x00006dd8      mov x8, x1
│ │╎│││││   0x00006ddc      sub x10, x29, 0x160
│ │╎│││││   0x00006de0      ldur x1, [x10, -0x100]
│ │╎│││││   0x00006de4      sub x0, x29, 0xe0
│ │╎│││││   0x00006de8      sub x10, x29, 0x158
│ │╎│││││   0x00006dec      stur x0, [x10, -0x100]
│ │╎│││││   0x00006df0      stur x9, [x29, -0xe0]
│ │╎│││││   0x00006df4      stur x8, [x29, -0xd8]
│ │╎│││││   0x00006df8      bl sym.imp.DefaultStringInterpolation.append...C0yyxs06CustomB11ConvertibleRzs20TextOutputStreamableRzlF
│ │╎│││││   0x00006dfc      sub x8, x29, 0x140
│ │╎│││││   0x00006e00      ldur x20, [x8, -0x100]
│ │╎│││││   0x00006e04      sub x8, x29, 0x158
│ │╎│││││   0x00006e08      ldur x0, [x8, -0x100]                      ; int64_t arg1
│ │╎│││││   0x00006e0c      bl sym....sSSWOh                           ; func.00008a08
│ │╎│││││   0x00006e10      sub x8, x29, 0x14c
│ │╎│││││   0x00006e14      ldur w8, [x8, -0x100]
│ │╎│││││   0x00006e18      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│ │╎│││││   0x00006e1c      add x0, x0, 0x920                          ; "__objc_classrefs__DATA_CONST"
│ │╎│││││   0x00006e20      mov x1, 0
│ │╎│││││   0x00006e24      and w2, w8, 1
│ │╎│││││   0x00006e28      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│ │╎│││││   0x00006e2c      sub x8, x29, 0x148
│ │╎│││││   0x00006e30      stur x1, [x8, -0x100]
│ │╎│││││   0x00006e34      bl sym.imp.DefaultStringInterpolation.appendLiteral_...SSF_ ; DefaultStringInterpolation.appendLiteral(...SSF)
│ │╎│││││   0x00006e38      sub x8, x29, 0x148
│ │╎│││││   0x00006e3c      ldur x0, [x8, -0x100]                      ; void *arg0
│ │╎│││││   0x00006e40      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│ │╎│││││   0x00006e44      ldur x8, [x29, -0x60]
│ │╎│││││   0x00006e48      sub x9, x29, 0x130
│ │╎│││││   0x00006e4c      stur x8, [x9, -0x100]
│ │╎│││││   0x00006e50      ldur x0, [x29, -0x58]                      ; void *arg0
│ │╎│││││   0x00006e54      sub x8, x29, 0x138
│ │╎│││││   0x00006e58      stur x0, [x8, -0x100]
│ │╎│││││   0x00006e5c      bl sym.imp.swift_bridgeObjectRetain        ; void *swift_bridgeObjectRetain(void *arg0)
│ │╎│││││   0x00006e60      sub x8, x29, 0x140
│ │╎│││││   0x00006e64      ldur x0, [x8, -0x100]                      ; void *arg1
│ │╎│││││   0x00006e68      bl sym....ss26DefaultStringInterpolationVWOh ; func.00009098
│ │╎│││││   0x00006e6c      sub x8, x29, 0x138
│ │╎│││││   0x00006e70      ldur x1, [x8, -0x100]
│ │╎│││││   0x00006e74      sub x8, x29, 0x130
│ │╎│││││   0x00006e78      ldur x0, [x8, -0x100]
│ │╎│││││   0x00006e7c      bl sym.imp.stringInterpolation__String_...cfC_ ; stringInterpolation__String(...cfC)
│ │╎│││││   0x00006e80      mov x2, x0
│ │╎│││││   0x00006e84      sub x8, x29, 0xe8
│ │╎│││││   0x00006e88      ldur x0, [x8, -0x100]                      ; void *arg1
│ │╎│││││   0x00006e8c      sub x8, x29, 0x128
│ │╎│││││   0x00006e90      stur x2, [x8, -0x100]
│ │╎│││││   0x00006e94      mov x2, x1                                 ; int64_t arg_30h
│ │╎│││││   0x00006e98      sub x8, x29, 0xf0
│ │╎│││││   0x00006e9c      ldur x1, [x8, -0x100]                      ; int64_t arg2
│ │╎│││││   0x00006ea0      sub x8, x29, 0x120
│ │╎│││││   0x00006ea4      stur x2, [x8, -0x100]
│ │╎│││││   0x00006ea8      bl sym.Foundation.Data._Representation_...Oe_ ; func.000090c0
│ │╎│││││   0x00006eac      sub x8, x29, 0xc8
│ │╎│││││   0x00006eb0      ldur x0, [x8, -0x100]                      ; void *arg0
│ │╎│││││   0x00006eb4      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│ │╎│││││   0x00006eb8      sub x8, x29, 8
│ │╎│││││   0x00006ebc      ldur x8, [x8, -0x100]
│ │╎│││││   0x00006ec0      sub x9, x29, 0x10
│ │╎│││││   0x00006ec4      ldur x0, [x9, -0x100]
│ │╎│││││   0x00006ec8      ldur x1, [x29, -0xf0]
│ │╎│││││   0x00006ecc      ldr x8, [x8, 8]
│ │╎│││││   0x00006ed0      blr x8
│ │╎│││││   0x00006ed4      sub x8, x29, 0x128
│ │╎│││││   0x00006ed8      ldur x0, [x8, -0x100]
│ │╎│││││   0x00006edc      sub x8, x29, 0x120
│ │╎│││││   0x00006ee0      ldur x1, [x8, -0x100]
│ │╎│││││   0x00006ee4      sub x8, x29, 0x118
│ │╎│││││   0x00006ee8      stur x0, [x8, -0x100]
│ │╎│││││   0x00006eec      sub x8, x29, 0x110
│ │╎│││││   0x00006ef0      stur x1, [x8, -0x100]
│ ────────< 0x00006ef4      b 0x6f54
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6c54(x)
│ ────────> 0x00006ef8      b 0x6c5c
│ │╎│││││   ; CODE XREF from func.00006644 @ 0x6c58(x)
│ ────────> 0x00006efc      sub x0, x29, 0xb0                          ; void *arg1
│ │╎│││││   0x00006f00      bl sym....sSSSgWOh                         ; func.00008b3c
│ ────────< 0x00006f04      b 0x6c5c
│ │╎│││││   ; CODE XREFS from func.00006644 @ 0x6838(x), 0x68f4(x)
│ │╎└└────> 0x00006f08      mov x8, 0
│ │╎  │││   0x00006f0c      mov x9, x8
│ │╎  │││   0x00006f10      sub x10, x29, 0x78
│ │╎  │││   0x00006f14      stur x9, [x10, -0x100]
│ │╎  │││   0x00006f18      sub x9, x29, 0x70
│ │╎  │││   0x00006f1c      stur x8, [x9, -0x100]
│ │└──────< 0x00006f20      b 0x6960
│ │   │││   ; CODE XREFS from func.00006644 @ 0x67e8(x), 0x69c8(x), 0x6a80(x)
│ └───└└──> 0x00006f24      adrp x0, sym.imp.swift_getOpaqueTypeConformance2 ; 0x1a000
│       │   0x00006f28      add x0, x0, 0x4e0                          ; 0x1a4e0 ; "No session payload in the URL.\nExpected mastgtest://import?session=<base64 archive>."
│       │   0x00006f2c      mov w8, 0x54                               ; 'T'
│       │   0x00006f30      mov x1, x8
│       │   0x00006f34      mov w8, 1
│       │   0x00006f38      and w2, w8, 1
│       │   0x00006f3c      bl sym.imp._builtinStringLiteral.utf8CodeUnitCount.isASCII__String:_Builtin.Word__B_...cfC_ ; _builtinStringLiteral.utf8CodeUnitCount.isASCII__String: Builtin.Word, B(...cfC)
│       │   0x00006f40      sub x8, x29, 0x118
│       │   0x00006f44      stur x0, [x8, -0x100]
│       │   0x00006f48      sub x8, x29, 0x110
│       │   0x00006f4c      stur x1, [x8, -0x100]
│      ┌──< 0x00006f50      b 0x6f54
│      ││   ; CODE XREFS from func.00006644 @ 0x6ef4(x), 0x6f50(x)
│ ─────└──> 0x00006f54      sub x8, x29, 0x118
│       │   0x00006f58      ldur x0, [x8, -0x100]
│       │   0x00006f5c      sub x8, x29, 0x110
│       │   0x00006f60      ldur x1, [x8, -0x100]
│       │   0x00006f64      sub sp, x29, 0x20
│       │   0x00006f68      ldp x29, x30, [var_20h]
│       │   0x00006f6c      ldp x20, x19, [var_10h]
│       │   0x00006f70      ldp x22, x21, [sp], 0x30
│       │   0x00006f74      ret
│       │   ; CODE XREF from func.00006644 @ 0x6898(x)
└       └─> 0x00006f78      brk 1
