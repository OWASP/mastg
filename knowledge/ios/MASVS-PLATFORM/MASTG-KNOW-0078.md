---
masvs_category: MASVS-PLATFORM
platform: ios
title: Inter-Process Communication (IPC)
---

iOS does not provide a general purpose mechanism for arbitrary third-party apps to communicate directly. Instead, apps exchange data through platform-mediated channels, user actions, shared entitlements, or network interfaces.

From a security perspective, each IPC mechanism should be assessed by asking who can send data, who can receive data, whether user interaction is required, how long the data remains available, and whether the channel is restricted by an entitlement or app group.

## User-mediated Channels

- @MASTG-KNOW-0083: for clipboard style data exchange between apps. Treat pasteboard data as exposed to other apps unless access is limited with suitable options, such as local only or expiration.

- @MASTG-KNOW-0079 and @MASTG-KNOW-0080, for launching an app and passing small amounts of routing data. Universal Links are generally safer for web to app routing because they are bound to an associated domain, while custom URL schemes can conflict between apps.

- @MASTG-KNOW-0081: Share sheets for explicit user-initiated sharing of text, files, URLs, and other content.

Document picker, document interaction, and open in place, for exchanging files selected by the user.

Handoff, App Intents, and Siri Shortcuts, for system-mediated continuation, automation, or intent-based data exchange.

## Entitlement-scoped Channels

App Groups, for sharing files, UserDefaults, databases, preferences, or other data between apps and extensions from the same developer team.

Keychain access groups, for sharing keychain items between apps from the same developer team.

- @MASTG-KNOW-0082: for controlled interaction between a host app, an extension, and the containing app. Shared storage is commonly implemented with App Groups.

File coordination APIs, for coordinating safe concurrent access to shared files, especially in App Group containers.

## Network-based Channels

Apps may also communicate through local or remote networking, such as sockets, HTTP, Bonjour, or backend services. These are not iOS specific IPC mechanisms and require normal transport security, authentication, authorization, and input validation.

## Limited or System-focused Mechanisms

@MASTG-KNOW-0104 including XPC, Mach ports, and CFMessagePort are used by Apple frameworks and system services, and in some extension or framework-mediated designs. They are not general-purpose app-to-app IPC options for normal iOS App Store apps but can be useful for security testing in certain contexts, such as analyzing app extensions, frameworks, or custom IPC implementations.
