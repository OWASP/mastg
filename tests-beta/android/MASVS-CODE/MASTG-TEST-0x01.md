---
platform: android
title: Implicit Intents Used for Internal App Communication
id: MASTG-TEST-0x01
type: [dynamic]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0x14]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

Android enables communication between its components through intents, which serve as messaging objects to request actions from other application components. Intents can be explicit, targeting a specific component of a known app, or implicit, where the system identifies the suitable component based on the intent's action, data, or category. When an app uses an implicit intent to reach one of its **own** components, the Android system performs intent resolution across every installed app, not just the originating app. Any other app that declares a matching `<intent-filter>` for the same action becomes a valid target. This is an inappropriate use of implicit intents: communication between an app's own components should always be performed with an explicit target so that the intent cannot be diverted to or observed by another app on the device.

## Steps

1. Install the vulnerable app on the device.
2. Install an attacker app that declares an `<intent-filter>` matching the action the vulnerable app sends.
3. Launch the vulnerable app and trigger the code path that sends the implicit intent.

## Observation

The output should contain evidence that, when the application sends an implicit intent intended for one of its own components, the Android system resolves the intent against every installed app and offers the attacker app as a valid target by displaying an app chooser or by routing the intent directly to the attacker app.

## Evaluation

The test case fails if the app sends an implicit intent intended for one of its own internal components without restricting the target (for example, via `setPackage(...)`, `setComponent(...)`, or a fully qualified class name).
