---
platform: android
title: Arbitrary File Read via Implicit Intent Hijacking
id: MASTG-DEMO-XXXD
code: [kotlin]
tools: [MASTG-TOOL-0004]
test: MASTG-TEST-XXXD
---

## Sample

The vulnerable app fires an implicit intent with a custom action `REQUEST_FILE` to request content from another app. Since no target component is specified, any app on the device can register a matching intent-filter and intercept this intent. The attacker app returns a `file://` URI pointing to the victim's internal SharedPreferences, which the victim copies to its world-readable external cache directory.

{{ MastgTest.kt AndroidManifest.xml }}

## Attacker App

The attacker app registers an exported activity with a high-priority intent-filter matching the victim's custom action. When selected, it returns a `file://` URI pointing to the victim's internal storage (`/data/data/org.owasp.mastestapp/shared_prefs/session.xml`).

{{ attacker/EvilContentActivity.kt # attacker/AndroidManifest.xml }}

## Steps

1. Install the vulnerable and attacker app on the device.
2. Launch the vulnerable app and click "Start" to trigger the `REQUEST_FILE` implicit intent.
3. When the resolver dialog appears showing both the legitimate handler and the attacker app ("FileProvider"), select the attacker app.

## Observation

The output contains the internal SharedPreferences file containing sensitive tokens and credentials. The victim app copies this file to its external cache directory (`/sdcard/Android/data/org.owasp.mastestapp/cache/tmp`), making it world-readable.

{{ output.txt }}

## Evaluation

The test fails because:

- The vulnerable app sends an implicit intent with action `REQUEST_FILE` without specifying a target component, allowing the attacker app to intercept it by registering a matching `<intent-filter>`.
- The attacker app returned a `file://` URI pointing to the victim's internal SharedPreferences (`session.xml`), which contained sensitive data including `auth_token`, `api_key`, and `session_id`.
- The victim app copied the content referenced by the returned URI to the external cache directory without validating the URI scheme, origin, or path, making the stolen file world-readable.
