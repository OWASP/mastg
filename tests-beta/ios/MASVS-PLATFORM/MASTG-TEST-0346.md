---
platform: ios
title: References to APIs Hiding Sensitive Data in Text Input Fields
id: MASTG-TEST-0346
type: [static, code, manual]
weakness: MASWE-0053
profiles: [L2]
best-practices: [MASTG-BEST-0044]
knowledge: [MASTG-KNOW-0121, MASTG-KNOW-0141]
---

## Overview

If the app does not mask text input fields that contain sensitive data, such data may be visible to bystanders (shoulder surfing) or captured in screenshots and screen recordings. Marking a field as secure also keeps it on the system keyboard: iOS does not offer installed third-party (custom) keyboards for secure fields (see @MASTG-KNOW-0141), so they never receive the typed characters.

This test statically analyzes the app binary for references to text input APIs and checks whether the app configures input fields to mask sensitive text entries. In iOS, masking replaces typed characters with bullet characters using the following settings:

- In `UIKit`, this is done by setting [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) to `true` on a `UITextField`.
- In `SwiftUI`, this is done by using [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) instead of `TextField`.

Example for UIKit:

```swift
let passwordField = UITextField()
passwordField.isSecureTextEntry = true
// Alternatively, toggling the property
textField.isSecureTextEntry.toggle()
```

Example for SwiftUI:

```swift
SecureField("Password", text: $password)
```

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0066 to look for uses of the relevant APIs.
3. Use @MASTG-TECH-0076 to analyze the relevant code paths and determine whether sensitive data is stored in the input fields.

## Observation

The output should contain a list of locations where the app:

- Creates text input fields, such as `UITextField`, `SecureField` or `TextField`.
- Explicitly configures visibility attributes that mask the inputted text.

## Evaluation

The test case fails if the app uses text input fields to input sensitive data and these input fields are not masked. This occurs when:

- `UIKit` `UITextField` used for a password, PIN, or OTP does not have [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) set to `true`.
- `SwiftUI` `TextField` is used instead of [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) for a password, PIN, or OTP field.

!!! note
    It is not a failure if non-sensitive text input fields (for example, for a username or email address) are unmasked. Validating whether a text input field is used for sensitive data may require a review of the app's UI and business logic to determine the context in which the field is used.

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `UITextField` or `SecureField` (for example in custom UI frameworks or game engines, or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at rest).
