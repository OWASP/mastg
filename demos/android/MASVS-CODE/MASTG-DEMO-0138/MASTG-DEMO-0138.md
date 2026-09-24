---
id: MASTG-DEMO-0138
title: Leaking Sensitive Information via Implicit Intents
platform: android
code: [kotlin]
test: MASTG-TEST-0374
kind: fail
---

## Sample

This demo uses the same sample app as @MASTG-DEMO-0136. The app sends `user_id` and `session_token` (which are considered sensitive information) as extras in an implicit intent.

Because the intent is implicit, any application that registers an activity with an `<intent-filter>` that matches the action can receive it and extract those values.

{{ ../MASTG-DEMO-0136/MastgTest.kt # ../MASTG-DEMO-0136/MastgTest_reversed.java }}

## Steps

Let's use @MASTG-TECH-0014 with an @MASTG-TOOL-0110 rule to scan the reverse-engineered code for implicit intents that carry sensitive extras.

{{ ../../../../rules/mastg-android-implicit-intent-leaking-extras.yml }}

{{ run.sh }}

## Observation

The output shows one `Intent` dispatch with extras:

- Intent creation: `new Intent()`.
- Action assignment: `implicitIntent.setAction("org.owasp.mastestapp.INTERNAL_ACTION")`.
- Extra keys and values: `user_id` with value `12345` and `session_token` with value `abcde-fghij-12345`.
- Dispatch API: `this.context.startActivity(implicitIntent)`.

{{ output.txt }}

## Evaluation

The test case fails because the app attaches sensitive extras (`user_id` and `session_token`) to an implicit intent and dispatches it without constraining the recipient.

The reported dispatch does not name a target package or component and does not verify the selected handler's identity before dispatch. The reported block does not show a target-defining call such as `setPackage`, `setClass`, `setClassName`, `setComponent`, or an explicit `Intent(context, Class)` constructor. Any app that declares a matching `<intent-filter>` for `org.owasp.mastestapp.INTERNAL_ACTION` can become a candidate and receive the full extras `Bundle`.
