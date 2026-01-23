            ; CALL XREF from sym.func.10000ad8c @ 0x10000adc4(x)
┌ 5628: sym.func.1000086f4 (int64_t arg1, int64_t arg2, int64_t arg3, int64_t arg_10h, int64_t arg_20h, int64_t arg_30h, int64_t arg_40h, int64_t arg_50h);
│           0x1000086f4      stp x28, x27, [sp, -0x60]!
│           0x1000086f8      stp x26, x25, [var_0hx10]
│           0x1000086fc      stp x24, x23, [var_0hx20]
│           0x100008700      stp x22, x21, [var_0hx30]
│           0x100008704      stp x20, x19, [var_0hx40]
│           0x100008708      stp x29, x30, [var_0hx50]
│           0x10000870c      add x29, sp, 0x50
│           0x100008710      sub sp, sp, 0x160
│           0x100008714      mov x19, sp
│           0x100008718      stp x1, x2, [x19, 0x30]                   ; arg3
│           0x10000871c      str x0, [x19, 0x28]                       ; arg1
│           0x100008720      mov x26, 0x33                             ; '3'
│           0x100008724      movk x26, 0xe900, lsl 48
│           0x100008728      mov x24, 0x23                             ; '#'
│           0x10000872c      movk x24, 0xd000, lsl 48
│           0x100008730      mov x25, 0x4f54                           ; 'TO'
│           0x100008734      movk x25, 0x454b, lsl 16                  ; 'KE'
│           0x100008738      movk x25, 0x3d4e, lsl 32                  ; 'N='
│           0x10000873c      movk x25, 0x3231, lsl 48                  ; '12'
│           0x100008740      mov x0, 0
│           0x100008744      bl sym Foundation...8EncodingVMa          ; sym.imp.Foundation...8EncodingVMa
│           0x100008748      ldur x8, [x0, -8]
│           0x10000874c      stp x8, x0, [x19, 0x18]
│           0x100008750      ldr x8, [x8, 0x40]
│           0x100008754      mov x9, x8
│           0x100008758      adrp x16, reloc.Foundation.Data._bridgeToObjectiveC.NSData...F ; 0x100014000
│           0x10000875c      ldr x16, [x16, 0x4e8]                     ; [0x1000144e8:4]=156
│                                                                      ; reloc.__chkstk_darwin
│           0x100008760      blr x16
│           0x100008764      mov x9, sp
│           0x100008768      add x8, x8, 0xf
│           0x10000876c      and x8, x8, 0xfffffffffffffff0
│           0x100008770      sub x8, x9, x8
│           0x100008774      str x8, [x19, 0x10]
│           0x100008778      mov sp, x8
│           0x10000877c      adrp x0, segment.__DATA                   ; 0x100018000
│           0x100008780      add x0, x0, 0xd8                          ; int64_t arg1
│           0x100008784      bl sym.func.100009cf0
│           0x100008788      mov x21, x0
│           0x10000878c      mov w1, 0x40                              ; '@'
│           0x100008790      mov w2, 7
│           0x100008794      bl sym.imp.swift_allocObject
│           0x100008798      mov x22, x0
│           0x10000879c      adrp x8, 0x10000d000
│           0x1000087a0      ldr q0, [x8, 0xdf0]                       ; [0x10000ddf0:4]=1
│                                                                      ; section.2.__TEXT.__const
│                                                                      [02] -r-x section size 436 named 2.__TEXT.__const
│           0x1000087a4      str q0, [x19, 0x40]
│           0x1000087a8      str q0, [x0, 0x10]
│           0x1000087ac      mov x23, -0x2000000000000000
│           0x1000087b0      stp xzr, x23, [x19, 0x58]
│           0x1000087b4      add x20, x19, 0x58
│           0x1000087b8      mov w0, 0x1c
│           0x1000087bc      bl sym _StringGuts.grow...SiF             ; sym.imp._StringGuts.grow...SiF
│           0x1000087c0      ldr x0, [x19, 0x60]                       ; void *arg0
│           0x1000087c4      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x1000087c8      adrp x28, reloc.Foundation.Data._bridgeToObjectiveC.NSData...F ; 0x100014000
│           0x1000087cc      ldr x28, [x28, 0x400]                     ; [0x100014400:4]=127
│                                                                      ; reloc....SSN
│           0x1000087d0      str x28, [x22, 0x38]
│           0x1000087d4      adrp x8, 0x10000e000
│           0x1000087d8      add x8, x8, 0x8f0                         ; 0x10000e8f0 ; "print: Leaking TOKEN=123 from print"
│           0x1000087dc      sub x8, x8, 0x20
│           0x1000087e0      orr x8, x8, 0x8000000000000000
│           0x1000087e4      stp x24, x8, [x22, 0x20]
│           0x1000087e8      mov x0, x22
│           0x1000087ec      mov w1, 0x20
│           0x1000087f0      mov x2, -0x1f00000000000000
│           0x1000087f4      mov w3, 0xa
│           0x1000087f8      mov x4, -0x1f00000000000000
│           0x1000087fc      bl sym print.separator.terminator...2StF  ; sym.imp.print.separator.terminator...2StF
│           0x100008800      mov x0, x22                               ; void *arg0
│           0x100008804      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x100008808      mov x0, x21
│           0x10000880c      mov w1, 0x40                              ; '@'
│           0x100008810      mov w2, 7
│           0x100008814      bl sym.imp.swift_allocObject
│           0x100008818      mov x21, x0
│           0x10000881c      ldr q0, [x19, 0x40]
│           0x100008820      str q0, [x0, 0x10]
│           0x100008824      stp xzr, x23, [x19, 0x58]
│           0x100008828      add x20, x19, 0x58
│           0x10000882c      mov w0, 0x26                              ; '&'
│           0x100008830      bl sym _StringGuts.grow...SiF             ; sym.imp._StringGuts.grow...SiF
│           0x100008834      ldr x0, [x19, 0x60]                       ; void *arg0
│           0x100008838      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x10000883c      str x28, [x21, 0x38]
│           0x100008840      add x8, x24, 0xa
│           0x100008844      adrp x9, 0x10000e000
│           0x100008848      add x9, x9, 0x920                         ; 0x10000e920 ; "debugPrint: Leaking TOKEN=123 from debugPrint"
│           0x10000884c      sub x9, x9, 0x20
│           0x100008850      orr x9, x9, 0x8000000000000000
│           0x100008854      stp x8, x9, [x21, 0x20]
│           0x100008858      mov x0, x21
│           0x10000885c      mov w1, 0x20
│           0x100008860      mov x2, -0x1f00000000000000
│           0x100008864      mov w3, 0xa
│           0x100008868      mov x4, -0x1f00000000000000
│           0x10000886c      bl sym debugPrint.separator.terminator...2StF ; sym.imp.debugPrint.separator.terminator...2StF
│           0x100008870      mov x0, x21                               ; void *arg0
│           0x100008874      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x100008878      stp x25, x26, [x29, -0x68]
│           0x10000887c      sub x1, x24, 6
│           0x100008880      adrp x8, 0x10000e000
│           0x100008884      add x8, x8, 0x950                         ; 0x10000e950 ; "dump: Leaking token from dump"
│           0x100008888      sub x8, x8, 0x20
│           0x10000888c      orr x2, x8, 0x8000000000000000
│           0x100008890      add x8, x19, 0x58
│           0x100008894      sub x0, x29, 0x68
│           0x100008898      mov x3, 0
│           0x10000889c      mov x4, 0x7fffffffffffffff
│           0x1000088a0      mov x5, 0x7fffffffffffffff
│           0x1000088a4      mov x6, x28
│           0x1000088a8      bl sym dump.name.indent__String...SgS3itlF ; sym.imp.dump.name.indent__String...SgS3itlF
│           0x1000088ac      ldr x0, [x19, 0x60]                       ; void *arg0
│           0x1000088b0      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x1000088b4      stp xzr, x23, [x19, 0x58]
│           0x1000088b8      add x20, x19, 0x58
│           0x1000088bc      mov w0, 0x1c
│           0x1000088c0      bl sym _StringGuts.grow...SiF             ; sym.imp._StringGuts.grow...SiF
│           0x1000088c4      ldr x0, [x19, 0x60]                       ; void *arg0
│           0x1000088c8      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x1000088cc      adrp x8, 0x10000e000
│           0x1000088d0      add x8, x8, 0x970                         ; 0x10000e970 ; "NSLog: Leaking TOKEN=123 from NSLog"
│           0x1000088d4      sub x8, x8, 0x20
│           0x1000088d8      orr x1, x8, 0x8000000000000000
│           0x1000088dc      adrp x2, reloc.Foundation.Data._bridgeToObjectiveC.NSData...F ; 0x100014000
│           0x1000088e0      ldr x2, [x2, 0x4f8]                       ; [0x1000144f8:4]=158
│                                                                      ; reloc._swiftEmptyArrayStorage
│           0x1000088e4      mov x0, 0x23                              ; '#'
│           0x1000088e8      movk x0, 0xd000, lsl 48
│           0x1000088ec      bl sym Foundation.NSLogCVarArg...dtF      ; sym.imp.Foundation.NSLogCVarArg...dtF
│           0x1000088f0      mov x0, 0
│           0x1000088f4      bl sym.func.100009d30
│           0x1000088f8      mov x20, x0
│           0x1000088fc      bl sym OS_os_log.allocator...0E7defaultABvgZ ; sym.imp.OS_os_log.allocator...0E7defaultABvgZ
│           0x100008900      mov x20, x0
│           0x100008904      bl sym os_log_type_t...0A0E4infoABvgZ     ; sym.imp.os_log_type_t...0A0E4infoABvgZ
│           0x100008908      mov x21, x0
│           0x10000890c      adrp x0, segment.__DATA                   ; 0x100018000
│           0x100008910      add x0, x0, 0xe8                          ; int64_t arg1
│           0x100008914      bl sym.func.100009cf0
│           0x100008918      str x0, [x19]
│           0x10000891c      mov w1, 0x48                              ; 'H'
│           0x100008920      mov w2, 7
│           0x100008924      bl sym.imp.swift_allocObject
│           0x100008928      mov x22, x0
│           0x10000892c      ldr q0, [x19, 0x40]
│           0x100008930      str q0, [x0, 0x10]
│           0x100008934      str x28, [x0, 0x38]
│           0x100008938      bl sym.func.100009d6c
│           0x10000893c      str x0, [x19, 8]
│           0x100008940      str x0, [x22, 0x40]
│           0x100008944      stp x25, x26, [x22, 0x20]
│           0x100008948      adrp x0, 0x10000e000
│           0x10000894c      add x0, x0, 0x9a0                         ; 0x10000e9a0 ; "os_log: Leaking %{public}@ from os_log"
│           0x100008950      adrp x3, sym.__mh_execute_header          ; 0x100000000
│           0x100008954      add x3, x3, 0
│           0x100008958      mov w1, 0x26                              ; '&'
│           0x10000895c      mov w2, 2
│           0x100008960      mov x4, x20
│           0x100008964      mov x5, x21
│           0x100008968      mov x6, x22
│           0x10000896c      bl sym osbool...dtF                       ; sym.imp.osbool...dtF
│           0x100008970      bl sym.imp.objc_release_x20
│           0x100008974      mov x0, x22                               ; void *arg0
│           0x100008978      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x10000897c      adrp x8, segment.__DATA                   ; 0x100018000
│           0x100008980      ldr x8, [x8, 0xb8]                        ; [0x1000180b8:4]=0
│                                                                      ; section.22.__DATA.__data
│                                                                      [22] -rw- section size 776 named 22.__DATA.__data
│           0x100008984      cmn x8, 1
│       ┌─< 0x100008988      b.ne 0x100009cd8
│       │   ; CODE XREF from sym.func.1000086f4 @ 0x100009cec(x)
│      ┌──> 0x10000898c      mov x0, 0
│      ╎│   0x100008990      bl sym os.Logger...VMa                    ; sym.imp.os.Logger...VMa
│      ╎│   0x100008994      adrp x1, segment.__DATA                   ; 0x100018000
│      ╎│   0x100008998      add x1, x1, 0xc0                          ; int64_t arg2
│      ╎│   0x10000899c      bl sym.func.100009db0
│      ╎│   0x1000089a0      mov x20, x0
│      ╎│   0x1000089a4      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x1000089a8      mov x21, x0
│      ╎│   0x1000089ac      bl sym os_log_type_t...0A0E5debugABvgZ    ; sym.imp.os_log_type_t...0A0E5debugABvgZ
│      ╎│   0x1000089b0      and w22, w0, 0xff
│      ╎│   0x1000089b4      mov x0, x21
│      ╎│   0x1000089b8      mov x1, x22
│      ╎│   0x1000089bc      bl sym.imp.os_log_type_enabled
│      ╎│   0x1000089c0      adrp x28, 0x10000d000
│      ╎│   0x1000089c4      adrp x26, reloc.Foundation.Data._bridgeToObjectiveC.NSData...F ; 0x100014000
│      ╎│   0x1000089c8      ldr x26, [x26, 0x4c0]                     ; [0x1000144c0:4]=151
│      ╎│                                                              ; reloc....ypN
│     ┌───< 0x1000089cc      cbz w0, 0x100008a74
│     │╎│   0x1000089d0      mov w0, 0xc
│     │╎│   0x1000089d4      mov x1, -1
│     │╎│   0x1000089d8      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000089dc      mov x23, x0
│     │╎│   0x1000089e0      mov w0, 0x20
│     │╎│   0x1000089e4      mov x1, -1
│     │╎│   0x1000089e8      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000089ec      mov x24, x0
│     │╎│   0x1000089f0      str x0, [x19, 0x58]
│     │╎│   0x1000089f4      ldr d0, [x28, 0xe00]
│     │╎│   0x1000089f8      str s0, [x23]
│     │╎│   0x1000089fc      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008a00      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008a04      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008a08      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008a0c      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008a10      mov x1, 0x33                              ; '3'
│     │╎│   0x100008a14      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008a18      bl sym.func.10000d18c
│     │╎│   0x100008a1c      stur x0, [x23, 4]
│     │╎│   0x100008a20      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008a24      add x0, x0, 0
│     │╎│   0x100008a28      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008a2c      add x3, x3, 0x440
│     │╎│   0x100008a30      mov x1, x21
│     │╎│   0x100008a34      mov x2, x22
│     │╎│   0x100008a38      mov x4, x23
│     │╎│   0x100008a3c      mov w5, 0xc
│     │╎│   0x100008a40      bl sym.imp._os_log_impl
│     │╎│   0x100008a44      add x2, x26, 8
│     │╎│   0x100008a48      mov x0, x24
│     │╎│   0x100008a4c      mov w1, 1
│     │╎│   0x100008a50      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008a54      mov x0, x24
│     │╎│   0x100008a58      mov x1, -1
│     │╎│   0x100008a5c      mov x2, -1
│     │╎│   0x100008a60      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008a64      mov x0, x23
│     │╎│   0x100008a68      mov x1, -1
│     │╎│   0x100008a6c      mov x2, -1
│     │╎│   0x100008a70      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x1000089cc(x)
│     └───> 0x100008a74      bl sym.imp.objc_release_x21
│      ╎│   0x100008a78      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008a7c      mov x21, x0
│      ╎│   0x100008a80      bl sym os_log_type_t...0A0E5debugABvgZ    ; sym.imp.os_log_type_t...0A0E5debugABvgZ
│      ╎│   0x100008a84      and w22, w0, 0xff
│      ╎│   0x100008a88      mov x0, x21
│      ╎│   0x100008a8c      mov x1, x22
│      ╎│   0x100008a90      bl sym.imp.os_log_type_enabled
│      ╎│   0x100008a94      adrp x25, 0x10000d000
│     ┌───< 0x100008a98      cbz w0, 0x100008b40
│     │╎│   0x100008a9c      mov w0, 0xc
│     │╎│   0x100008aa0      mov x1, -1
│     │╎│   0x100008aa4      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008aa8      mov x23, x0
│     │╎│   0x100008aac      mov w0, 0x20
│     │╎│   0x100008ab0      mov x1, -1
│     │╎│   0x100008ab4      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008ab8      mov x24, x0
│     │╎│   0x100008abc      str x0, [x19, 0x58]
│     │╎│   0x100008ac0      ldr d0, [x25, 0xe08]
│     │╎│   0x100008ac4      str s0, [x23]
│     │╎│   0x100008ac8      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008acc      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008ad0      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008ad4      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008ad8      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008adc      mov x1, 0x33                              ; '3'
│     │╎│   0x100008ae0      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008ae4      bl sym.func.10000d18c
│     │╎│   0x100008ae8      stur x0, [x23, 4]
│     │╎│   0x100008aec      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008af0      add x0, x0, 0
│     │╎│   0x100008af4      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008af8      add x3, x3, 0x400
│     │╎│   0x100008afc      mov x1, x21
│     │╎│   0x100008b00      mov x2, x22
│     │╎│   0x100008b04      mov x4, x23
│     │╎│   0x100008b08      mov w5, 0xc
│     │╎│   0x100008b0c      bl sym.imp._os_log_impl
│     │╎│   0x100008b10      add x2, x26, 8
│     │╎│   0x100008b14      mov x0, x24
│     │╎│   0x100008b18      mov w1, 1
│     │╎│   0x100008b1c      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008b20      mov x0, x24
│     │╎│   0x100008b24      mov x1, -1
│     │╎│   0x100008b28      mov x2, -1
│     │╎│   0x100008b2c      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008b30      mov x0, x23
│     │╎│   0x100008b34      mov x1, -1
│     │╎│   0x100008b38      mov x2, -1
│     │╎│   0x100008b3c      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008a98(x)
│     └───> 0x100008b40      bl sym.imp.objc_release_x21
│      ╎│   0x100008b44      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008b48      mov x21, x0
│      ╎│   0x100008b4c      bl sym os_log_type_t...0A0E5debugABvgZ    ; sym.imp.os_log_type_t...0A0E5debugABvgZ
│      ╎│   0x100008b50      and w22, w0, 0xff
│      ╎│   0x100008b54      mov x0, x21
│      ╎│   0x100008b58      mov x1, x22
│      ╎│   0x100008b5c      bl sym.imp.os_log_type_enabled
│      ╎│   0x100008b60      adrp x27, 0x10000d000
│     ┌───< 0x100008b64      cbz w0, 0x100008c0c
│     │╎│   0x100008b68      mov w0, 0xc
│     │╎│   0x100008b6c      mov x1, -1
│     │╎│   0x100008b70      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008b74      mov x23, x0
│     │╎│   0x100008b78      mov w0, 0x20
│     │╎│   0x100008b7c      mov x1, -1
│     │╎│   0x100008b80      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008b84      mov x24, x0
│     │╎│   0x100008b88      str x0, [x19, 0x58]
│     │╎│   0x100008b8c      ldr d0, [x27, 0xe10]
│     │╎│   0x100008b90      str s0, [x23]
│     │╎│   0x100008b94      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008b98      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008b9c      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008ba0      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008ba4      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008ba8      mov x1, 0x33                              ; '3'
│     │╎│   0x100008bac      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008bb0      bl sym.func.10000d18c
│     │╎│   0x100008bb4      stur x0, [x23, 4]
│     │╎│   0x100008bb8      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008bbc      add x0, x0, 0
│     │╎│   0x100008bc0      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008bc4      add x3, x3, 0x3c0
│     │╎│   0x100008bc8      mov x1, x21
│     │╎│   0x100008bcc      mov x2, x22
│     │╎│   0x100008bd0      mov x4, x23
│     │╎│   0x100008bd4      mov w5, 0xc
│     │╎│   0x100008bd8      bl sym.imp._os_log_impl
│     │╎│   0x100008bdc      add x2, x26, 8
│     │╎│   0x100008be0      mov x0, x24
│     │╎│   0x100008be4      mov w1, 1
│     │╎│   0x100008be8      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008bec      mov x0, x24
│     │╎│   0x100008bf0      mov x1, -1
│     │╎│   0x100008bf4      mov x2, -1
│     │╎│   0x100008bf8      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008bfc      mov x0, x23
│     │╎│   0x100008c00      mov x1, -1
│     │╎│   0x100008c04      mov x2, -1
│     │╎│   0x100008c08      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008b64(x)
│     └───> 0x100008c0c      bl sym.imp.objc_release_x21
│      ╎│   0x100008c10      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008c14      mov x21, x0
│      ╎│   0x100008c18      bl sym os_log_type_t...0A0E5debugABvgZ    ; sym.imp.os_log_type_t...0A0E5debugABvgZ
│      ╎│   0x100008c1c      and w22, w0, 0xff
│      ╎│   0x100008c20      mov x0, x21
│      ╎│   0x100008c24      mov x1, x22
│      ╎│   0x100008c28      bl sym.imp.os_log_type_enabled
│      ╎│   0x100008c2c      adrp x23, 0x10000d000
│     ┌───< 0x100008c30      cbz w0, 0x100008cf4
│     │╎│   0x100008c34      mov w0, 0xc
│     │╎│   0x100008c38      mov x1, -1
│     │╎│   0x100008c3c      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008c40      mov x25, x26
│     │╎│   0x100008c44      mov x26, x27
│     │╎│   0x100008c48      mov x27, x28
│     │╎│   0x100008c4c      mov x28, x23
│     │╎│   0x100008c50      mov x23, x0
│     │╎│   0x100008c54      mov w0, 0x20
│     │╎│   0x100008c58      mov x1, -1
│     │╎│   0x100008c5c      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008c60      mov x24, x0
│     │╎│   0x100008c64      str x0, [x19, 0x58]
│     │╎│   0x100008c68      ldr d0, [x28, 0xe18]
│     │╎│   0x100008c6c      mov x28, x27
│     │╎│   0x100008c70      mov x27, x26
│     │╎│   0x100008c74      mov x26, x25
│     │╎│   0x100008c78      str s0, [x23]
│     │╎│   0x100008c7c      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008c80      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008c84      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008c88      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008c8c      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008c90      mov x1, 0x33                              ; '3'
│     │╎│   0x100008c94      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008c98      bl sym.func.10000d18c
│     │╎│   0x100008c9c      stur x0, [x23, 4]
│     │╎│   0x100008ca0      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008ca4      add x0, x0, 0
│     │╎│   0x100008ca8      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008cac      add x3, x3, 0x380
│     │╎│   0x100008cb0      mov x1, x21
│     │╎│   0x100008cb4      mov x2, x22
│     │╎│   0x100008cb8      mov x4, x23
│     │╎│   0x100008cbc      mov w5, 0xc
│     │╎│   0x100008cc0      bl sym.imp._os_log_impl
│     │╎│   0x100008cc4      add x2, x25, 8
│     │╎│   0x100008cc8      mov x0, x24
│     │╎│   0x100008ccc      mov w1, 1
│     │╎│   0x100008cd0      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008cd4      mov x0, x24
│     │╎│   0x100008cd8      mov x1, -1
│     │╎│   0x100008cdc      mov x2, -1
│     │╎│   0x100008ce0      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008ce4      mov x0, x23
│     │╎│   0x100008ce8      mov x1, -1
│     │╎│   0x100008cec      mov x2, -1
│     │╎│   0x100008cf0      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008c30(x)
│     └───> 0x100008cf4      bl sym.imp.objc_release_x21
│      ╎│   0x100008cf8      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008cfc      mov x21, x0
│      ╎│   0x100008d00      bl sym os_log_type_t...0A0E4infoABvgZ     ; sym.imp.os_log_type_t...0A0E4infoABvgZ
│      ╎│   0x100008d04      and w22, w0, 0xff
│      ╎│   0x100008d08      mov x0, x21
│      ╎│   0x100008d0c      mov x1, x22
│      ╎│   0x100008d10      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100008d14      cbz w0, 0x100008dbc
│     │╎│   0x100008d18      mov w0, 0xc
│     │╎│   0x100008d1c      mov x1, -1
│     │╎│   0x100008d20      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008d24      mov x23, x0
│     │╎│   0x100008d28      mov w0, 0x20
│     │╎│   0x100008d2c      mov x1, -1
│     │╎│   0x100008d30      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008d34      mov x24, x0
│     │╎│   0x100008d38      str x0, [x19, 0x58]
│     │╎│   0x100008d3c      ldr d0, [x28, 0xe00]
│     │╎│   0x100008d40      str s0, [x23]
│     │╎│   0x100008d44      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008d48      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008d4c      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008d50      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008d54      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008d58      mov x1, 0x33                              ; '3'
│     │╎│   0x100008d5c      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008d60      bl sym.func.10000d18c
│     │╎│   0x100008d64      stur x0, [x23, 4]
│     │╎│   0x100008d68      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008d6c      add x0, x0, 0
│     │╎│   0x100008d70      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008d74      add x3, x3, 0x360
│     │╎│   0x100008d78      mov x1, x21
│     │╎│   0x100008d7c      mov x2, x22
│     │╎│   0x100008d80      mov x4, x23
│     │╎│   0x100008d84      mov w5, 0xc
│     │╎│   0x100008d88      bl sym.imp._os_log_impl
│     │╎│   0x100008d8c      add x2, x26, 8
│     │╎│   0x100008d90      mov x0, x24
│     │╎│   0x100008d94      mov w1, 1
│     │╎│   0x100008d98      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008d9c      mov x0, x24
│     │╎│   0x100008da0      mov x1, -1
│     │╎│   0x100008da4      mov x2, -1
│     │╎│   0x100008da8      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008dac      mov x0, x23
│     │╎│   0x100008db0      mov x1, -1
│     │╎│   0x100008db4      mov x2, -1
│     │╎│   0x100008db8      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008d14(x)
│     └───> 0x100008dbc      bl sym.imp.objc_release_x21
│      ╎│   0x100008dc0      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008dc4      mov x21, x0
│      ╎│   0x100008dc8      bl sym os_log_type_t...0A0E4infoABvgZ     ; sym.imp.os_log_type_t...0A0E4infoABvgZ
│      ╎│   0x100008dcc      and w22, w0, 0xff
│      ╎│   0x100008dd0      mov x0, x21
│      ╎│   0x100008dd4      mov x1, x22
│      ╎│   0x100008dd8      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100008ddc      cbz w0, 0x100008e88
│     │╎│   0x100008de0      mov w0, 0xc
│     │╎│   0x100008de4      mov x1, -1
│     │╎│   0x100008de8      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008dec      mov x23, x0
│     │╎│   0x100008df0      mov w0, 0x20
│     │╎│   0x100008df4      mov x1, -1
│     │╎│   0x100008df8      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008dfc      mov x24, x0
│     │╎│   0x100008e00      str x0, [x19, 0x58]
│     │╎│   0x100008e04      adrp x8, 0x10000d000
│     │╎│   0x100008e08      ldr d0, [x8, 0xe08]
│     │╎│   0x100008e0c      str s0, [x23]
│     │╎│   0x100008e10      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008e14      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008e18      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008e1c      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008e20      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008e24      mov x1, 0x33                              ; '3'
│     │╎│   0x100008e28      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008e2c      bl sym.func.10000d18c
│     │╎│   0x100008e30      stur x0, [x23, 4]
│     │╎│   0x100008e34      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008e38      add x0, x0, 0
│     │╎│   0x100008e3c      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008e40      add x3, x3, 0x320
│     │╎│   0x100008e44      mov x1, x21
│     │╎│   0x100008e48      mov x2, x22
│     │╎│   0x100008e4c      mov x4, x23
│     │╎│   0x100008e50      mov w5, 0xc
│     │╎│   0x100008e54      bl sym.imp._os_log_impl
│     │╎│   0x100008e58      add x2, x26, 8
│     │╎│   0x100008e5c      mov x0, x24
│     │╎│   0x100008e60      mov w1, 1
│     │╎│   0x100008e64      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008e68      mov x0, x24
│     │╎│   0x100008e6c      mov x1, -1
│     │╎│   0x100008e70      mov x2, -1
│     │╎│   0x100008e74      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008e78      mov x0, x23
│     │╎│   0x100008e7c      mov x1, -1
│     │╎│   0x100008e80      mov x2, -1
│     │╎│   0x100008e84      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008ddc(x)
│     └───> 0x100008e88      bl sym.imp.objc_release_x21
│      ╎│   0x100008e8c      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008e90      mov x21, x0
│      ╎│   0x100008e94      bl sym os_log_type_t...0A0E4infoABvgZ     ; sym.imp.os_log_type_t...0A0E4infoABvgZ
│      ╎│   0x100008e98      and w22, w0, 0xff
│      ╎│   0x100008e9c      mov x0, x21
│      ╎│   0x100008ea0      mov x1, x22
│      ╎│   0x100008ea4      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100008ea8      cbz w0, 0x100008f50
│     │╎│   0x100008eac      mov w0, 0xc
│     │╎│   0x100008eb0      mov x1, -1
│     │╎│   0x100008eb4      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008eb8      mov x23, x0
│     │╎│   0x100008ebc      mov w0, 0x20
│     │╎│   0x100008ec0      mov x1, -1
│     │╎│   0x100008ec4      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008ec8      mov x24, x0
│     │╎│   0x100008ecc      str x0, [x19, 0x58]
│     │╎│   0x100008ed0      ldr d0, [x27, 0xe10]
│     │╎│   0x100008ed4      str s0, [x23]
│     │╎│   0x100008ed8      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008edc      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008ee0      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008ee4      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008ee8      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008eec      mov x1, 0x33                              ; '3'
│     │╎│   0x100008ef0      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008ef4      bl sym.func.10000d18c
│     │╎│   0x100008ef8      stur x0, [x23, 4]
│     │╎│   0x100008efc      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008f00      add x0, x0, 0
│     │╎│   0x100008f04      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008f08      add x3, x3, 0x2e0
│     │╎│   0x100008f0c      mov x1, x21
│     │╎│   0x100008f10      mov x2, x22
│     │╎│   0x100008f14      mov x4, x23
│     │╎│   0x100008f18      mov w5, 0xc
│     │╎│   0x100008f1c      bl sym.imp._os_log_impl
│     │╎│   0x100008f20      add x2, x26, 8
│     │╎│   0x100008f24      mov x0, x24
│     │╎│   0x100008f28      mov w1, 1
│     │╎│   0x100008f2c      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008f30      mov x0, x24
│     │╎│   0x100008f34      mov x1, -1
│     │╎│   0x100008f38      mov x2, -1
│     │╎│   0x100008f3c      bl sym.imp.swift_slowDealloc
│     │╎│   0x100008f40      mov x0, x23
│     │╎│   0x100008f44      mov x1, -1
│     │╎│   0x100008f48      mov x2, -1
│     │╎│   0x100008f4c      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008ea8(x)
│     └───> 0x100008f50      bl sym.imp.objc_release_x21
│      ╎│   0x100008f54      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100008f58      mov x21, x0
│      ╎│   0x100008f5c      bl sym os_log_type_t...0A0E4infoABvgZ     ; sym.imp.os_log_type_t...0A0E4infoABvgZ
│      ╎│   0x100008f60      and w22, w0, 0xff
│      ╎│   0x100008f64      mov x0, x21
│      ╎│   0x100008f68      mov x1, x22
│      ╎│   0x100008f6c      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100008f70      cbz w0, 0x10000901c
│     │╎│   0x100008f74      mov w0, 0xc
│     │╎│   0x100008f78      mov x1, -1
│     │╎│   0x100008f7c      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008f80      mov x23, x0
│     │╎│   0x100008f84      mov w0, 0x20
│     │╎│   0x100008f88      mov x1, -1
│     │╎│   0x100008f8c      bl sym.imp.swift_slowAlloc
│     │╎│   0x100008f90      mov x24, x0
│     │╎│   0x100008f94      str x0, [x19, 0x58]
│     │╎│   0x100008f98      adrp x8, 0x10000d000
│     │╎│   0x100008f9c      ldr d0, [x8, 0xe18]
│     │╎│   0x100008fa0      str s0, [x23]
│     │╎│   0x100008fa4      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100008fa8      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100008fac      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100008fb0      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100008fb4      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100008fb8      mov x1, 0x33                              ; '3'
│     │╎│   0x100008fbc      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100008fc0      bl sym.func.10000d18c
│     │╎│   0x100008fc4      stur x0, [x23, 4]
│     │╎│   0x100008fc8      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100008fcc      add x0, x0, 0
│     │╎│   0x100008fd0      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100008fd4      add x3, x3, 0x2a0
│     │╎│   0x100008fd8      mov x1, x21
│     │╎│   0x100008fdc      mov x2, x22
│     │╎│   0x100008fe0      mov x4, x23
│     │╎│   0x100008fe4      mov w5, 0xc
│     │╎│   0x100008fe8      bl sym.imp._os_log_impl
│     │╎│   0x100008fec      add x2, x26, 8
│     │╎│   0x100008ff0      mov x0, x24
│     │╎│   0x100008ff4      mov w1, 1
│     │╎│   0x100008ff8      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100008ffc      mov x0, x24
│     │╎│   0x100009000      mov x1, -1
│     │╎│   0x100009004      mov x2, -1
│     │╎│   0x100009008      bl sym.imp.swift_slowDealloc
│     │╎│   0x10000900c      mov x0, x23
│     │╎│   0x100009010      mov x1, -1
│     │╎│   0x100009014      mov x2, -1
│     │╎│   0x100009018      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008f70(x)
│     └───> 0x10000901c      bl sym.imp.objc_release_x21
│      ╎│   0x100009020      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100009024      mov x21, x0
│      ╎│   0x100009028      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x10000902c      and w22, w0, 0xff
│      ╎│   0x100009030      mov x0, x21
│      ╎│   0x100009034      mov x1, x22
│      ╎│   0x100009038      bl sym.imp.os_log_type_enabled
│     ┌───< 0x10000903c      cbz w0, 0x1000090e4
│     │╎│   0x100009040      mov w0, 0xc
│     │╎│   0x100009044      mov x1, -1
│     │╎│   0x100009048      bl sym.imp.swift_slowAlloc
│     │╎│   0x10000904c      mov x23, x0
│     │╎│   0x100009050      mov w0, 0x20
│     │╎│   0x100009054      mov x1, -1
│     │╎│   0x100009058      bl sym.imp.swift_slowAlloc
│     │╎│   0x10000905c      mov x24, x0
│     │╎│   0x100009060      str x0, [x19, 0x58]
│     │╎│   0x100009064      ldr d0, [x28, 0xe00]
│     │╎│   0x100009068      str s0, [x23]
│     │╎│   0x10000906c      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100009070      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009074      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100009078      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x10000907c      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100009080      mov x1, 0x33                              ; '3'
│     │╎│   0x100009084      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100009088      bl sym.func.10000d18c
│     │╎│   0x10000908c      stur x0, [x23, 4]
│     │╎│   0x100009090      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009094      add x0, x0, 0
│     │╎│   0x100009098      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x10000909c      add x3, x3, 0x280
│     │╎│   0x1000090a0      mov x1, x21
│     │╎│   0x1000090a4      mov x2, x22
│     │╎│   0x1000090a8      mov x4, x23
│     │╎│   0x1000090ac      mov w5, 0xc
│     │╎│   0x1000090b0      bl sym.imp._os_log_impl
│     │╎│   0x1000090b4      add x2, x26, 8
│     │╎│   0x1000090b8      mov x0, x24
│     │╎│   0x1000090bc      mov w1, 1
│     │╎│   0x1000090c0      bl sym.imp.swift_arrayDestroy
│     │╎│   0x1000090c4      mov x0, x24
│     │╎│   0x1000090c8      mov x1, -1
│     │╎│   0x1000090cc      mov x2, -1
│     │╎│   0x1000090d0      bl sym.imp.swift_slowDealloc
│     │╎│   0x1000090d4      mov x0, x23
│     │╎│   0x1000090d8      mov x1, -1
│     │╎│   0x1000090dc      mov x2, -1
│     │╎│   0x1000090e0      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x10000903c(x)
│     └───> 0x1000090e4      bl sym.imp.objc_release_x21
│      ╎│   0x1000090e8      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x1000090ec      mov x21, x0
│      ╎│   0x1000090f0      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x1000090f4      and w22, w0, 0xff
│      ╎│   0x1000090f8      mov x0, x21
│      ╎│   0x1000090fc      mov x1, x22
│      ╎│   0x100009100      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100009104      cbz w0, 0x1000091b0
│     │╎│   0x100009108      mov w0, 0xc
│     │╎│   0x10000910c      mov x1, -1
│     │╎│   0x100009110      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009114      mov x23, x0
│     │╎│   0x100009118      mov w0, 0x20
│     │╎│   0x10000911c      mov x1, -1
│     │╎│   0x100009120      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009124      mov x24, x0
│     │╎│   0x100009128      str x0, [x19, 0x58]
│     │╎│   0x10000912c      adrp x8, 0x10000d000
│     │╎│   0x100009130      ldr d0, [x8, 0xe08]
│     │╎│   0x100009134      str s0, [x23]
│     │╎│   0x100009138      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x10000913c      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009140      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100009144      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009148      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x10000914c      mov x1, 0x33                              ; '3'
│     │╎│   0x100009150      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100009154      bl sym.func.10000d18c
│     │╎│   0x100009158      stur x0, [x23, 4]
│     │╎│   0x10000915c      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009160      add x0, x0, 0
│     │╎│   0x100009164      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100009168      add x3, x3, 0x240
│     │╎│   0x10000916c      mov x1, x21
│     │╎│   0x100009170      mov x2, x22
│     │╎│   0x100009174      mov x4, x23
│     │╎│   0x100009178      mov w5, 0xc
│     │╎│   0x10000917c      bl sym.imp._os_log_impl
│     │╎│   0x100009180      add x2, x26, 8
│     │╎│   0x100009184      mov x0, x24
│     │╎│   0x100009188      mov w1, 1
│     │╎│   0x10000918c      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100009190      mov x0, x24
│     │╎│   0x100009194      mov x1, -1
│     │╎│   0x100009198      mov x2, -1
│     │╎│   0x10000919c      bl sym.imp.swift_slowDealloc
│     │╎│   0x1000091a0      mov x0, x23
│     │╎│   0x1000091a4      mov x1, -1
│     │╎│   0x1000091a8      mov x2, -1
│     │╎│   0x1000091ac      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100009104(x)
│     └───> 0x1000091b0      bl sym.imp.objc_release_x21
│      ╎│   0x1000091b4      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x1000091b8      mov x21, x0
│      ╎│   0x1000091bc      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x1000091c0      and w22, w0, 0xff
│      ╎│   0x1000091c4      mov x0, x21
│      ╎│   0x1000091c8      mov x1, x22
│      ╎│   0x1000091cc      bl sym.imp.os_log_type_enabled
│     ┌───< 0x1000091d0      cbz w0, 0x100009278
│     │╎│   0x1000091d4      mov w0, 0xc
│     │╎│   0x1000091d8      mov x1, -1
│     │╎│   0x1000091dc      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000091e0      mov x23, x0
│     │╎│   0x1000091e4      mov w0, 0x20
│     │╎│   0x1000091e8      mov x1, -1
│     │╎│   0x1000091ec      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000091f0      mov x24, x0
│     │╎│   0x1000091f4      str x0, [x19, 0x58]
│     │╎│   0x1000091f8      ldr d0, [x27, 0xe10]
│     │╎│   0x1000091fc      str s0, [x23]
│     │╎│   0x100009200      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100009204      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009208      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x10000920c      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009210      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100009214      mov x1, 0x33                              ; '3'
│     │╎│   0x100009218      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x10000921c      bl sym.func.10000d18c
│     │╎│   0x100009220      stur x0, [x23, 4]
│     │╎│   0x100009224      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009228      add x0, x0, 0
│     │╎│   0x10000922c      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100009230      add x3, x3, 0x200
│     │╎│   0x100009234      mov x1, x21
│     │╎│   0x100009238      mov x2, x22
│     │╎│   0x10000923c      mov x4, x23
│     │╎│   0x100009240      mov w5, 0xc
│     │╎│   0x100009244      bl sym.imp._os_log_impl
│     │╎│   0x100009248      add x2, x26, 8
│     │╎│   0x10000924c      mov x0, x24
│     │╎│   0x100009250      mov w1, 1
│     │╎│   0x100009254      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100009258      mov x0, x24
│     │╎│   0x10000925c      mov x1, -1
│     │╎│   0x100009260      mov x2, -1
│     │╎│   0x100009264      bl sym.imp.swift_slowDealloc
│     │╎│   0x100009268      mov x0, x23
│     │╎│   0x10000926c      mov x1, -1
│     │╎│   0x100009270      mov x2, -1
│     │╎│   0x100009274      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x1000091d0(x)
│     └───> 0x100009278      bl sym.imp.objc_release_x21
│      ╎│   0x10000927c      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100009280      mov x21, x0
│      ╎│   0x100009284      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x100009288      and w22, w0, 0xff
│      ╎│   0x10000928c      mov x0, x21
│      ╎│   0x100009290      mov x1, x22
│      ╎│   0x100009294      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100009298      cbz w0, 0x100009344
│     │╎│   0x10000929c      mov w0, 0xc
│     │╎│   0x1000092a0      mov x1, -1
│     │╎│   0x1000092a4      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000092a8      mov x23, x0
│     │╎│   0x1000092ac      mov w0, 0x20
│     │╎│   0x1000092b0      mov x1, -1
│     │╎│   0x1000092b4      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000092b8      mov x24, x0
│     │╎│   0x1000092bc      str x0, [x19, 0x58]
│     │╎│   0x1000092c0      adrp x8, 0x10000d000
│     │╎│   0x1000092c4      ldr d0, [x8, 0xe18]
│     │╎│   0x1000092c8      str s0, [x23]
│     │╎│   0x1000092cc      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x1000092d0      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x1000092d4      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x1000092d8      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x1000092dc      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x1000092e0      mov x1, 0x33                              ; '3'
│     │╎│   0x1000092e4      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x1000092e8      bl sym.func.10000d18c
│     │╎│   0x1000092ec      stur x0, [x23, 4]
│     │╎│   0x1000092f0      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x1000092f4      add x0, x0, 0
│     │╎│   0x1000092f8      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x1000092fc      add x3, x3, 0x1c0
│     │╎│   0x100009300      mov x1, x21
│     │╎│   0x100009304      mov x2, x22
│     │╎│   0x100009308      mov x4, x23
│     │╎│   0x10000930c      mov w5, 0xc
│     │╎│   0x100009310      bl sym.imp._os_log_impl
│     │╎│   0x100009314      add x2, x26, 8
│     │╎│   0x100009318      mov x0, x24
│     │╎│   0x10000931c      mov w1, 1
│     │╎│   0x100009320      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100009324      mov x0, x24
│     │╎│   0x100009328      mov x1, -1
│     │╎│   0x10000932c      mov x2, -1
│     │╎│   0x100009330      bl sym.imp.swift_slowDealloc
│     │╎│   0x100009334      mov x0, x23
│     │╎│   0x100009338      mov x1, -1
│     │╎│   0x10000933c      mov x2, -1
│     │╎│   0x100009340      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100009298(x)
│     └───> 0x100009344      bl sym.imp.objc_release_x21
│      ╎│   0x100009348      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x10000934c      mov x21, x0
│      ╎│   0x100009350      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x100009354      and w22, w0, 0xff
│      ╎│   0x100009358      mov x0, x21
│      ╎│   0x10000935c      mov x1, x22
│      ╎│   0x100009360      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100009364      cbz w0, 0x10000940c
│     │╎│   0x100009368      mov w0, 0xc
│     │╎│   0x10000936c      mov x1, -1
│     │╎│   0x100009370      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009374      mov x23, x0
│     │╎│   0x100009378      mov w0, 0x20
│     │╎│   0x10000937c      mov x1, -1
│     │╎│   0x100009380      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009384      mov x24, x0
│     │╎│   0x100009388      str x0, [x19, 0x58]
│     │╎│   0x10000938c      ldr d0, [x28, 0xe00]
│     │╎│   0x100009390      str s0, [x23]
│     │╎│   0x100009394      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100009398      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x10000939c      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x1000093a0      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x1000093a4      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x1000093a8      mov x1, 0x33                              ; '3'
│     │╎│   0x1000093ac      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x1000093b0      bl sym.func.10000d18c
│     │╎│   0x1000093b4      stur x0, [x23, 4]
│     │╎│   0x1000093b8      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x1000093bc      add x0, x0, 0
│     │╎│   0x1000093c0      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x1000093c4      add x3, x3, 0x1a0
│     │╎│   0x1000093c8      mov x1, x21
│     │╎│   0x1000093cc      mov x2, x22
│     │╎│   0x1000093d0      mov x4, x23
│     │╎│   0x1000093d4      mov w5, 0xc
│     │╎│   0x1000093d8      bl sym.imp._os_log_impl
│     │╎│   0x1000093dc      add x2, x26, 8
│     │╎│   0x1000093e0      mov x0, x24
│     │╎│   0x1000093e4      mov w1, 1
│     │╎│   0x1000093e8      bl sym.imp.swift_arrayDestroy
│     │╎│   0x1000093ec      mov x0, x24
│     │╎│   0x1000093f0      mov x1, -1
│     │╎│   0x1000093f4      mov x2, -1
│     │╎│   0x1000093f8      bl sym.imp.swift_slowDealloc
│     │╎│   0x1000093fc      mov x0, x23
│     │╎│   0x100009400      mov x1, -1
│     │╎│   0x100009404      mov x2, -1
│     │╎│   0x100009408      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100009364(x)
│     └───> 0x10000940c      bl sym.imp.objc_release_x21
│      ╎│   0x100009410      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100009414      mov x21, x0
│      ╎│   0x100009418      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x10000941c      and w22, w0, 0xff
│      ╎│   0x100009420      mov x0, x21
│      ╎│   0x100009424      mov x1, x22
│      ╎│   0x100009428      bl sym.imp.os_log_type_enabled
│     ┌───< 0x10000942c      cbz w0, 0x1000094d8
│     │╎│   0x100009430      mov w0, 0xc
│     │╎│   0x100009434      mov x1, -1
│     │╎│   0x100009438      bl sym.imp.swift_slowAlloc
│     │╎│   0x10000943c      mov x23, x0
│     │╎│   0x100009440      mov w0, 0x20
│     │╎│   0x100009444      mov x1, -1
│     │╎│   0x100009448      bl sym.imp.swift_slowAlloc
│     │╎│   0x10000944c      mov x24, x0
│     │╎│   0x100009450      str x0, [x19, 0x58]
│     │╎│   0x100009454      adrp x8, 0x10000d000
│     │╎│   0x100009458      ldr d0, [x8, 0xe08]
│     │╎│   0x10000945c      str s0, [x23]
│     │╎│   0x100009460      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100009464      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009468      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x10000946c      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009470      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100009474      mov x1, 0x33                              ; '3'
│     │╎│   0x100009478      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x10000947c      bl sym.func.10000d18c
│     │╎│   0x100009480      stur x0, [x23, 4]
│     │╎│   0x100009484      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009488      add x0, x0, 0
│     │╎│   0x10000948c      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100009490      add x3, x3, 0x160
│     │╎│   0x100009494      mov x1, x21
│     │╎│   0x100009498      mov x2, x22
│     │╎│   0x10000949c      mov x4, x23
│     │╎│   0x1000094a0      mov w5, 0xc
│     │╎│   0x1000094a4      bl sym.imp._os_log_impl
│     │╎│   0x1000094a8      add x2, x26, 8
│     │╎│   0x1000094ac      mov x0, x24
│     │╎│   0x1000094b0      mov w1, 1
│     │╎│   0x1000094b4      bl sym.imp.swift_arrayDestroy
│     │╎│   0x1000094b8      mov x0, x24
│     │╎│   0x1000094bc      mov x1, -1
│     │╎│   0x1000094c0      mov x2, -1
│     │╎│   0x1000094c4      bl sym.imp.swift_slowDealloc
│     │╎│   0x1000094c8      mov x0, x23
│     │╎│   0x1000094cc      mov x1, -1
│     │╎│   0x1000094d0      mov x2, -1
│     │╎│   0x1000094d4      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x10000942c(x)
│     └───> 0x1000094d8      bl sym.imp.objc_release_x21
│      ╎│   0x1000094dc      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x1000094e0      mov x21, x0
│      ╎│   0x1000094e4      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x1000094e8      and w22, w0, 0xff
│      ╎│   0x1000094ec      mov x0, x21
│      ╎│   0x1000094f0      mov x1, x22
│      ╎│   0x1000094f4      bl sym.imp.os_log_type_enabled
│     ┌───< 0x1000094f8      cbz w0, 0x1000095a0
│     │╎│   0x1000094fc      mov w0, 0xc
│     │╎│   0x100009500      mov x1, -1
│     │╎│   0x100009504      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009508      mov x23, x0
│     │╎│   0x10000950c      mov w0, 0x20
│     │╎│   0x100009510      mov x1, -1
│     │╎│   0x100009514      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009518      mov x24, x0
│     │╎│   0x10000951c      str x0, [x19, 0x58]
│     │╎│   0x100009520      ldr d0, [x27, 0xe10]
│     │╎│   0x100009524      str s0, [x23]
│     │╎│   0x100009528      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x10000952c      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009530      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100009534      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009538      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x10000953c      mov x1, 0x33                              ; '3'
│     │╎│   0x100009540      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100009544      bl sym.func.10000d18c
│     │╎│   0x100009548      stur x0, [x23, 4]
│     │╎│   0x10000954c      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009550      add x0, x0, 0
│     │╎│   0x100009554      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100009558      add x3, x3, 0x120
│     │╎│   0x10000955c      mov x1, x21
│     │╎│   0x100009560      mov x2, x22
│     │╎│   0x100009564      mov x4, x23
│     │╎│   0x100009568      mov w5, 0xc
│     │╎│   0x10000956c      bl sym.imp._os_log_impl
│     │╎│   0x100009570      add x2, x26, 8
│     │╎│   0x100009574      mov x0, x24
│     │╎│   0x100009578      mov w1, 1
│     │╎│   0x10000957c      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100009580      mov x0, x24
│     │╎│   0x100009584      mov x1, -1
│     │╎│   0x100009588      mov x2, -1
│     │╎│   0x10000958c      bl sym.imp.swift_slowDealloc
│     │╎│   0x100009590      mov x0, x23
│     │╎│   0x100009594      mov x1, -1
│     │╎│   0x100009598      mov x2, -1
│     │╎│   0x10000959c      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x1000094f8(x)
│     └───> 0x1000095a0      bl sym.imp.objc_release_x21
│      ╎│   0x1000095a4      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x1000095a8      mov x21, x0
│      ╎│   0x1000095ac      bl sym os_log_type_t...0A0E5errorABvgZ    ; sym.imp.os_log_type_t...0A0E5errorABvgZ
│      ╎│   0x1000095b0      and w22, w0, 0xff
│      ╎│   0x1000095b4      mov x0, x21
│      ╎│   0x1000095b8      mov x1, x22
│      ╎│   0x1000095bc      bl sym.imp.os_log_type_enabled
│     ┌───< 0x1000095c0      cbz w0, 0x10000966c
│     │╎│   0x1000095c4      mov w0, 0xc
│     │╎│   0x1000095c8      mov x1, -1
│     │╎│   0x1000095cc      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000095d0      mov x23, x0
│     │╎│   0x1000095d4      mov w0, 0x20
│     │╎│   0x1000095d8      mov x1, -1
│     │╎│   0x1000095dc      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000095e0      mov x24, x0
│     │╎│   0x1000095e4      str x0, [x19, 0x58]
│     │╎│   0x1000095e8      adrp x8, 0x10000d000
│     │╎│   0x1000095ec      ldr d0, [x8, 0xe18]
│     │╎│   0x1000095f0      str s0, [x23]
│     │╎│   0x1000095f4      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x1000095f8      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x1000095fc      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100009600      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009604      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100009608      mov x1, 0x33                              ; '3'
│     │╎│   0x10000960c      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100009610      bl sym.func.10000d18c
│     │╎│   0x100009614      stur x0, [x23, 4]
│     │╎│   0x100009618      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x10000961c      add x0, x0, 0
│     │╎│   0x100009620      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100009624      add x3, x3, 0xe0
│     │╎│   0x100009628      mov x1, x21
│     │╎│   0x10000962c      mov x2, x22
│     │╎│   0x100009630      mov x4, x23
│     │╎│   0x100009634      mov w5, 0xc
│     │╎│   0x100009638      bl sym.imp._os_log_impl
│     │╎│   0x10000963c      add x2, x26, 8
│     │╎│   0x100009640      mov x0, x24
│     │╎│   0x100009644      mov w1, 1
│     │╎│   0x100009648      bl sym.imp.swift_arrayDestroy
│     │╎│   0x10000964c      mov x0, x24
│     │╎│   0x100009650      mov x1, -1
│     │╎│   0x100009654      mov x2, -1
│     │╎│   0x100009658      bl sym.imp.swift_slowDealloc
│     │╎│   0x10000965c      mov x0, x23
│     │╎│   0x100009660      mov x1, -1
│     │╎│   0x100009664      mov x2, -1
│     │╎│   0x100009668      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x1000095c0(x)
│     └───> 0x10000966c      bl sym.imp.objc_release_x21
│      ╎│   0x100009670      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100009674      mov x21, x0
│      ╎│   0x100009678      bl sym os_log_type_t...0A0E5faultABvgZ    ; sym.imp.os_log_type_t...0A0E5faultABvgZ
│      ╎│   0x10000967c      and w22, w0, 0xff
│      ╎│   0x100009680      mov x0, x21
│      ╎│   0x100009684      mov x1, x22
│      ╎│   0x100009688      bl sym.imp.os_log_type_enabled
│     ┌───< 0x10000968c      cbz w0, 0x100009734
│     │╎│   0x100009690      mov w0, 0xc
│     │╎│   0x100009694      mov x1, -1
│     │╎│   0x100009698      bl sym.imp.swift_slowAlloc
│     │╎│   0x10000969c      mov x23, x0
│     │╎│   0x1000096a0      mov w0, 0x20
│     │╎│   0x1000096a4      mov x1, -1
│     │╎│   0x1000096a8      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000096ac      mov x24, x0
│     │╎│   0x1000096b0      str x0, [x19, 0x58]
│     │╎│   0x1000096b4      ldr d0, [x28, 0xe00]
│     │╎│   0x1000096b8      str s0, [x23]
│     │╎│   0x1000096bc      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x1000096c0      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x1000096c4      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x1000096c8      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x1000096cc      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x1000096d0      mov x1, 0x33                              ; '3'
│     │╎│   0x1000096d4      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x1000096d8      bl sym.func.10000d18c
│     │╎│   0x1000096dc      stur x0, [x23, 4]
│     │╎│   0x1000096e0      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x1000096e4      add x0, x0, 0
│     │╎│   0x1000096e8      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x1000096ec      add x3, x3, 0xc0
│     │╎│   0x1000096f0      mov x1, x21
│     │╎│   0x1000096f4      mov x2, x22
│     │╎│   0x1000096f8      mov x4, x23
│     │╎│   0x1000096fc      mov w5, 0xc
│     │╎│   0x100009700      bl sym.imp._os_log_impl
│     │╎│   0x100009704      add x2, x26, 8
│     │╎│   0x100009708      mov x0, x24
│     │╎│   0x10000970c      mov w1, 1
│     │╎│   0x100009710      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100009714      mov x0, x24
│     │╎│   0x100009718      mov x1, -1
│     │╎│   0x10000971c      mov x2, -1
│     │╎│   0x100009720      bl sym.imp.swift_slowDealloc
│     │╎│   0x100009724      mov x0, x23
│     │╎│   0x100009728      mov x1, -1
│     │╎│   0x10000972c      mov x2, -1
│     │╎│   0x100009730      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x10000968c(x)
│     └───> 0x100009734      bl sym.imp.objc_release_x21
│      ╎│   0x100009738      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x10000973c      mov x21, x0
│      ╎│   0x100009740      bl sym os_log_type_t...0A0E5faultABvgZ    ; sym.imp.os_log_type_t...0A0E5faultABvgZ
│      ╎│   0x100009744      and w22, w0, 0xff
│      ╎│   0x100009748      mov x0, x21
│      ╎│   0x10000974c      mov x1, x22
│      ╎│   0x100009750      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100009754      cbz w0, 0x100009800
│     │╎│   0x100009758      mov w0, 0xc
│     │╎│   0x10000975c      mov x1, -1
│     │╎│   0x100009760      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009764      mov x23, x0
│     │╎│   0x100009768      mov w0, 0x20
│     │╎│   0x10000976c      mov x1, -1
│     │╎│   0x100009770      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009774      mov x24, x0
│     │╎│   0x100009778      str x0, [x19, 0x58]
│     │╎│   0x10000977c      adrp x8, 0x10000d000
│     │╎│   0x100009780      ldr d0, [x8, 0xe08]
│     │╎│   0x100009784      str s0, [x23]
│     │╎│   0x100009788      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x10000978c      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009790      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100009794      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009798      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x10000979c      mov x1, 0x33                              ; '3'
│     │╎│   0x1000097a0      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x1000097a4      bl sym.func.10000d18c
│     │╎│   0x1000097a8      stur x0, [x23, 4]
│     │╎│   0x1000097ac      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x1000097b0      add x0, x0, 0
│     │╎│   0x1000097b4      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x1000097b8      add x3, x3, 0x80
│     │╎│   0x1000097bc      mov x1, x21
│     │╎│   0x1000097c0      mov x2, x22
│     │╎│   0x1000097c4      mov x4, x23
│     │╎│   0x1000097c8      mov w5, 0xc
│     │╎│   0x1000097cc      bl sym.imp._os_log_impl
│     │╎│   0x1000097d0      add x2, x26, 8
│     │╎│   0x1000097d4      mov x0, x24
│     │╎│   0x1000097d8      mov w1, 1
│     │╎│   0x1000097dc      bl sym.imp.swift_arrayDestroy
│     │╎│   0x1000097e0      mov x0, x24
│     │╎│   0x1000097e4      mov x1, -1
│     │╎│   0x1000097e8      mov x2, -1
│     │╎│   0x1000097ec      bl sym.imp.swift_slowDealloc
│     │╎│   0x1000097f0      mov x0, x23
│     │╎│   0x1000097f4      mov x1, -1
│     │╎│   0x1000097f8      mov x2, -1
│     │╎│   0x1000097fc      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100009754(x)
│     └───> 0x100009800      bl sym.imp.objc_release_x21
│      ╎│   0x100009804      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x100009808      mov x21, x0
│      ╎│   0x10000980c      bl sym os_log_type_t...0A0E5faultABvgZ    ; sym.imp.os_log_type_t...0A0E5faultABvgZ
│      ╎│   0x100009810      and w22, w0, 0xff
│      ╎│   0x100009814      mov x0, x21
│      ╎│   0x100009818      mov x1, x22
│      ╎│   0x10000981c      bl sym.imp.os_log_type_enabled
│     ┌───< 0x100009820      cbz w0, 0x1000098c8
│     │╎│   0x100009824      mov w0, 0xc
│     │╎│   0x100009828      mov x1, -1
│     │╎│   0x10000982c      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009830      mov x23, x0
│     │╎│   0x100009834      mov w0, 0x20
│     │╎│   0x100009838      mov x1, -1
│     │╎│   0x10000983c      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009840      mov x24, x0
│     │╎│   0x100009844      str x0, [x19, 0x58]
│     │╎│   0x100009848      ldr d0, [x27, 0xe10]
│     │╎│   0x10000984c      str s0, [x23]
│     │╎│   0x100009850      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100009854      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009858      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x10000985c      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x100009860      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100009864      mov x1, 0x33                              ; '3'
│     │╎│   0x100009868      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x10000986c      bl sym.func.10000d18c
│     │╎│   0x100009870      stur x0, [x23, 4]
│     │╎│   0x100009874      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009878      add x0, x0, 0
│     │╎│   0x10000987c      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x100009880      add x3, x3, 0x40
│     │╎│   0x100009884      mov x1, x21
│     │╎│   0x100009888      mov x2, x22
│     │╎│   0x10000988c      mov x4, x23
│     │╎│   0x100009890      mov w5, 0xc
│     │╎│   0x100009894      bl sym.imp._os_log_impl
│     │╎│   0x100009898      add x2, x26, 8
│     │╎│   0x10000989c      mov x0, x24
│     │╎│   0x1000098a0      mov w1, 1
│     │╎│   0x1000098a4      bl sym.imp.swift_arrayDestroy
│     │╎│   0x1000098a8      mov x0, x24
│     │╎│   0x1000098ac      mov x1, -1
│     │╎│   0x1000098b0      mov x2, -1
│     │╎│   0x1000098b4      bl sym.imp.swift_slowDealloc
│     │╎│   0x1000098b8      mov x0, x23
│     │╎│   0x1000098bc      mov x1, -1
│     │╎│   0x1000098c0      mov x2, -1
│     │╎│   0x1000098c4      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100009820(x)
│     └───> 0x1000098c8      bl sym.imp.objc_release_x21
│      ╎│   0x1000098cc      bl sym os.Logger.logObject.OS_._...C0Cvg  ; sym.imp.os.Logger.logObject.OS_._...C0Cvg
│      ╎│   0x1000098d0      mov x20, x0
│      ╎│   0x1000098d4      bl sym os_log_type_t...0A0E5faultABvgZ    ; sym.imp.os_log_type_t...0A0E5faultABvgZ
│      ╎│   0x1000098d8      and w21, w0, 0xff
│      ╎│   0x1000098dc      mov x0, x20
│      ╎│   0x1000098e0      mov x1, x21
│      ╎│   0x1000098e4      bl sym.imp.os_log_type_enabled
│     ┌───< 0x1000098e8      cbz w0, 0x100009994
│     │╎│   0x1000098ec      mov w0, 0xc
│     │╎│   0x1000098f0      mov x1, -1
│     │╎│   0x1000098f4      bl sym.imp.swift_slowAlloc
│     │╎│   0x1000098f8      mov x22, x0
│     │╎│   0x1000098fc      mov w0, 0x20
│     │╎│   0x100009900      mov x1, -1
│     │╎│   0x100009904      bl sym.imp.swift_slowAlloc
│     │╎│   0x100009908      mov x23, x0
│     │╎│   0x10000990c      str x0, [x19, 0x58]
│     │╎│   0x100009910      adrp x8, 0x10000d000
│     │╎│   0x100009914      ldr d0, [x8, 0xe18]
│     │╎│   0x100009918      str s0, [x22]
│     │╎│   0x10000991c      add x2, x19, 0x58                         ; int64_t arg3
│     │╎│   0x100009920      mov x0, 0x4f54                            ; 'TO'
│     │╎│   0x100009924      movk x0, 0x454b, lsl 16                   ; 'KE'
│     │╎│   0x100009928      movk x0, 0x3d4e, lsl 32                   ; 'N='
│     │╎│   0x10000992c      movk x0, 0x3231, lsl 48                   ; '12' ; int64_t arg1
│     │╎│   0x100009930      mov x1, 0x33                              ; '3'
│     │╎│   0x100009934      movk x1, 0xe900, lsl 48                   ; int64_t arg2
│     │╎│   0x100009938      bl sym.func.10000d18c
│     │╎│   0x10000993c      stur x0, [x22, 4]
│     │╎│   0x100009940      adrp x0, sym.__mh_execute_header          ; 0x100000000
│     │╎│   0x100009944      add x0, x0, 0
│     │╎│   0x100009948      adrp x3, section.15.__TEXT.__oslogstring  ; 0x100010000
│     │╎│   0x10000994c      add x3, x3, 0                             ; 0x100010000 ; "logger.fault (privacy: .sensitive): Leaking %{sensitive}s"
│     │╎│   0x100009950      mov x1, x20
│     │╎│   0x100009954      mov x2, x21
│     │╎│   0x100009958      mov x4, x22
│     │╎│   0x10000995c      mov w5, 0xc
│     │╎│   0x100009960      bl sym.imp._os_log_impl
│     │╎│   0x100009964      add x2, x26, 8
│     │╎│   0x100009968      mov x0, x23
│     │╎│   0x10000996c      mov w1, 1
│     │╎│   0x100009970      bl sym.imp.swift_arrayDestroy
│     │╎│   0x100009974      mov x0, x23
│     │╎│   0x100009978      mov x1, -1
│     │╎│   0x10000997c      mov x2, -1
│     │╎│   0x100009980      bl sym.imp.swift_slowDealloc
│     │╎│   0x100009984      mov x0, x22
│     │╎│   0x100009988      mov x1, -1
│     │╎│   0x10000998c      mov x2, -1
│     │╎│   0x100009990      bl sym.imp.swift_slowDealloc
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x1000098e8(x)
│     └───> 0x100009994      bl sym.imp.objc_release_x20
│      ╎│   0x100009998      mov x8, -0x2000000000000000
│      ╎│   0x10000999c      stp xzr, x8, [x19, 0x58]
│      ╎│   0x1000099a0      add x20, x19, 0x58
│      ╎│   0x1000099a4      mov w0, 0x1d
│      ╎│   0x1000099a8      bl sym _StringGuts.grow...SiF             ; sym.imp._StringGuts.grow...SiF
│      ╎│   0x1000099ac      ldr x0, [x19, 0x60]                       ; void *arg0
│      ╎│   0x1000099b0      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│      ╎│   0x1000099b4      bl sym Darwin.stderr....sFILEVGvg         ; sym.imp.Darwin.stderr....sFILEVGvg
│      ╎│   0x1000099b8      mov x1, x0                                ; FILE *stream
│      ╎│   0x1000099bc      adrp x0, 0x10000e000
│      ╎│   0x1000099c0      add x0, x0, 0x9d0                         ; 0x10000e9d0 ; "fputs: Leaking TOKEN=123 from fputs\n" ; const char *s
│      ╎│   0x1000099c4      bl sym.imp.fputs                          ; int fputs(const char *s, FILE *stream)
│      ╎│   0x1000099c8      bl sym Darwin.stderr....sFILEVGvg         ; sym.imp.Darwin.stderr....sFILEVGvg
│      ╎│   0x1000099cc      mov x20, x0
│      ╎│   0x1000099d0      sub x1, x29, 0xb0                         ; void *arg1
│      ╎│   0x1000099d4      ldr x28, [x19]
│      ╎│   0x1000099d8      mov x0, x28                               ; void *arg0
│      ╎│   0x1000099dc      bl sym.imp.swift_initStackObject          ; void *swift_initStackObject(void *arg0, void *arg1)
│      ╎│   0x1000099e0      mov x21, x0
│      ╎│   0x1000099e4      ldr q0, [x19, 0x40]
│      ╎│   0x1000099e8      str q0, [x0, 0x10]
│      ╎│   0x1000099ec      adrp x27, reloc.Foundation.Data._bridgeToObjectiveC.NSData...F ; 0x100014000
│      ╎│   0x1000099f0      ldr x27, [x27, 0x400]                     ; [0x100014400:4]=127
│      ╎│                                                              ; reloc....SSN
│      ╎│   0x1000099f4      ldr x24, [x19, 8]
│      ╎│   0x1000099f8      stp x27, x24, [x0, 0x38]
│      ╎│   0x1000099fc      mov x22, x0
│      ╎│   0x100009a00      mov x25, 0x4f54                           ; 'TO'
│      ╎│   0x100009a04      movk x25, 0x454b, lsl 16                  ; 'KE'
│      ╎│   0x100009a08      movk x25, 0x3d4e, lsl 32                  ; 'N='
│      ╎│   0x100009a0c      movk x25, 0x3231, lsl 48                  ; '12'
│      ╎│   0x100009a10      str x25, [x22, 0x20]!
│      ╎│   0x100009a14      mov x26, 0x33                             ; '3'
│      ╎│   0x100009a18      movk x26, 0xe900, lsl 48
│      ╎│   0x100009a1c      str x26, [x0, 0x28]
│      ╎│   0x100009a20      adrp x8, 0x10000e000
│      ╎│   0x100009a24      add x8, x8, 0xa00                         ; 0x10000ea00 ; "vfprintf: Leaking %s from vfprintf\n"
│      ╎│   0x100009a28      sub x8, x8, 0x20
│      ╎│   0x100009a2c      orr x3, x8, 0x8000000000000000            ; int64_t arg4
│      ╎│   0x100009a30      mov x1, x20                               ; int64_t arg2
│      ╎│   0x100009a34      mov x2, 0x23                              ; '#'
│      ╎│   0x100009a38      movk x2, 0xd000, lsl 48                   ; int64_t arg3
│      ╎│   0x100009a3c      bl sym.func.1000082f4
│      ╎│   0x100009a40      mov x0, x21
│      ╎│   0x100009a44      bl sym.imp.swift_setDeallocating
│      ╎│   0x100009a48      adrp x0, segment.__DATA                   ; 0x100018000
│      ╎│   0x100009a4c      add x0, x0, 0xf8                          ; int64_t arg1
│      ╎│   0x100009a50      bl sym.func.100009cf0
│      ╎│   0x100009a54      mov x20, x0
│      ╎│   0x100009a58      mov x0, x22
│      ╎│   0x100009a5c      mov w1, 1
│      ╎│   0x100009a60      mov x2, x20
│      ╎│   0x100009a64      bl sym.imp.swift_arrayDestroy
│      ╎│   0x100009a68      bl sym Darwin.stderr....sFILEVGvg         ; sym.imp.Darwin.stderr....sFILEVGvg
│      ╎│   0x100009a6c      bl sym.imp.fileno                         ; int fileno(FILE *stream)
│      ╎│   0x100009a70      mov x21, x0
│      ╎│   0x100009a74      add x1, x19, 0xb8                         ; void *arg1
│      ╎│   0x100009a78      mov x0, x28                               ; void *arg0
│      ╎│   0x100009a7c      bl sym.imp.swift_initStackObject          ; void *swift_initStackObject(void *arg0, void *arg1)
│      ╎│   0x100009a80      mov x22, x0
│      ╎│   0x100009a84      ldr q0, [x19, 0x40]
│      ╎│   0x100009a88      str q0, [x0, 0x10]
│      ╎│   0x100009a8c      stp x27, x24, [x0, 0x38]
│      ╎│   0x100009a90      mov x23, x0
│      ╎│   0x100009a94      str x25, [x23, 0x20]!
│      ╎│   0x100009a98      str x26, [x0, 0x28]
│      ╎│   0x100009a9c      adrp x8, 0x10000e000
│      ╎│   0x100009aa0      add x8, x8, 0xa30                         ; 0x10000ea30 ; "vdprintf: Leaking %s from vdprintf\n"
│      ╎│   0x100009aa4      sub x8, x8, 0x20
│      ╎│   0x100009aa8      orr x3, x8, 0x8000000000000000            ; int64_t arg4
│      ╎│   0x100009aac      mov x1, x21                               ; int64_t arg2
│      ╎│   0x100009ab0      mov x2, 0x23                              ; '#'
│      ╎│   0x100009ab4      movk x2, 0xd000, lsl 48                   ; int64_t arg3
│      ╎│   0x100009ab8      bl sym.func.1000084f4
│      ╎│   0x100009abc      mov x0, x22
│      ╎│   0x100009ac0      bl sym.imp.swift_setDeallocating
│      ╎│   0x100009ac4      mov x0, x23
│      ╎│   0x100009ac8      mov w1, 1
│      ╎│   0x100009acc      mov x2, x20
│      ╎│   0x100009ad0      bl sym.imp.swift_arrayDestroy
│      ╎│   0x100009ad4      add x1, x19, 0x70                         ; void *arg1
│      ╎│   0x100009ad8      mov x0, x28                               ; void *arg0
│      ╎│   0x100009adc      bl sym.imp.swift_initStackObject          ; void *swift_initStackObject(void *arg0, void *arg1)
│      ╎│   0x100009ae0      mov x21, x0
│      ╎│   0x100009ae4      ldr q0, [x19, 0x40]
│      ╎│   0x100009ae8      str q0, [x0, 0x10]
│      ╎│   0x100009aec      stp x27, x24, [x0, 0x38]
│      ╎│   0x100009af0      mov x24, -0x2000000000000000
│      ╎│   0x100009af4      mov x22, x0
│      ╎│   0x100009af8      str x25, [x22, 0x20]!
│      ╎│   0x100009afc      str x26, [x0, 0x28]
│      ╎│   0x100009b00      mov x26, 0x23                             ; '#'
│      ╎│   0x100009b04      movk x26, 0xd000, lsl 48
│      ╎│   0x100009b08      sub x2, x26, 2                            ; int64_t arg3
│      ╎│   0x100009b0c      adrp x8, 0x10000e000
│      ╎│   0x100009b10      add x8, x8, 0xa60                         ; 0x10000ea60 ; "vsyslog: Leaking %s from vsyslog\n"
│      ╎│   0x100009b14      sub x8, x8, 0x20
│      ╎│   0x100009b18      orr x3, x8, 0x8000000000000000            ; int64_t arg4
│      ╎│   0x100009b1c      mov w1, 0xe
│      ╎│   0x100009b20      bl sym.func.1000080e8
│      ╎│   0x100009b24      mov x0, x21
│      ╎│   0x100009b28      bl sym.imp.swift_setDeallocating
│      ╎│   0x100009b2c      mov x0, x22
│      ╎│   0x100009b30      mov w1, 1
│      ╎│   0x100009b34      mov x2, x20
│      ╎│   0x100009b38      bl sym.imp.swift_arrayDestroy
│      ╎│   0x100009b3c      stp xzr, x24, [x19, 0x58]
│      ╎│   0x100009b40      add x20, x19, 0x58
│      ╎│   0x100009b44      mov w0, 0x43                              ; 'C'
│      ╎│   0x100009b48      bl sym _StringGuts.grow...SiF             ; sym.imp._StringGuts.grow...SiF
│      ╎│   0x100009b4c      ldp x8, x9, [x19, 0x58]
│      ╎│   0x100009b50      stp x8, x9, [x19, 0x58]
│      ╎│   0x100009b54      sub x0, x26, 1
│      ╎│   0x100009b58      adrp x8, 0x10000e000
│      ╎│   0x100009b5c      add x8, x8, 0xa90                         ; 0x10000ea90 ; "FileHandle.standardError: Leaking "
│      ╎│   0x100009b60      sub x8, x8, 0x20
│      ╎│   0x100009b64      orr x1, x8, 0x8000000000000000
│      ╎│   0x100009b68      add x20, x19, 0x58
│      ╎│   0x100009b6c      bl sym append...ySSF                      ; sym.imp.append...ySSF
│      ╎│   0x100009b70      add x20, x19, 0x58
│      ╎│   0x100009b74      mov x0, 0x4f54                            ; 'TO'
│      ╎│   0x100009b78      movk x0, 0x454b, lsl 16                   ; 'KE'
│      ╎│   0x100009b7c      movk x0, 0x3d4e, lsl 32                   ; 'N='
│      ╎│   0x100009b80      movk x0, 0x3231, lsl 48                   ; '12'
│      ╎│   0x100009b84      mov x1, 0x33                              ; '3'
│      ╎│   0x100009b88      movk x1, 0xe900, lsl 48
│      ╎│   0x100009b8c      bl sym append...ySSF                      ; sym.imp.append...ySSF
│      ╎│   0x100009b90      sub x0, x26, 4
│      ╎│   0x100009b94      adrp x8, 0x10000e000
│      ╎│   0x100009b98      add x8, x8, 0xac0                         ; 0x10000eac0 ; " from FileHandle.standardError\n"
│      ╎│   0x100009b9c      sub x8, x8, 0x20
│      ╎│   0x100009ba0      orr x1, x8, 0x8000000000000000
│      ╎│   0x100009ba4      add x20, x19, 0x58
│      ╎│   0x100009ba8      bl sym append...ySSF                      ; sym.imp.append...ySSF
│      ╎│   0x100009bac      ldp x20, x22, [x19, 0x58]
│      ╎│   0x100009bb0      ldr x23, [x19, 0x10]
│      ╎│   0x100009bb4      mov x8, x23
│      ╎│   0x100009bb8      bl sym Foundation...8EncodingV4utf8ACvgZ  ; sym.imp.Foundation...8EncodingV4utf8ACvgZ
│      ╎│   0x100009bbc      mov x0, x23
│      ╎│   0x100009bc0      mov w1, 0
│      ╎│   0x100009bc4      mov x2, x20
│      ╎│   0x100009bc8      mov x3, x22
│      ╎│   0x100009bcc      bl sym Foundation...btF                   ; sym.imp.Foundation...btF
│      ╎│   0x100009bd0      mov x20, x0
│      ╎│   0x100009bd4      mov x21, x1
│      ╎│   0x100009bd8      mov x0, x22                               ; void *arg0
│      ╎│   0x100009bdc      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│      ╎│   0x100009be0      ldp x8, x1, [x19, 0x18]
│      ╎│   0x100009be4      ldr x8, [x8, 8]
│      ╎│   0x100009be8      mov x0, x23
│      ╎│   0x100009bec      blr x8
│      ╎│   0x100009bf0      lsr x8, x21, 0x3c
│      ╎│   0x100009bf4      cmp x8, 0xe
│     ┌───< 0x100009bf8      b.hi 0x100009c58
│     │╎│   0x100009bfc      adrp x8, reloc.Foundation.Data._bridgeToObjectiveC.NSData...F ; 0x100014000
│     │╎│   0x100009c00      ldr x0, [x8, 0x4d0]                       ; [0x1000144d0:4]=153
│     │╎│                                                              ; reloc.NSFileHandle ; void *arg0
│     │╎│   0x100009c04      bl sym.imp.objc_opt_self                  ; void *objc_opt_self(void *arg0)
│     │╎│   0x100009c08      adrp x8, segment.__DATA                   ; 0x100018000
│     │╎│   0x100009c0c      ldr x1, [x8, 0x90]                        ; [0x100018090:4]=0xebc9 ; section.21.__DATA.__objc_selrefs
│     │╎│                                                              [21] -rw- section size 40 named 21.__DATA.__objc_selrefs ; char *selector
│     │╎│   0x100009c10      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│     │╎│   0x100009c14      mov x29, x29
│     │╎│   0x100009c18      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│     │╎│   0x100009c1c      mov x22, x0
│     │╎│   0x100009c20      mov x0, x20
│     │╎│   0x100009c24      mov x1, x21
│     │╎│   0x100009c28      bl sym.imp.Foundation.Data._bridgeToObjectiveC.NSData...F
│     │╎│   0x100009c2c      mov x23, x0
│     │╎│   0x100009c30      adrp x8, segment.__DATA                   ; 0x100018000
│     │╎│   0x100009c34      ldr x1, [x8, 0x98]                        ; char *selector
│     │╎│   0x100009c38      mov x0, x22                               ; void *instance
│     │╎│   0x100009c3c      mov x2, x23
│     │╎│   0x100009c40      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│     │╎│   0x100009c44      bl sym.imp.objc_release_x22
│     │╎│   0x100009c48      bl sym.imp.objc_release_x23
│     │╎│   0x100009c4c      mov x0, x20                               ; int64_t arg2
│     │╎│   0x100009c50      mov x1, x21
│     │╎│   0x100009c54      bl sym.func.100009dc8
│     │╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100009bf8(x)
│     └───> 0x100009c58      stp xzr, x24, [x19, 0x58]
│      ╎│   0x100009c5c      add x20, x19, 0x58
│      ╎│   0x100009c60      mov w0, 0x1f
│      ╎│   0x100009c64      bl sym _StringGuts.grow...SiF             ; sym.imp._StringGuts.grow...SiF
│      ╎│   0x100009c68      ldr x0, [x19, 0x60]                       ; void *arg0
│      ╎│   0x100009c6c      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│      ╎│   0x100009c70      ldr x9, [x19, 0x28]
│      ╎│   0x100009c74      ldr x8, [x19, 0x30]
│      ╎│   0x100009c78      stp x9, x8, [x19, 0x58]
│      ╎│   0x100009c7c      ldr x8, [x19, 0x38]
│      ╎│   0x100009c80      str x8, [x19, 0x68]
│      ╎│   0x100009c84      add x8, x26, 3
│      ╎│   0x100009c88      adrp x9, 0x10000e000
│      ╎│   0x100009c8c      add x9, x9, 0xae0                         ; 0x10000eae0 ; "Successfully logged a token: TOKEN=123"
│      ╎│   0x100009c90      sub x9, x9, 0x20
│      ╎│   0x100009c94      orr x9, x9, 0x8000000000000000
│      ╎│   0x100009c98      stp x8, x9, [x29, -0x68]
│      ╎│   0x100009c9c      adrp x0, segment.__DATA                   ; 0x100018000
│      ╎│   0x100009ca0      add x0, x0, 0x100                         ; int64_t arg1
│      ╎│   0x100009ca4      bl sym.func.100009cf0
│      ╎│   0x100009ca8      mov x1, x0
│      ╎│   0x100009cac      sub x0, x29, 0x68
│      ╎│   0x100009cb0      add x20, x19, 0x58
│      ╎│   0x100009cb4      bl sym SwiftUI.State.wrappedValue...s     ; sym.imp.SwiftUI.State.wrappedValue...s
│      ╎│   0x100009cb8      sub sp, x29, 0x50
│      ╎│   0x100009cbc      ldp x29, x30, [var_0hx50]
│      ╎│   0x100009cc0      ldp x20, x19, [var_0hx40]
│      ╎│   0x100009cc4      ldp x22, x21, [var_0hx30]
│      ╎│   0x100009cc8      ldp x24, x23, [var_0hx20]
│      ╎│   0x100009ccc      ldp x26, x25, [var_0hx10]
│      ╎│   0x100009cd0      ldp x28, x27, [sp], 0x60
│      ╎│   0x100009cd4      ret
│      ╎│   ; CODE XREF from sym.func.1000086f4 @ 0x100008988(x)
│      ╎└─> 0x100009cd8      adrp x0, segment.__DATA                   ; 0x100018000
│      ╎    0x100009cdc      add x0, x0, 0xb8                          ; 0x1000180b8
│      ╎                                                               ; section.22.__DATA.__data
│      ╎                                                               [22] -rw- section size 776 named 22.__DATA.__data
│      ╎    0x100009ce0      adrp x1, sym.func.100008000               ; 0x100008000
│      ╎    0x100009ce4      add x1, x1, 0
│      ╎    0x100009ce8      bl sym.imp.swift_once
└      └──< 0x100009cec      b 0x10000898c
