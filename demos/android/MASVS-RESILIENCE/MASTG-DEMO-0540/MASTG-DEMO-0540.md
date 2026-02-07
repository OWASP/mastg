---
platform: android
title: Uses of Root Detection Techniques with Semgrep
id: MASTG-DEMO-0540
code: [kotlin]
test: MASTG-TEST-0501
tools: [MASTG-TOOL-0110]
kind: pass
---

## Sample

This sample demonstrates common root detection techniques used in Android applications, including:

- Checking for the `su` binary in common locations
- Detecting root management packages using `PackageManager`
- Identifying `test-keys` builds indicating custom ROMs
- Reading system properties that may indicate root or debugging

To ensure that the requests using `PackageManager.getPackageInfo` work as expected, the app includes the relevant `<queries>` element in the `AndroidManifest.xml`.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml }}

## Steps

Let's run @MASTG-TOOL-0110 with the following rule:

{{ ../../../../rules/mastg-android-root-detection.yaml }}

{{ run.sh }}

## Observation

The output shows all locations where root detection checks are implemented in the code.

{{ output.txt }}

## Evaluation

The test passes because the output shows multiple root detection implementations:

- Line 61: File existence checks for `su` binaries and root-related files
- Line 82: `PackageManager` checks for root management apps
- Line 100: `Build.TAGS` check for `test-keys` indicating custom ROM
- Line 129: `Runtime.exec()` and `getprop` calls to read system properties
