e scr.color=0
e scr.interactive=false
e bin.relocs.apply=true
e bin.cache=true
e search.in=io.maps.x
e asm.lines=false

?e === Analyzing iOS Binary for Verbose Logging ===
?e

?e [*] Cross references to logging related imports
axt @@ sym.imp.*~NSLog
axt @@ sym.imp.*~print
axt @@ sym.imp.*~debugPrint
axt @@ sym.imp.*~dump
axt @@ sym.imp.*~os_log
axt @@ sym.imp.*~Logger
axt @@ sym.imp.*~_os_log_impl
axt @@ sym.imp.*~os_log_type_enabled
?e

?e [*] Done