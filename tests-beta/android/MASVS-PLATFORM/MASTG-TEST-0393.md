---
title: Use of Unverified App Links
platform: android
id: MASTG-TEST-0393
type: [static, config]
weakness: MASWE-0058
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0019]
best-practices: [MASTG-BEST-0070]
---

## Overview

Android App Links are `http`/`https` deep links that the OS verifies against a website's [Digital Asset Links](https://developers.google.com/digital-asset-links/v1/getting-started) file before routing them to the app. An app opts into this verification by setting `android:autoVerify="true"` on the `<intent-filter>` that declares the deep link in the `AndroidManifest.xml`.

When a deep link `<intent-filter>` declares an `http`/`https` `<data>` scheme (together with the `android.intent.action.VIEW` action and the `android.intent.category.BROWSABLE` category) but is missing the `android:autoVerify="true"` attribute, Android cannot confirm the app's ownership of the declared domain. A malicious app can register the same intent filter and intercept the deep links, enabling phishing, credential theft, or hijacking of user actions.

The Android version the app runs on also influences the risk. Before Android 12 (API level 31), if the app has any [non-verifiable links](https://developer.android.com/training/app-links/verify-android-applinks#fix-errors) (e.g., missing `autoVerify`, an invalid Digital Asset Links file, or custom URL schemes), the system may skip verification for all Android App Links declared by that app—leaving even correctly configured App Links unprotected. Starting with Android 12, a generic web intent resolves to the user's default browser unless the target app is approved for the specific domain, reducing but not eliminating the attack surface.

This test checks whether the app declares `http`/`https` deep links without enabling App Links verification.

Real-world exploitation has been publicly documented:

- [HackerOne #1372667 - Able to steal bearer token from deep link](https://hackerone.com/reports/1372667)
- [HackerOne #401793 - Insecure deeplink leads to sensitive information disclosure](https://hackerone.com/reports/401793)
- [HackerOne #583987 - Android app deeplink leads to CSRF in follow action](https://hackerone.com/reports/583987)
- [HackerOne #341908 - XSS via Direct Message deeplinks](https://hackerone.com/reports/341908)

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0172 to enumerate the deep links declared in the manifest.

## Observation

The output should contain `<intent-filter>` elements that declare `http`/`https` deep links but do not include the `android:autoVerify="true"` attribute.

## Evaluation

The test case fails if you identify any deep link `<intent-filter>` element that declares an `http`/`https` `<data>` scheme without the `android:autoVerify="true"` attribute, because App Links verification is not enforced and malicious apps can hijack the deep links and redirect users to attacker-controlled content.

Note that the presence of `android:autoVerify="true"` is necessary but not sufficient: the website association must also succeed. Use @MASTG-TECH-0174 to confirm the declared domains are actually verified, since a misconfigured Digital Asset Links file leaves the App Links unverified even when the attribute is set.
