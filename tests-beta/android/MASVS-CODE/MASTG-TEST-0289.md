---
title: Deep Link Intent Filter Missing android:autoVerify
platform: android
id: MASTG-TEST-0289
type: [static]
weakness: MASWE-0058
profiles: [L1, L2]
---

### Overview

This vulnerability occurs when an application defines a deep link intent filter in its `AndroidManifest.xml` without the `android:autoVerify="true"` attribute. Without this attribute, Android App Links verification is not enforced, do the android operating system cannot confirm that the app legitimately owns the declared domain. As a result, a malicious app can register the same intent filter and intercept deep links, leading to phishing, credential theft, or hijacking of user actions.

### Steps

Run a static analysis tool such as @MASTG-TOOL-0110 on the `AndroidManifest.xml` to detect deep link intent filters that are missing the `android:autoVerify="true"` attribute.

### Observation

The output shows a `<intent-filter>` that define deep links but do not include the `android:autoVerify="true"` attribute.

### Evaluation

The test fails because the application does not enforce App Links verification. Without `android:autoVerify="true"`, malicious apps can intercept and handle the app’s deep links, redirecting users to attacker-controlled content.
