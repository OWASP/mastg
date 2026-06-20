e scr.color=0
e scr.interactive=false
e bin.relocs.apply=true

?e
?e Evidence that the app stores data on disk (filename string literal):
izz~user_profile.json

?e
?e Searching the import table for file storage integrity APIs:

?e
?e HMAC (CCHmac / CCHmacFinal):
ii~CCHmac

?e
?e Hash functions (CC_SHA256 / CC_SHA512):
ii~CC_SHA

?e
?e Asymmetric signing (SecKeyCreateSignature):
ii~SecKeyCreateSignature

?e
?e (No matches under the integrity APIs means none are referenced by the app.)