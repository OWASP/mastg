---
masvs_category: MASVS-PLATFORM
platform: ios
title: App Groups
available_since: 8
---

App Groups is an entitlement-based mechanism that lets apps and extensions from the same developer team share a common file container, shared preferences, or files such as SQLite databases.

## Entitlement

An app opts into an App Group by enabling the [App Groups capability](https://developer.apple.com/documentation/xcode/configuring-app-groups) and adding the [`com.apple.security.application-groups`](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.security.application-groups) entitlement. The App ID and provisioning profile must allow the same group identifier. The group identifier follows the format `group.<reverse-DNS-string>` and is tied to the developer team.

All apps and extensions sharing the same group identifier can access the shared resources. Different developer teams cannot use the same app group.

## Shared Storage

The shared container is a directory on the file system accessible via [`containerURL(forSecurityApplicationGroupIdentifier:)`](https://developer.apple.com/documentation/foundation/filemanager/containerurl(forsecurityapplicationgroupidentifier:)):

```swift
FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: "group.com.example.myapp")
```

Apps typically use this location to store:

- Shared `UserDefaults` via `UserDefaults(suiteName: "group.com.example.myapp")`.
- SQLite databases, Core Data stores, or other files shared between the main app and its extensions, such as a widget or a Share extension.
- Shared preference files or configuration data.

## Scope and Constraints

- The shared container is accessible to all apps and extensions enrolled in the group, running on the same device.
- App Groups do not enable sharing between apps from different developer teams.
- App Groups do not add an extra encryption layer. Files in the shared container rely on the standard iOS file system protections and the Data Protection class applied to each file.
- Widgets, notification content extensions, and other app extensions commonly use App Groups to read data shared by their containing app.
- Any app or extension in the group can read or modify shared data, so sensitive data should be minimized and access should be controlled at the application layer.

## Relationship to Other Mechanisms

App extensions (see @MASTG-KNOW-0082) commonly use App Groups as their primary data-sharing channel with the containing app. File coordination (see @MASTG-KNOW-0127) is recommended when multiple processes read and write the same file in the shared container concurrently.
