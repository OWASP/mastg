---
platform: android
title: Testing for Overlay Attack Protection in Layouts
id: MASTG-TEST-XXXX
type: [static]
weakness: MASWE-0056
profiles: [L2]
---

## Overview

If the app does not implement overlay attack protection on sensitive views, a malicious app can draw an overlay on top of the legitimate app to trick the user into performing unintended actions. This can lead to unauthorized permissions being granted, unintended purchases, or credential theft through UI redressing (also known as "Tapjacking"). This test checks whether the app properly protects sensitive UI elements against overlay attacks by using the `android:filterTouchesWhenObscured` attribute.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0017) and identify sensitive views such as buttons or input fields for entering credentials.
2. Review the XML layout files containing these sensitive views and check for the presence of the `android:filterTouchesWhenObscured` attribute set to `true`.

## Observation

The output should contain a list of sensitive views in XML layout files that do not have the `android:filterTouchesWhenObscured` attribute set to `true`, indicating potential tapjacking vulnerabilities.

## Evaluation

The test case fails if sensitive views (e.g., buttons for granting permissions, confirming purchases, or input fields for entering credentials) within the application's XML layout files do not include the `android:filterTouchesWhenObscured="true"` attribute or have it set to `false`.