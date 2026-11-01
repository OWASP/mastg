---
platform: android
title: Jetpack Compose UI Elements Without isSensitiveData with semgrep
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0403
---

## Sample

The sample extends the MASTestApp's `MainActivity.kt` with a Jetpack Compose payment form, shown in a dialog when you tap **Start**. Compose apps mark UI elements as sensitive with the `isSensitiveData` semantics property instead of the `View` APIs (@MASTG-KNOW-0143):

1. **Username field:** explicitly not marked as sensitive with `Modifier.semantics { isSensitiveData = false }`. It also uses `.sensitiveContent(isContentSensitive = true)` to protect content during [screen sharing](https://developer.android.com/about/versions/15/behavior-changes-all#screenshare_protection) on Android 15 (API level 35) and higher, independently of accessibility; the default system screen recorder is exempt. This protection is unrelated to accessibility services.
2. **Password field:** uses `SecureTextField`, which masks the password, but isn't marked with `isSensitiveData`.
3. **Card number field:** uses `Modifier.semantics { isSensitiveData = true }`.
4. **Confirm Payment button:** isn't marked with `isSensitiveData`.
5. **Cancel button:** isn't marked with `isSensitiveData`. It doesn't handle sensitive data, so it doesn't need the protection.

Tapping **Confirm Payment** or **Cancel** closes the dialog and shows "Payment confirmed." or "Payment cancelled." in the app.

<img src="Images/Demos/MASTG-DEMO-0x01/demo-compose.png" width="50%" />

{{ MainActivity.kt # MainActivityKt_reversed.java # ComposableSingletons$MainActivityKt_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reverse-engineered code.

{{ ../../../../rules/mastg-android-accessibility-data-sensitive.yaml }}

{{ run.sh }}

## Observation

The rule reports two calls to `SemanticsPropertiesKt.setSensitiveData`, which is what the Kotlin compiler generates for the `isSensitiveData = false/true` assignments. Because the `semantics` lambda doesn't capture any variables, the compiler places it in `ComposableSingletons$MainActivityKt` instead of `MainActivityKt`.

{{ output.txt }}

The two findings are:

- The `setSensitiveData(semantics, false)` call at line 278, in the `semantics` lambda that the username field (`TextField`, line 225) passes to `SemanticsModifierKt.semantics$default`.
- The `setSensitiveData(semantics, true)` call at line 319, in the `semantics` lambda that the card number field (`TextField`, line 242) passes to `SemanticsModifierKt.semantics$default`.

The fields are in `ComposableSingletons$MainActivityKt_reversed.java`. The **Confirm Payment** and **Cancel** buttons are in `MainActivityKt_reversed.java`, inside the `PaymentDialog` function, because their click handlers capture the `onResult` callback.

## Evaluation

The test case fails because three UI elements that handle sensitive data aren't marked as sensitive with `isSensitiveData`:

- The **Username** field (`ComposableSingletons$MainActivityKt_reversed.java`, line 225) explicitly sets `isSensitiveData` to `false`, as reported at line 278.
- The **Password** field (`ComposableSingletons$MainActivityKt_reversed.java`, line 226) doesn't appear in the output. `SecureTextField` masks the password and marks the field as a password field, but it doesn't set `isSensitiveData`, so accessibility services that aren't accessibility tools can still access it.
- The **Confirm Payment** button (`MainActivityKt_reversed.java`, line 427) doesn't appear in the output, even though it triggers a sensitive action.

The **Card number** field (line 242) is protected, as shown by the call to `setSensitiveData` with the value `true` at line 319. The **Cancel** button (`MainActivityKt_reversed.java`, line 463) isn't protected, which is expected because it doesn't handle sensitive data.

As with `View`-based UIs, the output only lists the elements for which the app sets the property. Elements without it, such as the password field and the payment button, don't appear, so you have to find them by reviewing the reverse-engineered code. Note that each call to `setSensitiveData` is in a separate lambda method, so you need to follow the lambda back to the `semantics$default` call to identify the UI element it belongs to.

This detection relies on the name of the Compose library function. The sample is built without obfuscation. In release builds that obfuscate the bundled Compose library code, `SemanticsPropertiesKt.setSensitiveData` may be renamed or inlined, and the rule won't recognize it.
