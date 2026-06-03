e scr.color=0
e scr.interactive=false
e bin.relocs.apply=true
e bin.cache=true
e search.in=io.maps.x
e asm.bytes=false
e asm.var=false

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

# The xrefs above only prove the logging APIs are referenced. To show WHAT is
# actually logged, disassemble each call site: the message is loaded into a
# register with an `adrp`/`add` pair immediately before the `bl` to the logging
# API, and r2 resolves that pointer and annotates it with the literal string.
?e [*] Recovered log message contents
?e     (string operand loaded immediately before each logging call)
?e

?e "=== NSLog -> internal API endpoint (mastgTest) ==="
pd 2 @ 0x100006864
?e

?e "=== print -> username (performLogin) ==="
pd 2 @ 0x1000048c4
?e

?e "=== debugPrint -> mock session token literal (performLogin) ==="
pd 2 @ 0x10000498c
?e

?e "=== print -> SSL pinning disabled (performNetworkRequest) ==="
pd 2 @ 0x100004d44
?e

?e "=== Logger.debug -> password hashing algorithm (validateCredentials) ==="
pd 2 @ 0x1000047a8
?e

?e "=== NSLog -> error code & internal module (performLogin) ==="
pd 2 @ 0x100004a7c
?e

?e [*] Done
