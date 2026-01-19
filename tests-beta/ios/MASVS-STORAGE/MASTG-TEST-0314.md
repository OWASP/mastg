---
platform: ios
title: Runtime Monitoring of Text Fields Eligible for Keyboard Caching
id: MASTG-TEST-0314
type: [dynamic]
weakness: MASWE-0053
profiles: [L2]
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0026]
knowledge: [MASTG-KNOW-0100]
---

## Overview

This test complements @MASTG-TEST-0313. It monitors text inputs in the app at runtime, for example [`UITextField`](https://developer.apple.com/documentation/uikit/uitextfield), [`UITextView`](https://developer.apple.com/documentation/uikit/uitextview) or [`UISearchBar`](https://developer.apple.com/documentation/uikit/uisearchbar), and checks whether they are eligible for keyboard caching when the user enters sensitive information.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0067 to look for text input fields in the app's UI and identify those that use the relevant attributes.
3. Exercise the app thoroughly, entering realistic sensitive information (for example, usernames, passwords, email addresses, credit card numbers, recovery codes) into each identified input field.

## Observation

The output should allow the tester to associate each text entry with the corresponding input field and its protection status. At minimum it should contain:

- The input widget details, including class and accessibility identifier when available.
- The input traits relevant to keyboard caching, for example: `isSecureTextEntry`, `autocorrectionType`, `spellCheckingType`, and any other traits or flags that influence keyboard prediction or caching.
- The entered values so they can be correlated with sensitive information.

## Evaluation

The test fails if any UI inputs that may handle sensitive values (for example, usernames, passwords, email addresses, credit card numbers, recovery codes) are eligible for keyboard caching. This occurs when:

- `isSecureTextEntry` is not enabled, or
- `autocorrectionType` is set to `default` or `yes`, or
- `spellCheckingType` is set to `default` or `yes`.

The test passes if all security-sensitive inputs are protected against keyboard caching.

**Note:** This test may produce false negatives if the app uses custom text input controls that do not rely on standard UIKit classes such as `UITextField` or `UITextView` (for example in custom UI frameworks or game engines), or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at runtime.
