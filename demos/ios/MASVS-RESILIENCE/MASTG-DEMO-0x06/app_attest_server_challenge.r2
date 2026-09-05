e asm.bytes=false
e scr.color=false
e asm.var=false

?e === App Attest API references ===
f~generateKeyWithCompletionHandle
f~attestKey:clientDataHash:
f~generateAssertion:clientDataHas
f~isSupported

?e
?e === No hardcoded challenge string in the binary ===
izz~app-attest-challenge

?e
?e === Challenge is fetched from a server endpoint instead ===
izz~example.com

?e
?e === Cross references to the challenge endpoint ===
axt @ 0x10000be30

?e
?e === The endpoint is turned into a URL for the request ===
pd 10 @ 0x100004078

?e
?e === The response data is hashed to build the clientDataHash ===
pd 8 @ 0x100004114

?e
?e === Network call used to retrieve the challenge ===
axt @ 0x100014150

?e
?e === attestKey called with the server-derived clientDataHash ===
axt @ 0x100014140

?e
?e === generateAssertion called with its own server-derived hash ===
axt @ 0x100014158
