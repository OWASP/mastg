---
platform: ios
title: References to UIActivityViewController Initialization Without excludedActivityTypes
code: [swift]
id: MASTG-DEMO-0x71
test: MASTG-TEST-0x71
kind: fail
status: draft
---

## Sample

The following sample demonstrates a [`UIActivityViewController`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller) initialized with sensitive data items and without configuring [`excludedActivityTypes`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/excludedactivitytypes). All system activity types are therefore available when the share sheet is presented.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the script.

{{ uiactivity.r2 # run.sh }}

## Observation

The output shows a reference to `initWithActivityItems:applicationActivities:` called from `sym.MASTestApp.MastgTest.mastg.completion__1`. There is no reference to `excludedActivityTypes`.

{{ output.txt }}

## Evaluation

The test case fails because the app initializes a `UIActivityViewController` with sensitive data (an account token and a private URL) but does not set `excludedActivityTypes`. This means all system activity types, including AirDrop, Mail, Messages, and social network posting, are available when the share sheet is presented.
