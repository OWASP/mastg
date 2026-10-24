e asm.bytes=false
e scr.color=false
e asm.var=false

?e === URL handler method ===
f~handleURL

?e
?e === References to Int conversion (input validation) ===
f~Int_init

?e
?e === References to onOpenURL (SwiftUI URL handling) ===
f~onOpenURL

?e
?e === Disassembly of handleURL: URL parsing and action comparison ===
pd 30 @ 0x100004340

?e
?e === Disassembly of handleURL: value extraction through string interpolation ===
pd 45 @ 0x100004500
