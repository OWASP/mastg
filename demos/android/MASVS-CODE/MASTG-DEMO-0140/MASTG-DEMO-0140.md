---
platform: android
title: Attacker App Registering for Internal Implicit Intent
id: MASTG-DEMO-0140
code: [kotlin, xml]
test: MASTG-TEST-0372
kind: info
---

## Sample

The following attacker app registers an `<intent-filter>` for the custom action `org.owasp.mastestapp.INTERNAL_ACTION`, which the victim app in @MASTG-DEMO-0136 sends as an implicit intent. Because the victim uses an implicit intent, the Android system will present a chooser dialog when multiple apps handle the same action, allowing this attacker app to intercept the intent.

{{ MastgTest.kt # AndroidManifest.xml }}

You can use this app to demonstrate the vulnerability shown in @MASTG-DEMO-0136: install both apps on the same device, launch the victim app, and tap **Start**. The system will present a chooser dialog listing both apps as candidates for handling `INTERNAL_ACTION`.

Note that this app is not inherently malicious. It simply illustrates that any app can register for a custom action and be presented to the user as a valid handler. The actual vulnerability lies in the victim app using an implicit intent for internal component communication.

## Steps

Let's run our @MASTG-TOOL-0110 rule against the manifest to detect the registration for the custom action.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The rule detected the custom action registration in the manifest:

{{ output.txt }}

## Evaluation

The finding confirms that this app declares an `<intent-filter>` for `org.owasp.mastestapp.INTERNAL_ACTION`. This isn't a vulnerability in this app itself; it shows that the app has the capability to intercept implicit intents targeting that action. The actual vulnerability lies in the victim app (@MASTG-DEMO-0136) that uses an implicit intent for internal component communication.

When assessing an app for implicit intent vulnerabilities, the threat model should include third-party apps that register for the same custom actions.
