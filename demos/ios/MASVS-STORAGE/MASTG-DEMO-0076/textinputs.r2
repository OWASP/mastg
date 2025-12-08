e asm.bytes=false
e scr.color=false
e asm.var=false

?e Print flags about \"UITextField,UITextView,UISearchBar\"
f~+UITextField,UITextView,UISearchBar

?e Print xrefs to 0x100010180
axt @ 0x100010180

?e Print flags about \"autocorrectionType,setSecureTextEntry,spellCheckingType\"
f~+autocorrectionType,setSecureTextEntry,spellCheckingType

?e

?e Print xrefs to 0x100010110
axt @ 0x100010110

?e Print xrefs to 0x100010120
axt @ 0x100010120

?e Print xrefs to 0x100010128
axt @ 0x100010128

?e

?e Print disassembly around \"name_field\" in the function
pd--10 @ 0x100004550

?e

?e Print disassembly around \"email_field\" in the function
pd--10 @ 0x100004604

?e

?e Print disassembly around \"password_field\" in the function
pd--10 @ 0x1000046b0