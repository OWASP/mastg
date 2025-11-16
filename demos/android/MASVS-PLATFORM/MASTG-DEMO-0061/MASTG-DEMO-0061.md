---
platform: android
title: Uses of FLAG_SECURE with semgrep
id: MASTG-DEMO-0061
code: [kotlin]
test: MASTG-TEST-0291
---

### Sample

The sample uses the `addFlags` method to set the `FLAG_SECURE` window flag on an activity that displays sensitive data.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed java code.

{{ ../../../../rules/mastg-android-sensitive-data-in-screenshot.yml }}

{{ run.sh }}

### Observation

The rule has identified one location in the code file where the app has set the `FLAG_SECURE` window flag using the `addFlags` method.

{{ output.txt }}

### Evaluation

This test passes because the app used the `addFlags` method to set the `FLAG_SECURE` window flag on an activity that displays sensitive data.
