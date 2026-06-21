---
title: Use of Unverified App Links
platform: android
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0058
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0019]
best-practices: [MASTG-BEST-0x01]
---

## Overview

The Android version the app runs on also influences the risk. Before Android 12 (API level 31), if the app has any [non-verifiable links](https://developer.android.com/training/app-links/verify-android-applinks#fix-errors) (e.g., missing `autoVerify`, invalid Digital Asset Links file, or custom URL schemes), the system may skip verification for all Android App Links declared by that app—leaving even correctly configured App Links unprotected. Starting with Android 12 (API level 31), a generic web intent resolves to the user's default browser unless the target app is approved for the specific domain, reducing but not eliminating the attack surface.

This vulnerability occurs when a deep link intent filter in `AndroidManifest.xml` lacks the `android:autoVerify="true"` attribute. Without verification, Android cannot confirm the app's ownership of the declared domain. A malicious app could register the same intent filter and intercept deep links, enabling phishing, credential theft, or hijacking of user actions.

Real-world exploitation has been publicly documented:

- [HackerOne #1372667 - Able to steal bearer token from deep link](https://hackerone.com/reports/1372667)
- [HackerOne #401793 - Insecure deeplink leads to sensitive information disclosure](https://hackerone.com/reports/401793)
- [HackerOne #583987 - Android app deeplink leads to CSRF in follow action](https://hackerone.com/reports/583987)
- [HackerOne #341908 - XSS via Direct Message deeplinks](https://hackerone.com/reports/341908)

## Steps

1. Run @MASTG-TECH-0x01 on the app to look for deep links with unverified app link.

## Observation

The output should contain `<intent-filter>` elements that define deep links but do not include the `android:autoVerify="true"` attribute.

## Evaluation

The test case fails if you identify any deep link `<intent-filter>` elements without the `android:autoVerify="true"` attribute, because app Links verification is not enforced and malicious apps can hijack deep links and redirect users to attacker-controlled content.
