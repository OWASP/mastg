---
platform: android
title: SafeBrowsing Disabled Detection with grep
id: MASTG-DEMO-0x01
code: [xml]
test: MASTG-TEST-0x02
---

## Sample

The following AndroidManifest.xml sample demonstrates an app that has explicitly disabled SafeBrowsing for WebViews by setting the `android.webkit.WebView.EnableSafeBrowsing` meta-data to `false`.

{{ AndroidManifest.xml }}

## Steps

Search for the SafeBrowsing configuration in the AndroidManifest.xml using grep.

{{ run.sh }}

## Observation

The output shows the meta-data element that disables SafeBrowsing.

{{ output.txt }}

## Evaluation

The test case fails because the `android.webkit.WebView.EnableSafeBrowsing` meta-data is present with `android:value="false"`. This explicitly disables SafeBrowsing protection for all WebViews in the app, removing an important security layer that protects users from known malicious URLs.
