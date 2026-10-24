---
masvs_category: MASVS-PLATFORM
platform: ios
title: Custom Keyboards
available_since: 8
---

A [custom keyboard](https://developer.apple.com/documentation/uikit/creating-a-custom-keyboard) is an app extension (see @MASTG-KNOW-0082) that replaces the system keyboard across all apps on the device. The user installs it through its containing app and must explicitly enable it in **Settings** (**General > Keyboard > Keyboards**).

By default [a custom keyboard runs without "Full Access"](https://developer.apple.com/documentation/uikit/configuring-open-access-for-a-custom-keyboard), which prevents it from making network requests or accessing shared containers; the user can grant "Full Access" in Settings, which the keyboard requests via the [`RequestsOpenAccess`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsextension/nsextensionattributes/requestsopenaccess) key and is able to check whether it has it through the [`hasFullAccess`](https://developer.apple.com/documentation/uikit/uiinputviewcontroller/hasfullaccess) property of `UIInputViewController`.

iOS gives apps two relevant controls over which keyboard processes their text fields:

- A `UITextField` or `UITextView` whose [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) trait is `true` (or a SwiftUI `SecureField`) always uses the system keyboard. Third-party keyboards are not shown for secure fields, so they never receive the typed characters.
- An app can reject custom keyboard extensions across the whole app by implementing [`application(_:shouldAllowExtensionPointIdentifier:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/application(_:shouldallowextensionpointidentifier:)) in its `UIApplicationDelegate` and returning `false` for `UIApplicationKeyboardExtensionPointIdentifier` (`com.apple.keyboard-service`). The system then uses the built-in keyboard throughout the app, regardless of the keyboards the user has installed.
