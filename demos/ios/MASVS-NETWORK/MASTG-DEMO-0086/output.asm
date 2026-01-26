Uses of the BSD sockets functions:
139 0x100006940 NONE FUNC               connect
150 0x1000069c4 NONE FUNC               getaddrinfo
155 0x100006a00 NONE FUNC               recv
157 0x100006a18 NONE FUNC               send
158 0x100006a24 NONE FUNC               socket

xrefs to getaddrinfo:
sym.func.100004108 0x1000041d0 [CALL:--x] bl sym.imp.getaddrinfo

Use of getaddrinfo:
│           0x100004180      str w8, [arg_f0hx5c]
│           0x100004184      str xzr, [var_48h]
│           0x100004188      mov w8, 0x50                              ; 'P'
│           0x10000418c      strh w8, [var_30h]
│           0x100004190      adrp x0, segment.__DATA_CONST             ; 0x100008000
│           0x100004194      ldr x0, [x0, 0x3e8]                       ; [0x1000083e8:4]=124 ; "|"
│           0x100004198      adrp x1, segment.__DATA_CONST             ; 0x100008000
│           0x10000419c      ldr x1, [x1, 0x3f0]                       ; [0x1000083f0:4]=125 ; "}"
│           0x1000041a0      add x20, sp, 0x30
│           0x1000041a4      bl sym.imp.CustomStringConvertible.description__String_...vgTj_ ; CustomStringConvertible.description__String(...vgTj)
│           0x1000041a8      mov x20, x1
│           0x1000041ac      bl sym.imp.utf8CString.ContiguousArray.setter...Vys4Int8VGvg
│           0x1000041b0      mov x24, x0
│           0x1000041b4      mov x0, x20                               ; void *arg0
│           0x1000041b8      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│           0x1000041bc      adrp x0, 0x100006000
│           0x1000041c0      add x0, x0, 0xd50                         ; 0x100006d50 ; "httpbin.org"
│           0x1000041c4      add x1, x24, 0x20
│           0x1000041c8      add x2, sp, 0x50
│           0x1000041cc      add x3, sp, 0x48
│           0x1000041d0      bl sym.imp.getaddrinfo
│           0x1000041d4      mov x21, x0
│           0x1000041d8      mov x0, x24                               ; void *arg0
│           0x1000041dc      bl sym.imp.swift_release                  ; void swift_release(void *arg0)
│       ┌─< 0x1000041e0      cbz w21, 0x10000429c
│       │   ; CODE XREF from sym.func.100004108 @ 0x1000042a0(x)
│       │   0x1000041e4      mov x8, -0x2000000000000000
│       │   0x1000041e8      stp xzr, x8, [var_30h]
│       │   0x1000041ec      add x20, sp, 0x30
│       │   0x1000041f0      mov w0, 0x1f
│       │   0x1000041f4      bl sym.imp._StringGuts.grow_...SiF_       ; _StringGuts.grow(...SiF)
│       │   0x1000041f8      ldr x0, [arg0]                            ; void *arg0
│       │   0x1000041fc      bl sym.imp.swift_bridgeObjectRelease      ; void swift_bridgeObjectRelease(void *arg0)
│       │   0x100004200      adrp x8, 0x100006000
│       │   0x100004204      add x8, x8, 0xd60                         ; 0x100006d60 ; "Failed to resolve hostname: "
│       │   0x100004208      sub x8, x8, 0x20
│       │   0x10000420c      orr x8, x8, 0x8000000000000000
│       │   0x100004210      add x9, x27, 9
│       │   0x100004214      stp x9, x8, [var_30h]
│       │   0x100004218      mov x0, x21
│       │   0x10000421c      bl sym.imp.gai_strerror

Value passed to getaddrinfo:
80
