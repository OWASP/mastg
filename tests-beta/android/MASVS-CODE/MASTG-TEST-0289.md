---
title: Deep Link Intent Filter Missing android:autoVerify
platform: android
id: MASTG-TEST-0289
type: [static]
weakness: MASWE-0058
profiles: [L1, L2]
---

### Overview

This vulnerability occurs when a deep link intent filter in `AndroidManifest.xml` lacks the `android:autoVerify="true"` attribute. Without verification, Android cannot confirm the app's ownership of the declared domain. A malicious app could register the same intent filter and intercept deep links, enabling phishing, credential theft, or hijacking of user actions.

### Steps

Run a static analysis tool such as @MASTG-TOOL-0110 on the `AndroidManifest.xml` to detect deep link intent filters that are missing the `android:autoVerify="true"` attribute.

### Observation

The output shows a `<intent-filter>` that define deep links but do not include the `android:autoVerify="true"` attribute.

### Evaluation

The test fails as App Links verification is not enforced. Without `android:autoVerify="true"`, malicious apps can hijack deep links and redirect users to attacker-controlled content.
