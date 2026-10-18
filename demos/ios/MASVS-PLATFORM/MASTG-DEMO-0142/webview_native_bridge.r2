e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false
e bin.relocs.apply=true

?e List all uses of the 'addScriptMessageHandler:name:' selector:
f~addScriptMessageHandler

?e

?e xrefs to 'addScriptMessageHandler:name:':
axt @ reloc.fixup.addScriptMessageHandler:name:

?e

?e Code snippet containing the bridge registration call:
pd-- 20 @ 0x1eac
