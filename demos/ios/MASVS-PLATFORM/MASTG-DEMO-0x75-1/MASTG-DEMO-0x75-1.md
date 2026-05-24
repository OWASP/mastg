---
platform: ios
title: Use of Deprecated Custom URL Scheme Handler Methods
code: [swift]
id: MASTG-DEMO-0x75-1
test: MASTG-TEST-0x75-1
kind: fail
status: draft
---

## Sample

The following sample demonstrates an app that registers and handles a custom URL scheme using the deprecated `application:handleOpenURL:` delegate method. This method was deprecated in iOS 9.0 in favor of `application:openURL:options:`, which provides additional context about the request (such as the source application) via the `options` dictionary.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the script.

{{ url_scheme_deprecated.r2 # run.sh }}

## Observation

The output should contain references to deprecated URL scheme handler methods (`handleOpenURL:`) used in the app binary.

{{ output.txt }}

## Evaluation

The test case fails because the app implements the deprecated `application:handleOpenURL:` method. This method lacks the `options` dictionary available in `application:openURL:options:`, which prevents the app from performing source-based validation to determine which caller triggered the URL scheme.
