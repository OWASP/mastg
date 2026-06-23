---
title: Keep Sensitive Input on the System Keyboard
alias: keep-sensitive-input-on-system-keyboard
id: MASTG-BEST-0069
platform: ios
knowledge: [MASTG-KNOW-0082, MASTG-KNOW-0141]
---

Custom keyboards are app extensions that replace the system keyboard across all apps and, once granted "Full Access", can transmit what the user types off the device (see @MASTG-KNOW-0082). For input that carries secrets, such as passwords, PINs, one-time passcodes, or payment data, keep the entry on the trusted system keyboard rather than relying on whichever keyboard the user has installed.

## Mark Sensitive Fields as Secure (Preferred)

Set [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) to `true` on the `UITextField`/`UITextView`, or use a SwiftUI [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield). iOS does not display third-party keyboards for secure fields, so the typed characters stay on the system keyboard. This is field-scoped, so it does not disrupt the user's keyboard choice elsewhere in the app, and it also masks the input and prevents keyboard caching.

```swift
let pinField = UITextField()
pinField.isSecureTextEntry = true
```

## Reject Custom Keyboards App-Wide (When Appropriate)

For apps where no field should ever use a third-party keyboard (for example, high-assurance banking apps), reject the keyboard extension point in the app delegate using [`application(_:shouldAllowExtensionPointIdentifier:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/application(_:shouldallowextensionpointidentifier:)). The system then uses the built-in keyboard throughout the app.

```swift
func application(_ application: UIApplication,
                 shouldAllowExtensionPointIdentifier extensionPointIdentifier: UIApplication.ExtensionPointIdentifier) -> Bool {
    if extensionPointIdentifier == .keyboard {
        return false
    }
    return true
}
```

!!! note

    Use this app-wide control deliberately. It removes the user's ability to use accessibility-oriented or localized third-party keyboards anywhere in the app, so prefer per-field `isSecureTextEntry` for individual sensitive fields and reserve the app-wide restriction for apps that genuinely require it.
