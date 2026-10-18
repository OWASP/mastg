---
title: Secure Data Sharing Between App Extensions and Containing Apps
alias: secure-app-extension-data-sharing
id: MASTG-BEST-0068
platform: ios
knowledge: [MASTG-KNOW-0082]
---

When an app and its [extensions](https://developer.apple.com/app-extensions/) share data through an [App Group](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups), the shared container is readable and writable by every member of the group, with no per-item access control between members (see @MASTG-KNOW-0082). Choose the sharing channel based on the sensitivity of the data, and protect what you store.

## Prefer a Shared Keychain for Secrets

Store credentials, tokens, and keys that both the app and an extension need in a shared [Keychain Access Group](https://developer.apple.com/documentation/security/sharing-access-to-keychain-items-among-a-collection-of-apps) (the `keychain-access-groups` entitlement), not in shared `UserDefaults` or a shared file container. The Keychain provides dedicated, access-controlled key storage with its own accessibility class. Set an appropriate accessibility attribute such as [`kSecAttrAccessibleWhenUnlockedThisDeviceOnly`](https://developer.apple.com/documentation/security/ksecattraccessiblewhenunlockedthisdeviceonly).

## Protect Sensitive Files in the Shared Container

When sensitive data must live in the shared file container, apply [Data Protection](https://developer.apple.com/documentation/uikit/protecting_the_user_s_privacy/encrypting_your_app_s_files) so its contents are encrypted while the device is locked. Write files with the `NSFileProtectionComplete` class:

```swift
try data.write(to: sharedContainerURL, options: .completeFileProtection)
```

This applies to all shared storage, in the same way it applies to the app's private storage (see @MASTG-TEST-0299).

## Minimize What You Share

Limit the shared container to the data each extension actually needs. Avoid placing whole datasets or secrets there when an extension only needs a subset, and grant each extension only the App Group identifiers required for its functionality.
