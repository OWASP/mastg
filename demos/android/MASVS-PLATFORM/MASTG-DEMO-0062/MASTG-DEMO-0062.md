---
platform: android
title: Enabling Screenshots in Recents via setRecentsScreenshotEnabled with semgrep
id: MASTG-DEMO-0062
code: [kotlin]
test: MASTG-TEST-0292
---

### Sample

The sample demonstrates how an app does not prevent screenshots/leaks of sensitive content in the Recents screen via calls to `setRecentsScreenshotEnabled(true)`.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed Java code.

{{ ../../../../rules/mastg-android-sensitive-data-in-screenshot.yml }}

{{ run.sh }}

### Observation

The rule has identified one location in the code file where the app calls `setRecentsScreenshotEnabled()`.

{{ output.txt }}

### Evaluation

The test fails because the app calls `setRecentsScreenshotEnabled(true)`, which allows screenshots in recents and may lead to sensitive data exposure. The app does not use `FLAG_SECURE` or any other screenshot prevention mechanism.
