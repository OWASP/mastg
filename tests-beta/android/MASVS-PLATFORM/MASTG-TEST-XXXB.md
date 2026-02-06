---
title: Use of Unverified App Links
platform: android
id: MASTG-TEST-0293
type: [static]
weakness: MASWE-0058
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0019]
---

## Overview

This vulnerability occurs when a deep link intent filter in `AndroidManifest.xml` lacks the `android:autoVerify="true"` attribute. Without verification, Android cannot confirm the app's ownership of the declared domain. A malicious app could register the same intent filter and intercept deep links, enabling phishing, credential theft, or hijacking of user actions.

## Steps

1. Run @MASTG-TECH-XXXX on the app to look for deep links with unverified app link.

## Observation

The output shows a `<intent-filter>` that define deep links but do not include the `android:autoVerify="true"` attribute.

## Evaluation

The test fails as App Links verification is not enforced. Without `android:autoVerify="true"`, malicious apps can hijack deep links and redirect users to attacker-controlled content.
