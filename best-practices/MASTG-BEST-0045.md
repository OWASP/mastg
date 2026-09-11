---
title: Limit Sensitive Data Exposure Through iOS IPC Channels
alias: limit-sensitive-data-exposure-through-ios-ipc-channels
id: MASTG-BEST-0045
platform: ios
knowledge: [MASTG-KNOW-0083, MASTG-KNOW-0079, MASTG-KNOW-0080, MASTG-KNOW-0081, MASTG-KNOW-0082, MASTG-KNOW-0122, MASTG-KNOW-0123, MASTG-KNOW-0124, MASTG-KNOW-0125, MASTG-KNOW-0126, MASTG-KNOW-0127, MASTG-KNOW-0128, MASTG-KNOW-0129, MASTG-KNOW-0130, MASTG-KNOW-0131, MASTG-KNOW-0104]
---

When your app exchanges data across iOS IPC channels, share the minimum amount of data for the shortest time possible. Design these flows so that intercepted payloads are low value and short lived. Follow the principle of least privilege: grant each IPC channel and shared container only the minimum permissions required for its intended purpose, and validate all inbound data as untrusted input.

For guidance on channel behavior, see @MASTG-KNOW-0078.

## Restrict Pasteboard Usage

Avoid placing sensitive values in `UIPasteboard.general` unless there is a strict product requirement. If you must use the pasteboard, set restrictive options with [`setItems(_:options:)`](https://developer.apple.com/documentation/uikit/uipasteboard/setitems(_:options:)), such as [`UIPasteboard.OptionsKey.localOnly`](https://developer.apple.com/documentation/uikit/uipasteboard/optionskey/localonly) and [`UIPasteboard.OptionsKey.expirationDate`](https://developer.apple.com/documentation/uikit/uipasteboard/optionskey/expirationdate).

## Prefer Short-Lived Exchange Data

For URL-based interfaces, such as [custom URL schemes](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app) and [Universal Links](https://developer.apple.com/documentation/xcode/supporting-universal-links-in-your-app), avoid embedding long-lived secrets, tokens, credentials, or personal data in URLs. Use one-time or short-lived references and have the receiving side redeem them through an authenticated channel.

Apply the same pattern to Handoff, Siri Shortcuts, App Intents, shared files, and document exchange flows. Pass references, identifiers, or scoped URLs instead of full sensitive payloads whenever possible.

## Validate All IPC Input

Treat data received through IPC channels as untrusted input, even when it comes from a system-mediated flow. Validate URLs, pasteboard contents, shared files, document imports, App Intent parameters, Siri Shortcut parameters, `NSUserActivity.userInfo`, and files read from App Group containers before use.

Reject unexpected schemes, hosts, file types, identifiers, entity references, and parameter combinations. Enforce authorization checks in the receiving handler, not only in the normal app UI.

## Constrain Shared Container Data

When using [App Groups](https://developer.apple.com/documentation/xcode/configuring-app-groups), store only data that must be shared with extensions or companion apps. Protect files with the [Data Protection API](https://developer.apple.com/documentation/foundation/fileprotectiontype), and remove shared artifacts as soon as they are no longer needed, for example after the receiving process confirms consumption or after a short server-defined timeout.

Any app or extension in the App Group can potentially read or modify shared data, so avoid storing secrets, session tokens, or high-value personal data unless strictly necessary.

## Limit Shared Keychain Access

When using [Keychain access groups](https://developer.apple.com/documentation/security/sharing-access-to-keychain-items-among-a-collection-of-apps), create dedicated shared groups for specific use cases instead of sharing broadly across all apps from the same team. Store only items that must be shared, and protect them with appropriate `kSecAttrAccessible*` and access control settings.

Review all apps and extensions that declare the same access group, because each one becomes part of the trust boundary for the shared keychain items.

Avoid storing overly sensitive data such as hashed passwords in the keychain, even in non-shared access groups. On jailbroken devices, the keychain of any process can be read directly, so items stored there are not protected in that threat model.

## Coordinate and Audit Shared File Access

When multiple processes access shared files, use @MASTG-KNOW-0127 to keep access patterns explicit and predictable. This is especially important for App Group containers, external documents, open in place flows, and files that may be modified by another app, extension, or File Provider.

Review shared file locations during code review and testing. Check file permissions, Data Protection classes, cleanup behavior, locking, and whether stale files can expose sensitive data after the intended exchange is complete.

## Protect Document Exchange Flows

For document picker, document interaction, share sheet, and open in place flows, treat received files as untrusted input. Validate file type, size, structure, and content before parsing. Avoid assuming that the declared Uniform Type Identifier accurately reflects the file contents.

When opening external documents, access only the files selected by the user and release security scoped access as soon as it is no longer needed.

## Minimize App Intent, Siri Shortcut, and Handoff Data

For App Intents, Siri Shortcuts, and Handoff, avoid placing secrets or unnecessary personal data in intent parameters, shortcut configuration, donated interactions, entity metadata, dialogs, summaries, Spotlight metadata, or `NSUserActivity.userInfo`.

Require confirmation or authentication for sensitive, destructive, financial, privacy-impacting, or irreversible actions. App Intent and shortcut handlers should perform the same authorization checks as the corresponding in-app flow.

## Secure Network-Based IPC

For Bonjour, local sockets, HTTP, and backend-mediated communication, do not rely on local network presence as a trust signal. Authenticate peers, encrypt traffic where appropriate, and validate all messages.

Bonjour provides service discovery, not transport security. The connection established after discovery still requires normal authentication, authorization, transport protection, and input validation.

## Avoid Unsupported Low-Level IPC

XPC, Mach ports, and CFMessagePort are not designed for general-purpose communication between unrelated third-party iOS apps. The iOS sandbox prevents direct use of these mechanisms between apps from different developers. Prefer Apple-supported system-mediated APIs, App Groups, Keychain access groups, document exchange, App Intents, Siri Shortcuts, Handoff, or network protocols where appropriate.

If low-level IPC appears in app extensions, platform-specific extension mechanisms, enterprise builds, or security research contexts, review the exposed interface, accepted messages, entitlements, sandbox profile, input validation, and authorization checks.
