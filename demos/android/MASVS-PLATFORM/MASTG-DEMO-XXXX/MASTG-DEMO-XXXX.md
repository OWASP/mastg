---
platform: android
title: Testing Overlay Attack using Semgrep
id: MASTG-DEMO-XXXX
code: [xml]
test: MASTG-TEST-XXXX
tools: [semgrep]
---

## Sample

The snippet below shows a layout file that explicitly sets `android:filterTouchesWhenObscured="false"` on the root `LinearLayout`, leaving its UI elements including a `Button` that triggers a sensitive action unprotected against overlay attacks (tapjacking).

{{ activity_vulnerable_login.xml }}

## Steps

Let's run the @MASTG-TOOL-0110 rules against sample layout.

{{ ../../../../rules/mastg-android-overlay-attack.yml }}

{{ run.sh }}

## Observation

The rule has identified an activity that is sensitive to overlay attacks because it handles runtime permissions but does not set `android:filterTouchesWhenObscured="true"` in its manifest declaration, allowing a malicious app to draw an overlay on top of it and intercept user touches.

{{ output.txt }}

## Evaluation

The test fails because two sensitive UI elements in the layout are missing `android:filterTouchesWhenObscured="true"`.

- Line 12: The `<EditText>` used to capture a username or account number. Without touch filtering, an overlay could be placed on top of this field to silently intercept credential input.
- Line 21: The `<Button>` labeled "Execute Sensitive Action". A malicious overlay can trick the user into tapping this button unintentionally, leading to unauthorized actions such as confirming a payment or granting a permission.
