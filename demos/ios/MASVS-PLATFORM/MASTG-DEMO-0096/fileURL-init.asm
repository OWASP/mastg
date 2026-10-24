            ; ICOD XREF from func.100004284 @ 0x100004604(r) ; sym.MASTestApp.MastgTest.mastg.completion_...FZ_
            ; ICOD XREF from func.100004d24 @ 0x100005018(r) ; sym.MASTestApp.MastgTest.showHtmlRegistrationView._6E8AB2C58CE173A727EF27CB85DF8CD8.username.completion
┌ 36: E8AB2C.CE173A727EF27CB85DF8CD8_...Z_ ();
│           0x100004144      adrp x1, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000 ; MASTestApp.MastgTest.fileURL._6E8AB2C58CE173A727EF27CB85DF8CD8(...Z)
│           0x100004148      add x1, x1, 0x1d8
│           0x10000414c      mov x2, 0x6e69                            ; 'in'
│           0x100004150      movk x2, 0x6564, lsl 16                   ; 'de'
│           0x100004154      movk x2, 0x2e78, lsl 32                   ; 'x.'
│           0x100004158      movk x2, 0x7468, lsl 48                   ; 'ht'
│           0x10000415c      mov x3, 0x6c6d                            ; 'ml'
│           0x100004160      movk x3, 0xea00, lsl 48
└       ┌─< 0x100004164      b sym.MASTestApp.MastgTest.fileURL._6E8AB2C58CE173A727EF27CB85DF8CD8_...ZTm_ ; func.10000418c
            ; CODE XREF from func.100004144 @ 0x100004164(x) ; sym.MASTestApp.MastgTest.fileURL._6E8AB2C58CE173A727EF27CB85DF8CD8_...Z_
            ; CODE XREF from func.100004168 @ 0x100004188(x) ; sym.MASTestApp.MastgTest.secretURL._6E8AB2C58CE173A727EF27CB85DF8CD8_...Z_
┌ 248: E8AB2C.CE173A727EF27CB85DF8CD8_...ZTm_ (int64_t arg1, int64_t arg2, int64_t arg3);
; MASTestApp.MastgTest.fileURL._6E8AB2C58CE173A727EF27CB85DF8CD8(...ZT
; m)
│           0x10000418c      stp x24, x23, [sp, -0x40]!
│           0x100004190      stp x22, x21, [var_10h]
│           0x100004194      stp x20, x19, [var_20h]
│           0x100004198      stp x29, x30, [var_30h]
│           0x10000419c      add x29, sp, 0x30
│           0x1000041a0      mov x19, x3                               ; arg4
│           0x1000041a4      mov x20, x2                               ; arg3
│           0x1000041a8      mov x23, x1                               ; arg2
│           0x1000041ac      mov x0, 0
│           0x1000041b0      bl sym.imp.Foundation.URL...VMa
│           0x1000041b4      mov x21, x0
│           0x1000041b8      ldur x24, [x0, -8]
│           0x1000041bc      ldr x8, [x24, 0x40]
│           0x1000041c0      mov x9, x8
│           0x1000041c4      adrp x16, segment.__DATA_CONST            ; 0x100014000
│           0x1000041c8      ldr x16, [x16, 0x50]                      ; [0x100014050:4]=9 ; "\t"
│           0x1000041cc      blr x16
│           0x1000041d0      mov x9, sp
│           0x1000041d4      add x8, x8, 0xf
│           0x1000041d8      and x8, x8, 0xfffffffffffffff0
│           0x1000041dc      sub x22, x9, x8
│           0x1000041e0      mov sp, x22
│           0x1000041e4      mov x1, x23                               ; int64_t arg2
│           0x1000041e8      bl sym.___swift_allocate_value_buffer
│           0x1000041ec      mov x0, x21                               ; int64_t arg1
│           0x1000041f0      mov x1, x23                               ; int64_t arg2
│           0x1000041f4      bl sym.___swift_project_value_buffer
│           0x1000041f8      mov x23, x0
│           0x1000041fc      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x100004200      ldr x8, [x8, 0x1b0]                       ; [0x1000181b0:4]=0
│                                                                      ; sym.MASTestApp.MastgTest.docDir._6E8AB2C58CE173A727EF27CB85DF8CD8_...z_
│                                                                      [24] -rw- section size 929 named 24.__DATA.__data
│           0x100004204      cmn x8, 1
│       ┌─< 0x100004208      b.ne 0x10000426c
│       │   ; CODE XREF from func.10000418c @ 0x100004280(x)
│      ┌──> 0x10000420c      adrp x1, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│      ╎│   0x100004210      add x1, x1, 0x1b8                         ; int64_t arg2
│      ╎│   0x100004214      mov x0, x21                               ; int64_t arg1
│      ╎│   0x100004218      bl sym.___swift_project_value_buffer
│      ╎│   0x10000421c      mov x1, x0
│      ╎│   0x100004220      ldr x8, [x24, 0x10]
│      ╎│   0x100004224      mov x0, x22
│      ╎│   0x100004228      mov x2, x21
│      ╎│   0x10000422c      blr x8
│      ╎│   0x100004230      mov x8, x23
│      ╎│   0x100004234      mov x0, x20
│      ╎│   0x100004238      mov x1, x19
│      ╎│   0x10000423c      mov x20, x22
│      ╎│   0x100004240      bl sym.imp.Foundation.URL.appendingPathComponent_...CSSF_ ; Foundation.URL.appendingPathComponent(...CSSF)
│      ╎│   0x100004244      ldr x8, [x24, 8]
│      ╎│   0x100004248      mov x0, x22
│      ╎│   0x10000424c      mov x1, x21
│      ╎│   0x100004250      blr x8
│      ╎│   0x100004254      sub sp, x29, 0x30
│      ╎│   0x100004258      ldp x29, x30, [var_30h]
│      ╎│   0x10000425c      ldp x20, x19, [var_20h]
│      ╎│   0x100004260      ldp x22, x21, [var_10h]
│      ╎│   0x100004264      ldp x24, x23, [sp], 0x40
│      ╎│   0x100004268      ret
│      ╎│   ; CODE XREF from func.10000418c @ 0x100004208(x)
│      ╎└─> 0x10000426c      adrp x0, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│      ╎    0x100004270      add x0, x0, 0x1b0
│      ╎    0x100004274      adrp x1, sym.MASTestApp.MastgTest.docDir._6E8AB2C58CE173A727EF27CB85DF8CD8_...Z_ ; 0x100004000
│      ╎    0x100004278      add x1, x1, 0
│      ╎    0x10000427c      bl sym.imp.swift_once
└      └──< 0x100004280      b 0x10000420c
