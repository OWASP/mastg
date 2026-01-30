Uses of the Network.NWEndpoint.Port.integerLiteral function:
0x100006c00 12 sym.imp.Network.NWEndpoint.Port.integerLiteral.UInt16...V_tcfC
0x10000c0c8 8 reloc.Network.NWEndpoint.Port.integerLiteral.UInt16...V_tcfC

xrefs to Network.NWEndpoint.Port.integerLiteral:
sym.func.1000046c4 0x1000047f4 [CALL:--x] bl sym.imp.Network.NWEndpoint.Port.integerLiteral.UInt16...V_tcfC

Use of Network.NWEndpoint.Port.integerLiteral:
│           0x1000047e0      movk x1, 0xeb00, lsl 48
│           0x1000047e4      mov x8, x23
│           0x1000047e8      bl sym.imp.Network.NWEndpoint.Host.stringLiteral.__String_...cfC_
│           0x1000047ec      mov x8, x21
│           0x1000047f0      mov w0, 0x50                              ; 'P'
│           0x1000047f4      bl sym.imp.Network.NWEndpoint.Port.integerLiteral.UInt16...V_tcfC
│           0x1000047f8      mov x0, 0
│           0x1000047fc      bl sym.imp.Network.NWConnection.allocator_...a_ ; Network.NWConnection.allocator(...a)
│           0x100004800      mov x26, x0
│           0x100004804      ldr x8, [x19, 0x10]

Value passed to Network.NWEndpoint.Port.integerLiteral:
80
