// SUMMARY: This sample imports a user session that arrives in a custom URL scheme
// (mastgtest://import?session=<base64 archive>), which an attacker can deliver from Safari, Notes,
// or another app. The FAIL path uses NSCoding and disables secure coding, so a substituted archive
// is decoded without type enforcement, which lets an attacker run another class's init(coder:)
// (here CachedDocument, which writes a file) just by getting the victim to open a link.
// The PASS path uses NSSecureCoding and class-restricted unarchiving, which rejects the substituted
// class before it is instantiated.

import Foundation

// MARK: FAIL example

// FAIL: [MASTG-TEST-0386] InsecureUserSession conforms to NSCoding instead of NSSecureCoding,
// so it provides no class restriction when its archives are decoded.
@objc class InsecureUserSession: NSObject, NSCoding {
    let userID: String
    let isAdmin: Bool

    init(userID: String, isAdmin: Bool) {
        self.userID = userID
        self.isAdmin = isAdmin
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(userID, forKey: "userID")
        coder.encode(isAdmin, forKey: "isAdmin")
    }

    required convenience init?(coder: NSCoder) {
        // FAIL: [MASTG-TEST-0386] decodeObject(forKey:) doesn't restrict the expected class.
        guard let userID = coder.decodeObject(forKey: "userID") as? String else {
            return nil
        }

        let isAdmin = coder.decodeBool(forKey: "isAdmin")
        self.init(userID: userID, isAdmin: isAdmin)
    }
}

// MARK: PASS example

// PASS: SecureUserSession conforms to NSSecureCoding and enforces class restrictions during decoding.
@objc class SecureUserSession: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool {
        return true
    }

    let userID: String
    let isAdmin: Bool

    init(userID: String, isAdmin: Bool) {
        self.userID = userID
        self.isAdmin = isAdmin
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(userID, forKey: "userID")
        coder.encode(isAdmin, forKey: "isAdmin")
    }

    required convenience init?(coder: NSCoder) {
        // PASS: decodeObject(of:forKey:) restricts the decoded value to the expected class.
        guard let userID = coder.decodeObject(of: NSString.self, forKey: "userID") as? String else {
            return nil
        }

        let isAdmin = coder.decodeBool(forKey: "isAdmin")
        self.init(userID: userID, isAdmin: isAdmin)
    }
}

// MARK: Cached document model (deserialization gadget)

// CachedDocument is a realistic app model: an app that lets users open documents offline might
// keep an index of cached files and rehydrate each one to disk when the index is loaded. Writing
// the file in init(coder:) is a legitimate part of that flow. The problem is that the insecure path
// doesn't restrict the decoded class, so an attacker who substitutes this class into the archive
// turns a normal cache restore into a file write they control (the file name and contents come from
// the archive), running during decoding. The secure path rejects this class before it is
// instantiated, so the write never happens.
@objc(CachedDocument) class CachedDocument: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool {
        return true
    }

    let fileName: String
    let contents: String

    init(fileName: String, contents: String) {
        self.fileName = fileName
        self.contents = contents
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(fileName, forKey: "fileName")
        coder.encode(contents, forKey: "contents")
    }

    required convenience init?(coder: NSCoder) {
        let fileName = coder.decodeObject(forKey: "fileName") as? String ?? "offline_cache.txt"
        let contents = coder.decodeObject(forKey: "contents") as? String ?? ""
        self.init(fileName: fileName, contents: contents)
        restoreToDisk()
    }

    // Rehydrates the cached document so it can be opened offline.
    private func restoreToDisk() {
        guard let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else {
            return
        }
        let url = documents.appendingPathComponent(fileName)
        try? contents.write(to: url, atomically: true, encoding: .utf8)
    }
}

// Holds the most recent URL the app was opened with (set in MASTestAppApp.onOpenURL),
// so mastgTest() can process it when the user taps Start.
enum URLState {
    static var lastURL: URL?
}

struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        guard let url = URLState.lastURL else {
            completion("""
            Waiting for a deep link.
            Open mastgtest://import?session=<base64 archive> from Safari or Notes, then tap Start.
            """)
            return
        }

        URLState.lastURL = nil
        completion(importSharedSession(from: url))
    }

    // FAIL: [MASTG-TEST-0386] The app accepts a session from a deep link and deserializes
    // attacker-controlled data. Anyone can send a mastgtest://import?session=<base64 archive> link
    // (for example in Safari or Notes); opening it runs the insecure decode below, with no app of
    // the attacker's own required.
    static func importSharedSession(from url: URL) -> String {
        guard
            let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
            let encoded = components.queryItems?.first(where: { $0.name == "session" })?.value,
            let data = Data(base64Encoded: encoded)
        else {
            return "No session payload in the URL.\nExpected mastgtest://import?session=<base64 archive>."
        }

        return """
        Imported a session from a deep link (\(url.scheme ?? "")://\(url.host ?? "")).

        INSECURE (NSCoding):
        \(decodeInsecurely(data))

        SECURE (NSSecureCoding):
        \(decodeSecurely(data))
        """
    }

    private static func decodeInsecurely(_ data: Data) -> String {
        do {
            let unarchiver = try NSKeyedUnarchiver(forReadingFrom: data)

            // FAIL: [MASTG-TEST-0386] Secure coding enforcement is explicitly disabled.
            unarchiver.requiresSecureCoding = false

            // FAIL: [MASTG-TEST-0386] The root object is decoded without restricting the expected class.
            // A substituted class is instantiated here, and its init(coder:) runs, before the cast below can reject it.
            let object = unarchiver.decodeObject(forKey: NSKeyedArchiveRootObjectKey)
            unarchiver.finishDecoding()

            if let document = object as? CachedDocument {
                return "Decoding instantiated CachedDocument and ran its initializer, which wrote '\(document.fileName)' into the app sandbox."
            }

            guard let object else {
                return "Decoded no root object."
            }

            guard let session = object as? InsecureUserSession else {
                return "Decoded an unexpected type: \(type(of: object))."
            }

            return accessDecision(userID: session.userID, isAdmin: session.isAdmin)
        } catch {
            return "Decoding failed: \(error.localizedDescription)"
        }
    }

    private static func decodeSecurely(_ data: Data) -> String {
        do {
            // PASS: unarchivedObject(ofClass:from:) requires secure coding and rejects unexpected classes
            // before they are instantiated, so a substituted CachedDocument's init(coder:) never runs.
            guard let session = try NSKeyedUnarchiver.unarchivedObject(ofClass: SecureUserSession.self, from: data) else {
                return "Decoding returned nil."
            }
            return accessDecision(userID: session.userID, isAdmin: session.isAdmin)
        } catch {
            return "Decoding rejected before instantiation: \(error.localizedDescription)"
        }
    }

    // Simulates a security-relevant decision driven by the decoded session.
    private static func accessDecision(userID: String, isAdmin: Bool) -> String {
        let role = isAdmin ? "ADMIN access granted" : "standard user access"
        return "userID: \(userID), isAdmin: \(isAdmin) -> \(role)"
    }
}
