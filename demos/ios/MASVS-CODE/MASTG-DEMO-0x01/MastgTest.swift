// SUMMARY: This sample demonstrates insecure object deserialization using NSCoding instead of NSSecureCoding, which allows type confusion attacks during unarchiving.

import Foundation

// FAIL: [MASTG-TEST-0x01] UserSession conforms to NSCoding instead of NSSecureCoding.
// This means any class can be substituted during deserialization without type validation.
@objc class UserSession: NSObject, NSCoding {
    var userID: String
    var isAdmin: Bool

    init(userID: String, isAdmin: Bool) {
        self.userID = userID
        self.isAdmin = isAdmin
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(userID, forKey: "userID")
        coder.encode(isAdmin, forKey: "isAdmin")
    }

    // FAIL: [MASTG-TEST-0x01] decodeObject(forKey:) does not restrict which class is decoded,
    // leaving the app open to type confusion if the archive is supplied by an attacker.
    required convenience init?(coder: NSCoder) {
        guard let userID = coder.decodeObject(forKey: "userID") as? String else { return nil }
        let isAdmin = coder.decodeBool(forKey: "isAdmin")
        self.init(userID: userID, isAdmin: isAdmin)
    }
}

struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        let session = UserSession(userID: "user-001", isAdmin: false)

        do {
            // FAIL: [MASTG-TEST-0x01] Archiving with requiringSecureCoding: false disables type enforcement.
            let data = try NSKeyedArchiver.archivedData(withRootObject: session, requiringSecureCoding: false)

            // FAIL: [MASTG-TEST-0x01] Unarchiving without requiresSecureCoding = true accepts any class.
            let unarchiver = try NSKeyedUnarchiver(forReadingFrom: data)
            unarchiver.requiresSecureCoding = false
            guard let loaded = unarchiver.decodeObject(forKey: NSKeyedArchiveRootObjectKey) as? UserSession else {
                completion("Failed to decode session")
                return
            }

            completion("""
            Session loaded (INSECURE - no type enforcement):
            userID: \(loaded.userID)
            isAdmin: \(loaded.isAdmin)
            """)
        } catch {
            completion("Error: \(error.localizedDescription)")
        }
    }
}
