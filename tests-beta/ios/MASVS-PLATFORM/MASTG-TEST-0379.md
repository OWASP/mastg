---
platform: ios
title: References to `evaluateJavaScript` Without Content World Isolation
id: MASTG-TEST-0379
type: [static, code]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0061]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

## Overview

When an app uses [`evaluateJavaScript(_:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) to read data from the DOM (for example, to extract form field values, account details, or page structure), the script executes in the `.page` world. In this world, the JavaScript prototype chain is shared with page scripts. A malicious or compromised page can override built-in functions such as `document.querySelector` or `Element.prototype.getAttribute` before the inspection code runs, causing it to return attacker-controlled values instead of the real DOM content.

The content-world-aware variant [`evaluateJavaScript(_:in:in:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:in:in:completionhandler:)) runs the script in an isolated world with an independent prototype chain that page JavaScript cannot modify.

This test checks whether the app uses the legacy `evaluateJavaScript:completionHandler:` selector when reading DOM data in security-relevant contexts.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where `evaluateJavaScript:completionHandler:` is called, along with the enclosing function symbols.

## Evaluation

The test case fails if `evaluateJavaScript:completionHandler:` is used to read DOM content in a security-relevant context.

**Further Validation Required:**

Inspect each reported call site using @MASTG-TECH-0076 to confirm whether the evaluated JavaScript string reads data from the DOM (for example via `document.querySelector`, `document.getElementById`, `getAttribute`, `.value`, `.textContent`, or `.innerHTML`) and whether that data is security-relevant, such as account details, credentials, or authentication tokens.

Also confirm that the call uses the legacy `evaluateJavaScript:completionHandler:` selector rather than the content-world-aware `evaluateJavaScript:inFrame:inContentWorld:completionHandler:` variant. Only the legacy selector runs in the `.page` world, where page JavaScript can override built-in functions (such as `document.querySelector`) and return attacker-controlled values; reads performed in an isolated content world are not affected.
