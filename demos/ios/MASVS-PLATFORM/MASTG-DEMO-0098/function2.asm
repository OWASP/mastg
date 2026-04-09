            ; CODE XREF from sym.func.100007d54 @ 0x100007d5c(x)
┌ 440: sym.func.1000050c0 (int64_t arg1, int64_t arg2, int64_t arg3);
│           0x1000050c0      stp x28, x27, [sp, -0x60]!
│           0x1000050c4      stp x26, x25, [var_10h]
│           0x1000050c8      stp x24, x23, [var_20h]
│           0x1000050cc      stp x22, x21, [var_30h]
│           0x1000050d0      stp x20, x19, [var_40h]
│           0x1000050d4      stp x29, x30, [var_50h]
│           0x1000050d8      add x29, sp, 0x50
│           0x1000050dc      mov x19, x2                               ; arg3
│           0x1000050e0      mov x20, x1                               ; arg2
│           0x1000050e4      mov x24, x0                               ; arg1
│           0x1000050e8      mov x0, 0
│           0x1000050ec      bl sym.imp.Foundation.URL...VMa
│           0x1000050f0      mov x21, x0
│           0x1000050f4      ldur x28, [x0, -8]
│           0x1000050f8      ldr x8, [x28, 0x40]
│           0x1000050fc      mov x9, x8
│           0x100005100      adrp x16, segment.__DATA_CONST            ; 0x100010000
│           0x100005104      ldr x16, [x16, 0x1d8]                     ; [0x1000101d8:4]=0
│                                                                      ; reloc.__chkstk_darwin
│           0x100005108      blr x16
│           0x10000510c      mov x9, sp
│           0x100005110      add x8, x8, 0xf
│           0x100005114      and x8, x8, 0xfffffffffffffff0
│           0x100005118      sub x22, x9, x8
│           0x10000511c      mov sp, x22
│           0x100005120      adrp x8, segment.__DATA                   ; 0x100014000
│           0x100005124      ldr x8, [x8, 0x338]
│           0x100005128      cmn x8, 1
│       ┌─< 0x10000512c      b.ne 0x100005248
│       │   ; CODE XREF from sym.func.1000050c0 @ 0x10000525c(x)
│      ┌──> 0x100005130      adrp x1, segment.__DATA                   ; 0x100014000
│      ╎│   0x100005134      add x1, x1, 0x340                         ; int64_t arg2
│      ╎│   0x100005138      mov x0, x21                               ; int64_t arg1
│      ╎│   0x10000513c      bl sym.func.100007be4
│      ╎│   0x100005140      mov x23, x0
│      ╎│   0x100005144      ldr x27, [x28, 0x10]
│      ╎│   0x100005148      mov x0, x22
│      ╎│   0x10000514c      mov x1, x23
│      ╎│   0x100005150      mov x2, x21
│      ╎│   0x100005154      blr x27
│      ╎│   0x100005158      mov x0, x22                               ; int64_t arg1
│      ╎│   0x10000515c      mov x1, 0                                 ; int64_t arg2
│      ╎│   0x100005160      mov x2, -0x2000000000000000               ; int64_t arg3
│      ╎│   0x100005164      bl sym.func.100005278
│      ╎│   0x100005168      mov x25, x0
│      ╎│   0x10000516c      mov x26, x1
│      ╎│   0x100005170      ldr x28, [x28, 8]
│      ╎│   0x100005174      mov x0, x22
│      ╎│   0x100005178      mov x1, x21
│      ╎│   0x10000517c      blr x28
│      ╎│   0x100005180      mov x0, x25
│      ╎│   0x100005184      mov x1, x26
│      ╎│   0x100005188      blr x24
│      ╎│   0x10000518c      mov x0, x26                               ; void *arg0
│      ╎│   0x100005190      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│      ╎│   0x100005194      adrp x8, segment.__DATA                   ; 0x100014000
│      ╎│   0x100005198      ldr x8, [x8, 0x2f8]
│      ╎│   0x10000519c      cmn x8, 1
│     ┌───< 0x1000051a0      b.ne 0x100005260
│     │╎│   ; CODE XREF from sym.func.1000050c0 @ 0x100005274(x)
│    ┌────> 0x1000051a4      adrp x1, segment.__DATA                   ; 0x100014000
│    ╎│╎│   0x1000051a8      add x1, x1, 0x300                         ; int64_t arg2
│    ╎│╎│   0x1000051ac      mov x0, x21                               ; int64_t arg1
│    ╎│╎│   0x1000051b0      bl sym.func.100007be4
│    ╎│╎│   0x1000051b4      mov x1, x0
│    ╎│╎│   0x1000051b8      mov x0, x22
│    ╎│╎│   0x1000051bc      mov x2, x21
│    ╎│╎│   0x1000051c0      blr x27
│    ╎│╎│   0x1000051c4      mov x20, x22
│    ╎│╎│   0x1000051c8      bl sym.imp.Foundation.URL._bridgeToObjectiveC.NSURL_...F_ ; Foundation.URL._bridgeToObjectiveC.NSURL(...F)
│    ╎│╎│   0x1000051cc      mov x24, x0
│    ╎│╎│   0x1000051d0      mov x0, x22
│    ╎│╎│   0x1000051d4      mov x1, x21
│    ╎│╎│   0x1000051d8      blr x28
│    ╎│╎│   0x1000051dc      mov x0, x22
│    ╎│╎│   0x1000051e0      mov x1, x23
│    ╎│╎│   0x1000051e4      mov x2, x21
│    ╎│╎│   0x1000051e8      blr x27
│    ╎│╎│   0x1000051ec      bl sym.imp.Foundation.URL._bridgeToObjectiveC.NSURL_...F_ ; Foundation.URL._bridgeToObjectiveC.NSURL(...F)
│    ╎│╎│   0x1000051f0      mov x20, x0
│    ╎│╎│   0x1000051f4      mov x0, x22
│    ╎│╎│   0x1000051f8      mov x1, x21
│    ╎│╎│   0x1000051fc      blr x28
│    ╎│╎│   0x100005200      mov x0, x19
│    ╎│╎│   0x100005204      mov x2, x24
│    ╎│╎│   0x100005208      mov x3, x20
│    ╎│╎│   0x10000520c      bl fcn.10000c680
│    ╎│╎│   0x100005210      mov x29, x29
│    ╎│╎│   0x100005214      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│    ╎│╎│   0x100005218      mov x19, x0
│    ╎│╎│   0x10000521c      bl sym.imp.objc_release_x24
│    ╎│╎│   0x100005220      bl sym.imp.objc_release_x20
│    ╎│╎│   0x100005224      bl sym.imp.objc_release_x19
│    ╎│╎│   0x100005228      sub sp, x29, 0x50
│    ╎│╎│   0x10000522c      ldp x29, x30, [var_50h]
│    ╎│╎│   0x100005230      ldp x20, x19, [var_40h]
│    ╎│╎│   0x100005234      ldp x22, x21, [var_30h]
│    ╎│╎│   0x100005238      ldp x24, x23, [var_20h]
│    ╎│╎│   0x10000523c      ldp x26, x25, [var_10h]
│    ╎│╎│   0x100005240      ldp x28, x27, [sp], 0x60
│    ╎│╎│   0x100005244      ret
│    ╎│╎│   ; CODE XREF from sym.func.1000050c0 @ 0x10000512c(x)
│    ╎│╎└─> 0x100005248      adrp x0, segment.__DATA                   ; 0x100014000
│    ╎│╎    0x10000524c      add x0, x0, 0x338
│    ╎│╎    0x100005250      adrp x1, sym.func.100004000               ; 0x100004000
│    ╎│╎    0x100005254      add x1, x1, 0x120
│    ╎│╎    0x100005258      bl sym.imp.swift_once
│    ╎│└──< 0x10000525c      b 0x100005130
│    ╎│     ; CODE XREF from sym.func.1000050c0 @ 0x1000051a0(x)
│    ╎└───> 0x100005260      adrp x0, segment.__DATA                   ; 0x100014000
│    ╎      0x100005264      add x0, x0, 0x2f8
│    ╎      0x100005268      adrp x1, sym.func.100004000               ; 0x100004000
│    ╎      0x10000526c      add x1, x1, 0x348
│    ╎      0x100005270      bl sym.imp.swift_once
└    └────< 0x100005274      b 0x1000051a4
