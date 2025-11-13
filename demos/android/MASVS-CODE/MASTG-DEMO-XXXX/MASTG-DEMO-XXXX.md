---
platform: android
title: Overlay attack via malicious app overlay
id: MASTG-DEMO-XXXX
code: [kotlin]
test: MASTG-TEST-XXXX
status: new
---

### Sample

The following layout implements a vulnerable component that creates an always-on-top overlay window (using WindowManager with TYPE_APPLICATION_OVERLAY / SYSTEM_ALERT_WINDOW) which can be used to intercept touches or obscure UI of other apps.

{{ MastgTest.kt # MastgTest_reversed.kt }}

### Steps


{{ ../../../../rules/mastg-android-overlay-attack.yml }}

{{ run.sh }}

### Observation

The rule has identified an activity that is sensitive to overlay attacks because it handles runtime permissions but does not set `android:filterTouchesWhenObscured="true"` in its manifest declaration, allowing a malicious app to draw an overlay on top of it and intercept user touches.

{{ output.txt }}

### Evaluation

The test fails because the application does not implement adequate defenses against overlay attacks (tapjacking) on a sensitive screen. Specifically, the activity that initiates a potentially dangerous action (like requesting a runtime permission or processing a user login) does not include the security attribute `android:filterTouchesWhenObscured="true"` in the <activity> tag within the `AndroidManifest.xml`. By omitting this attribute, the application allows touch events to be processed even when another window (an overlay) is obscuring the activity's UI, enabling an attacker to trick the user into granting permissions or initiating unintended actions. The absence of this attribute makes the application vulnerable to UI redressing.
