---
platform: ios
title: Pasteboard Contents Not Restricted to Local Device
id: MASTG-TEST-0280
type: [static, code]
weakness: MASWE-0053
threat: [app]
profiles: [L2]
knowledge: [MASTG-KNOW-0083]
---

## Overview

This test checks if the app restricts the contents of the general pasteboard ([`UIPasteboard.general`](https://developer.apple.com/documentation/uikit/uipasteboard/general "UIPasteboard generalPasteboard")) to the local device by using the [`UIPasteboard.setItems(_:options:)`](https://developer.apple.com/documentation/uikit/uipasteboard/setitems(_:options:) "UIPasteboard setItems(_:options:)") method with the `UIPasteboard.OptionsKey.localOnly` option. If sensitive data is placed in the general pasteboard without this restriction, it can be synced across devices via Universal Clipboard, leading to potential data leaks. See @MASTG-KNOW-0083 for more details on the general pasteboard.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if the app uses the general pasteboard without restricting its contents to the local device. Specifically, ensure that the `UIPasteboard.setItems(_:options:)` method is called with the `UIPasteboard.Options.localOnly` option.
