---
platform: ios
title: Identifying Data Sharing Between App and Extensions via App Groups
id: MASTG-DEMO-0x01
code: [swift]
test: MASTG-TEST-0x01
---

### Sample

The code snippet below demonstrates an app that uses App Groups to share data with its app extension. The app stores sensitive user information (email, authentication tokens, API keys) in shared storage that can be accessed by both the main app and the extension.

{{ MastgTest.swift }}

The app includes a Share Extension with the following configuration files:

Extension Info.plist showing the extension type and supported data types:

{{ ShareExtension_Info.plist }}

Entitlements file showing the App Groups configuration:

{{ entitlements.plist }}

### Steps

1. Extract the app package (@MASTG-TECH-0058) and locate the `PlugIns/` folder containing app extensions (`.appex` files).
2. For each extension, examine the `Info.plist` file to identify the extension type via `NSExtensionPointIdentifier` and supported data types via `NSExtensionActivationRule`.
3. Check the entitlements file (or `embedded.mobileprovision`) for the `com.apple.security.application-groups` entitlement in both the main app and extensions.
4. Review the app's code (@MASTG-TECH-0076) to identify usage of shared storage APIs such as `UserDefaults(suiteName:)` and `FileManager.containerURL(forSecurityApplicationGroupIdentifier:)`.
5. Run the analysis script to detect these patterns:

{{ run.sh }}

### Observation

The output shows:

- The presence of an app extension (Share Extension) with identifier `com.apple.share-services`
- The extension supports text, URLs, and images as input data types
- Both the app and extension use the App Group `group.org.owasp.mastestapp`
- The code accesses shared storage at multiple locations using the App Group identifier
- Sensitive data (email addresses, authentication tokens, subscription info) is stored in shared UserDefaults
- A credentials file containing API keys and refresh tokens is written to the shared container

{{ output.txt }}

### Evaluation

The test fails because the app stores sensitive data in the App Group shared container without encryption:

- **Shared UserDefaults**: Contains `userEmail`, `authToken`, and `subscriptionLevel` in plaintext
- **Shared Files**: The `user_credentials.json` file in the shared container contains `username`, `apiKey`, and `refreshToken` in plaintext
- **Weak Protection**: No evidence of encryption or access controls protecting the sensitive data in the shared storage

Any app extension with access to `group.org.owasp.mastestapp` can read this sensitive information. According to @MASTG-BEST-0025, sensitive data shared via App Groups should be encrypted, and access should be minimized to only what each extension requires for its functionality.
