---
title: Use Non-Caching Input Types for Sensitive Fields
alias: use-non-caching-input-types
id: MASTG-BEST-0019
knowledge: [MASTG-KNOW-0055]
platform: android
refs:
- https://developer.android.com/reference/android/text/InputType
- https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/KeyboardOptions
---

Use non-caching input types for any UI element that handles sensitive information (passwords, passphrases, PINs, recovery phrases, payment data). Password-style input types instruct the on-screen keyboard not to cache or learn from the entered text and disable suggestions and auto-correction. Password variations disable dictionary learning and suggestions in most IMEs and prevent caching of entered secrets.

Combine this practice with broader data leakage controls e.g., @MASTG-BEST-0014 when appropriate.

Recommendations:

- XML layouts (EditText)
    - Passwords: `android:inputType="textPassword"`
    - Numeric PINs: `android:inputType="numberPassword"`
    - Avoid: `textVisiblePassword` and plain `text`/`number` without password variation.

- Programmatic (View-based UI)
    - Passwords: `setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)`
    - Numeric PINs: `setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)`
    - Optionally disable suggestions for non-password sensitive fields: add `InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS`.

- Jetpack Compose
    - Passwords: `KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false)`
    - Numeric PINs: `KeyboardOptions(keyboardType = KeyboardType.NumberPassword, autoCorrect = false)`
    - Also set a visual transformation where appropriate (e.g., `PasswordVisualTransformation()`).

- For sensitive but non-password inputs (e.g., security answers), prefer password variations or at minimum disable suggestions (`TYPE_TEXT_FLAG_NO_SUGGESTIONS`, `autoCorrect = false`).

- Avoid overriding a non-caching type later in code with a generic class type (e.g., setting `TYPE_NUMBER_VARIATION_PASSWORD` and then setting `TYPE_CLASS_NUMBER`).

