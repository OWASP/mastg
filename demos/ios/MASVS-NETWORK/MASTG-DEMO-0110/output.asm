References to URLSessionConfiguration tlsMinimumSupportedProtocolVersion setter:
0x10000a772 39 str.setTLSMinimumSupportedProtocolVersion:
0x100010180 8 reloc.fixup.setTLSMinimumSupportedProtocolV

xrefs to tlsMinimumSupportedProtocolVersion setter:
fcn.1000091a0 0x1000091a4 [DATA:r--] ldr x1, reloc.fixup.setTLSMinimumSupportedProtocolV

Code setting tlsMinimumSupportedProtocolVersion:
└           0x100009154      brk 1
            0x100009158      brk 1
            0x10000915c      brk 1
            ; CALL XREF from sym.func.100004000 @ 0x10000426c(x)
┌ 24: fcn.100009160 ();
│           0x100009160      adrp x1, segment.__DATA                   ; 0x100010000
│           0x100009164      ldr x1, [x1, 0x170]                       ; [0x100010170:4]=0xa751 ; "Q\xa7"
│           0x100009168      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x10000916c      ldr x16, [x16, 0xc0]                      ; [0x10000c0c0:4]=22
│                                                                      ; reloc.objc_msgSend
│           0x100009170      br x16
└           0x100009174      brk 1
            0x100009178      brk 1
            0x10000917c      brk 1
            ; CALL XREF from sym.func.100004000 @ 0x1000041ac(x)
┌ 24: fcn.100009180 ();
│           0x100009180      adrp x1, segment.__DATA                   ; 0x100010000
│           0x100009184      ldr x1, [x1, 0x178]                       ; [0x100010178:4]=0xa758 ; "X\xa7"
│           0x100009188      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x10000918c      ldr x16, [x16, 0xc0]                      ; [0x10000c0c0:4]=22
│                                                                      ; reloc.objc_msgSend
│           0x100009190      br x16
└           0x100009194      brk 1
            0x100009198      brk 1
            0x10000919c      brk 1
            ; CALL XREF from sym.func.100004000 @ 0x100004198(x)
┌ 24: fcn.1000091a0 ();
│           0x1000091a0      adrp x1, segment.__DATA                   ; 0x100010000
│           0x1000091a4      ldr x1, [x1, 0x180]                       ; [0x100010180:4]=0xa772 ; "r\xa7"
│           0x1000091a8      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000091ac      ldr x16, [x16, 0xc0]                      ; [0x10000c0c0:4]=22
│                                                                      ; reloc.objc_msgSend
│           0x1000091b0      br x16
└           0x1000091b4      brk 1
            0x1000091b8      brk 1
            0x1000091bc      brk 1
            ; CALL XREF from sym.func.100004370 @ 0x100004700(x)
┌ 24: fcn.1000091c0 ();
│           0x1000091c0      adrp x1, segment.__DATA                   ; 0x100010000
│           0x1000091c4      ldr x1, [x1, 0x188]                       ; [0x100010188:4]=0xa799 ; reloc.fixup.statusCode
│           0x1000091c8      adrp x16, segment.__DATA_CONST            ; 0x10000c000
│           0x1000091cc      ldr x16, [x16, 0xc0]                      ; [0x10000c0c0:4]=22
│                                                                      ; reloc.objc_msgSend
│           0x1000091d0      br x16
└           0x1000091d4      brk 1
            0x1000091d8      brk 1
            0x1000091dc      brk 1
            ;-- section.3.__TEXT.__const:
            ; NULL XREF from segment.__TEXT @ +0x1c0(r)
            ; DATA XREF from sym.func.100004000 @ 0x1000041fc(r)
            ; DATA XREF from sym.func.100004370 @ 0x10000486c(r)
            0x1000091e0      .qword 0x0000000042000000                 ; [03] -r-x section size 1236 named 3.__TEXT.__const
            ; DATA XREF from sym.func.100004000 @ 0x10000403c(r)
            ; DATA XREF from sym.func.100004328 @ 0x100004344(r)
            0x1000091e8      .qword 0x00000007000004cc
            ; DATA XREF from sym.func.100004370 @ 0x1000048d8(r)
            ; DATA XREF from sym.func.100004c20 @ 0x100004c44(r)
            0x1000091f0      .qword 0x00000009000004e0
            0x1000091f8      .qword 0x0000000000000003
            ; DATA XREF from sym.func.100005500 @ 0x10000581c(r)
            0x100009200      .qword 0x0000000000000002

Search for ARM64 instructions that load the TLS protocol constants used by tlsMinimumSupportedProtocolVersion.
The TLS constants are 0x0301 for TLS 1.0, 0x0302 for TLS 1.1, 0x0303 for TLS 1.2, and 0x0304 for TLS 1.3.
In this binary, the value is passed as the first argument to the Objective C setter in register w2. For example, mov w2, 0x301 loads TLS 1.0.
The instruction mov w2, 0x301 is encoded as the ARM64 word 0x52806022. Because the binary stores instructions in little endian order, the bytes appear as 22 60 80 52, which r2 searches as 22608052.
Search for the encoded mov w2 instructions for each TLS version.
0x100004194 hit4_0 22608052
Print the surrounding instructions
│           0x10000416c      mov x1, x24
│           0x100004170      mov x2, x22
│           0x100004174      blr x8
│           0x100004178      adrp x8, segment.__DATA_CONST             ; 0x10000c000
│           0x10000417c      ldr x0, [x8, 0xb28]                       ; [0x10000cb28:4]=246
│                                                                      ; reloc.NSURLSessionConfiguration
│                                                                      [23] -rw- section size 40 named 23.__DATA_CONST.__objc_classrefs ; void *arg0
│           0x100004180      bl sym.imp.objc_opt_self                  ; void *objc_opt_self(void *arg0)
│           0x100004184      bl fcn.100009120
│           0x100004188      mov x29, x29
│           0x10000418c      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x100004190      mov x24, x0
│           ;-- _0:
│           0x100004194      mov w2, 0x301
│           0x100004198      bl fcn.1000091a0
│           0x10000419c      adrp x8, segment.__DATA_CONST             ; 0x10000c000
│           0x1000041a0      ldr x0, [x8, 0xb30]                       ; [0x10000cb30:4]=247
│                                                                      ; reloc.NSURLSession ; void *arg0
│           0x1000041a4      bl sym.imp.objc_opt_self                  ; void *objc_opt_self(void *arg0)
│           0x1000041a8      mov x2, x24
│           0x1000041ac      bl fcn.100009180
│           0x1000041b0      mov x29, x29
│           0x1000041b4      bl sym.imp.objc_retainAutoreleasedReturnValue ; void objc_retainAutoreleasedReturnValue(void *instance)
│           0x1000041b8      mov x25, x0
