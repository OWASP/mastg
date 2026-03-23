---
platform: android
title: Dynamic Detection of Implicit Intent Hijacking
id: MASTG-DEMO-XXXA
code: [kotlin]
test: MASTG-TEST-XXXA
profiles: [L1, L2]
---

## Sample

This demo consists of two applications. One which is vulnerable and one which hijacks @MASTG-KNOW-0025 and steals sensitive data from the vulnerable app.

## Vulnerable App

The code snippet below demonstrates the use of an implicit intent which is consumed by the application itself again. This is an improper use of implicit intent as they are generally not used for internal IPC.

{{ MastgTest.kt }}

The component `VulnerableActivity` within the vulnerable app originally intended to process sensitive data, but is exposed via an implicit intent mechanism. Intents targeted towards this component can be hijacked by any app that claims to handle the same action.

{{ VulnerableActivity.kt }}

## Attacker App

The attacker app has an exported activity that includes a corresponding `<intent-filter>` which registers the custom action from the sample app, enabling it to capture the implicit intent sent out by the vulnerable app.

{{ interceptor/IntentInterceptorActivity.kt # interceptor/AndroidManifest.xml }}

## Steps

1. Install the vulnerable app and attacker app on the device using @MASTG-TECH-0004.
2. On the vulnerable app, click on start to start the test.
3. Android system will ask you which app should be used to handle the intent. Choose "IntentInterceptor" in app chooser.

## Observation

The attacker app successfully intercepted the intent containing sensitive extras such as tokens, API keys, and credentials. This confirms that any app declaring a matching `<intent-filter>` can receive these values without restriction.

## Evaluation

The test fails due to the use of an exported activity (VulnerableActivity) that includes an intent filter with a custom action. Combined with the implicit intent in `MastgTest.kt`, this creates a vulnerable pattern where sensitive data is transmitted to an untrusted receiver.
