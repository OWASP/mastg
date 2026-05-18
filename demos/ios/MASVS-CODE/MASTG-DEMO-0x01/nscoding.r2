?e;?e

?e === Checking for NSCoding conformances (insecure) ===
?e

?e NSCoding encode/decode methods:
afl~NSCoding
afl~encodeWith
afl~initWithCoder

?e
?e === NSKeyedArchiver / NSKeyedUnarchiver usage ===
?e

?e NSKeyedArchiver and NSKeyedUnarchiver symbols:
afl~NSKeyedArchiver
afl~NSKeyedUnarchiver

?e
?e xrefs to NSKeyedArchiver.archivedData:
axt @ sym.imp.Foundation.NSKeyedArchiver.archivedData.withRootObject.requiringSecureCoding

?e
?e xrefs to NSKeyedUnarchiver.init:
axt @ sym.imp.Foundation.NSKeyedUnarchiver.init.forReadingFrom

?e
?e === Checking requiresSecureCoding usage ===
?e

?e Setting requiresSecureCoding to false (insecure):
/c requiresSecureCoding
