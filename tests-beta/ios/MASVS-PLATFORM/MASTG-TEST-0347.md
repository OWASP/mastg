---
platform: ios
title: Runtime Use of APIs Hiding Sensitive Data in Text Input Fields
id: MASTG-TEST-0347
type: [dynamic, hooks]
weakness: MASWE-0053
profiles: [L2]
best-practices: [MASTG-BEST-0044]
knowledge: [MASTG-KNOW-0121]
---

## Overview

This test complements @MASTG-TEST-0346. It monitors text input fields in the app at runtime to check if the app masks the text entry when the user enters sensitive data.

If the app does not mask text input fields that contain sensitive data, such data may be visible to bystanders (shoulder surfing) or captured in screenshots and screen recordings.

Ensure you exercise the app thoroughly, entering realistic sensitive data (for example, usernames, passwords, email addresses, credit card numbers, recovery codes) into each identified text input field.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain evidences that allow associating each text entry with the corresponding input field and its protection status. At minimum it should contain:

- The input field class, such as `UITextField`, `SecureField` or `TextField`.
- The input traits relevant to visibility, for example, `isSecureTextEntry`.
- The entered values so they can be correlated with sensitive data.

## Evaluation

The test case fails if the app uses UI elements that do not allow masking text or if any text input field used containing sensitive data is found unmasked. For example, due to the following:

- A `UITextField` used for a password, PIN, or OTP does not have [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) set to `true`.
- A SwiftUI `TextField` is used instead of [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) for a password, PIN, or OTP field.

!!! note
    It is not a failure if non-sensitive text input fields (for example, for a username or email address) are unmasked. Validating whether a text input field is used for sensitive data may require a review of the app's UI and business logic to determine the context in which the field is used.

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `UITextField` or `SecureField` (for example in custom UI frameworks or game engines, or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at rest).
