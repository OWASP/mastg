---
platform: android
title: Dynamic Detection of Implicit Intents Used for Internal App Communication
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
---

## Sample

The demo includes a vulnerable application and a second application that demonstrates how the vulnerability can be exploited.

## Vulnerable App

The code snippet below shows an app sending an implicit intent to one of its components. This is an improper use of implicit intents, which are not meant for communication within the same app because another app's matching `<intent-filter>` can intercept it.

{{ MastgTest.kt }}

The component `VulnerableActivity` within the vulnerable app, originally intended to be reached from within the app, can be invoked by any app that claims to handle the same action.

{{ VulnerableActivity.kt }}

## Attacker App

The attacker app has an exported activity that includes a corresponding `<intent-filter>` which registers the same custom action, enabling it to capture the implicit intent sent out by the vulnerable app.

{{ interceptor/IntentInterceptorActivity.kt # interceptor/AndroidManifest.xml }}

## Steps

1. Install the vulnerable and attacker apps on the device (@MASTG-TECH-0005).
2. Open the vulnerable app and click the **Start** button.
3. The Android system will ask you which app should be used to handle the intent. Choose **IntentInterceptor** in the app chooser.

## Observation

The output shows that the Android system resolves the vulnerable app's implicit intent against every installed app and offers the attacker app as a valid target. The intent intended for an internal component is routed to a third-party app, confirming that the implicit intent leaks outside of the originating app.

## Evaluation

The test case fails because `MastgTest.kt` sends an implicit intent to reach one of its own components without restricting the target (no `setPackage(...)`, `setComponent(...)`, or class-qualified target). The Android system therefore resolves the intent across every installed app and the same intent can be handled by any app declaring a matching `<intent-filter>`.
