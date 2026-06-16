---
title: Use NSSecureCoding for Object Deserialization
alias: use-nssecurecoding-for-object-deserialization
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0075]
---

Use [`NSSecureCoding`](https://developer.apple.com/documentation/foundation/nssecurecoding) instead of `NSCoding` when deserializing objects. `NSSecureCoding` requires explicit type checks during decoding, preventing type confusion attacks where an attacker substitutes a different class in the serialized payload.

## Data Sources to Treat as Untrusted

Even when data is stored in the app's sandbox, treat it as untrusted if it can be modified by an adversary. This includes:

- Files written to shared containers or received via AirDrop, document sharing, or file providers.
- Values received through IPC (URL schemes, App Extensions, `UIPasteboard`).
- Network responses that are deserialized without signature verification.
- Data restored from device backups or transferred via iCloud on a jailbroken device.

