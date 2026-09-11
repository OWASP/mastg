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

?e Code snippet showing the construction of the JavaScript string and the call to evaluateJavaScript:
pd 35 @ 0x100004910
