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

?e

?e Search for calls to `loadHTMLString:baseURL:`
f~loadHTMLString

?e

?e xrefs to `loadHTMLString:baseURL:`:
axt @ 0x1000140a0

?e

?e Code snippet showing how the string is passed to `loadHTMLString:baseURL:`

pd 10 @ 0x100004258