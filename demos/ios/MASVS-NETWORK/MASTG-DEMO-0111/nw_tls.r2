e asm.bytes=false
e scr.color=false
e asm.var=false

?e References to sec_protocol_options_set_min_tls_protocol_version:
f~sec_protocol_options_set_min_tls_protocol_version

?e

?e xrefs to sec_protocol_options_set_min_tls_protocol_version:
axt @ 0x10000c240

?e

?e Import stub for sec_protocol_options_set_min_tls_protocol_version:
pd-- 10 @ 0x1000099e8

?e

?e Search for ARM64 instructions that load TLS constants into w1:
?e 0x0301, TLS 1.0
?e 0x0302, TLS 1.1
?e 0x0303, TLS 1.2
?e 0x0304, TLS 1.3

/x 21608052
/x 41608052
/x 61608052
/x 81608052

?e

?e Print the surrounding instructions for the mov w1, 0x301 instruction that sets TLS 1.0:
pd-- 10 @ 0x1000041d4