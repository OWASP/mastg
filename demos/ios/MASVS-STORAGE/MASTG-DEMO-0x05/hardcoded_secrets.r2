e asm.bytes=false
e scr.color=0
e scr.interactive=false
e scr.utf8=false
e bin.relocs.apply=true
e asm.var=false

?e === All strings in the __TEXT.__cstring section ===
izz~__TEXT.__cstring

?e
?e === Credentials matching well-known provider formats ===
izz~AIza
izz~AKIA

?e
?e === Cross references to the embedded credentials ===
axt @ 0x100009460
axt @ 0x1000094b0
axt @ 0x1000094f0

?e
?e === Disassembly of the function referencing them ===
pd 40 @ 0x100004ec0
