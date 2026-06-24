---
id: MASTG-DEMO-0136
title: Internal Activity Communication via Implicit Intent
platform: android
code: [kotlin, xml]
test: MASTG-TEST-0372
kind: fail
---

## Sample

The following sample app attempts to start `InternalActivity` for app-internal communication by sending an implicit intent with the custom action `org.owasp.mastestapp.INTERNAL_ACTION`. This approach is insecure because another app can register a matching `<intent-filter>` and become a candidate during Android's intent resolution. For more details on the intent resolution mechanism, see @MASTG-KNOW-0025.

The intent in this sample is implicit because it relies on `setAction` without explicitly defining the target package or component. For the secure alternative, see @MASTG-BEST-0056.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml }}

!!! note
    @MASTG-DEMO-0140 provides an example attacker app that declares a matching `<intent-filter>` for `org.owasp.mastestapp.INTERNAL_ACTION`. It demonstrates how another app can become a candidate for handling this sample app's implicit intent.

## Steps

Let's use @MASTG-TECH-0014 with an @MASTG-TOOL-0110 rule to scan the reverse-engineered code for implicit intents used for internal communication.

{{ ../../../../rules/mastg-android-implicit-intent-internal-communication.yml }}

{{ run.sh }}

## Observation

The output shows one `Intent` dispatch:

- Intent creation: `new Intent()`.
- Action assignment: `implicitIntent.setAction("org.owasp.mastestapp.INTERNAL_ACTION")`.
- Extras added before dispatch: `user_id` and `session_token`.
- Dispatch API: `this.context.startActivity(implicitIntent)`.

{{ output.txt }}

## Evaluation

The test case fails because the app uses an implicit intent (`org.owasp.mastestapp.INTERNAL_ACTION`) for app-internal communication with `InternalActivity`.

The action is app-specific and the manifest declares `InternalActivity` as the component intended to handle it, but the reported dispatch does not name that component or restrict the target package. The reported block does not show a target-defining call such as `setPackage`, `setClass`, `setClassName`, `setComponent`, or an explicit `Intent(context, Class)` constructor before dispatch. Another app can declare a matching `<intent-filter>` and become a candidate during Android intent resolution.
