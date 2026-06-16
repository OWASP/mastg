---
title: Internal Component Unintentionally Exported
platform: android
id: MASTG-TEST-0373
type: [static, config]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0056]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

Activities intended for internal use within an application should not be exported. An exported activity can be launched by any other application on the device, potentially exposing sensitive functionality or data to unauthorized parties. While some activities must be exported (like the main launcher activity), internal activities should have `android:exported="false"` in the `AndroidManifest.xml`. If an activity has an `<intent-filter>`, it is exported by default on older Android versions, and must be explicitly marked as exported or not on newer versions. This test identifies exported activities and evaluates if they should have been kept internal.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the `AndroidManifest.xml`.
3. Use @MASTG-TECH-0150 to identify exported activities (for example, `android:exported="true"`).
4. For each exported activity, determine whether external access is intended (e.g., launcher, deep link handler) or if it performs internal-only tasks.

## Observation

The output should contain a list of all exported activities found in the manifest.

## Evaluation

The test case fails if any activity intended for internal use is exported (for example, `android:exported="true"`).
