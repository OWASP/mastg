---
platform: android
title: Dynamic Detection of Sensitive Data Exposure via Implicit Intents
id: MASTG-DEMO-0x02
code: [kotlin]
test: MASTG-TEST-0x02
---

## Sample

The demo includes a vulnerable application and a second application that demonstrates how the vulnerability can be exploited to access sensitive data.

## Vulnerable App

The code snippet below shows an app sending an implicit intent that carries sensitive extras (a token, user credentials, and an API key). Because the intent is implicit, any app on the device that declares a matching `<intent-filter>` is a valid recipient and will receive these extras.

{{ MastgTest.kt }}

The component `VulnerableActivity` within the vulnerable app originally intended to process sensitive data can be hijacked by any app that claims to handle the same action.

{{ VulnerableActivity.kt }}

## Attacker App

The attacker app declares an exported activity with an `<intent-filter>` that registers the same custom action. When the system resolves the vulnerable app's implicit intent, the attacker app appears as a valid target and receives the sensitive extras unchanged.

{{ interceptor/IntentInterceptorActivity.kt # interceptor/AndroidManifest.xml }}

## Steps

1. Install the vulnerable and attacker apps on the device (@MASTG-TECH-0005).
2. Open the vulnerable app and click the **Start** button.
3. The Android system will ask you which app should be used to handle the intent. Choose **IntentInterceptor** in the app chooser.

## Observation

The attacker app's screen displays the intercepted extras (token, credentials, API key) that were sent by the vulnerable app. This confirms that the sensitive payload travelled outside of the originating app to a third-party app on the device.

## Evaluation

The test case fails because `MastgTest.kt` attaches sensitive extras (`sensitive_token`, `user_credentials`, `api_key`) to an implicit intent without restricting the target component. The attacker app receives the implicit intent and reads the extras directly, demonstrating that any app registering a matching `<intent-filter>` can read the sensitive data.
