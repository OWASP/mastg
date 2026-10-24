            ; CODE XREF from func.1000062e4 @ 0x1000062ec(x) ; sym.MASTestApp.MastgTest.showHtmlRegistrationView._6E8AB2C58CE173A727EF27CB85DF8CD8.username.completion_...A_
┌ 792: E8AB2C.CE173A727EF27CB85DF8CD8.username.completion (int64_t arg1, int64_t arg2, int64_t arg3, int64_t arg4, int64_t arg_10h, int64_t arg_20h, int64_t arg_30h, int64_t arg_40h, int64_t arg_50h);
; MASTestApp.MastgTest.showHtmlRegistrationView._6E8AB2C58CE173A727EF2
; 7CB85DF8CD8.username.completion
│           0x100004d24      stp x28, x27, [sp, -0x60]!
│           0x100004d28      stp x26, x25, [var_60h]
│           0x100004d2c      stp x24, x23, [arg_50h]
│           0x100004d30      stp x22, x21, [arg_40h]
│           0x100004d34      stp x20, x19, [arg_30h]
│           0x100004d38      stp x29, x30, [arg_20h]
│           0x100004d3c      add x29, sp, 0x50
│           0x100004d40      sub sp, sp, 0x10
│           0x100004d44      mov x24, x3                               ; arg4
│           0x100004d48      mov x23, x2                               ; arg3
│           0x100004d4c      mov x26, x1                               ; arg2
│           0x100004d50      mov x27, x0                               ; arg1
│           0x100004d54      mov x0, 0
│           0x100004d58      bl sym.imp.Foundation.URL...VMa
│           0x100004d5c      mov x19, x0
│           0x100004d60      ldur x28, [x0, -8]
│           0x100004d64      ldr x8, [x28, 0x40]
│           0x100004d68      mov x9, x8
│           0x100004d6c      adrp x16, segment.__DATA_CONST            ; 0x100014000
│           0x100004d70      ldr x16, [x16, 0x50]                      ; [0x100014050:4]=9 ; "\t"
│           0x100004d74      blr x16
│           0x100004d78      mov x9, sp
│           0x100004d7c      add x8, x8, 0xf
│           0x100004d80      and x8, x8, 0xfffffffffffffff0
│           0x100004d84      sub x22, x9, x8
│           0x100004d88      mov sp, x22
│           0x100004d8c      adrp x0, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x100004d90      add x0, x0, 0x2a8                         ; int64_t arg1
│           0x100004d94      bl sym.___swift_instantiateConcreteTypeFromMangledName
│           0x100004d98      ldur x8, [x0, -8]                         ; [0x1000182a0:4]=0
│                                                                      ; sym....sSo11UITextFieldCML
│                                                                      ...sSo11UITextFieldCML
│           0x100004d9c      ldr x8, [x8, 0x40]
│           0x100004da0      mov x9, x8
│           0x100004da4      adrp x16, segment.__DATA_CONST            ; 0x100014000
│           0x100004da8      ldr x16, [x16, 0x50]                      ; [0x100014050:4]=9 ; "\t"
│           0x100004dac      blr x16
│           0x100004db0      mov x9, sp
│           0x100004db4      add x10, x8, 0xf
│           0x100004db8      and x12, x10, 0xfffffffffffffff0
│           0x100004dbc      sub x25, x9, x12
│           0x100004dc0      mov sp, x25
│           0x100004dc4      mov x9, x8
│           0x100004dc8      adrp x16, segment.__DATA_CONST            ; 0x100014000
│           0x100004dcc      ldr x16, [x16, 0x50]                      ; [0x100014050:4]=9 ; "\t"
│           0x100004dd0      blr x16
│           0x100004dd4      mov x8, sp
│           0x100004dd8      sub x21, x8, x12
│           0x100004ddc      mov sp, x21
│           0x100004de0      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│           0x100004de4      ldr x8, [x8, 0x1d0]                       ; [0x1000181d0:4]=0
│                                                                      ; sym.MASTestApp.MastgTest.fileURL._6E8AB2C58CE173A727EF27CB85DF8CD8_...z_
│                                                                      MASTestApp.MastgTest.fileURL._6E8AB2C58CE173A727EF27CB85DF8CD8(...z)
│           0x100004de8      cmn x8, 1
│       ┌─< 0x100004dec      b.ne 0x10000500c
│       │   ; CODE XREF from func.100004d24 @ 0x100005020(x)
│      ┌──> 0x100004df0      adrp x1, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│      ╎│   0x100004df4      add x1, x1, 0x1d8                         ; int64_t arg2
│      ╎│   0x100004df8      mov x0, x19                               ; int64_t arg1
│      ╎│   0x100004dfc      bl sym.___swift_project_value_buffer
│      ╎│   0x100004e00      mov x20, x0
│      ╎│   0x100004e04      bl sym.imp.Foundation.URL.absoluteString_...vg_
│      ╎│   0x100004e08      stp x0, x1, [x29, -0x60]
│      ╎│   0x100004e0c      mov x0, 0x753f                            ; '?u'
│      ╎│   0x100004e10      movk x0, 0x6573, lsl 16                   ; 'se'
│      ╎│   0x100004e14      movk x0, 0x6e72, lsl 32                   ; 'rn'
│      ╎│   0x100004e18      movk x0, 0x6d61, lsl 48                   ; 'am'
│      ╎│   0x100004e1c      sub x20, x29, 0x60
│      ╎│   0x100004e20      mov x1, 0x3d65                            ; 'e='
│      ╎│   0x100004e24      movk x1, 0xea00, lsl 48
│      ╎│   0x100004e28      bl sym.imp.append_...ySSF_                ; append(...ySSF)
│      ╎│   0x100004e2c      mov x0, x27
│      ╎│   0x100004e30      mov x1, x26
│      ╎│   0x100004e34      bl sym.imp.append_...ySSF_                ; append(...ySSF)
│      ╎│   0x100004e38      ldp x0, x20, [x29, -0x60]
│      ╎│   0x100004e3c      mov x8, x21
│      ╎│   0x100004e40      mov x1, x20
│      ╎│   0x100004e44      bl sym.imp.Foundation.URL.string_...cfC_  ; Foundation.URL.string(...cfC)
│      ╎│   0x100004e48      mov x0, x20                               ; void *arg0
│      ╎│   0x100004e4c      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│      ╎│   0x100004e50      mov x0, x21                               ; int64_t arg1
│      ╎│   0x100004e54      mov x1, x25                               ; int64_t arg2
│      ╎│   0x100004e58      bl sym.Foundation.URL:_GenericAccessorW.bool____GenericAccessor ; func.1000062f0
│      ╎│   0x100004e5c      ldr x8, [x28, 0x30]
│      ╎│   0x100004e60      mov x0, x25
│      ╎│   0x100004e64      mov w1, 1
│      ╎│   0x100004e68      mov x2, x19
│      ╎│   0x100004e6c      blr x8
│      ╎│   0x100004e70      cmp w0, 1
│     ┌───< 0x100004e74      b.ne 0x100004ea4
│     │╎│   0x100004e78      mov x0, x25                               ; int64_t arg1
│     │╎│   0x100004e7c      bl sym.Foundation.URL:_GenericAccessorW.bool____GenericAccessor__1 ; func.100006338
│     │╎│   0x100004e80      adrp x8, 0x10000b000
│     │╎│   0x100004e84      add x8, x8, 0x3d0                         ; 0x10000b3d0 ; "Failed create URL object."
│     │╎│   0x100004e88      sub x8, x8, 0x20
│     │╎│   0x100004e8c      orr x1, x8, 0x8000000000000000
│     │╎│   0x100004e90      mov x0, 0x19
│     │╎│   0x100004e94      movk x0, 0xd000, lsl 48
│     │╎│   0x100004e98      mov x20, x24
│     │╎│   0x100004e9c      blr x23
│    ┌────< 0x100004ea0      b 0x100004fe4
│    ││╎│   ; CODE XREF from func.100004d24 @ 0x100004e74(x)
│    │└───> 0x100004ea4      ldr x8, [x28, 0x20]
│    │ ╎│   0x100004ea8      mov x0, x22
│    │ ╎│   0x100004eac      mov x1, x25
│    │ ╎│   0x100004eb0      mov x2, x19
│    │ ╎│   0x100004eb4      blr x8
│    │ ╎│   0x100004eb8      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│    │ ╎│   0x100004ebc      ldr x0, [x8, 0x190]                       ; [0x100018190:4]=252
│    │ ╎│                                                              ; reloc.WKWebView ; void *arg0
│    │ ╎│   0x100004ec0      bl sym.imp.objc_allocWithZone             ; void *objc_allocWithZone(void *arg0)
│    │ ╎│   0x100004ec4      adrp x27, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│    │ ╎│   0x100004ec8      ldr x1, [x27, 0x110]                      ; [0x100018110:4]=0xc0ee ; reloc.fixup.init ; char *selector
│    │ ╎│   0x100004ecc      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│    │ ╎│   0x100004ed0      mov x25, x0
│    │ ╎│   0x100004ed4      mov x20, x22
│    │ ╎│   0x100004ed8      bl sym.imp.Foundation.URL._bridgeToObjectiveC.NSURL_...F_ ; Foundation.URL._bridgeToObjectiveC.NSURL(...F)
│    │ ╎│   0x100004edc      mov x26, x0
│    │ ╎│   0x100004ee0      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│    │ ╎│   0x100004ee4      ldr x8, [x8, 0x1b0]                       ; [0x1000181b0:4]=0
│    │ ╎│                                                              ; sym.MASTestApp.MastgTest.docDir._6E8AB2C58CE173A727EF27CB85DF8CD8_...z_
│    │ ╎│                                                              [24] -rw- section size 929 named 24.__DATA.__data
│    │ ╎│   0x100004ee8      cmn x8, 1
│    │┌───< 0x100004eec      b.ne 0x100005024
│    ││╎│   ; CODE XREF from func.100004d24 @ 0x100005038(x)
│   ┌─────> 0x100004ef0      adrp x1, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│   ╎││╎│   0x100004ef4      add x1, x1, 0x1b8                         ; int64_t arg2
│   ╎││╎│   0x100004ef8      mov x0, x19                               ; int64_t arg1
│   ╎││╎│   0x100004efc      bl sym.___swift_project_value_buffer
│   ╎││╎│   0x100004f00      mov x20, x0
│   ╎││╎│   0x100004f04      bl sym.imp.Foundation.URL._bridgeToObjectiveC.NSURL_...F_ ; Foundation.URL._bridgeToObjectiveC.NSURL(...F)
│   ╎││╎│   0x100004f08      mov x20, x0
│   ╎││╎│   0x100004f0c      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│   ╎││╎│   0x100004f10      ldr x1, [x8, 0x118]                       ; [0x100018118:4]=0xc0ff ; reloc.fixup.loadFileURL:allowingReadAccessT ; char *selector
│   ╎││╎│   0x100004f14      mov x0, x25                               ; void *instance
│   ╎││╎│   0x100004f18      mov x2, x26
│   ╎││╎│   0x100004f1c      mov x3, x20
│   ╎││╎│   0x100004f20      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│   ╎││╎│   0x100004f24      mov x29, x29
│   ╎││╎│   0x100004f28      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│   ╎││╎│   0x100004f2c      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│   ╎││╎│   0x100004f30      mov x0, x26                               ; void *instance
│   ╎││╎│   0x100004f34      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│   ╎││╎│   0x100004f38      mov x0, x20                               ; void *instance
│   ╎││╎│   0x100004f3c      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│   ╎││╎│   0x100004f40      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│   ╎││╎│   0x100004f44      ldr x0, [x8, 0x198]                       ; [0x100018198:4]=253
│   ╎││╎│                                                              ; reloc.UIViewController ; void *arg0
│   ╎││╎│   0x100004f48      bl sym.imp.objc_allocWithZone             ; void *objc_allocWithZone(void *arg0)
│   ╎││╎│   0x100004f4c      ldr x1, [x27, 0x110]                      ; [0x100018110:4]=0xc0ee ; reloc.fixup.init ; char *selector
│   ╎││╎│   0x100004f50      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│   ╎││╎│   0x100004f54      mov x26, x0
│   ╎││╎│   0x100004f58      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│   ╎││╎│   0x100004f5c      ldr x1, [x8, 0x120]                       ; [0x100018120:4]=0xc191 ; reloc.fixup.setView: ; char *selector
│   ╎││╎│   0x100004f60      mov x2, x25
│   ╎││╎│   0x100004f64      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│   ╎││╎│   0x100004f68      mov x0, 0                                 ; int64_t arg1
│   ╎││╎│   0x100004f6c      bl sym.MASTestApp.MastgTest.topViewController._6E8AB2C58CE173A727EF27CB85DF8CD8.base.UIView...G0CSgAI_tFZ ; func.10000503c
│  ┌──────< 0x100004f70      cbz x0, 0x100004fa0
│  │╎││╎│   0x100004f74      adrp x8, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│  │╎││╎│   0x100004f78      ldr x1, [x8, 0xb0]                        ; [0x1000180b0:4]=0xc124 ; "$\xc1" ; char *selector
│  │╎││╎│   0x100004f7c      mov x20, x0
│  │╎││╎│   0x100004f80      mov x2, x26
│  │╎││╎│   0x100004f84      mov w3, 1
│  │╎││╎│   0x100004f88      mov x4, 0
│  │╎││╎│   0x100004f8c      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│  │╎││╎│   0x100004f90      mov x0, x25                               ; void *instance
│  │╎││╎│   0x100004f94      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│  │╎││╎│   0x100004f98      mov x0, x20
│ ┌───────< 0x100004f9c      b 0x100004fc8
│ ││╎││╎│   ; CODE XREF from func.100004d24 @ 0x100004f70(x)
│ │└──────> 0x100004fa0      adrp x8, 0x10000b000
│ │ ╎││╎│   0x100004fa4      add x8, x8, 0x3f0                         ; 0x10000b3f0 ; "Failed to present: no view controller."
│ │ ╎││╎│   0x100004fa8      sub x8, x8, 0x20
│ │ ╎││╎│   0x100004fac      mov x9, 0x19
│ │ ╎││╎│   0x100004fb0      movk x9, 0xd000, lsl 48
│ │ ╎││╎│   0x100004fb4      add x0, x9, 0xd
│ │ ╎││╎│   0x100004fb8      orr x1, x8, 0x8000000000000000
│ │ ╎││╎│   0x100004fbc      mov x20, x24
│ │ ╎││╎│   0x100004fc0      blr x23
│ │ ╎││╎│   0x100004fc4      mov x0, x25
│ │ ╎││╎│   ; CODE XREF from func.100004d24 @ 0x100004f9c(x)
│ └───────> 0x100004fc8      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│   ╎││╎│   0x100004fcc      mov x0, x26                               ; void *instance
│   ╎││╎│   0x100004fd0      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│   ╎││╎│   0x100004fd4      ldr x8, [x28, 8]
│   ╎││╎│   0x100004fd8      mov x0, x22
│   ╎││╎│   0x100004fdc      mov x1, x19
│   ╎││╎│   0x100004fe0      blr x8
│   ╎││╎│   ; CODE XREF from func.100004d24 @ 0x100004ea0(x)
│   ╎└────> 0x100004fe4      mov x0, x21                               ; int64_t arg1
│   ╎ │╎│   0x100004fe8      bl sym.Foundation.URL:_GenericAccessorW.bool____GenericAccessor__1 ; func.100006338
│   ╎ │╎│   0x100004fec      sub sp, x29, 0x50
│   ╎ │╎│   0x100004ff0      ldp x29, x30, [arg_20h]
│   ╎ │╎│   0x100004ff4      ldp x20, x19, [arg_30h]
│   ╎ │╎│   0x100004ff8      ldp x22, x21, [arg_40h]
│   ╎ │╎│   0x100004ffc      ldp x24, x23, [arg_50h]
│   ╎ │╎│   ; DATA XREFS from func.1000049b4 @ 0x100004a30(r), 0x100004ad4(r)
│   ╎ │╎│   ; DATA XREFS from func.1000056d8 @ 0x1000058e0(r), 0x100005940(r)
│   ╎ │╎│   ; DATA XREF from func.100005ab0 @ 0x100005b18(r)
│   ╎ │╎│   0x100005000      ldp x26, x25, [var_60h]
│   ╎ │╎│   0x100005004      ldp x28, x27, [sp], 0x60
│   ╎ │╎│   0x100005008      ret
│   ╎ │╎│   ; CODE XREF from func.100004d24 @ 0x100004dec(x)
│   ╎ │╎└─> 0x10000500c      adrp x0, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│   ╎ │╎    0x100005010      add x0, x0, 0x1d0
│   ╎ │╎    0x100005014      adrp x1, sym.MASTestApp.MastgTest.docDir._6E8AB2C58CE173A727EF27CB85DF8CD8_...Z_ ; 0x100004000
│   ╎ │╎    0x100005018      add x1, x1, 0x144
│   ╎ │╎    0x10000501c      bl sym.imp.swift_once
│   ╎ │└──< 0x100005020      b 0x100004df0
│   ╎ │     ; CODE XREF from func.100004d24 @ 0x100004eec(x)
│   ╎ └───> 0x100005024      adrp x0, sym.__METACLASS_DATA__TtC10MASTestAppP33_9471609302C95FC8EC1D59DD4CF2A2DB19ResourceBundleClass ; 0x100018000
│   ╎       0x100005028      add x0, x0, 0x1b0
│   ╎       0x10000502c      adrp x1, sym.MASTestApp.MastgTest.docDir._6E8AB2C58CE173A727EF27CB85DF8CD8_...Z_ ; 0x100004000
│   ╎       0x100005030      add x1, x1, 0
│   ╎       0x100005034      bl sym.imp.swift_once
└   └─────< 0x100005038      b 0x100004ef0
