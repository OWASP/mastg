e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false
e bin.relocs.apply=true

?e Search for input type=password in the binary string table:
iz~input type="password"

?e

?e xrefs to the string containing the password field:
axt @ 0x10000ac00
