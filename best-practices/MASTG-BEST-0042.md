---
title: Limit Sensitive Data Exposure Through iOS IPC Channels
alias: limit-sensitive-data-exposure-through-ios-ipc-channels
id: MASTG-BEST-0042
platform: ios
knowledge: [MASTG-KNOW-0121, MASTG-KNOW-0083]
---

When your app exchanges data across iOS IPC channels, share the minimum amount of data for the shortest time possible. Design these flows so that intercepted payloads are low value and short lived.

For guidance on channel behavior, see @MASTG-KNOW-0121. You can validate these controls with @MASTG-TEST-0056.

## Restrict Pasteboard Usage

Avoid placing sensitive values in `UIPasteboard.general` unless there is a strict product requirement. If you must use pasteboard, set restrictive options with [`setItems(_:options:)`](https://developer.apple.com/documentation/uikit/uipasteboard/setitems(_:options:)) such as [`UIPasteboard.OptionsKey.localOnly`](https://developer.apple.com/documentation/uikit/uipasteboard/optionskey/localonly) and [`UIPasteboard.OptionsKey.expirationDate`](https://developer.apple.com/documentation/uikit/uipasteboard/optionskey/expirationdate).

## Prefer Short-Lived Exchange Data

For URL-based handoff ([custom URL schemes](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app) and [Universal Links](https://developer.apple.com/documentation/xcode/supporting-universal-links-in-your-app)), avoid embedding long-lived secrets in URLs. Use one-time or short-lived references that the receiving side redeems through an authenticated channel.

## Constrain Shared Container Data

When using [App Groups](https://developer.apple.com/documentation/xcode/configuring-app-groups), store only data that must be shared with extensions or companion apps. Protect files with the [Data Protection API](https://developer.apple.com/documentation/foundation/fileprotectiontype), and remove shared artifacts as soon as they are no longer needed.

## Coordinate and Audit Shared File Access

When multiple processes access shared files, use [`NSFileCoordinator`](https://developer.apple.com/documentation/foundation/nsfilecoordinator) and related APIs to keep access patterns explicit and predictable. Review IPC call sites during code review to confirm that sensitive data handling still matches your intended trust boundaries.
