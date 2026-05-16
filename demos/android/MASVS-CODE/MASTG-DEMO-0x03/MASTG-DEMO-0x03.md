---
platform: android
title: Static Detection of Unintentionally Exported Internal Components
id: MASTG-DEMO-0x03
code: [xml]
test: MASTG-TEST-0x03
tools: [MASTG-TOOL-0110]
---

## Sample

The manifest below declares an internal activity (`InternalSettingsActivity`) with `android:exported="true"` and an `<intent-filter>`. The activity is intended for use only from within the app, but the declaration makes it reachable by any other app on the device.

{{ AndroidManifest.xml # AndroidManifest_reversed.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the manifest file.

{{ ../../../../rules/mastg-android-custom-intent-filter-intercept.yml }}

{{ run.sh }}

## Observation

The output contains the internal component `org.owasp.mastestapp.InternalSettingsActivity` declared with `android:exported="true"` and an `<intent-filter>` for the custom action `org.owasp.mastestapp.OPEN_INTERNAL_SETTINGS`.

{{ output.txt }}

## Evaluation

The test case fails because the `AndroidManifest.xml` declares `InternalSettingsActivity` — an internal screen — with `android:exported="true"` and an `<intent-filter>`. This combination exposes the component to every other app on the device, so any installed app can launch it directly regardless of the app's intended navigation flow.
