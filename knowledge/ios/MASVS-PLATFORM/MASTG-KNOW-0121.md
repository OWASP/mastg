---
masvs_category: MASVS-PLATFORM
platform: ios
title: Text Input Field Masking in iOS
---

iOS provides dedicated APIs to mask text entered in input fields, replacing visible characters with bullet characters. This helps prevent entered text from being observed on-screen by bystanders, shoulder surfing, and may help protect the text from some forms of screen capture or broadcast.

## UIKit: UITextField and isSecureTextEntry

In UIKit, the [`UITextField`](https://developer.apple.com/documentation/uikit/uitextfield) class adopts the [`UITextInputTraits`](https://developer.apple.com/documentation/uikit/uitextinputtraits) protocol, which exposes the [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) property. When set to `true`, the field masks characters as they are typed. The default value is `false`.

Setting this property also disables copying from the field and disables automatic text entry suggestions from the keyboard.

```swift
let passwordField = UITextField()
passwordField.isSecureTextEntry = true
```

The property can also be toggled at runtime, for example to implement a "show/hide password" button:

```swift
textField.isSecureTextEntry.toggle()
```

## SwiftUI: SecureField vs. TextField

In SwiftUI, [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) is the designated component for masked text entry. Use it when you want the behavior of a [`TextField`](https://developer.apple.com/documentation/swiftui/textfield), but want to hide the field's text. Unlike `UITextField`, there is no `isSecureTextEntry` property to configure on `SecureField`, masking is always active.

```swift
// Masked: use SecureField
SecureField("Password", text: $password)

// Unmasked: use TextField (not appropriate for sensitive input)
TextField("Username", text: $username)
```

## Other Input Controls

Controls that adopt `UITextInputTraits` may also expose `isSecureTextEntry`, though masking is rarely appropriate for multi-line text views.

Custom input controls that bypass `UITextField` or `SecureField` entirely, for example, those implemented in game engines or cross-platform UI frameworks, do not inherit these masking mechanisms and must implement their own.
