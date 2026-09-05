---
platform: android
title: Root Detection Logic in Native Layer with Insufficient Obfuscation
id: MASTG-DEMO-0133
code: [kotlin, cpp]
test: MASTG-TEST-0369
kind: fail
---

## Sample

The sample code demonstrates an Android app that performs a root detection routine implemented in a native library.

The Java/Kotlin layer loads `librootcheck.so` and calls `findRootArtifactPath()`, a native function that checks common `su` binary paths. Regardless of whether this protection mechanism is sufficient to detect root in a real-world scenario, the purpose of this demo is to show that the obfuscation applied to the native layer is not enough to prevent an attacker from reverse engineering the root detection logic with reasonable effort.

{{ MastgTest.kt # native-root-check.cpp # CMakeLists.txt }}

The app is not obfuscated at the Java/Kotlin layer, and the native library is included in the APK as-is without any protection. See @MASTG-KNOW-0033 for reference on common obfuscation techniques.

## Steps

1. Use `unzip` to extract the native libraries. In this case, we extracted `lib/arm64-v8a/librootcheck.so` from the APK.
2. Use @MASTG-TOOL-0028 to disassemble `librootcheck.so`.
3. Use @MASTG-TECH-0024 to inspect the disassembled native code and search for strings referencing typical root indicators, such as `su`.

{{ root_detection.r2 # run.sh }}

> **Note:** Filtering strings with `izz~su` is a simplification used here for demonstration purposes. In a real-world scenario, the set of root-related strings and paths to look for is broader. Refer to @MASTG-KNOW-0027 for a comprehensive list of root detection indicators.

## Observation

The output contains all the strings containing `su` paths found in `.rodata`.

{{ output.txt }}

## Evaluation

The test case fails because the native library can be reverse engineered with little effort and the security-relevant root detection logic remains easy to identify.

The output reveals the paths `/system/bin/su`, `/sbin/su`, and `/system/xbin/su` stored as plaintext in `.rodata`, showing that the root detection indicators are not encoded or encrypted. Finding these strings alone is sufficient to demonstrate the test failure: they tell an attacker exactly what artifacts the library is looking for.

> **Note:** For the purpose of this demo we stop here. In a real assessment you would go further and trace where these strings are used — for example by following cross-references or pivoting through file-access imports like `access()` — to fully map the detection logic and understand how the result is returned to the Java/Kotlin layer.

**Additional Context**:

If we use @MASTG-TECH-0017 to decompile the app, we'll also see that the Java/Kotlin layer isn't obfuscated, and the code helps correlate the native code with the security-relevant logic. For example, in `MastgTest_reversed.java`, lines 17, 29 to 37, 48 to 54, and 57 to 58 show that the Java/Kotlin layer loads `librootcheck.so`, calls the native `findRootArtifactPath()` function, logs the detected path, and closes the app through `finishAffinity()` when the native code reports a match.

{{ MastgTest_reversed.java }}
