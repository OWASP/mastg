            ; CALL XREFS from sym.func.100004888 @ 0x1000049e0(x), 0x100004aa8(x), 0x100004afc(x)
┌ 492: sym.func.10000469c (int64_t arg1, int64_t arg2, int64_t arg3, int64_t arg4, int64_t arg5, int64_t arg6, int64_t arg7, int64_t arg8, int64_t arg_20h, int64_t arg_d0h);
│           0x10000469c      sub sp, sp, 0xd0
│           0x1000046a0      stp x27, x26, [var_80h]
│           0x1000046a4      stp x25, x24, [var_90h]
│           0x1000046a8      stp x23, x22, [var_a0h]
│           0x1000046ac      stp x20, x19, [arg_20h]
│           0x1000046b0      stp x29, x30, [var_c0h]
│           0x1000046b4      add x29, sp, 0xc0
│           0x1000046b8      mov x19, x21
│           0x1000046bc      mov x20, x2                               ; arg3
│           0x1000046c0      mov x2, x0                                ; arg1
│           0x1000046c4      stp x20, x3, [arg_d0hx30]                 ; arg4
│           0x1000046c8      stp x4, x5, [arg_d0hx40]                  ; arg6
│           0x1000046cc      stp x6, x7, [arg_d0hx50]                  ; arg8
│       ┌─< 0x1000046d0      tbnz x1, 0x3c, 0x100004848
│       │   0x1000046d4      mov x22, x7
│       │   0x1000046d8      mov x23, x6
│      ┌──< 0x1000046dc      tbnz x1, 0x3d, 0x1000046fc
│     ┌───< 0x1000046e0      tbz x2, 0x3c, 0x100004848
│     │││   0x1000046e4      lsr x8, x5, 0x3e
│     │││   0x1000046e8      cmp w8, 1
│    ┌────< 0x1000046ec      b.gt 0x100004734
│   ┌─────< 0x1000046f0      cbnz w8, 0x1000047a8
│   │││││   0x1000046f4      ubfx x24, x5, 0x30, 8
│  ┌──────< 0x1000046f8      b 0x1000047c0
│  ││││││   ; CODE XREF from sym.func.10000469c @ 0x1000046dc(x)
│  ││││└──> 0x1000046fc      and x8, x1, 0xffffffffffffff
│  ││││ │   0x100004700      stp x2, x8, [x29, -0x58]
│  ││││ │   0x100004704      lsr x8, x5, 0x3e
│  ││││ │   0x100004708      cmp w8, 1
│  ││││┌──< 0x10000470c      b.gt 0x10000471c
│ ┌───────< 0x100004710      cbnz w8, 0x10000474c
│ │││││││   0x100004714      ubfx x24, x5, 0x30, 8
│ ────────< 0x100004718      b 0x100004764
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x10000470c(x)
│ │││││└──> 0x10000471c      cmp w8, 2
│ │││││┌──< 0x100004720      b.ne 0x100004760
│ │││││││   0x100004724      ldp x9, x8, [x4, 0x10]
│ │││││││   0x100004728      subs x24, x8, x9
│ ────────< 0x10000472c      b.vc 0x100004764
│ │││││││   0x100004730      brk 1
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x1000046ec(x)
│ │││└────> 0x100004734      cmp w8, 2
│ │││┌────< 0x100004738      b.ne 0x1000047bc
│ │││││││   0x10000473c      ldp x9, x8, [x4, 0x10]
│ │││││││   0x100004740      subs x24, x8, x9
│ ────────< 0x100004744      b.vc 0x1000047c0
│ │││││││   0x100004748      brk 1
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x100004710(x)
│ └───────> 0x10000474c      lsr x8, x4, 0x20
│  ││││││   0x100004750      subs w8, w8, w4
│ ┌───────< 0x100004754      b.vs 0x100004880
│ │││││││   0x100004758      sxtw x24, w8
│ ────────< 0x10000475c      b 0x100004764
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x100004720(x)
│ │││││└──> 0x100004760      mov x24, 0
│ │││││ │   ; CODE XREFS from sym.func.10000469c @ 0x100004718(x), 0x10000472c(x), 0x10000475c(x)
│ ────────> 0x100004764      ldr x25, [x23]
│ │││││ │   0x100004768      ldr x26, [x25, 0x10]
│ │││││ │   0x10000476c      mov x0, x25
│ │││││ │   0x100004770      bl sym.imp.swift_isUniquelyReferenced_nonNull_native
│ │││││ │   0x100004774      str x25, [x23]
│ │││││┌──< 0x100004778      tbnz w0, 0, 0x100004794
│ │││││││   0x10000477c      ldr x1, [x25, 0x10]                       ; int64_t arg2
│ │││││││   0x100004780      mov w0, 0
│ │││││││   0x100004784      mov w2, 0
│ │││││││   0x100004788      mov x3, x25
│ │││││││   0x10000478c      bl sym.func.100004488
│ │││││││   0x100004790      mov x25, x0
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x100004778(x)
│ │││││└──> 0x100004794      str x25, [x23]
│ │││││ │   0x100004798      add x8, x25, 0x20
│ │││││ │   0x10000479c      stp x26, x22, [var_8h]
│ │││││ │   0x1000047a0      sub x3, x29, 0x58
│ │││││┌──< 0x1000047a4      b 0x100004804
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x1000046f0(x)
│ ││└─────> 0x1000047a8      lsr x8, x4, 0x20
│ ││ ││││   0x1000047ac      subs w8, w8, w4
│ ││┌─────< 0x1000047b0      b.vs 0x100004884
│ │││││││   0x1000047b4      sxtw x24, w8
│ ────────< 0x1000047b8      b 0x1000047c0
│ │││││││   ; CODE XREF from sym.func.10000469c @ 0x100004738(x)
│ │││└────> 0x1000047bc      mov x24, 0
│ │││ │││   ; CODE XREFS from sym.func.10000469c @ 0x1000046f8(x), 0x100004744(x), 0x1000047b8(x)
│ ─└──────> 0x1000047c0      and x26, x1, 0xfffffffffffffff
│ │ │ │││   0x1000047c4      ldr x25, [x23]
│ │ │ │││   0x1000047c8      ldr x27, [x25, 0x10]
│ │ │ │││   0x1000047cc      mov x0, x25
│ │ │ │││   0x1000047d0      bl sym.imp.swift_isUniquelyReferenced_nonNull_native
│ │ │ │││   0x1000047d4      str x25, [x23]
│ │ │┌────< 0x1000047d8      tbnz w0, 0, 0x1000047f4
│ │ │││││   0x1000047dc      ldr x1, [x25, 0x10]                       ; int64_t arg2
│ │ │││││   0x1000047e0      mov w0, 0
│ │ │││││   0x1000047e4      mov w2, 0
│ │ │││││   0x1000047e8      mov x3, x25
│ │ │││││   0x1000047ec      bl sym.func.100004488
│ │ │││││   0x1000047f0      mov x25, x0
│ │ │││││   ; CODE XREF from sym.func.10000469c @ 0x1000047d8(x)
│ │ │└────> 0x1000047f4      str x25, [x23]
│ │ │ │││   0x1000047f8      add x8, x25, 0x20
│ │ │ │││   0x1000047fc      stp x27, x22, [var_8h]
│ │ │ │││   0x100004800      add x3, x26, 0x20
│ │ │ │││   ; CODE XREF from sym.func.10000469c @ 0x1000047a4(x)
│ │ │ │└──> 0x100004804      str x8, [sp]
│ │ │ │ │   0x100004808      mov w0, 0
│ │ │ │ │   0x10000480c      mov w1, 0
│ │ │ │ │   0x100004810      mov w2, 3
│ │ │ │ │   0x100004814      mov w4, 0x10
│ │ │ │ │   0x100004818      mov x5, 0
│ │ │ │ │   0x10000481c      mov x6, x20
│ │ │ │ │   0x100004820      mov x7, x24
│ │ │ │ │   0x100004824      bl sym.imp.CCCrypt                        ; CCCryptorStatus CCCrypt(CCOperation op, CCAlgorithm alg, int32_t options, const void *key, uint32_t keyLength, const void *iv, const void *dataIn, uint32_t dataInLength, void *dataOut, uint32_t dataOutAvailable, uint32_t *dataOutMoved)
│ │ │ │ │   ; CODE XREFS from sym.func.10000469c @ 0x100004874(x), 0x10000487c(x)
│ │ │┌─┌──> 0x100004828      mov x21, x19
│ │ │╎│╎│   0x10000482c      ldp x29, x30, [var_c0h]
│ │ │╎│╎│   0x100004830      ldp x20, x19, [arg_20h]
│ │ │╎│╎│   0x100004834      ldp x23, x22, [var_a0h]
│ │ │╎│╎│   0x100004838      ldp x25, x24, [var_90h]
│ │ │╎│╎│   0x10000483c      ldp x27, x26, [var_80h]
│ │ │╎│╎│   0x100004840      add sp, sp, 0xd0                          ; 0x178000
│ │ │╎│╎│   0x100004844      ret
│ │ │╎│╎│   ; CODE XREFS from sym.func.10000469c @ 0x1000046d0(x), 0x1000046e0(x)
│ │ │╎└─└─> 0x100004848      adrp x0, 0x100005000
│ │ │╎ ╎    0x10000484c      add x0, x0, 0x374
│ │ │╎ ╎    0x100004850      adrp x4, segment.__DATA_CONST             ; 0x10000c000
│ │ │╎ ╎    0x100004854      ldr x4, [x4, 0x3f0]                       ; [0x10000c3f0:4]=125 ; "}"
│ │ │╎ ╎    0x100004858      sub x8, x29, 0x44
│ │ │╎ ╎    0x10000485c      mov x3, x1                                ; arg2
│ │ │╎ ╎    0x100004860      add x1, sp, 0x20
│ │ │╎ ╎    0x100004864      mov x21, x19
│ │ │╎ ╎    0x100004868      bl sym.imp._StringGuts._slowWithCString.Int8...VGKXEKlF
│ │ │╎ ╎    0x10000486c      mov x19, x21
│ │ │╎ ╎┌─< 0x100004870      cbz x21, 0x100004878
│ │ │└────< 0x100004874      b 0x100004828
│ │ │  ╎│   ; CODE XREF from sym.func.10000469c @ 0x100004870(x)
│ │ │  ╎└─> 0x100004878      ldur w0, [x29, -0x44]
│ │ │  └──< 0x10000487c      b 0x100004828
│ │ │       ; CODE XREF from sym.func.10000469c @ 0x100004754(x)
│ └───────> 0x100004880      brk 1
│   │       ; CODE XREF from sym.func.10000469c @ 0x1000047b0(x)
└   └─────> 0x100004884      brk 1
