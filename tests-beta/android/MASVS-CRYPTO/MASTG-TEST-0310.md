---
platform: android
title: Runtime Use of Reused Initialization Vectors in Symmetric Encryption
id: MASTG-TEST-0310
type: [dynamic]
weakness: MASWE-0022
status: placeholder
profiles: [L2]
note: Reusing a symmetric key is acceptable when IVs or nonces follow the rules defined for the mode. NIST SP 800 38A states that CBC requires a fresh or unpredictable IV for every encryption. NIST SP 800 38D states that counter based modes require a nonce that never repeats under the same key. Repeating a key and IV or nonce pair defeats confidentiality and can also undermine integrity.
---
