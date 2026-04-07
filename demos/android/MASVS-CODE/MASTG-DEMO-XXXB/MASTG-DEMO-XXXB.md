---
platform: android
title: Static Detection of External Intent Misuse for Internal IPC
id: MASTG-DEMO-XXXB
code: [xml]
test: MASTG-TEST-XXXB
tools: [MASTG-TOOL-0110]
---

## Sample

The manifest snippet outlines an exported activity featuring an `<intent-filter>` with a unique action. This allows the component to be reachable by any application on the device that registers the identical intent action, which could allow a malicious app to capture such intents.

{{ AndroidManifest.xml # AndroidManifest_reversed.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the manifest file and code.

{{ ../../../../rules/mastg-android-custom-intent-filter-intercept.yml }}

{{ run.sh }}

## Observation

The output contains the exported component `org.owasp.mastestapp.VulnerableActivity`, that declares an `<intent-filter>` with a custom action `org.owasp.mastestapp.PROCESS_SENSITIVE_DATA`

## Evaluation

The test fails because this configuration allows any third-party app to register the same action and receive the implicit intent, enabling potential hijacking of sensitive data.

- Marked as `android:exported="true"`.
- Declares an `<intent-filter>` with a custom action `org.owasp.mastestapp.PROCESS_SENSITIVE_DATA`.
