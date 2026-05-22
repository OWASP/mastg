---
platform: ios
title: Overly Broad File Read Access in WebViews
id: MASTG-TEST-0333
type: [static, code, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0033]
knowledge: [MASTG-KNOW-0076]
profiles: [L1, L2]
---

## Overview

iOS apps can load local files into a [`WKWebView`](https://developer.apple.com/documentation/webkit/wkwebview) using [`loadFileURL(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:)).

This test checks whether the app uses `loadFileURL(_:allowingReadAccessTo:)` with an overly broad `readAccessURL`. If attacker-controlled input influences the loaded file URL and the read access scope is too broad, the WebView may gain access to sensitive files within the app container.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations in the binary where `WKWebView.loadFileURL(_:allowingReadAccessTo:)` is called.

## Evaluation

The test case fails if any call to `loadFileURL(_:allowingReadAccessTo:)` is found where the `readAccessURL` argument grants overly broad read access, for example to the entire `Documents` directory or the app container root.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076:

- Inspect the `fileURL` argument and determine whether it can be influenced by attacker-controlled input.
- Inspect the `readAccessURL` argument and determine whether it grants broader access than necessary.
- Verify that the allowed read scope is restricted to the minimum directory required for the intended content.
