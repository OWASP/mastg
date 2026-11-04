e asm.bytes=false
e scr.color=false
e asm.var=false

?e === App Attest API references ===
f~generateKeyWithCompletionHandle
f~attestKey:clientDataHash:
f~generateAssertion:clientDataHas
f~isSupported

?e
?e === Hardcoded challenge string ===
izz~mastg-app-attest-challenge

?e
?e === Cross references to the hardcoded challenge ===
axt @ 0x10000b0b0

?e
?e === clientDataHash built from the hardcoded constant ===
pd 30 @ 0x100006390

?e
?e === attestKey called with that clientDataHash ===
pd 12 @ 0x100004100

?e
?e === generateAssertion called with the same clientDataHash ===
pd 12 @ 0x100004360
