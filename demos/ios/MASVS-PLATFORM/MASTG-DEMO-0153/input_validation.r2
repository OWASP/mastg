e asm.bytes=false
e scr.color=false
e asm.var=false

?e === Universal link handler method ===
f~handleUniversalLink

?e
?e === References to Int conversion (input validation) ===
f~Int_init

?e
?e === References to onContinueUserActivity (SwiftUI universal link handling) ===
f~onContinueUserActivity

?e
?e === References to webpageURL (universal link URL) ===
f~webpageURL

?e
?e === Disassembly of handleUniversalLink: URL parsing and path (action) comparison ===
pd 50 @ 0x1000069a0

?e
?e === Disassembly of handleUniversalLink: query value extraction through string interpolation ===
pd 80 @ 0x100006bc0
