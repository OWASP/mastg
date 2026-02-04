---
platform: android
title: App Exposing User Authentication Data in Text Input Fields
id: MASTG-TEST-0316
type: [static, manual]
weakness: MASWE-0053
profiles: [L2]
---

## Overview

This test verifies that the app handles user input correctly, ensuring that access codes (passwords or pins) and verification codes (OTPs) are not exposed in plain text within text input fields.

Proper masking (e.g., dots instead of input characters) of these codes is essential to protect user privacy. This can be achieved by using appropriate input types that obscure the characters entered by the user. In Jetpack Compose, `SecureTextField` uses `TextObfuscationMode`, which [by default is `TextObfuscationMode.RevealLastTyped`](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/SecureTextField.kt;l=115?q=SecureTextField), so a developer can simply use `SecureTextField` without explicitly setting `textObfuscationMode` unless another behavior is required.

XML view:

```xml
<EditText
    android:inputType="textPassword"
    ...
/>
```

Jetpack Compose:

```kotlin
SecureTextField(
    // textObfuscationMode defaults to TextObfuscationMode.RevealLastTyped
    textObfuscationMode = TextObfuscationMode.RevealLastTyped, // or TextObfuscationMode.Hidden
    ...
)
```

!!! note
    Even if `SecureTextField` uses the default `TextObfuscationMode.RevealLastTyped` or is configured explicitly with `RevealLastTyped` or `Hidden`, it can later be changed to `Visible` programmatically.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for references to the text field classes and text obfuscation APIs.
3. Manually evaluate and shortlist the fields for access or verification codes usage.

## Observation

The output should contain a list of locations where text input fields for access or verification codes are used.

## Evaluation

The test case fails if any text input field used for access or verification codes is found to be unmasked. For example, due to the following:

- `TextField` is used
- `SecureTextField` is used but configured with `TextObfuscationMode.Visible`

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `TextField` or `SecureTextField` (for example in custom UI frameworks or game engines).
