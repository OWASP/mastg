---
platform: android
title: App Exposing Access and Verification Codes in Text Input Fields
id: MASTG-DEMO-0079
code: [kotlin]
test: MASTG-TEST-0316
---

## Sample

The following sample demonstrates a pair of username and password input fields in Compose.

{{ MainActivity.kt # MainActivityKt_reversed.java # ComposableSingletons$MainActivityKt_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the Kotlin code.

{{ ../../../../rules/mastg-android-input-field-usage.yml }}

{{ run.sh }}

## Observation

The rule detected one instance of `SecureTextField` and one of `TextField`.

{{ output.txt }}

## Evaluation

After reviewing the decompiled code at the location specified in the output (file and line number), we can conclude that the test fails because the `SecureTextField`, found in line 143, is used with `textObfuscationMode` set to `TextObfuscationMode.Visible` (in Java this appears as `SecureTextFieldKt.m3241SecureTextFieldXvU6IwQ(..., TextObfuscationMode.Companion.m1459getVisiblevTwcZD0(),...)`).

We also conclude that the `TextField` instance (line 142) is a false positive, as it's used for a username field and not a password field. We conclude this by trailing back its label parameter, which is set to "Username": its seventh (label) parameter calls `m10512getLambda2$app_debug()` function, which returns `f61lambda2` instance, which in turn creates the Text label composable `TextKt.m3574TextNvy7gAk("Username", ...`.
