e asm.bytes=false
e scr.color=false
e asm.var=false

?e References to URLSessionConfiguration tlsMinimumSupportedProtocolVersion setter:
f~setTLSMinimumSupportedProtocol

?e

?e xrefs to tlsMinimumSupportedProtocolVersion setter:
axt @ 0x100010180

?e

?e Code setting tlsMinimumSupportedProtocolVersion:
pd-- 20 @ 0x1000091a4


?e 

?e Search for ARM64 instructions that load the TLS protocol constants used by tlsMinimumSupportedProtocolVersion.

?e The TLS constants are 0x0301 for TLS 1.0, 0x0302 for TLS 1.1, 0x0303 for TLS 1.2, and 0x0304 for TLS 1.3.

?e In this binary, the value is passed as the first argument to the Objective C setter in register w2. For example, mov w2, 0x301 loads TLS 1.0.

?e The instruction mov w2, 0x301 is encoded as the ARM64 word 0x52806022. Because the binary stores instructions in little endian order, the bytes appear as 22 60 80 52, which r2 searches as 22608052.

?e Search for the encoded mov w2 instructions for each TLS version.

/x 22608052
/x 42608052
/x 62608052
/x 82608052

?e

?e Print the surrounding instructions

pd-- 10 @ 0x100004194