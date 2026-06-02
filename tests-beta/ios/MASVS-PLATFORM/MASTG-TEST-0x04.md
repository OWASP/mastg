---
platform: ios
title: References to evaluateJavaScript Without Content World Isolation
id: MASTG-TEST-0x04
type: [static, code]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0x04]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0x01]
---

## Overview

When an app uses [`evaluateJavaScript(_:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) to read data from the DOM (for example, to extract form field values, account details, or page structure), the script executes in the `.page` world. In this world, the JavaScript prototype chain is shared with page scripts. A malicious or compromised page can override built-in functions such as `document.querySelector` or `Element.prototype.getAttribute` before the inspection code runs, causing it to return attacker-controlled values instead of the real DOM content.

The content-world-aware variant [`evaluateJavaScript(_:in:in:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:in:in:completionhandler:)) runs the script in an isolated world with an independent prototype chain that page JavaScript cannot modify.

This test checks whether the app uses the legacy `evaluateJavaScript:completionHandler:` selector when reading DOM data in security-relevant contexts.

## Steps

1. Use @MASTG-TECH-0058 to extract the app.
2. Use @MASTG-TECH-0066 to look for uses of `evaluateJavaScript:completionHandler:` in the app binary.

## Observation

The output should contain a list of locations where `evaluateJavaScript:completionHandler:` is called, along with the enclosing function symbols.

## Evaluation

The test case fails if `evaluateJavaScript:completionHandler:` is used to read DOM content in a security-relevant context.
