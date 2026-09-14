---
masvs_category: MASVS-PLATFORM
platform: android
title: Text Input Field Masking in Android
---

Android provides different mechanisms for masking text entered into input fields. The available mechanisms depend on whether the app uses the Android View system or Jetpack Compose.

## Android Views

In the Android View system, text fields such as `EditText` use [`InputType`](https://developer.android.com/reference/android/text/InputType) values to describe the type of content being entered.

Password-related input types include:

- [`TYPE_TEXT_VARIATION_PASSWORD`](https://developer.android.com/reference/android/text/InputType#TYPE_TEXT_VARIATION_PASSWORD), for textual passwords.
- [`TYPE_NUMBER_VARIATION_PASSWORD`](https://developer.android.com/reference/android/text/InputType#TYPE_NUMBER_VARIATION_PASSWORD), for numeric passwords and PINs.
- [`TYPE_TEXT_VARIATION_WEB_PASSWORD`](https://developer.android.com/reference/android/text/InputType#TYPE_TEXT_VARIATION_WEB_PASSWORD), for passwords entered in web form text controls.
- [`TYPE_TEXT_VARIATION_VISIBLE_PASSWORD`](https://developer.android.com/reference/android/text/InputType#TYPE_TEXT_VARIATION_VISIBLE_PASSWORD), for password text that remains visible.

In XML layouts, the corresponding values include `textPassword`, `numberPassword`, `textWebPassword`, and `textVisiblePassword`.

Text masking can also be applied using [`PasswordTransformationMethod`](https://developer.android.com/reference/android/text/method/PasswordTransformationMethod), which transforms the displayed representation of the text while preserving the underlying value.

The Android platform implementation of [`PasswordTransformationMethod`](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/text/method/PasswordTransformationMethod.java) can temporarily display the most recently entered character when the platform's password visibility setting is enabled.

## Jetpack Compose Secure Text Fields

Jetpack Compose provides [`BasicSecureTextField`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/BasicSecureTextField) in Compose Foundation and `SecureTextField` and `OutlinedSecureTextField` in Material libraries.

Secure text fields use [`TextObfuscationMode`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/input/TextObfuscationMode) to determine how the entered text is displayed. It defines the following modes:

- `Hidden`, which hides all characters.
- `RevealLastTyped`, which temporarily reveals the most recently entered character.
- `System`, which delegates the choice between hiding and temporarily revealing characters to the platform.
- `Visible`, which does not obscure the text.

The behavior and defaults have changed across Compose versions.

Starting with [Compose Foundation 1.12.0-beta01, released on June 17, 2026](https://developer.android.com/jetpack/androidx/releases/compose-foundation#1.12.0-beta01), `TextObfuscationMode.Default` was renamed to `TextObfuscationMode.System`. In the same release, `RevealLastTyped` was changed to act as an explicit override that always reveals the most recently entered character, and the default `textObfuscationMode` of `BasicSecureTextField` was changed to `System`. These changes are also included in [Compose Foundation 1.12.0, released on August 12, 2026](https://developer.android.com/jetpack/androidx/releases/compose-foundation#1.12.0).

On September 9, 2026, [Material 3 1.5.0-alpha28](https://developer.android.com/jetpack/androidx/releases/compose-material3#1.5.0-alpha28) changed `SecureTextField`, `OutlinedSecureTextField`, and deprecated `BasicSecureTextField` overloads from `TextObfuscationMode.RevealLastTyped` to `TextObfuscationMode.System` by default. The corresponding change is available in the [AndroidX source repository](https://github.com/androidx/androidx/commit/e37bf4e641582f6f97c8458eab1ad7a49cefa3bb).

The current [`TextObfuscationMode.System`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/input/TextObfuscationMode#System%28%29) behavior depends on the Android version and platform configuration. Below SDK 37, it follows the system-wide `Settings.System.TEXT_SHOW_PASSWORD` setting. On SDK 37 and later, it can respect granular platform settings that distinguish between touch input and physical keyboard input.

When the platform chooses to show password characters, `System` behaves like `RevealLastTyped`. Otherwise, it behaves like `Hidden`.

## Compose TextField and Visual Transformations

A regular Compose `TextField` can also mask its displayed content using a visual transformation.

[`PasswordVisualTransformation`](https://developer.android.com/reference/kotlin/androidx/compose/ui/text/input/PasswordVisualTransformation) replaces the displayed characters with masking characters without modifying the underlying text value.

This mechanism is commonly found in Compose code that predates the secure text field APIs or uses a regular `TextField` directly.

## Other Input Controls

Custom input controls and controls implemented by third-party UI frameworks, cross-platform frameworks, or game engines may not use Android View password input types or Jetpack Compose secure text field APIs.

Their masking behavior depends on how the framework or custom control renders and manages the entered text.
