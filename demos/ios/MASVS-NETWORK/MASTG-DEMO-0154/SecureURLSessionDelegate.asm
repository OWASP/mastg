            ; CALL XREF from func.00004780 @ 0x4820(x) ; method.MASTestApp.SecureURLSessionDelegate.URLSession:didReceiveChallenge:completionHandler:
┌ 704: NSURLCredential.allocator__GenericAccessorSgtctF_..partial.apply (int64_t arg1, int64_t arg2, int64_t arg3, void *arg4, int64_t arg_f0h);
; MASTestApp.SecureURLSessionDelegate.urlSession.allocator.didReceive.
; completionHandler(...o15NSURLCredentialCSgtctF)
│           0x00004490      sub sp, sp, 0xf0
│           0x00004494      stp x20, x19, [var_d0h]
│           0x00004498      stp x29, x30, [var_e0h]
│           0x0000449c      add x29, sp, 0xe0
│           0x000044a0      mov x8, x0                                 ; arg1
│           0x000044a4      mov x0, x1                                 ; arg2
│           0x000044a8      str x0, [var_30h]
│           0x000044ac      str x2, [var_38h]                          ; arg3
│           0x000044b0      str x3, [var_40h]                          ; arg4
│           0x000044b4      adrp x9, segment.__DATA_CONST              ; 0x20000
│           0x000044b8      ldr x9, [x9, 0x158]                        ; [0x20158:8]=0x8010000000000029 ; ")"
│           0x000044bc      ldr x9, [x9]
│           0x000044c0      stur x9, [x29, -0x18]
│           0x000044c4      stur xzr, [x29, -0x28]
│           0x000044c8      stur xzr, [x29, -0x30]
│           0x000044cc      stur xzr, [x29, -0x40]
│           0x000044d0      stur xzr, [x29, -0x38]
│           0x000044d4      stur xzr, [x29, -0x48]
│           0x000044d8      stur xzr, [x29, -0x50]
│           0x000044dc      stur x8, [x29, -0x28]
│           0x000044e0      mov x8, x0
│           0x000044e4      stur x8, [x29, -0x30]
│           0x000044e8      stur x2, [x29, -0x40]                      ; arg3
│           0x000044ec      stur x3, [x29, -0x38]                      ; arg4
│           0x000044f0      stur x20, [x29, -0x48]
│           0x000044f4      bl sym._objc_msgSend_protectionSpace
│           0x000044f8      mov x29, x29
│           0x000044fc      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x00004500      ldr x1, [var_48h]
│           0x00004504      str x0, [var_50h]
│           0x00004508      bl sym._objc_msgSend_authenticationMethod
│           0x0000450c      mov x29, x29
│           0x00004510      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x00004514      mov x8, x0
│           0x00004518      ldr x0, [var_50h]
│           0x0000451c      str x8, [var_58h]
│           0x00004520      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004524      ldr x8, [x8, 0x118]                        ; [0x20118:8]=0x8010000000000021 ; "!"
│           0x00004528      blr x8
│           0x0000452c      ldr x0, [var_58h]
│           0x00004530      bl sym.imp.Foundation_...nconditionallyBridgeFromObjectiveCySSSo8NSStringCSgFZ_ ; Foundation(...nconditionallyBridgeFromObjectiveCySSSo8NSStringCSgFZ)
│           0x00004534      mov x2, x0
│           0x00004538      ldr x0, [var_58h]
│           0x0000453c      str x2, [var_68h]
│           0x00004540      stur x1, [x29, -0x60]
│           0x00004544      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004548      ldr x8, [x8, 0x118]                        ; [0x20118:8]=0x8010000000000021 ; "!"
│           0x0000454c      blr x8
│           0x00004550      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004554      ldr x8, [x8, 0xf0]                         ; [0x200f0:8]=0x801000000000001c ; reloc.NSURLAuthenticationMethodServerTrust
│           0x00004558      ldr x0, [x8]
│           0x0000455c      str x0, [var_60h]
│           0x00004560      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004564      ldr x8, [x8, 0x120]                        ; [0x20120:8]=0x8010000000000022 ; "\""
│           0x00004568      blr x8
│           0x0000456c      ldr x0, [var_60h]
│           0x00004570      bl sym.imp.Foundation_...nconditionallyBridgeFromObjectiveCySSSo8NSStringCSgFZ_ ; Foundation(...nconditionallyBridgeFromObjectiveCySSSo8NSStringCSgFZ)
│           0x00004574      mov x2, x0
│           0x00004578      ldr x0, [var_60h]
│           0x0000457c      str x2, [var_70h]
│           0x00004580      stur x1, [x29, -0x68]
│           0x00004584      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004588      ldr x8, [x8, 0x118]                        ; [0x20118:8]=0x8010000000000021 ; "!"
│           0x0000458c      blr x8
│           0x00004590      ldr x0, [var_68h]
│           0x00004594      ldr x2, [var_70h]
│           0x00004598      ldur x3, [x29, -0x68]
│           0x0000459c      ldur x1, [x29, -0x60]
│           0x000045a0      bl sym.imp.Swift__String_...tFZ_           ; Swift__String(...tFZ)
│           0x000045a4      mov x8, x0
│           0x000045a8      ldur x0, [x29, -0x68]                      ; void *arg0
│           0x000045ac      stur w8, [x29, -0x54]
│           0x000045b0      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│           0x000045b4      ldur x0, [x29, -0x60]                      ; void *arg0
│           0x000045b8      bl sym.imp.swift_bridgeObjectRelease       ; void swift_bridgeObjectRelease(void *arg0)
│           0x000045bc      ldur w0, [x29, -0x54]
│       ┌─< 0x000045c0      tbz w0, 0, 0x46f0
│      ┌──< 0x000045c4      b 0x45c8
│      ││   ; CODE XREF from func.00004490 @ 0x45c4(x)
│      └──> 0x000045c8      ldr x1, [var_48h]
│       │   0x000045cc      ldr x0, [var_30h]
│       │   0x000045d0      bl sym._objc_msgSend_protectionSpace
│       │   0x000045d4      mov x29, x29
│       │   0x000045d8      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│       │   0x000045dc      ldr x1, [var_48h]
│       │   0x000045e0      str x0, [var_20h]
│       │   0x000045e4      bl sym._objc_msgSend_serverTrust
│       │   0x000045e8      mov x29, x29
│       │   0x000045ec      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│       │   0x000045f0      mov x8, x0
│       │   0x000045f4      ldr x0, [var_20h]
│       │   0x000045f8      str x8, [var_28h]
│       │   0x000045fc      adrp x8, segment.__DATA_CONST              ; 0x20000
│       │   0x00004600      ldr x8, [x8, 0x118]                        ; [0x20118:8]=0x8010000000000021 ; "!"
│       │   0x00004604      blr x8
│       │   0x00004608      ldr x0, [var_28h]
│      ┌──< 0x0000460c      cbz x0, 0x4620
│     ┌───< 0x00004610      b 0x4614
│     │││   ; CODE XREF from func.00004490 @ 0x4610(x)
│     └───> 0x00004614      ldr x8, [var_28h]
│      ││   0x00004618      str x8, [var_18h]
│     ┌───< 0x0000461c      b 0x4624
│     │││   ; CODE XREF from func.00004490 @ 0x460c(x)
│    ┌─└──> 0x00004620      b 0x46f4
│    ││ │   ; CODE XREF from func.00004490 @ 0x461c(x)
│    │└───> 0x00004624      ldr x0, [var_18h]
│    │  │   0x00004628      str x0, [var_10h]
│    │  │   0x0000462c      stur x0, [x29, -0x50]
│    │  │   0x00004630      sub x1, x29, 0x20
│    │  │   0x00004634      stur xzr, [x29, -0x20]
│    │  │   0x00004638      bl sym.imp.SecTrustEvaluateWithError
│    │ ┌──< 0x0000463c      tbz w0, 0, 0x46a8
│    │┌───< 0x00004640      b 0x4644
│    ││││   ; CODE XREF from func.00004490 @ 0x4640(x)
│    │└───> 0x00004644      ldr x0, [var_40h]
│    │ ││   0x00004648      bl sym.imp.swift_retain
│    │ ││   0x0000464c      mov x0, 0                                  ; int64_t arg_20h
│    │ ││   0x00004650      str x0, [sp]
│    │ ││   0x00004654      bl sym....sSo15NSURLCredentialCMa          ; func.0000415c
│    │ ││   0x00004658      mov x20, x0
│    │ ││   0x0000465c      ldr x0, [var_10h]
│    │ ││   0x00004660      adrp x8, segment.__DATA_CONST              ; 0x20000
│    │ ││   0x00004664      ldr x8, [x8, 0x120]                        ; [0x20120:8]=0x8010000000000022 ; "\""
│    │ ││   0x00004668      blr x8
│    │ ││   0x0000466c      ldr x0, [var_10h]                          ; int64_t arg1
│    │ ││   0x00004670      bl sym.__C.NSURLCredential                 ; func.000041bc
│    │ ││   0x00004674      ldr x20, [var_40h]
│    │ ││   0x00004678      ldr x8, [var_38h]
│    │ ││   0x0000467c      mov x1, x0
│    │ ││   0x00004680      ldr x0, [sp]
│    │ ││   0x00004684      str x1, [var_8h]
│    │ ││   0x00004688      blr x8
│    │ ││   0x0000468c      ldr x0, [var_8h]
│    │ ││   0x00004690      adrp x8, segment.__DATA_CONST              ; 0x20000
│    │ ││   0x00004694      ldr x8, [x8, 0x118]                        ; [0x20118:8]=0x8010000000000021 ; "!"
│    │ ││   0x00004698      blr x8
│    │ ││   0x0000469c      ldr x0, [var_40h]                          ; void *arg0
│    │ ││   0x000046a0      bl sym.imp.swift_release                   ; void swift_release(void *arg0)
│    │┌───< 0x000046a4      b 0x46d4
│    ││││   ; CODE XREF from func.00004490 @ 0x463c(x)
│    ││└──> 0x000046a8      ldr x20, [var_40h]
│    ││ │   0x000046ac      mov x0, x20
│    ││ │   0x000046b0      bl sym.imp.swift_retain
│    ││ │   0x000046b4      ldr x8, [var_38h]
│    ││ │   0x000046b8      mov w9, 2
│    ││ │   0x000046bc      mov x0, x9
│    ││ │   0x000046c0      mov x1, 0
│    ││ │   0x000046c4      blr x8
│    ││ │   0x000046c8      ldr x0, [var_40h]                          ; void *arg0
│    ││ │   0x000046cc      bl sym.imp.swift_release                   ; void swift_release(void *arg0)
│    ││┌──< 0x000046d0      b 0x46d4
│    ││││   ; CODE XREFS from func.00004490 @ 0x46a4(x), 0x46d0(x)
│    │└└──> 0x000046d4      sub x0, x29, 0x20                          ; int64_t arg1
│    │  │   0x000046d8      bl sym....sSo10CFErrorRefaSgWOh            ; func.00004750
│    │  │   0x000046dc      ldr x0, [var_10h]
│    │  │   0x000046e0      adrp x8, segment.__DATA_CONST              ; 0x20000
│    │  │   0x000046e4      ldr x8, [x8, 0x118]                        ; [0x20118:8]=0x8010000000000021 ; "!"
│    │  │   0x000046e8      blr x8
│    │ ┌──< 0x000046ec      b 0x4720
│    │ ││   ; CODE XREF from func.00004490 @ 0x45c0(x)
│    │┌─└─> 0x000046f0      b 0x46f4
│    │││    ; CODE XREFS from func.00004490 @ 0x4620(x), 0x46f0(x)
│    └└───> 0x000046f4      ldr x20, [var_40h]
│      │    0x000046f8      mov x0, x20
│      │    0x000046fc      bl sym.imp.swift_retain
│      │    0x00004700      ldr x8, [var_38h]
│      │    0x00004704      mov w9, 1
│      │    0x00004708      mov x0, x9
│      │    0x0000470c      mov x1, 0
│      │    0x00004710      blr x8
│      │    0x00004714      ldr x0, [var_40h]                          ; void *arg0
│      │    0x00004718      bl sym.imp.swift_release                   ; void swift_release(void *arg0)
│      │┌─< 0x0000471c      b 0x4720
│      ││   ; CODE XREFS from func.00004490 @ 0x46ec(x), 0x471c(x)
│      └└─> 0x00004720      ldur x9, [x29, -0x18]
│           0x00004724      adrp x8, segment.__DATA_CONST              ; 0x20000
│           0x00004728      ldr x8, [x8, 0x158]                        ; [0x20158:8]=0x8010000000000029 ; ")"
│           0x0000472c      ldr x8, [x8]
│           0x00004730      subs x8, x8, x9
│       ┌─< 0x00004734      b.eq 0x4740
│      ┌──< 0x00004738      b 0x473c
│      ││   ; CODE XREF from func.00004490 @ 0x4738(x)
│      └──> 0x0000473c      bl sym.imp.__stack_chk_fail                ; void stack_chk_fail(void)
│       │   ; CODE XREF from func.00004490 @ 0x4734(x)
│       └─> 0x00004740      ldp x29, x30, [var_e0h]
│           0x00004744      ldp x20, x19, [var_d0h]
│           0x00004748      add sp, sp, 0xf0                           ; 0x178000
└           0x0000474c      ret
