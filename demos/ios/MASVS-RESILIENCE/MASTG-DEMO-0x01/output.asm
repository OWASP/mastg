
Evidence of a security-sensitive routine (license key string literal):
105 0x00008de0 0x100008de0 16  17   4.__TEXT.__cstring         ascii   MAS-PREMIUM-2025

Searching the import table for source code integrity APIs:

Binary base address resolution (dladdr):
205 0x100008704 NONE FUNC               dladdr

Cryptographic hash functions over code (CC_MD5 / CC_SHA256 / CC_SHA512):
179 0x1000086d4 NONE FUNC               CC_SHA256

(Any integrity APIs listed above are referenced only by an unrelated routine and are not used to verify the license check.)
