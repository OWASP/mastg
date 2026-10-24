---
platform: ios
title: Use of the Deprecated UIWebView
code: [swift]
id: MASTG-DEMO-0094
test: MASTG-TEST-0331
kind: fail
---

## Sample

The following sample demonstrates the use of [`UIWebView`](https://developer.apple.com/documentation/uikit/uiwebview).

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the script.

{{ uiwebview.r2 # run.sh }}

## Observation

The output contains a reference to [`UIWebView`](https://developer.apple.com/documentation/uikit/uiwebview) used from the `sym.MASTestApp.MastgTest.mastg.completion__1` function.

{{ output.txt }}

## Evaluation

The test fails because the app uses the deprecated `UIWebView` class.
