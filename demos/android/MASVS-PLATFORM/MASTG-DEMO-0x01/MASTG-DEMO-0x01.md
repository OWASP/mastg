---
platform: android
title: Jetpack Compose UI Elements Without isSensitiveData with semgrep
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0403
---

## Sample

The sample extends the MASTestApp's `MainActivity.kt` with a Jetpack Compose payment form, shown in a dialog when you tap **Start**. Compose apps mark UI elements as sensitive with the `isSensitiveData` semantics property instead of the `View` APIs (@MASTG-KNOW-0143):

1. **Username field:** isn't marked with `isSensitiveData`.
2. **Password field:** uses `SecureTextField`, which masks the password, but isn't marked with `isSensitiveData`.
3. **Card number field:** uses `Modifier.semantics { isSensitiveData = true }`.
4. **Confirm Payment button:** isn't marked with `isSensitiveData`.
5. **Cancel button:** isn't marked with `isSensitiveData`. It doesn't handle sensitive data, so it doesn't need the protection.

Tapping **Confirm Payment** or **Cancel** closes the dialog and shows "Payment confirmed." or "Payment cancelled." in the app.

{{ MainActivity.kt # MainActivityKt_reversed.java # ComposableSingletons$MainActivityKt_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reverse-engineered code.

{{ ../../../../rules/mastg-android-accessibility-data-sensitive.yaml }}

{{ run.sh }}

## Observation

The rule reports the call to `SemanticsPropertiesKt.setSensitiveData`, which is what the Kotlin compiler generates for the `isSensitiveData = true` assignment. Because the `semantics` lambda doesn't capture any variables, the compiler places it in `ComposableSingletons$MainActivityKt` instead of `MainActivityKt`.

{{ output.txt }}

The only finding is the `setSensitiveData(semantics, true)` call at line 296, in the `semantics` lambda that the card number field (`TextField`, line 226) passes to `SemanticsModifierKt.semantics$default`.

The fields are in `ComposableSingletons$MainActivityKt_reversed.java`. The **Confirm Payment** and **Cancel** buttons are in `MainActivityKt_reversed.java`, inside the `PaymentDialog` function, because their click handlers capture the `onResult` callback.

## Evaluation

The test case fails because three UI elements that handle sensitive data aren't marked with `isSensitiveData`:

- The **Username** field (`ComposableSingletons$MainActivityKt_reversed.java`, line 209) doesn't appear in the output because its modifier has no `semantics` block that sets `isSensitiveData`.
- The **Password** field (`ComposableSingletons$MainActivityKt_reversed.java`, line 210) doesn't appear in the output either. `SecureTextField` masks the password and marks the field as a password field, but it doesn't set `isSensitiveData`, so accessibility services that aren't accessibility tools can still access it.
- The **Confirm Payment** button (`MainActivityKt_reversed.java`, line 427) doesn't appear in the output, even though it triggers a sensitive action.

The **Card number** field (line 226) is protected, as shown by the call to `setSensitiveData` with the value `true` at line 296. The **Cancel** button (`MainActivityKt_reversed.java`, line 463) isn't protected, which is expected because it doesn't handle sensitive data.

As with `View`-based UIs, the output only lists the elements for which the app sets the property. Elements without it, such as the username field, the password field, and the payment button, don't appear, so you have to find them by reviewing the reverse-engineered code. Note that each call to `setSensitiveData` is in a separate lambda method, so you need to follow the lambda back to the `semantics$default` call to identify the UI element it belongs to.

This detection relies on the name of the Compose library function. The sample is built without obfuscation. In release builds that obfuscate the bundled Compose library code, `SemanticsPropertiesKt.setSensitiveData` may be renamed or inlined, and the rule won't recognize it.
