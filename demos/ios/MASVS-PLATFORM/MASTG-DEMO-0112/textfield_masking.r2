e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false
e bin.relocs.apply=true

?e List all references to "UITextField", "setSecureTextEntry" and "SecureField":
f~UITextField,setSecureTextEntry,SecureField

?e
?e Cross-references to the "setSecureTextEntry:" selector:
axt @ 0x000201c0

?e
?e Cross-references to the "SecureField" initializer:
axt @ 0x000040a4

?e
?e Disassembly around the password field setup (isSecureTextEntry = false, FAIL):
pd--10 @ 0x1a30

?e
?e Disassembly around the OTP 1 field setup (isSecureTextEntry = true, PASS):
pd--10 @ 0x1b40

?e
?e Disassembly around the SecureField (OTP 2) setup (PASS):
pd 8 @ 0x3c50
