---
masvs_category: MASVS-PLATFORM
platform: ios
title: Pasteboard
---

Using the [`UIPasteboard`](https://developer.apple.com/documentation/uikit/uipasteboard) API, apps can access the iOS pasteboard, enabling data sharing either within the app or across apps. However, because the general pasteboard is system-wide, it introduces privacy and security risks — especially when sensitive data is copied programmatically without the user’s awareness.

There are two main types of pasteboards:

- **General pasteboard (`UIPasteboard.general`)**:  
  Shared across all foreground apps and, with [Universal Clipboard](https://support.apple.com/en-us/102430), across the user’s Apple devices.  
  It is persistent by default across device restarts unless cleared.  
  Since iOS 16, the general pasteboard requires explicit user interaction for access.

- **Custom or Named Pasteboards (`UIPasteboard(name:create:)`, `UIPasteboard.withUniqueName()`)**:  
  These are private pasteboards restricted to the app that created them or other apps signed with the same team ID.  
  They have been non-persistent by default since iOS 10 (deleted on app termination or system reboot).  
  Apple discourages persistent custom pasteboards and recommends using [App Groups](https://developer.apple.com/documentation/Xcode/configuring-app-groups) for inter-app data sharing.

## API Evolution and Security Implications

The pasteboard system has gone through several privacy-relevant changes:

- **iOS 9:**  
  Pasteboard access is restricted to foreground apps, reducing passive clipboard sniffing.  
  However, if sensitive data remains on the clipboard, any malicious foreground app (or widget) can still access it.  
  Example attack: Facebook clipboard reading incident (see report).

- **iOS 10:**  
  Universal Clipboard automatically syncs clipboard contents across iCloud-connected devices.  
  Developers may restrict synchronization via `UIPasteboard.localOnly`  
  or enforce automatic clearing using `UIPasteboard.expirationDate`.

- **iOS 14 – Pasteboard Transparency:**  
  iOS notifies users when an app reads the general pasteboard content that originates from another app without clear user intent.  
  Intent is inferred from user actions such as tapping a system paste button or selecting *Paste* from a contextual menu.

- **iOS 15 – Secure Paste:**  
  Secure Paste allows apps to support paste operations **without gaining direct access** to clipboard data until the user explicitly confirms the paste.  
  When Secure Paste is used, the system does **not** show pasteboard transparency alerts.  
  This reduces unnecessary clipboard exposure and improves user privacy.

- **iOS 16 – Paste Access Confirmation:**  
  Apps must receive explicit user approval via a system dialog before reading pasteboard content.  
  Developers can also use [`UIPasteControl`](https://developer.apple.com/documentation/uikit/uipastecontrol), which improves UX by presenting a system-managed paste button.  
  Although this does not necessarily increase security, it ensures that pasteboard reads occur only after clear user interaction.
