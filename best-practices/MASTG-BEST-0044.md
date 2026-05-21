---
title: Mask Sensitive Data in Text Input Fields
alias: mask-sensitive-data-in-text-input-fields-ios
id: MASTG-BEST-0044
platform: ios
knowledge: [MASTG-KNOW-0098]
---

For any text input field that handles sensitive information such as passwords, PINs, or OTPs, ensure that the entered text is visually masked to prevent bystanders or screen capture tools from exposing it.

## UIKit

Set [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) to `true` on any `UITextField` that captures sensitive data. This replaces typed characters with bullet characters (•) and prevents the text from appearing in plain text.

```swift
let passwordField = UITextField()
passwordField.isSecureTextEntry = true
```

## SwiftUI

Use [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) instead of `TextField` for any input that handles passwords, PINs, or OTPs. `SecureField` automatically masks its content as the user types.

```swift
SecureField("Password", text: $password)
```

!!! note
    Do not use a plain `TextField` for sensitive input, even if you intend to style it to look like a masked field at the application layer, because this does not provide the same level of protection as the system-provided secure text entry mechanisms.
