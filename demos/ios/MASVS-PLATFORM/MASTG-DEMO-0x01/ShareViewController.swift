import UIKit
import Social
import Security

// SUMMARY: This Share Extension reads the auth token from the shared Keychain (the correct source),
// but then caches it unencrypted in the App Group shared container, exposing the secret to every
// member of the App Group.

class ShareViewController: SLComposeServiceViewController {

  let appGroupID = "group.org.owasp.mastestapp"
  let keychainAccessGroup = "$(AppIdentifierPrefix)org.owasp.mastestapp"

  override func isContentValid() -> Bool {
    return true
  }

  override func didSelectPost() {
    // The token is available from the shared Keychain, the correct place for a shared secret.
    let token = readTokenFromSharedKeychain() ?? ""

    // FAIL: [MASTG-TEST-0x01] The extension caches the auth token (a secret) in the App Group
    // shared UserDefaults in plaintext. Every member of the App Group can read it.
    if let shared = UserDefaults(suiteName: appGroupID) {
      shared.set(token, forKey: "cachedAuthToken")
    }

    // FAIL: [MASTG-TEST-0x01] The extension also writes the token to a file in the App Group
    // shared container in plaintext and without NSFileProtectionComplete.
    if let containerURL = FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: appGroupID) {
      let fileURL = containerURL.appendingPathComponent("auth_cache.json")
      let json = #"{"authToken":"\#(token)"}"#
      try? json.write(to: fileURL, atomically: true, encoding: .utf8)
    }

    self.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
  }

  private func readTokenFromSharedKeychain() -> String? {
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrAccount as String: "authToken",
      kSecAttrAccessGroup as String: keychainAccessGroup,
      kSecReturnData as String: true
    ]
    var result: AnyObject?
    guard SecItemCopyMatching(query as CFDictionary, &result) == errSecSuccess,
          let data = result as? Data else {
      return nil
    }
    return String(data: data, encoding: .utf8)
  }
}
