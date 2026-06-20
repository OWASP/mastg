e scr.color=false
e asm.bytes=false
e bin.relocs.apply=true

?e === Relevant API inventory ===
?e Unsafe keyed unarchiving APIs:
ir~setRequiresSecureCoding
ir~decodeObjectForKey
izz~setRequiresSecureCoding
izz~decodeObjectForKey

?e
?e Secure keyed unarchiving APIs:
is~decodeObject2of6forKey
is~unarchivedObject7ofClass

?e
?e URL payload APIs and strings:
is~URLComponents
is~queryItems
is~URLQueryItem
is~base64Encoded
izz~session
izz~mastgtest

?e
?e Possible consequence APIs:
is~appendingPathComponent
is~write2to10atomically8encoding

?e
?e === Xrefs to decoding APIs ===
?e Functions that set requiresSecureCoding:
axt @ reloc.fixup.setRequiresSecureCoding:

?e
?e Functions that call unrestricted decodeObject(forKey:):
axt @ reloc.fixup.decodeObjectForKey:

?e
?e Secure decoding references for comparison:
axt @@=`is~decodeObject2of6forKey[2]`
axt @@=`is~unarchivedObject7ofClass[2]`

?e
?e === Focused proof snippets ===
?e setRequiresSecureCoding is called with 0, Boolean false:
pd 8 @ 0x70c8

?e
?e The same function calls decodeObjectForKey:
pd 8 @ 0x7130

?e
?e === Reachability from URL payload ===
?e URLComponents and queryItems:
pd 8 @ 0x67b4
pd 8 @ 0x6810

?e
?e Query item value and Base64 decoding:
pd 8 @ 0x6900
pd 8 @ 0x69f4

?e
?e The URL import function calls the insecure decoder:
pd 8 @ 0x6d20

?e
?e The same URL import function calls the secure decoder:
pd 8 @ 0x6dc0

?e
?e === Consequence path ===
?e The substituted class initializer decodes fileName and contents:
pd 8 @ 0x5644
pd 8 @ 0x5818

?e
?e The initializer calls restoreToDisk:
pd 8 @ 0x5a78

?e
?e restoreToDisk builds a path and writes contents:
pd 8 @ 0x5d9c
pd 8 @ 0x5e10

# === Full functions for side review ===
pdf @ 0x6644 > importSharedSession.asm
pdf @ 0x7040 > decodeInsecurely.asm