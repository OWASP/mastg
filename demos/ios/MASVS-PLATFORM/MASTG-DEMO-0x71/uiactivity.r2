e asm.bytes=false
e scr.color=false
e scr.interactive=false
e asm.var=false

?e List all references to 'initWithActivityItems:applicationActivities:':

f~initWithActivityItems

?e

?e List all cross-references to 'initWithActivityItems:applicationActivities:':

axt @ 0x0001c188

?e

?e Use of 'initWithActivityItems:applicationActivities:':

pd 10 @ 0x20f4

?e

?e List all references to 'excludedActivityTypes':

f~setExcludedActivityTypes

?e
?e List all cross-references to 'setExcludedActivityTypes':

axt @ 0x0001c158

?e

?e Use of 'setExcludedActivityTypes':
pd 10 @ 0x16e0

?e
?e List all UIActivityType instances

f~reloc.UIActivityType