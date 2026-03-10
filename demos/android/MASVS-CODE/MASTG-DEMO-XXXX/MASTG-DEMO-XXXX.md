---
platform: android
title: Local Storage for Input Validation with semgrep
id: MASTG-DEMO-XXXX
tools: [MASTG-TOOL-0110]
code: [kotlin]
test: MASTG-TEST-XXXX
---

### Sample

The code snippet demonstrates the insecure use of `SharedPreferences`, as data is loaded without an integrity check.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-local-storage-input-validation.yml }}

{{ run.sh }}

### Observation

The rule identifies that data is being loaded without being validated.

{{ output.txt }}

### Evaluation

The test fails because the application doesn't use an `HMAC` integrity check when loading sensitive data from `SharedPreferences`.

- Line 23: The rule identifies an insecure initialization of the storage class where HMAC protection is explicitly disabled (`false`).
- Line 35: Another instance of unvalidated data access is detected.
