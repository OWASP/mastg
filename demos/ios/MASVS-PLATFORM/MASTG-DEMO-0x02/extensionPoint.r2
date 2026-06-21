e asm.bytes=false
e scr.color=false
e scr.utf8=true
e asm.var=false

?e [1] Find the delegate method by name. The flag lists its address (0x5640):
f~+shouldAllowExtensionPointIdentifier

?e
?e [2] Disassemble that method at 0x5640, filtering to its calls. It is a compiler-generated
?e     Objective-C thunk that forwards to a Swift function and converts its Bool result to ObjCBool.
?e     The forwarded Swift function is func.00005614:
pdf @ 0x00005640~bl sym

?e
?e [3] Disassemble that Swift function at 0x5614. It holds the allow/deny logic:
pdf @ 0x00005614
