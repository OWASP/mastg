---
platform: android
title: Testing for Exported Activities With Custom Intent Filter Actions
id: MASTG-TEST-XXXB
type: [static]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0x14]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

Android enables communication between its components through intents, which serve as messaging objects to request actions from other application components. Intents can be explicit, targeting a specific component, or implicit, where the system identifies the suitable component based on the intent's action, data, or category. When a component such as an Activity is declared with `android:exported="true"` and includes an `<intent-filter>` with a custom action, it becomes accessible to external applications. If an app sends an implicit intent with sensitive data, and that intent can be intercepted by a malicious app, it results in a serious information disclosure vulnerability. Static analysis helps detect both the unsafe intent-sending code and the misconfigured exported component.

## Steps

1. Run @MASTG-TECH-0014 on the `AndroidManifest.xml` file.

## Observation

The output should contain a activity with `android:exported="true"` and an intent filter that matches the custom action `org.owasp.mastestapp.PROCESS_SENSITIVE_DATA`.

## Evaluation

The test fails because the `AndroidManifest.xml` declares an exported activity with an `<intent-filter>` that uses a custom action. This configuration allows the component to be triggered by any external application using an implicit intent with the matching action.
