            ;-- section.0.__TEXT.__text:
            ;-- pc:
            ; CALL XREFS from func.000041f8 @ 0x4290(x), 0x4298(x) ; method.MASTestApp.InsecureWKNavigationDelegate.webView:didReceiveAuthenticationChallenge:completionHandler:
            ; ICOD XREF from func.00004584 @ 0x46a8(x) ; sym.MASTestApp.MastgTest.mastg.completion_...FZ_
┌ 348: NSURLCredential.allocator__GenericAccessorSgtctF_..partial.apply (int64_t arg1, int64_t arg2, int64_t arg3, void *arg4, int64_t arg_a0h);
│           0x00004000      sub sp, sp, 0xa0                           ; [00] -r-x section size 69084 named 0.__TEXT.__text
│           0x00004004      stp x20, x19, [var_80h]
│           0x00004008      stp x29, x30, [var_90h]
│           0x0000400c      add x29, sp, 0x90
│           0x00004010      mov x8, x0                                 ; arg1
│           0x00004014      mov x0, x1                                 ; arg2
│           0x00004018      str x2, [var_28h]                          ; arg3
│           0x0000401c      str x3, [arg0]                             ; arg4
│           0x00004020      stur xzr, [x29, -0x18]
│           0x00004024      stur xzr, [x29, -0x20]
│           0x00004028      stur xzr, [x29, -0x30]
│           0x0000402c      stur xzr, [x29, -0x28]
│           0x00004030      stur xzr, [x29, -0x38]
│           0x00004034      stur xzr, [x29, -0x40]
│           0x00004038      stur x8, [x29, -0x18]
│           0x0000403c      mov x8, x0
│           0x00004040      stur x8, [x29, -0x20]
│           0x00004044      stur x2, [x29, -0x30]                      ; arg3
│           0x00004048      stur x3, [x29, -0x28]                      ; arg4
│           0x0000404c      stur x20, [x29, -0x38]
│           0x00004050      bl sym._objc_msgSend_protectionSpace
│           0x00004054      mov x29, x29
│           0x00004058      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x0000405c      ldr x1, [var_38h]
│           0x00004060      str x0, [var_40h]
│           0x00004064      bl sym._objc_msgSend_serverTrust
│           0x00004068      mov x29, x29
│           0x0000406c      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x00004070      mov x8, x0
│           0x00004074      ldr x0, [var_40h]
│           0x00004078      str x8, [var_48h]
│           0x0000407c      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004080      ldr x8, [x8, 0x120]                        ; [0x20120:8]=0x8010000000000022 ; "\""
│           0x00004084      blr x8
│           0x00004088      ldr x0, [var_48h]
│       ┌─< 0x0000408c      cbz x0, 0x4120
│      ┌──< 0x00004090      b 0x4094
│      ││   ; CODE XREF from func.00004000 @ 0x4090(x)
│      └──> 0x00004094      ldr x8, [var_48h]
│       │   0x00004098      str x8, [var_20h]
│      ┌──< 0x0000409c      b 0x40a0
│      ││   ; CODE XREF from func.00004000 @ 0x409c(x)
│      └──> 0x000040a0      ldr x0, [arg0]
│       │   0x000040a4      ldr x8, [var_20h]
│       │   0x000040a8      str x8, [var_18h]
│       │   0x000040ac      stur x8, [x29, -0x40]
│       │   0x000040b0      bl sym.imp.swift_retain
│       │   0x000040b4      mov x0, 0                                  ; int64_t arg_20h
│       │   0x000040b8      str x0, [var_8h]
│       │   0x000040bc      bl sym....sSo15NSURLCredentialCMa          ; func.0000415c
│       │   0x000040c0      mov x20, x0
│       │   0x000040c4      ldr x0, [var_18h]
│       │   0x000040c8      adrp x8, segment.__DATA_CONST              ; 0x20000
│       │   0x000040cc      ldr x8, [x8, 0x128]                        ; [0x20128:8]=0x8010000000000023 ; "#"
│       │   0x000040d0      blr x8
│       │   0x000040d4      ldr x0, [var_18h]                          ; int64_t arg1
│       │   0x000040d8      bl sym.__C.NSURLCredential                 ; func.000041bc
│       │   0x000040dc      ldr x20, [arg0]
│       │   0x000040e0      ldr x8, [var_28h]
│       │   0x000040e4      mov x1, x0
│       │   0x000040e8      ldr x0, [var_8h]
│       │   0x000040ec      str x1, [var_10h]
│       │   0x000040f0      blr x8
│       │   0x000040f4      ldr x0, [var_10h]
│       │   0x000040f8      adrp x8, segment.__DATA_CONST              ; 0x20000
│       │   0x000040fc      ldr x8, [x8, 0x120]                        ; [0x20120:8]=0x8010000000000022 ; "\""
│       │   0x00004100      blr x8
│       │   0x00004104      ldr x0, [arg0]                             ; void *arg0
│       │   0x00004108      bl sym.imp.swift_release                   ; void swift_release(void *arg0)
│       │   0x0000410c      ldr x0, [var_18h]
│       │   0x00004110      adrp x8, segment.__DATA_CONST              ; 0x20000
│       │   0x00004114      ldr x8, [x8, 0x120]                        ; [0x20120:8]=0x8010000000000022 ; "\""
│       │   0x00004118      blr x8
│      ┌──< 0x0000411c      b 0x414c
│      ││   ; CODE XREF from func.00004000 @ 0x408c(x)
│      │└─> 0x00004120      ldr x20, [arg0]
│      │    0x00004124      mov x0, x20
│      │    0x00004128      bl sym.imp.swift_retain
│      │    0x0000412c      ldr x8, [var_28h]
│      │    0x00004130      mov w9, 2
│      │    0x00004134      mov x0, x9
│      │    0x00004138      mov x1, 0
│      │    0x0000413c      blr x8
│      │    0x00004140      ldr x0, [arg0]                             ; void *arg0
│      │    0x00004144      bl sym.imp.swift_release                   ; void swift_release(void *arg0)
│      │┌─< 0x00004148      b 0x414c
│      ││   ; CODE XREFS from func.00004000 @ 0x411c(x), 0x4148(x)
│      └└─> 0x0000414c      ldp x29, x30, [var_90h]
│           0x00004150      ldp x20, x19, [var_80h]
│           0x00004154      add sp, sp, 0xa0                           ; 0x178000
└           0x00004158      ret
