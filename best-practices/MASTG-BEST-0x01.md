---
title: Use NSSecureCoding for Object Deserialization
alias: use-nssecurecoding-for-object-deserialization
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0075]
---

Use [`NSSecureCoding`](https://developer.apple.com/documentation/foundation/nssecurecoding) instead of `NSCoding` when deserializing objects. `NSSecureCoding` requires explicit type checks during decoding, preventing type confusion attacks where an attacker substitutes a different class in the serialized payload.

## Implementing NSSecureCoding

To conform to `NSSecureCoding`, your class must set `supportsSecureCoding` to `true` and use type-safe decoding methods:

```swift
class UserSession: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool { return true }

    var userID: String

    init(userID: String) {
        self.userID = userID
    }

    func encode(with coder: NSCoder) {
        coder.encode(userID, forKey: "userID")
    }

    required init?(coder: NSCoder) {
        // Use decodeObject(of:forKey:) to restrict the allowed type
        guard let userID = coder.decodeObject(of: NSString.self, forKey: "userID") as? String else {
            return nil
        }
        self.userID = userID
    }
}
```

When unarchiving, always use `NSKeyedUnarchiver` with `requiresSecureCoding = true` (the default since iOS 11):

```swift
let unarchiver = try NSKeyedUnarchiver(forReadingFrom: data)
unarchiver.requiresSecureCoding = true
let session = try unarchiver.decodeTopLevelObject(of: UserSession.self, forKey: NSKeyedArchiveRootObjectKey)
```

Never set `requiresSecureCoding = false` when deserializing data from untrusted sources, such as files, IPC payloads, or network responses.

## Data Sources to Treat as Untrusted

Even when data is stored in the app's sandbox, treat it as untrusted if it can be modified by an adversary. This includes:

- Files written to shared containers or received via AirDrop, document sharing, or file providers.
- Values received through IPC (URL schemes, App Extensions, `UIPasteboard`).
- Network responses that are deserialized without signature verification.
- Data restored from device backups or transferred via iCloud on a jailbroken device.

See [Secure Coding Guide](https://developer.apple.com/library/archive/documentation/Security/Conceptual/SecureCodingGuide/Introduction.html) and [NSSecureCoding](https://developer.apple.com/documentation/foundation/nssecurecoding) in Apple's developer documentation.
