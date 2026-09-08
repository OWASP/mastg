---
platform: android
title: Attacker App Registering for Internal Implicit Intent
id: MASTG-DEMO-0140
code: [kotlin, xml]
test: MASTG-TEST-0372
kind: attack
---

## Sample

The following attacker app registers an `<intent-filter>` for the custom action `org.owasp.mastestapp.INTERNAL_ACTION`, used in the @MASTG-DEMO-0136 demo. When Android presents this app as a candidate handler and it is selected, it receives the victim app's intent and displays/logs the received extras.

{{ MastgTest.kt # AndroidManifest.xml }}

Note that this app is not inherently malicious. It illustrates that any app can register for a custom action and be presented to the user as a valid handler. The actual vulnerability lies in the victim app using an implicit intent for internal component communication.

## Steps

1. Use @MASTG-TECH-0005 to install the victim app from @MASTG-DEMO-0136.
2. Use @MASTG-TECH-0005 to install this attacker app on the same device.
3. Launch the victim app and tap **Start**.
4. If Android presents a resolver for `INTERNAL_ACTION`, select this attacker app.
5. Run `run.sh` to capture the intercepted intent details from logcat.

{{ run.sh }}

## Observation

Android presents the attacker app as a candidate for handling `INTERNAL_ACTION`:

<img src="../MASTG-DEMO-0136/implicit-intent-choose-app.png" width="50%" />

Once selected, the attacker app receives the intent and logs the action and extras sent by the victim app:

{{ output.txt }}

## Evaluation

The test case fails because the attacker app receives an implicit intent that the victim app (@MASTG-DEMO-0136) intended for app-internal communication.

The log output confirms that the attacker app handled `org.owasp.mastestapp.INTERNAL_ACTION` and received the extras sent with the intent.

When the victim app dispatches this implicit intent, Android resolves any installed app with a matching `<intent-filter>` as a possible handler. If the attacker app is selected, the intent is delivered outside the victim app even though the flow was intended for `InternalActivity`.

The attacker app therefore controls the receiving component and can read the action and full extras `Bundle`, including values such as `user_id` and `session_token`.
