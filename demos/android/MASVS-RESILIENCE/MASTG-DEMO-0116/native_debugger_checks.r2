e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false
e bin.relocs.apply=true

?e Native anti-debugging strings:
izz~TracerPid,/proc/self/status

?e

?e Imported ptrace symbol:
ii~ptrace

?e

?e Cross-references to ptrace:
axt @ sym.imp.ptrace

?e

?e Disassembly around ptrace references:
pd-- 16 @ `axtq @ sym.imp.ptrace`
