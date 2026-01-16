---
masvs_category: MASVS-PLATFORM
platform: ios
title: Pasteboard
---

Using the [`UIPasteboard`](https://developer.apple.com/documentation/uikit/uipasteboard) API, apps can access the iOS pasteboard, enabling data sharing either within the app or across apps. Because the general pasteboard is system-wide, it introduces privacy and security risks, especially when sensitive data is copied programmatically without the user’s awareness.

There are two main types of pasteboards:

- **General pasteboard (`UIPasteboard.general`)**: Shared across all foreground apps and, with [Universal Clipboard](https://support.apple.com/en-us/102430), across the user’s Apple devices. It is persistent by default across device restarts and app reinstalls unless explicitly cleared (observed behavior, not formally guaranteed by Apple). Since iOS 16, access requires explicit user interaction via a system confirmation dialog. See Apple documentation: <https://developer.apple.com/documentation/uikit/uipasteboard>

- **Custom or Named Pasteboards (`UIPasteboard(name:create:)`, `UIPasteboard.withUniqueName()`)**: These are private pasteboards restricted to the app that created them or other apps signed with the same Team ID. They have been non-persistent by default since iOS 10 (deleted on app termination or system reboot). Apple discourages the use of persistent custom pasteboards and recommends using [App Groups](https://developer.apple.com/documentation/xcode/configuring-app-groups) for inter-app data sharing. See: <https://developer.apple.com/documentation/uikit/uipasteboard>

## API Evolution and Security Implications

The pasteboard system has gone through several privacy-relevant changes:

- **iOS 9**: Pasteboard access is restricted to foreground apps, reducing passive clipboard sniffing by background processes. However, any foreground app can still read sensitive data left on the clipboard. This risk was highlighted by incidents such as Facebook’s unintended clipboard access. See: <https://support.apple.com/en-us/HT211650>

- **iOS 10 – Universal Clipboard**: Clipboard contents are automatically synchronized across iCloud-connected devices using Universal Clipboard. Developers may restrict synchronization by setting `UIPasteboard.localOnly` or enforce automatic clearing using `UIPasteboard.expirationDate`. See: <https://developer.apple.com/documentation/uikit/uipasteboard/1619800-localonly> and <https://developer.apple.com/documentation/uikit/uipasteboard/1619802-expirationdate>

- **iOS 14 – Pasteboard Transparency**: iOS displays a system notification when an app reads from the general pasteboard without clear user intent. Intent is inferred from explicit user actions such as tapping the system Paste button or selecting *Paste* from a contextual menu. See: <https://developer.apple.com/videos/play/wwdc2020/10114/>

- **iOS 15 – Secure Paste**: Apple introduced a privacy-preserving paste workflow where apps can request paste content without immediately accessing the clipboard data. The system asks the user to confirm the paste, and only upon confirmation is the data provided to the app. This behavior is part of UIKit’s privacy enhancements and is integrated with controls such as `UIPasteControl`. It is documented in Apple’s “What’s new in UIKit” WWDC 2021 session. See: <https://developer.apple.com/videos/play/wwdc2021/10059/> and <https://developer.apple.com/documentation/uikit/uipastecontrol>

- **iOS 16 – Paste Access Confirmation**: iOS 16 strengthened pasteboard privacy by enforcing a system confirmation dialog before an app can read pasteboard content that originates from another app. This is an OS-level privacy enforcement, not a new entitlement. Developers can integrate [`UIPasteControl`](https://developer.apple.com/documentation/uikit/uipastecontrol) to provide a system-managed paste button that aligns with this behavior and improves user experience. See: <https://developer.apple.com/documentation/uikit/uipasteboard>
