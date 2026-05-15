Print flags about "+UITextField,UITextView,UISearchBar"
0x100010180 8 reloc.UITextField

Print xrefs to 0x100010180
sym.func.100004704 0x100004760 [ICOD:r--] add x2, x2, reloc.UITextField
sym.func.100004704 0x10000481c [ICOD:r--] add x2, x2, reloc.UITextField
sym.func.100004704 0x1000048e0 [ICOD:r--] add x2, x2, reloc.UITextField
sym.func.10000558c 0x1000055d0 [DATA:r--] ldr x0, reloc.UITextField
sym.func.10000558c 0x10000563c [DATA:r--] ldr x0, reloc.UITextField
sym.func.10000558c 0x1000056c0 [ICOD:r--] add x2, x2, reloc.UITextField

Print flags about "autocorrectionType,setSecureTextEntry,spellCheckingType"
0x10000a125 23 str.setAutocorrectionType:
0x10000a13c 20 str.setSecureTextEntry:
0x10000a150 22 str.setSpellCheckingType:
0x100010110 8 reloc.fixup.setSecureTextEntry:
0x100010120 8 reloc.fixup.setAutocorrectionType:
0x100010128 8 reloc.fixup.setSpellCheckingType:

Print xrefs to 0x100010110
sym.func.10000465c 0x1000046b0 [DATA:r--] ldr x1, reloc.fixup.setSecureTextEntry:
Print xrefs to 0x100010120
sym.func.10000450c 0x100004550 [DATA:r--] ldr x1, reloc.fixup.setAutocorrectionType:
sym.func.1000045bc 0x100004604 [DATA:r--] ldr x1, reloc.fixup.setAutocorrectionType:
Print xrefs to 0x100010128
sym.func.10000450c 0x100004564 [DATA:r--] ldr x1, reloc.fixup.setSpellCheckingType:

Print disassembly around "name_field" in the function
│           0x100004528      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│           0x10000452c      mov x20, x0
│           0x100004530      adrp x8, segment.__DATA                   ; 0x100010000
│           0x100004534      ldr x1, [x8, 0x108]                       ; [0x100010108:4]=0xa01b ; reloc.fixup.setPlaceholder: ; char *selector
│           0x100004538      mov x0, x19                               ; void *instance
│           0x10000453c      mov x2, x20
│           0x100004540      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x100004544      mov x0, x20                               ; void *instance
│           0x100004548      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│           0x10000454c      adrp x8, segment.__DATA                   ; 0x100010000
│           0x100004550      ldr x1, [x8, 0x120]                       ; [0x100010120:4]=0xa125 ; reloc.fixup.setAutocorrectionType: ; char *selector
│           0x100004554      mov x0, x19                               ; void *instance
│           0x100004558      mov x2, 0
│           0x10000455c      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x100004560      adrp x8, segment.__DATA                   ; 0x100010000
│           0x100004564      ldr x1, [x8, 0x128]                       ; [0x100010128:4]=0xa150 ; reloc.fixup.setSpellCheckingType: ; char *selector
│           0x100004568      mov x0, x19                               ; void *instance
│           0x10000456c      mov w2, 1
│           0x100004570      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x100004574      mov x0, 0x616e                            ; 'na'

Print disassembly around "email_field" in the function
│           0x1000045dc      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│           0x1000045e0      mov x20, x0
│           0x1000045e4      adrp x8, segment.__DATA                   ; 0x100010000
│           0x1000045e8      ldr x1, [x8, 0x108]                       ; [0x100010108:4]=0xa01b ; reloc.fixup.setPlaceholder: ; char *selector
│           0x1000045ec      mov x0, x19                               ; void *instance
│           0x1000045f0      mov x2, x20
│           0x1000045f4      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x1000045f8      mov x0, x20                               ; void *instance
│           0x1000045fc      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│           0x100004600      adrp x8, segment.__DATA                   ; 0x100010000
│           0x100004604      ldr x1, [x8, 0x120]                       ; [0x100010120:4]=0xa125 ; reloc.fixup.setAutocorrectionType: ; char *selector
│           0x100004608      mov x0, x19                               ; void *instance
│           0x10000460c      mov w2, 1
│           0x100004610      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x100004614      mov x0, 0x6d65                            ; 'em'
│           0x100004618      movk x0, 0x6961, lsl 16                   ; 'ai'
│           0x10000461c      movk x0, 0x5f6c, lsl 32                   ; 'l_'
│           0x100004620      movk x0, 0x6966, lsl 48                   ; 'fi'
│           0x100004624      mov x1, 0x6c65                            ; 'el'
│           0x100004628      movk x1, 0x64, lsl 16                     ; 'd'

Print disassembly around "password_field" in the function
│           0x100004688      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)
│           0x10000468c      mov x21, x0
│           0x100004690      adrp x8, segment.__DATA                   ; 0x100010000
│           0x100004694      ldr x1, [x8, 0x108]                       ; [0x100010108:4]=0xa01b ; reloc.fixup.setPlaceholder: ; char *selector
│           0x100004698      mov x0, x19                               ; void *instance
│           0x10000469c      mov x2, x21
│           0x1000046a0      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x1000046a4      mov x0, x21                               ; void *instance
│           0x1000046a8      bl sym.imp.objc_release                   ; void objc_release(void *instance)
│           0x1000046ac      adrp x8, segment.__DATA                   ; 0x100010000
│           0x1000046b0      ldr x1, [x8, 0x110]                       ; [0x100010110:4]=0xa13c ; reloc.fixup.setSecureTextEntry: ; char *selector
│           0x1000046b4      mov x0, x19                               ; void *instance
│           0x1000046b8      mov w2, 1
│           0x1000046bc      bl sym.imp.objc_msgSend                   ; void *objc_msgSend(void *instance, char *selector)
│           0x1000046c0      mov x1, 0x665f                            ; '_f'
│           0x1000046c4      movk x1, 0x6569, lsl 16                   ; 'ie'
│           0x1000046c8      movk x1, 0x646c, lsl 32                   ; 'ld'
│           0x1000046cc      movk x1, 0xee00, lsl 48
│           0x1000046d0      orr x0, x20, 0x20
│           0x1000046d4      bl sym.imp.Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_ ; Foundationbool(...ridgeToObjectiveCSo8NSStringCyF)

