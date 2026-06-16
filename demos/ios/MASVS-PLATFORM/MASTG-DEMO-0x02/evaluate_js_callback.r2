e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false
e bin.relocs.apply=true

?e List all uses of the 'evaluateJavaScript:completionHandler:' selector:
f~evaluateJavaScript

?e

?e xrefs to 'evaluateJavaScript:completionHandler:':
axt @ reloc.fixup.evaluateJavaScript:completionHa

?e

?e Code snippet at first call site (getSecret case: builds window.receiveSecret('...') with the API key):
pd 105 @ 0x1510

?e

?e Code snippet at second call site (getCredentials case: builds window.receiveCredentials('...') with the credentials):
pd 105 @ 0x1764
