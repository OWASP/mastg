---
platform: android
title: Dynamic Detection of Implicit Intent Hijacking
id: MASTG-DEMO-XXXA
code: [kotlin]
test: MASTG-TEST-XXXA
---

## Sample

The demo includes a vulnerable application and a second application that demonstrates how the vulnerability can be exploited to access sensitive data.

## Vulnerable App

The code snippet below demonstrates the use of an implicit intent which is consumed by the application itself again. This is an improper use of implicit intent as they are generally not used for internal IPC.

{{ MastgTest.kt }}

The component `VulnerableActivity` within the vulnerable app originally intended to process sensitive data, but is exposed via an implicit intent mechanism. Intents targeted towards this component can be hijacked by any app that claims to handle the same action.

{{ VulnerableActivity.kt }}

## Attacker App

The attacker app has an exported activity that includes a corresponding `<intent-filter>` which registers the custom action from the sample app, enabling it to capture the implicit intent sent out by the vulnerable app.

{{ interceptor/IntentInterceptorActivity.kt # interceptor/AndroidManifest.xml }}

## Steps

1. Install the vulnerable app and attacker app on the device.
2. On the vulnerable app, click on start to start the test.
3. Android system will ask you which app should be used to handle the intent. Choose "IntentInterceptor" in app chooser.

## Observation

The output contains evidence that the attacker app successfully intercepted the intent containing sensitive extras such as tokens, API keys, and credentials to display on the attacker app. This confirms that any app declaring a matching `<intent-filter>` can receive these values without restriction.

## Evaluation

The test fails because:

- `VulnerableActivity` is declared with `android:exported="true"` and an `<intent-filter>` for the custom action `org.owasp.mastestapp.PROCESS_SENSITIVE_DATA`, making it reachable by any installed app.
- `MastgTest.kt` sends an implicit intent carrying sensitive extras (`sensitive_token`, `user_credentials`, `api_key`) without restricting the target component.
- The attacker app intercepted the intent and received all sensitive extras, confirming the component is exploitable by any app registering the same action.
