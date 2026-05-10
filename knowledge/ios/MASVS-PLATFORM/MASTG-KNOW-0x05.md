---
masvs_category: MASVS-PLATFORM
platform: ios
title: Keychain Access Groups
---

Keychain access groups allow multiple apps from the same developer team to share keychain items. Without a shared access group, keychain items are private to the app's default keychain access group.

## Entitlement

An app declares its keychain access groups in the [`keychain-access-groups`](https://developer.apple.com/documentation/bundleresources/entitlements/keychain-access-groups) entitlement. The entitlement value is an array of group identifiers, each prefixed with the app's App ID prefix, which is usually the Team ID, for example `TeamID.com.example.shared`.

Xcode adds the app's default access group, usually `TeamID.bundleID`, automatically. Additional shared groups must be declared explicitly through the [Keychain Sharing capability](https://developer.apple.com/documentation/xcode/configuring-keychain-sharing).

## Sharing Keychain Items

To store an item in a shared access group, include the [`kSecAttrAccessGroup`](https://developer.apple.com/documentation/security/ksecattraccessgroup) key in the query dictionary:

```swift
let query: [String: Any] = [
    kSecClass as String: kSecClassGenericPassword,
    kSecAttrAccessGroup as String: "TeamID.com.example.shared",
    kSecAttrAccount as String: "username",
    kSecValueData as String: passwordData
]
SecItemAdd(query as CFDictionary, nil)
````

Any app that has the same access group in its entitlement can access items stored in that group, subject to the item's other keychain attributes and access controls. If `kSecAttrAccessGroup` is omitted, the item is stored in the app's default access group.

## Scope and Constraints

- Keychain access groups are scoped to the app's App ID prefix, which is usually tied to the developer team. Apps from different teams normally cannot share a keychain access group.
- Items stored in a shared access group are accessible to all apps declaring that group, subject to the item's accessibility and access control settings.
- The access group is set when the keychain item is created by passing `kSecAttrAccessGroup` to `SecItemAdd`.
- Keychain items are protected by the iOS Keychain's Data Protection classes (`kSecAttrAccessible*`), independent of the access group.
- Shared keychain items should be minimized because any app or extension in the group may be able to read, update, or delete them.
