Uses of the CCCrypt function:
0x1000075ec    1     12 sym.imp.CCCrypt

xrefs to CCCrypt:
sym.func.100004000 0x1000040e0 [CALL:--x] bl sym.imp.CCCrypt
sym.func.10000469c 0x100004824 [CALL:--x] bl sym.imp.CCCrypt

Use of CCCrypt with ECB mode:
│           0x100004808      mov w0, 0
│           0x10000480c      mov w1, 0
│           0x100004810      mov w2, 3
│           0x100004814      mov w4, 0x10
│           0x100004818      mov x5, 0
│           0x10000481c      mov x6, x20
│           0x100004820      mov x7, x24
│           0x100004824      bl sym.imp.CCCrypt                        ; CCCryptorStatus CCCrypt(CCOperation op, CCAlgorithm alg, int32_t options, const void *key, uint32_t keyLength, const void *iv, const void *dataIn, uint32_t dataInLength, void *dataOut, uint32_t dataOutAvailable, uint32_t *dataOutMoved)
│           ; CODE XREFS from sym.func.10000469c @ 0x100004874(x), 0x10000487c(x)
│           0x100004828      mov x21, x19
│           0x10000482c      ldp x29, x30, [var_c0h]
│           0x100004830      ldp x20, x19, [arg_20h]
│           0x100004834      ldp x23, x22, [var_a0h]
│           0x100004838      ldp x25, x24, [var_90h]
│           0x10000483c      ldp x27, x26, [var_80h]
