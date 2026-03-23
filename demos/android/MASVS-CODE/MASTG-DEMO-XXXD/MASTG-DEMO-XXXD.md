---
platform: android
title: Arbitrary File Read via Implicit Intent Hijacking
id: MASTG-DEMO-XXXD
code: [kotlin]
test: MASTG-TEST-XXXD
profiles: [L1, L2]
---

## Sample

The vulnerable app fires an implicit intent with a custom action `REQUEST_FILE` to request content from another app. Since no target component is specified, any app on the device can register a matching intent-filter and intercept this intent. The attacker app returns a `file://` URI pointing to the victim's internal SharedPreferences, which the victim copies to its world-readable external cache directory.

{{ MastgTest.kt AndroidManifest.xml }}

## Attacker App

The attacker app registers an exported activity with a high-priority intent-filter matching the victim's custom action. When selected, it returns a `file://` URI pointing to the victim's internal storage (`/data/data/org.owasp.mastestapp/shared_prefs/session.xml`).

{{ attacker/EvilContentActivity.kt # attacker/AndroidManifest.xml }}

## Steps

1. Install the vulnerable app on the device using @MASTG-TECH-0004.
2. Install the attacker app on the device using @MASTG-TECH-0004.
3. Launch the vulnerable app and click "Start" to trigger the `REQUEST_FILE` implicit intent.
4. When the resolver dialog appears showing both the legitimate handler and the attacker app ("FileProvider"), select the attacker app.

## Observation

The attacker app intercepts the implicit intent and returns a `file://` URI pointing to the victim's internal SharedPreferences file containing sensitive tokens and credentials. The victim app copies this file to its external cache directory (`/sdcard/Android/data/org.owasp.mastestapp/cache/tmp`), making it world-readable.

The output file shows:

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="refresh_token">rt_8f14e45f-ceea-367f-a27f-abc123def456</string>
    <string name="user_email">admin@example.com</string>
    <string name="api_key">sk-live-1234567890abcdef</string>
    <string name="session_id">sess_a1b2c3d4e5f6</string>
    <string name="auth_token">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW4ifQ.secret</string>
    <string name="credit_card_last4">4242</string>
</map>
```

## Evaluation

The test case fails because the app uses an implicit intent without specifying a target component, and copies the returned URI content to a world-readable external cache directory without validating the URI origin or path. An attacker app with a matching intent-filter can return a `file://` URI pointing to any file within the victim's internal storage, achieving arbitrary file read.
