import SwiftUI
import os

// SUMMARY: This sample demonstrates data sharing between an app and its extension using App Groups.

struct MastgTest {
  
  static func mastgTest(completion: @escaping (String) -> Void) {
    // FAIL: [MASTG-TEST-0x01] The app shares sensitive data via App Groups without encryption
    
    let appGroupID = "group.org.owasp.mastestapp"
    
    // Store sensitive user data in shared UserDefaults
    if let sharedDefaults = UserDefaults(suiteName: appGroupID) {
      // Storing sensitive information in shared UserDefaults
      sharedDefaults.set("john.doe@example.com", forKey: "userEmail")
      sharedDefaults.set("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", forKey: "authToken")
      sharedDefaults.set("Premium", forKey: "subscriptionLevel")
      sharedDefaults.synchronize()
      
      completion("Shared data stored in App Group: \(appGroupID)")
    } else {
      completion("Failed to access App Group")
    }
    
    // Write sensitive file to shared container
    let fileManager = FileManager.default
    if let containerURL = fileManager.containerURL(forSecurityApplicationGroupIdentifier: appGroupID) {
      let fileURL = containerURL.appendingPathComponent("user_credentials.json")
      let credentials = """
      {
        "username": "john.doe",
        "apiKey": "sk-1234567890abcdef",
        "refreshToken": "rt-abcdef1234567890"
      }
      """
      
      do {
        try credentials.write(to: fileURL, atomically: true, encoding: .utf8)
        completion("Sensitive file written to shared container: \(fileURL.path)")
      } catch {
        completion("Error writing file: \(error.localizedDescription)")
      }
    }
    
    // PASS: [MASTG-TEST-0x01] Non-sensitive data can be safely shared
    if let sharedDefaults = UserDefaults(suiteName: appGroupID) {
      sharedDefaults.set("en-US", forKey: "preferredLanguage")
      sharedDefaults.set("light", forKey: "themeMode")
    }
  }
}
