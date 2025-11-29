---
platform: android
title: Incorrectly Preventing Screenshots with SecureFlagPolicy in Compose Dialogs with semgrep
id: MASTG-DEMO-0063
code: [kotlin]
test: MASTG-TEST-0293
---

### Sample

The sample demonstrates how an app allows screenshots/leaks of sensitive content by misusing screenshot prevention APIs in a Jetpack Compose dialog that uses `DialogProperties(securePolicy = SecureFlagPolicy.SecureOff)`.

{{ MastgTest.kt # MastgTest_reversed.java # MastgTestKt_reversed.java }}

### Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed Java code.

{{ ../../../../rules/mastg-android-sensitive-data-in-screenshot.yml }}

{{ run.sh }}

### Observation

The rule has identified one location in `MastgTestKt_reversed.java` where the app sets the `SecureFlagPolicy` in a Jetpack Compose `DialogProperties`.

{{ output.txt }}

### Evaluation

The test fails because `SecureFlagPolicy` is set to `SecureOff`, which doesn't prevent screenshots or screen recordings of the dialog content. The app does not use `FLAG_SECURE` or any other screenshot prevention mechanism.
