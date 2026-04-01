e asm.bytes = false
e scr.color=false
e asm.var=false

!printf "Uses of NSLog:\n"
afl~NSLog

!printf "\n"

!printf "xrefs to NSLog:\n"
axt @ 0x10000d780

!printf "\n"
!printf "Invocation of NSLog:\n"

pd-- 5 @ 0x1000088ec

pdf @ 0x1000088ec > function.asm
