---
platform: ios
title: References to App Extension APIs Storing Unencrypted Data
id: MASTG-TEST-0x01
type: [static, code, manual]
weakness: MASWE-0006
threat: [app]
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
---

## Overview

iOS [app extensions](https://developer.apple.com/app-extensions/) allow apps to extend custom functionality and content beyond the app. They are separate binaries bundled with the app, each serving a specific purpose, such as sharing content, providing widgets, or integrating with other apps.

App extensions and their containing apps run as separate processes with distinct sandboxes and cannot directly access each other's containers. However, they can share data via [App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups), which enable shared storage. When an app and its extensions are configured to use the same App Group, they can read from and write to a shared container, making data accessible across the extension and the containing app.

This test verifies whether the app contains app extensions and checks if they share sensitive data with the containing app via App Groups. Insecure data sharing can expose sensitive information to other apps or processes, particularly if the shared container does not implement adequate access controls or encryption.

## Steps

1. Extract the app package and use @MASTG-TECH-0058 to explore the contents of the IPA file.
2. Verify if the app contains app extensions by checking for the presence of a `PlugIns/` folder inside the app bundle (`.app` directory). Each app extension will have a `.appex` extension.
3. For each detected app extension, inspect the extension's `Info.plist` file to determine:
   - The extension type via the [`NSExtensionPointIdentifier`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsextension/nsextensionpointidentifier) key, which identifies the functionality the extension provides (e.g., share extension, widget, custom keyboard).
   - The supported data types via the [`NSExtensionActivationRule`](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionScenarios.html#//apple_ref/doc/uid/TP40014214-CH21-SW8) key (for share and action extensions), which specifies the types and amounts of data the extension can handle.
4. Check both the containing app and each app extension for the presence of the [App Groups entitlement](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups) (`com.apple.security.application-groups`) in their respective entitlements files or embedded provisioning profiles (`embedded.mobileprovision`). This entitlement indicates that data sharing is enabled between the app and its extensions.
5. Check if keychain sharing is configured via the `keychain-access-groups` entitlement, which allows sharing of Keychain items between the app and its extensions.

## Observation

The output should contain:

- A list of app extensions (`.appex` files) found in the `PlugIns/` directory.
- The extension type and supported data types for each extension identified in their `Info.plist` files.
- Confirmation of whether the App Groups entitlement is present in the containing app and each extension.
- Locations in the disassembled code where shared storage APIs are used.

## Evaluation

The test case fails if:

- The app and its extensions use the App Groups entitlement to share data via a common container, and sensitive data is stored in the shared container without adequate protection (e.g., encryption, access controls).
- Sensitive data can be accessed by any extension with the same App Group, even if the extension does not require access to that data for its intended functionality.
- Shared user defaults or shared file containers contain sensitive information in plaintext or with insufficient protection.

**Further Validation Required:**

Review the app's and extension's code using @MASTG-TECH-0076 to identify usage of shared storage APIs:
   - [`UserDefaults(suiteName:)`](https://developer.apple.com/documentation/foundation/userdefaults/1409957-init) to access shared user defaults.
   - [`FileManager.containerURL(forSecurityApplicationGroupIdentifier:)`](https://developer.apple.com/documentation/foundation/filemanager/1412643-containerurl) to access shared file containers.
   - [`NSPersistentContainer`](https://developer.apple.com/documentation/coredata/nspersistentcontainer) with App Group configurations for shared Core Data storage.

Determining what constitutes sensitive data is context-dependent. Review the identified code locations in the disassembled code to assess whether shared data includes sensitive information and whether appropriate safeguards are in place. Consider the functionality of each extension and whether the data sharing is necessary and minimized.
