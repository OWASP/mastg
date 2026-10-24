---
masvs_category: MASVS-PLATFORM
platform: ios
title: Inter-Process Communication (IPC)
---

iOS does not provide a general-purpose mechanism for third-party apps to communicate directly. Instead, apps exchange data through platform-mediated interfaces of varying levels of abstraction.

Each IPC mechanism can be characterized by who can send data, who can receive data, whether user interaction is required, how long the data remains available, and whether the channel is restricted by an entitlement or app group.

## User-mediated Channels

- @MASTG-KNOW-0083: clipboard-style data exchange between apps.

- @MASTG-KNOW-0079 and @MASTG-KNOW-0080: for launching an app and passing small amounts of routing data. Universal Links are generally safer for web-to-app routing because they are bound to an associated domain, while custom URL schemes can conflict between apps.

- @MASTG-KNOW-0081: share sheets for explicit user-initiated sharing of text, files, URLs, and other content.

- @MASTG-KNOW-0122: document picker, document interaction, and open in place, for exchanging files selected by the user.

- @MASTG-KNOW-0123, @MASTG-KNOW-0124, and @MASTG-KNOW-0129: Handoff, App Intents, and Siri Shortcuts, for system-mediated continuation, automation, or intent-based data exchange.

## Entitlement-scoped Channels

- @MASTG-KNOW-0125: App Groups, for sharing files, `UserDefaults`, databases, preferences, or other data between apps and extensions from the same developer team.

- @MASTG-KNOW-0126: Keychain access groups, for sharing keychain items between apps from the same developer team.

- @MASTG-KNOW-0082: app extensions, for controlled interaction between a host app and an extension. The extension and its containing app can share data through App Groups.

- @MASTG-KNOW-0127: file coordination APIs, for coordinating concurrent access to shared files, especially in App Group containers. File coordination supports shared file based IPC but is not a data exchange channel by itself.

## Network-based Channels

- @MASTG-KNOW-0128: Bonjour, for zero-configuration local network service discovery. Actual communication occurs over the network connection established after discovery.

- @MASTG-KNOW-0130: Core Bluetooth, for BLE-based communication with peripherals and other BLE-capable devices.

- @MASTG-KNOW-0131: Core NFC, for reading and writing NFC tags.

Apps may also communicate through sockets, HTTP, or backend services. These aren't iOS-specific IPC mechanisms and require normal transport security, authentication, authorization, and input validation.

## Low-Level System IPC Mechanisms

@MASTG-KNOW-0104 covers XPC Services, Mach ports, and CFMessagePort. These mechanisms are used internally by Apple frameworks, system daemons, and some extension-based architectures. They aren't designed for general app-to-app communication with unrelated third-party apps; the iOS sandbox prevents that. They're rarely used directly in typical App Store app development but are relevant for security testing when analyzing app extensions, system frameworks, or custom IPC implementations.
