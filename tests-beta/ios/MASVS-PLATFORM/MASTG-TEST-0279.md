---
platform: ios
title: Pasteboard Contents Not Expiring
id: MASTG-TEST-0279
type: [static, code]
weakness: MASWE-0053
threat: [app]
profiles: [L2]
knowledge: [MASTG-KNOW-0083]
---

## Overview

This test checks if the app sets an expiration date for the contents of the general pasteboard ([`UIPasteboard.general`](https://developer.apple.com/documentation/uikit/uipasteboard/general "UIPasteboard generalPasteboard")) using the [`UIPasteboard.setItems(_:options:)`](https://developer.apple.com/documentation/uikit/uipasteboard/setitems(_:options:) "UIPasteboard setItems(_:options:)") method with the `UIPasteboard.Options.expirationDate` option. If sensitive data is left in the pasteboard without an expiration date, it can be accessed by other apps indefinitely, leading to potential data leaks. See @MASTG-KNOW-0083 for more details on the general pasteboard.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if the app uses the general pasteboard without setting an expiration date for its contents. Specifically, ensure that the `UIPasteboard.setItems(_:options:)` method is called with the `UIPasteboard.Options.expirationDate` option.
