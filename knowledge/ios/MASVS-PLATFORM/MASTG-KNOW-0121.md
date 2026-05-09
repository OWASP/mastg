---
masvs_category: MASVS-PLATFORM
platform: ios
title: iOS Inter-Process Communication Channels
---

iOS apps exchange data with other local processes through a set of platform channels. These channels differ in visibility, lifetime, and how apps address each other.

Common IPC-related channels on iOS include:

- [Pasteboards](https://developer.apple.com/documentation/uikit/uipasteboard), for sharing clipboard-style data between apps.
- [Custom URL schemes](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app) and [Universal Links](https://developer.apple.com/documentation/xcode/supporting-universal-links-in-your-app), for app-to-app handoff and deep-link routing.
- [App Groups](https://developer.apple.com/documentation/xcode/configuring-app-groups) shared containers, where multiple apps or extensions from the same developer team can read and write common files.
- [App extensions](https://developer.apple.com/documentation/foundation/app-extension-programming-guide) using host-app communication patterns and shared storage.
- [File coordination APIs](https://developer.apple.com/documentation/foundation/nsfilecoordinator) that serialize read and write access across processes for shared files.

The implementation details vary by API, but in all cases you should understand who can access the channel, how long shared data remains available, and which process boundaries are involved.

For related platform behavior, see @MASTG-KNOW-0078 and @MASTG-KNOW-0083.
