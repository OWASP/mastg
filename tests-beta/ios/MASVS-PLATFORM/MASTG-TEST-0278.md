---
platform: ios
title: Pasteboard Contents Not Cleared After Use
id: MASTG-TEST-0278
type: [static, code]
weakness: MASWE-0053
threat: [app]
profiles: [L2]
knowledge: [MASTG-KNOW-0083]
---

## Overview

This test checks if the app clears the contents of the general @MASTG-KNOW-0083 when it moves to the background or terminates. If sensitive data is left in the pasteboard, it can be accessed by other apps, leading to potential data leaks.

Apps can clear the contents of the general pasteboard by setting `UIPasteboard.general.items = []` in the appropriate lifecycle methods, such as `applicationDidEnterBackground:` or `applicationWillTerminate:`. This would translate to calls to [`UIPasteboard.general`](https://developer.apple.com/documentation/uikit/uipasteboard/1622106-generalpasteboard "UIPasteboard generalPasteboard") and to [`UIPasteboard.setItems`](https://developer.apple.com/documentation/uikit/uipasteboard/setitems(_:options:) "UIPasteboard setItems") with an empty array (`[]`) in the reverse-engineered code.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if the app uses the general pasteboard and does not clear its contents when moving to the background or terminating. Specifically, it should be verified that there are calls to `UIPasteboard.setItems` with an empty array (`[]`) in the appropriate lifecycle methods.
