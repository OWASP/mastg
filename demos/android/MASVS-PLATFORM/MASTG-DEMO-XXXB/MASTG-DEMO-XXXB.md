---
platform: android
title: Deep Link Intent Filter Missing android:autoVerify with semgrep
id: MASTG-DEMO-XXXB
code: [kotlin]
test: MASTG-TEST-XXXB
status: new
---

## Sample

The following is a sample `AndroidManifest.xml` snippet that defines a deep link intent filter without the `android:autoVerify="true"` attribute.

{{ AndroidManifest_reversed.xml }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample manifest.

{{ ../../../../rules/mastg-android-autoverify-missing.yml }}

{{ run.sh }}

## Observation

The rule has identified that the deep link intent filter is missing the `android:autoVerify="true"` attribute.

{{ output.txt }}

## Evaluation

The test fails because the app does not enforce Android App Links verification. Without `android:autoVerify="true"`, malicious apps may intercept the app's deep links, leading to phishing or hijacking attacks.
