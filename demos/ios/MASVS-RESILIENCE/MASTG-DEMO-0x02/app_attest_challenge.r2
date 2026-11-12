e asm.bytes=false
e scr.color=0
e scr.interactive=false
e scr.utf8=false
e bin.relocs.apply=true
e asm.var=false

?e === App Attest API references ===
f~generateKeyWithCompletionHandle
f~attestKey:clientDataHash:
f~generateAssertion:clientDataHas
f~isSupported

?e
?e === Objective-C stubs that load each App Attest selector ===
axt @ 0x100014160
axt @ 0x100014158
axt @ 0x100014140
axt @ 0x100014150

?e
?e === Calls to isSupported, generateKey, attestKey and generateAssertion ===
axt @ 0x10000bb60~CALL
axt @ 0x10000bb40~CALL
axt @ 0x10000bae0~CALL
axt @ 0x10000bb20~CALL

?e
?e === Hardcoded challenge string ===
izz~mastg-app-attest-challenge

?e
?e === Cross references to the hardcoded challenge ===
axt @ 0x10000c2a0

?e
?e === Attestation: the constant is hashed before generateKey is called ===
pdf @ 0x100004348~mastg-app-attest,CryptoKit.HashFunction,fcn.10000bb60,fcn.10000bb40

?e
?e === Assertion: the constant goes into the JSON payload, which is hashed before generateAssertion ===
pdf @ 0x10000483c~mastg-app-attest,JSONEncoder.encode,CryptoKit.HashFunction,fcn.10000bb20
