            ;-- section.0.__TEXT.__text:
            ; XREFS: 0x1000000d0  ICOD 0x100004274  ICOD 0x100004278  
            ; XREFS: ICOD 0x1000044c8  ICOD 0x1000044ec  ICOD 0x1000045e8  
            ; XREFS: ICOD 0x100004600  ICOD 0x1000046a4  ICOD 0x1000048bc  
            ; XREFS: ICOD 0x100005014  ICOD 0x10000502c  ICOD 0x100005030  
┌ 324: E8AB2C.CE173A727EF27CB85DF8CD8_...Z_ ();
│           0x100004000      stp x24, x23, [sp, -0x40]!                ; [00] -r-x section size 26400 named 0.__TEXT.__text
│           0x100004004      stp x22, x21, [var_10h]
│           0x100004008      stp x20, x19, [var_20h]
│           0x10000400c      stp x29, x30, [var_30h]
│           0x100004010      add x29, sp, 0x30
│           0x100004014      mov x0, 0
│           0x100004018      bl sym.imp.Foundation.URL...VMa
│           0x10000401c      mov x19, x0
│           0x100004020      ldur x24, [x0, -8]
│           0x100004024      ldr x8, [x24, 0x40]
│           0x100004028      mov x9, x8
│           0x10000402c      adrp x16, segment.__DATA_CONST            ; 0x100014000
│           0x100004030      ldr x16, [x16, 0x50]                      ; [0x100014050:4]=9 ; "\t"
│           0x100004034      blr x16
│           0x100004038      mov x9, sp
│           0x10000403c      add x8, x8, 0xf
│           0x100004040      and x8, x8, 0xfffffffffffffff0
│           0x100004044      sub x20, x9, x8
│           0x100004048      mov sp, x20
│           0x10000404c      adrp x21, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x100004050      add x21, x21, 0x1b8
│           0x100004054      mov x1, x21                               ; int64_t arg2
│           0x100004058      bl sym.___swift_allocate_value_buffer
│           0x10000405c      mov x0, x19                               ; int64_t arg1
│           0x100004060      mov x1, x21                               ; int64_t arg2
│           0x100004064      bl sym.___swift_project_value_buffer
│           0x100004068      mov x21, x0
│           0x10000406c      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x100004070      ldr x0, [x8, 0x1a0]                       ; [0x1000181a0:4]=254
│                                                                      ; reloc.NSFileManager ; void *arg0
│           0x100004074      bl sym.imp.objc_opt_self                  ; void *objc_opt_self(void *arg0)
│           0x100004078      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x10000407c      ldr x1, [x8, 0x128]                       ; [0x100018128:4]=0xc0df ; reloc.fixup.defaultManager ; char *selector
│           0x100004080      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x100004084      mov x29, x29
│           0x100004088      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x10000408c      mov x22, x0
│           0x100004090      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x100004094      ldr x1, [x8, 0x130]                       ; [0x100018130:4]=0xc043 ; "C\xc0" ; char *selector
│           0x100004098      mov w2, 9
│           0x10000409c      mov w3, 1
│           0x1000040a0      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x1000040a4      mov x29, x29
│           0x1000040a8      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x1000040ac      mov x23, x0
│           0x1000040b0      mov x0, x22                               ; void *instance
│           0x1000040b4      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│           0x1000040b8      mov x0, x23
│           0x1000040bc      mov x1, x19
│           0x1000040c0      bl sym.imp.Foundation_...nconditionallyBridgeFromObjectiveCySayxGSo7NSArrayCSgFZ_ ; Foundation(...nconditionallyBridgeFromObjectiveCySayxGSo7NSArrayCSgFZ)
│           0x1000040c4      mov x22, x0
│           0x1000040c8      ldr x8, [x0, 0x10]
│       ┌─< 0x1000040cc      cbz x8, 0x100004138
│       │   0x1000040d0      mov x0, x23                               ; void *instance
│       │   0x1000040d4      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│       │   0x1000040d8      ldr x8, [x22, 0x10]
│      ┌──< 0x1000040dc      cbz x8, 0x100004134
│      ││   0x1000040e0      ldrb w8, [x24, 0x50]
│      ││   0x1000040e4      add x9, x8, 0x20
│      ││   0x1000040e8      bic x8, x9, x8
│      ││   0x1000040ec      ldr x9, [x24, 0x10]
│      ││   0x1000040f0      add x1, x22, x8
│      ││   0x1000040f4      mov x0, x20
│      ││   0x1000040f8      mov x2, x19
│      ││   0x1000040fc      blr x9
│      ││   0x100004100      mov x0, x22                               ; void *arg0
│      ││   0x100004104      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│      ││   0x100004108      ldr x8, [x24, 0x20]
│      ││   0x10000410c      mov x0, x21
│      ││   0x100004110      mov x1, x20
│      ││   0x100004114      mov x2, x19
│      ││   0x100004118      blr x8
│      ││   0x10000411c      sub sp, x29, 0x30
│      ││   0x100004120      ldp x29, x30, [var_30h]
│      ││   0x100004124      ldp x20, x19, [var_20h]
│      ││   0x100004128      ldp x22, x21, [var_10h]
│      ││   0x10000412c      ldp x24, x23, [sp], 0x40
│      ││   0x100004130      ret
│      ││   ; CODE XREF from func.100004000 @ 0x1000040dc(x)
│      └──> 0x100004134      brk 1
│       │   ; CODE XREF from func.100004000 @ 0x1000040cc(x)
│       └─> 0x100004138      mov x0, x22                               ; void *arg0
│           0x10000413c      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
└           0x100004140      brk 1
