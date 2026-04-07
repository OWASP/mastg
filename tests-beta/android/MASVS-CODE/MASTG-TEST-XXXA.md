---
platform: android
title: Implicit Intent to Intercept Internal App Components
id: MASTG-TEST-XXXA
type: [dynamic]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0x14]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

Android enables communication between its components through intents, which serve as messaging objects to request actions from other application components. Intents can be explicit, targeting a specific component, or implicit, where the system identifies the suitable component based on the intent's action, data, or category. When you declare an internal component, like an Activity, with `android:exported="true"` and link it to an `<intent-filter>`, it becomes available to external applications via implicit intents. This can introduce security vulnerabilities if the component handles sensitive tasks or accepts input from the intent without proper validation. An attacker might create a malicious app to activate these exported components, potentially altering application behavior or accessing sensitive data.

## Steps

1. Install the vulnerable app and attacker app on the device.
2. Launch the vulnerable app to trigger the implicit intent from vulnerable app.

## Observation

The output should contain a attacker application be able to successfully launch the VulnerableActivity using a crafted intent and receive sensitive information.

## Evaluation

The test fails if exported activity being accessible via an implicit intent.
