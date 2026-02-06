---
platform: android
title: Unvalidated URL from Deep Link Loaded in WebView with semgrep
id: MASTG-DEMO-XXXA
code: [kotlin]
test: MASTG-TEST-XXXA
status: new
---

## Sample

The following is a sample code file that contains a function to handle a deep link, which insecurely loads a URL into a WebView.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-unvalidated-deeplink-data.yml }}

{{ run.sh }}

## Observation

The output file shows usage of dangerous data flow from a source `getQueryParameter` to a sink `loadUrl`.

{{ output.txt }}

## Evaluation

The test fails because the app loads a user-controllable URL from a deep link directly into a WebView without validation.
