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

?e Code snippet at first call site:
pd 10 @ 0x1698

?e

?e Code snippet at second call site:
pd 10 @ 0x18ec
