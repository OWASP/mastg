
---
platform: android
title: Testing Overlay attack using semgrep
id: MASTG-DEMO-XXXX
code: [xml]
test: MASTG-TEST-XXXX
tools: [semgrep]
status: new
---

## Sample

The following layout implements a vulnerable component that creates an always-on-top overlay window (using WindowManager with TYPE_APPLICATION_OVERLAY / SYSTEM_ALERT_WINDOW) which can be used to intercept touches or obscure UI of other apps. This specific example demonstrates a `Button` that is vulnerable because it does not explicitly set `android:filterTouchesWhenObscured="true"`.

{{ activity_vulnerable_login.xml }}

## Steps

Let's run the @MASTG-TOOL-0110 rules against sample layout.

{{ ../../../../rules/mastg-android-overlay-attack.yml }}

{{ run.sh }}

## Observation

The rule has identified an activity that is sensitive to overlay attacks because it handles runtime permissions but does not set `android:filterTouchesWhenObscured="true"` in its manifest declaration, allowing a malicious app to draw an overlay on top of it and intercept user touches.

{{ output.txt }}

## Evaluation

The test fails because the application does not implement adequate defenses against overlay attacks (tapjacking) on a sensitive UI element. Specifically, a `Button` that could initiate a potentially dangerous action does not include the security attribute `android:filterTouchesWhenObscured="true"`. By omitting this attribute, the application allows touch events to be processed even when another window (an overlay) is obscuring the UI element, enabling an attacker to trick the user into granting permissions or initiating unintended actions. The absence of this attribute makes the application vulnerable to UI redressing.