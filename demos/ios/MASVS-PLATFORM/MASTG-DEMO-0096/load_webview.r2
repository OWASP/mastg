e asm.bytes=false
e scr.color=false
e asm.var=false

?e URLsForDirectory:inDomains: selector (API anchor for the allowingReadAccessTo initializer):
f~URLsForDirectory

?e

?e xrefs to URLsForDirectory:inDomains: (finds the allowingReadAccessTo lazy initializer):
axt @ 0x100018130

?e

?e appendingPathComponent selector (API anchor for the fileURL initializer):
f~appendingPathComponent

?e

?e xrefs to appendingPathComponent import stub (finds the fileURL materializer ZTm_):
axt @ 0x10000a738

?e

?e xrefs to ZTm_ (finds the swift_once guard Z_ that encodes the filename as inline constants):
axt @ 0x10000418c

?e

?e HTML template string (contains innerHTML injection):
/ innerHTML

pdf @ 0x100004094 > docDir-init.asm
pdf @ 0x100004164 > fileURL-init.asm
pdf @ 0x100004240 >> fileURL-init.asm
