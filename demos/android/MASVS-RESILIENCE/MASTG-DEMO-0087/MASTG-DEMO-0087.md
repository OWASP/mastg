---
platform: android
title: Uses of Root Detection Techniques with Semgrep
id: MASTG-DEMO-0087
code: [kotlin, xml]
test: MASTG-TEST-0324
kind: pass
---

## Sample

This sample demonstrates common root detection techniques used in Android applications, including:

- Checking for the `su` binary in common locations
- Checking for the `su` binary using the `which` command
- Detecting root management packages using `PackageManager`
- Identifying `test-keys` builds indicating the use of a custom ROM signed with the [AOSP default keys](https://source.android.com/docs/core/ota/sign_builds#release-keys)
- Reading system properties that may indicate root or debugging

To ensure that the queries using `PackageManager.getPackageInfo` work as expected, the app includes the relevant `<queries>` element in the `AndroidManifest.xml`.

The app also encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore when none of the checks reports a root indicator. When one of them does, the app skips that operation and reports it. In both cases, the app displays the results of every check.

!!! note
    The sample displays individual check results, writes them and the app's reaction to the system log, and uses descriptive method names to make the detection logic easy to follow. This detail is included for learning, not as an example of production behavior. In a production app, detection diagnostics should stay internal rather than appear in the UI or logs accessible to users or third parties. Exposing them can help an attacker understand the checks, reverse engineer the app's response, and defeat those checks.

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

- `MastgTest_reversed.java` line 135: File existence checks for `su` binaries and root-related files
- `MastgTest_reversed.java` line 154: `which su` command execution
- `MastgTest_reversed.java` line 213: `PackageManager` checks for root management apps
- `MastgTest_reversed.java` line 231: `Build.TAGS` check for `test-keys` indicating custom ROM
- `MastgTest_reversed.java` line 260: `Runtime.exec()` and `getprop` calls to read system properties
