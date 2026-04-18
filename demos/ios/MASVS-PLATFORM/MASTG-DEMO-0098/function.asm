            ; CODE XREF from sym.func.100007c60 @ 0x100007c64(x)
┌ 580: sym.func.100004ad8 (int64_t arg1, int64_t arg2, int64_t arg_30h, int64_t arg_40h, int64_t arg_50h, int64_t arg_60h, int64_t arg_70h, int64_t arg_80h);
│           0x100004ad8      sub sp, sp, 0x80
│           0x100004adc      stp x26, x25, [var_30h]
│           0x100004ae0      stp x24, x23, [var_40h]
│           0x100004ae4      stp x22, x21, [var_50h]
│           0x100004ae8      stp x20, x19, [var_60h]
│           0x100004aec      stp x29, x30, [var_70h]
│           0x100004af0      add x29, sp, 0x70
│           0x100004af4      mov x20, x1                               ; arg2
│           0x100004af8      mov x22, x0                               ; arg1
│           0x100004afc      mov x26, 0x1b
│           0x100004b00      movk x26, 0xd000, lsl 48
│           0x100004b04      adrp x8, segment.__DATA                   ; 0x100014000
│           0x100004b08      ldr x0, [x8, 0x200]                       ; [0x100014200:4]=0
│                                                                      ; reloc.WKWebViewConfiguration ; void *arg0
│           0x100004b0c      bl sym.imp.objc_allocWithZone             ; void *objc_allocWithZone(void *arg0)
│           0x100004b10      bl fcn.10000c620
│           0x100004b14      mov x19, x0
│           0x100004b18      adrp x8, segment.__DATA                   ; 0x100014000
│           0x100004b1c      ldr x0, [x8, 0x208]                       ; [0x100014208:4]=0
│                                                                      ; reloc.WKWebpagePreferences ; void *arg0
│           0x100004b20      bl sym.imp.objc_allocWithZone             ; void *objc_allocWithZone(void *arg0)
│           0x100004b24      bl fcn.10000c620
│           0x100004b28      mov x21, x0
│           0x100004b2c      mov w2, 1
│           0x100004b30      bl fcn.10000c740
│           0x100004b34      mov x0, x19
│           0x100004b38      mov x2, x21
│           0x100004b3c      bl fcn.10000c760
│           0x100004b40      mov x0, x19
│           0x100004b44      bl fcn.10000c6a0
│           0x100004b48      mov x29, x29
│           0x100004b4c      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x100004b50      mov x23, x0
│           0x100004b54      mov w0, 1
│           0x100004b58      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSNumberCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSNumberCyF)
│           0x100004b5c      mov x24, x0
│           0x100004b60      adrp x8, 0x10000d000
│           0x100004b64      add x8, x8, 0xa70                         ; 0x10000da70 ; "allowFileAccessFromFileURLs"
│           0x100004b68      sub x8, x8, 0x20
│           0x100004b6c      orr x1, x8, 0x8000000000000000
│           0x100004b70      mov x0, 0x1b
│           0x100004b74      movk x0, 0xd000, lsl 48
│           0x100004b78      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│           0x100004b7c      mov x25, x0
│           0x100004b80      mov x0, x23
│           0x100004b84      mov x2, x24
│           0x100004b88      mov x3, x25
│           0x100004b8c      bl fcn.10000c780
│           0x100004b90      bl sym.imp.objc_release_x23
│           0x100004b94      bl sym.imp.objc_release_x24
│           0x100004b98      bl sym.imp.objc_release_x25
│           0x100004b9c      mov w0, 0
│           0x100004ba0      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSNumberCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSNumberCyF)
│           0x100004ba4      mov x23, x0
│           0x100004ba8      adrp x8, 0x10000d000
│           0x100004bac      add x8, x8, 0xa90                         ; 0x10000da90 ; "allowUniversalAccessFromFileURLs"
│           0x100004bb0      sub x8, x8, 0x20
│           0x100004bb4      add x0, x26, 5
│           0x100004bb8      orr x1, x8, 0x8000000000000000
│           0x100004bbc      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│           0x100004bc0      mov x24, x0
│           0x100004bc4      mov x0, x19
│           0x100004bc8      mov x2, x23
│           0x100004bcc      mov x3, x24
│           0x100004bd0      bl fcn.10000c780
│           0x100004bd4      bl sym.imp.objc_release_x23
│           0x100004bd8      bl sym.imp.objc_release_x24
│           0x100004bdc      adrp x8, segment.__DATA                   ; 0x100014000
│           0x100004be0      ldr x0, [x8, 0x210]                       ; [0x100014210:4]=0
│                                                                      ; reloc.WKWebView ; void *arg0
│           0x100004be4      bl sym.imp.objc_allocWithZone             ; void *objc_allocWithZone(void *arg0)
│           0x100004be8      fmov d0, xzr
│           0x100004bec      fmov d1, xzr
│           0x100004bf0      fmov d2, xzr
│           0x100004bf4      fmov d3, xzr
│           0x100004bf8      mov x2, x19
│           0x100004bfc      bl fcn.10000c640
│           0x100004c00      mov x24, x0
│           0x100004c04      adrp x8, segment.__DATA                   ; 0x100014000
│           0x100004c08      ldr x0, [x8, 0x218]                       ; [0x100014218:4]=0
│                                                                      ; reloc.UIViewController ; void *arg0
│           0x100004c0c      bl sym.imp.objc_allocWithZone             ; void *objc_allocWithZone(void *arg0)
│           0x100004c10      bl fcn.10000c620
│           0x100004c14      mov x23, x0
│           0x100004c18      mov x2, x24
│           0x100004c1c      bl fcn.10000c7a0
│           0x100004c20      mov x0, 0                                 ; int64_t arg1
│           0x100004c24      bl sym.func.100004d48
│       ┌─< 0x100004c28      cbz x0, 0x100004cd8
│       │   0x100004c2c      mov x25, x0
│       │   0x100004c30      adrp x0, segment.__DATA_CONST             ; 0x100010000
│       │   0x100004c34      add x0, x0, 0x8b8                         ; 0x1000108b8
│       │                                                              ; aav.0x1000108b8
│       │   0x100004c38      mov w1, 0x28                              ; '('
│       │   0x100004c3c      mov w2, 7
│       │   0x100004c40      bl sym.imp.swift_allocObject
│       │   0x100004c44      stp x22, x20, [x0, 0x10]                  ; 0x1000108d0
│       │                                                              ; aav.0x1000108d0
│       │   0x100004c48      str x24, [x0, 0x20]
│       │   0x100004c4c      adrp x8, 0x100007000
│       │   0x100004c50      add x8, x8, 0xd54
│       │   0x100004c54      stp x8, x0, [var_20h]
│       │   0x100004c58      adrp x8, segment.__DATA_CONST             ; 0x100010000
│       │   0x100004c5c      ldr x8, [x8, 0x1d0]                       ; [0x1000101d0:4]=0
│       │                                                              ; reloc._NSConcreteStackBlock
│       │   0x100004c60      str x8, [sp]
│       │   0x100004c64      adrp x8, sym.imp.Foundation.Encoding.utf8_...vgZ_ ; 0x10000c000
│       │   0x100004c68      ldr d0, [x8, 0x820]                       ; [0x10000c820:4]=0x42000000 ; section.3.__TEXT.__const
│       │                                                              [03] -r-x section size 1700 named 3.__TEXT.__const
│       │   0x100004c6c      str d0, [var_0h_2]
│       │   0x100004c70      adrp x8, sym.func.100004000               ; 0x100004000
│       │   0x100004c74      add x8, x8, 0xd1c
│       │   0x100004c78      adrp x9, segment.__DATA_CONST             ; 0x100010000
│       │   0x100004c7c      add x9, x9, 0x8d0                         ; 0x1000108d0
│       │                                                              ; aav.0x1000108d0
│       │   0x100004c80      stp x8, x9, [var_0hx10]
│       │   0x100004c84      mov x0, sp
│       │   0x100004c88      bl sym.imp._Block_copy
│       │   0x100004c8c      mov x22, x0
│       │   0x100004c90      ldr x26, [var_28h]
│       │   0x100004c94      mov x0, x20
│       │   0x100004c98      bl sym.imp.swift_retain
│       │   0x100004c9c      bl sym.imp.objc_retain_x24
│       │   0x100004ca0      mov x20, x0
│       │   0x100004ca4      mov x0, x26                               ; void *arg0
│       │   0x100004ca8      bl sym.imp.swift_release                  ; void swift_release(void *arg0)
│       │   0x100004cac      mov x0, x25
│       │   0x100004cb0      mov x2, x23
│       │   0x100004cb4      mov w3, 1
│       │   0x100004cb8      mov x4, x22
│       │   0x100004cbc      bl fcn.10000c6c0
│       │   0x100004cc0      mov x0, x22
│       │   0x100004cc4      bl sym.imp._Block_release
│       │   0x100004cc8      bl sym.imp.objc_release_x20
│       │   0x100004ccc      bl sym.imp.objc_release_x23
│       │   0x100004cd0      bl sym.imp.objc_release_x25
│      ┌──< 0x100004cd4      b 0x100004cf8
│      ││   ; CODE XREF from sym.func.100004ad8 @ 0x100004c28(x)
│      │└─> 0x100004cd8      adrp x8, 0x10000d000
│      │    0x100004cdc      add x8, x8, 0xac0                         ; 0x10000dac0 ; "Failed to present, no view controller."
│      │    0x100004ce0      sub x8, x8, 0x20
│      │    0x100004ce4      add x0, x26, 0xb
│      │    0x100004ce8      orr x1, x8, 0x8000000000000000
│      │    0x100004cec      blr x22
│      │    0x100004cf0      bl sym.imp.objc_release_x24
│      │    0x100004cf4      bl sym.imp.objc_release_x23
│      │    ; CODE XREF from sym.func.100004ad8 @ 0x100004cd4(x)
│      └──> 0x100004cf8      bl sym.imp.objc_release_x19
│           0x100004cfc      bl sym.imp.objc_release_x21
│           0x100004d00      ldp x29, x30, [var_70h]
│           0x100004d04      ldp x20, x19, [var_60h]
│           0x100004d08      ldp x22, x21, [var_50h]
│           0x100004d0c      ldp x24, x23, [var_40h]
│           0x100004d10      ldp x26, x25, [var_30h]
│           0x100004d14      add sp, sp, 0x80                          ; 0x178000
└           0x100004d18      ret
