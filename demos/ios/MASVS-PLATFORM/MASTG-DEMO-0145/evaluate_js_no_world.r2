e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false

?e List all uses of the 'evaluateJavaScript:completionHandler:' selector:
f~evaluateJavaScript

?e

?e xrefs to 'evaluateJavaScript:completionHandler:':
axt @ reloc.fixup.evaluateJavaScript:completionHa

?e

?e Code snippet at first call site (attacker-injected prototype override):
pd 15 @ 0x1000047dc

?e
?e Code snippet at second call site (reads recipient account number):
pd 43 @ 0x100004820
