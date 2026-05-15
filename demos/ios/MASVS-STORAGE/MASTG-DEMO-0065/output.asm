Uses of NSLog:
0x10000d780    1     12 sym.imp.Foundation.NSLogCVarArg...dtF

xrefs to NSLog:
sym.func.1000086f4 0x1000088ec [CALL:--x] bl sym.imp.Foundation.NSLogCVarArg...dtF

Invocation of NSLog:
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
