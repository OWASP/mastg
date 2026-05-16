e asm.bytes=false
e scr.color=false
e asm.var=false

?e Uses of the BSD sockets functions:
ii~getaddrinfo,send,recv,connect,socket

?e

?e xrefs to getaddrinfo:
axt @ 0x1000069c4

?e

?e Use of getaddrinfo:

# Seek to the function where getaddrinfo is called
pd-- 20 @ 0x1000041d0

?e

?e Value passed to getaddrinfo:

? 0x50~uint32[1]