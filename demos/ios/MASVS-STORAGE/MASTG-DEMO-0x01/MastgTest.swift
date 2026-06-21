import SwiftUI
import Security
import UIKit

// SUMMARY: The main app stores the auth token in the shared Keychain (Keychain Access Group),
// the correct channel for a secret shared with the app's extensions. The Share Extension reads
// the token from there (see ShareViewController.swift).

struct MastgTest {

  static let keychainAccessGroup = "$(AppIdentifierPrefix)org.owasp.mastestapp"

  public static func mastgTest(completion: @escaping (String) -> Void) {
    let token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

    // PASS: [MASTG-TEST-0x01] The auth token is stored in the shared Keychain, scoped to the
    // Keychain Access Group, so the app's extensions can read it without copying it into the
    // App Group shared container.
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrAccount as String: "authToken",
      kSecAttrAccessGroup as String: keychainAccessGroup,
      kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
      kSecValueData as String: Data(token.utf8)
    ]
    SecItemDelete(query as CFDictionary)
    let status = SecItemAdd(query as CFDictionary, nil)

    // Present the system share sheet so the Share Extension can be triggered. Selecting the
    // "Share Extension" entry launches ShareViewController (a separate process), which reads the
    // token from the shared Keychain and then insecurely caches it in the App Group container.
    DispatchQueue.main.async {
      presentShareSheet()
    }

    completion("Stored authToken in shared Keychain (status: \(status)).\nTap the Share Extension in the share sheet to trigger the insecure caching.")
  }

  public static func presentShareSheet() {
    let shareText = "Check out this app!"
    let shareURL = URL(string: "https://mas.owasp.org")!

    let activityVC = UIActivityViewController(
      activityItems: [shareText, shareURL],
      applicationActivities: nil
    )

    if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
       let rootViewController = windowScene.windows.first?.rootViewController {
      // On iPad the share sheet is shown as a popover, which requires an anchor.
      if let popover = activityVC.popoverPresentationController {
        popover.sourceView = rootViewController.view
        popover.sourceRect = CGRect(x: rootViewController.view.bounds.midX,
                                    y: rootViewController.view.bounds.midY,
                                    width: 0, height: 0)
        popover.permittedArrowDirections = []
      }
      rootViewController.present(activityVC, animated: true)
    }
  }
}
