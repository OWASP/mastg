---
title: Secure Data Sharing Between App Extensions and Containing Apps
alias: secure-app-extension-data-sharing
id: MASTG-BEST-0x01
platform: ios
---

When using [App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups) to share data between an app and its extensions, you must protect sensitive information from unauthorized access. App Groups create a shared container accessible to all members of the group, which can expose data if not properly secured.

## Encrypt Sensitive Data in Shared Containers

Store sensitive data in the shared container only when necessary. When you must share sensitive information, encrypt it before writing to the shared container and decrypt it only when needed. Use platform encryption APIs such as:

- [CryptoKit](https://developer.apple.com/documentation/cryptokit) (iOS 13+) for modern encryption operations.
- [CommonCrypto](https://developer.apple.com/security/) for legacy support or lower-level cryptographic operations.

Store encryption keys in the iOS Keychain with appropriate accessibility attributes (e.g., [`kSecAttrAccessibleWhenUnlockedThisDeviceOnly`](https://developer.apple.com/documentation/security/ksecattraccessiblewhenunlockedthisdeviceonly)) and ensure that the Keychain items are shared via a [Keychain Access Group](https://developer.apple.com/documentation/security/keychain_services/keychain_items/sharing_access_to_keychain_items_among_a_collection_of_apps) if both the app and extension need access to the same key.

## Minimize Data Sharing

Limit the amount of data stored in the shared container to only what is necessary for the extension's functionality. Avoid storing entire datasets when the extension only needs a subset of information. Design your data-sharing architecture to pass minimal data and retrieve additional details from a secure source (e.g., a server or the app's protected container) when needed.

## Use Keychain Sharing for Credentials

For credentials and other highly sensitive data, prefer the Keychain over the shared container. The Keychain provides stronger access controls and encryption. Configure a [Keychain Access Group](https://developer.apple.com/documentation/security/keychain_services/keychain_items/sharing_access_to_keychain_items_among_a_collection_of_apps) to allow the app and extension to share Keychain items securely.

## Apply Appropriate File Protection

When storing files in the shared container, apply [Data Protection](https://developer.apple.com/documentation/uikit/protecting_the_user_s_privacy/encrypting_your_app_s_files) to encrypt file contents. Set the appropriate protection class (e.g., `NSFileProtectionComplete`) to ensure files are encrypted when the device is locked.

```swift
try data.write(to: sharedContainerURL, options: .completeFileProtection)
```

## Validate Data Integrity

When reading data from the shared container, validate its integrity to ensure it has not been tampered with. Use message authentication codes (MACs) or digital signatures to verify that the data originates from a trusted source and has not been modified.

## Review Extension Permissions

Ensure that each app extension only has access to the App Groups it requires. Avoid granting all extensions access to the same shared container unless necessary. Configure each extension's entitlements to include only the specific App Group identifiers needed for its functionality.

## References

- [Apple Developer Documentation - App Extensions](https://developer.apple.com/app-extensions/)
- [Apple Developer Documentation - App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups)
- [Apple Developer Documentation - Sharing Data with Your Containing App](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionScenarios.html#//apple_ref/doc/uid/TP40014214-CH21-SW11)
- [Apple Developer Documentation - Data Protection](https://developer.apple.com/documentation/uikit/protecting_the_user_s_privacy/encrypting_your_app_s_files)
- [Apple Developer Documentation - Keychain Access Groups](https://developer.apple.com/documentation/security/keychain_services/keychain_items/sharing_access_to_keychain_items_among_a_collection_of_apps)
