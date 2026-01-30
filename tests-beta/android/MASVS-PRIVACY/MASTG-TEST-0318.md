---
platform: android
title: References to SDK APIs Known to Handle Sensitive User Data
id: MASTG-TEST-0318
type: [static]
weakness: MASWE-0112
profiles: [P]
---

## Overview

This test verifies whether an app uses SDK (third-party library) APIs known to handle sensitive user data (e.g., as defined in [Google Play's Data safety section](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en#types&zippy=%2Cdata-types) or the relevant privacy regulations).

As a prerequisite, we need to identify the SDK API methods it uses as entry points for data collection by reviewing the library's documentation or codebase. For example, [Google Analytics for Firebase](https://firebase.google.com/docs/analytics) in its class `FirebaseAnalytics` provides methods such as [`setUserId`](https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics#setUserId(java.lang.String)), [`setUserProperty`](https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics#setUserProperty(java.lang.String,%20java.lang.String)), and [`logEvent`](https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics#logEvent(java.lang.String,%20android.os.Bundle)) that can be used to collect user data.

> Note: This test detects only **potential** sensitive user data handling. For **confirming** that actual user data are being shared, please refer to @MASTG-TEST-0319.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for uses of these methods where sensitive user data may be passed to the SDK.

## Observation

The output should list the locations where SDK methods are called.

## Evaluation

The test case fails if you can find the use of these SDK methods in the app code, indicating that the app is sharing sensitive user data with the third-party SDK. If no such references are found, the test case passes.
