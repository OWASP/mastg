e asm.bytes=false
e scr.color=false
e asm.var=false

?e === URL scheme handler methods ===
f~openURLContexts

?e
?e === Cross-references to sourceApplication from handler ===
axt @ reloc.fixup.sourceApplication

?e
?e === Disassembly around sourceApplication access (willConnectTo) ===
pd 15 @ 0x100004c8c

?e
?e === Disassembly around sourceApplication access (openURLContexts) ===
pd 15 @ 0x1000051a8
