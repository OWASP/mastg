---
title: Store Data Encrypted in App Sandbox Directory
alias: store-data-encrypted-in-the-app-sandbox-directory
id: MASTG-BEST-0024
platform: ios
---

Choose the right location for storing the app's and the user's data to the app sandbox (@MASTG-KNOW-0108): use **Documents** directory to store user-generated content and **Library** directory for app's internal data.

An app can be configured to make **Documents** directory accessible to the user in the Files app by setting `UIFileSharingEnabled` and `LSSupportsOpeningDocumentsInPlace`. Therefore, storing databases, config files, purchase state in this directory is highly dangerous because:

- a user can tamper with internal app files
- an attacker with a physical access to the device can copy content of `Documents` directory
- other apps can access `Documents` directory of other apps with a [document picker interface](https://developer.apple.com/documentation/uikit/uidocumentpickerviewcontroller)

**Note:** Storing data in the correct directory doesn't mean it's secure. For L2 profile apps, it's recommended to encrypt the files before storing them, and put the encryption key into the Keychain.
