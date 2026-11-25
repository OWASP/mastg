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
?e === Objective-C stubs that load each App Attest selector and dataTaskWithURL ===
axt @ 0x100014168
axt @ 0x100014160
axt @ 0x100014140
axt @ 0x100014158
axt @ 0x100014150

?e
?e === Calls to isSupported, generateKey, attestKey and generateAssertion ===
axt @ 0x10000bee0~CALL
axt @ 0x10000bec0~CALL
axt @ 0x10000be40~CALL
axt @ 0x10000bea0~CALL

?e
?e === Strings in the binary that mention a challenge ===
izz~challenge

?e
?e === The challenge endpoint is only used to build a URL ===
axt @ 0x10000cfd0~STRN
pd 8 @ 0x100004078

?e
?e === The function that uses that URL calls dataTaskWithURL ===
axt @ 0x100004000~sym.func
axt @ 0x10000be80~CALL

?e
?e === That function is called once before the attestation and once before the assertion ===
axt @ 0x100006afc

?e
?e === Attestation: the fetched challenge is hashed before generateKey is called ===
pdf @ 0x10000442c~",CryptoKit.HashFunction,fcn.10000bec0

?e
?e === Assertion: the fetched challenge goes into the JSON payload, which is hashed before generateAssertion ===
pdf @ 0x100004ae0~",JSONEncoder.encode,CryptoKit.HashFunction,fcn.10000bea0
