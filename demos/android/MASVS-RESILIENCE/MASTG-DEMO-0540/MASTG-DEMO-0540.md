---
platform: android
title: Uses of Root Detection Techniques with Semgrep
id: MASTG-DEMO-0540
code: [kotlin, java]
test: MASTG-TEST-0501
tools: [MASTG-TOOL-0110]
---

### Sample

This sample demonstrates common root detection techniques used in Android applications, including:

- Checking for su binary in common locations
- Detecting root management packages (SuperSU, Magisk, etc.)
- Identifying test-keys builds indicating custom ROMs
- Reading system properties that may indicate root or debugging

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run @MASTG-TOOL-0110 with our custom rules to detect root detection implementations.

{{ ../../../../rules/mastg-android-root-detection.yaml }}

{{ run.sh }}

### Observation

The output shows all locations where root detection checks are implemented in the code.

{{ output.txt }}

### Evaluation

The test passes because the output shows multiple root detection implementations:

- Line 80: File existence checks for su binaries and root-related files
- Line 107: PackageManager checks for root management apps
- Line 118: Build.TAGS check for test-keys indicating custom ROM
- Line 140: Runtime.exec() and getprop calls to read system properties

These findings confirm that the app implements multiple layers of root detection, which is considered a good security practice for resilience.
