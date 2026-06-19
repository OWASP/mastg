---
platform: android
title: Root Detection Logic in Java/Kotlin Layer Only Protected by Identifier Renaming
id: MASTG-DEMO-0132
code: [kotlin]
test: MASTG-TEST-0368
kind: fail
---

## Sample

The sample code demonstrates an Android app that performs a root detection routine implemented in the Java/Kotlin layer. The app checks whether well-known root manager packages are installed on the device.

{{ MastgTest.kt # AndroidManifest.xml }}

Regardless of whether this protection mechanism is sufficient to detect root in a real-world scenario, the purpose of this demo is to show that the obfuscation applied to the Java/Kotlin layer is not enough to prevent an attacker from reverse engineering the root detection logic with reasonable effort.

The app is obfuscated with R8, which applies identifier renaming and some code shrinking. However, no string encryption or control flow obfuscation is applied. See @MASTG-KNOW-0033 for reference on common obfuscation techniques.

{{ build.gradle.kts.build # proguard-rules.pro }}

## Steps

1. Use @MASTG-TOOL-0018 to decompile the app.

## Observation

The output contains the reverse-engineered Java/Kotlin code.

## Evaluation

The test case fails because when performing reverse engineering on the minified Java/Kotlin layer it is possible to identify and understand the security-relevant root detection logic with little effort.

In `MastgTest_reversed.java`, lines 21, 24, and 32 show that R8 shortened member names (`f7310a`, `f7311b`, and `a()`), so some identifier renaming was clearly applied. However, lines 29, 47, 50, 52, 59, and 64 still reveal the monitored package names, the `PackageManager` lookups, the root detection message, and the `finishAffinity()` call, as the strings are not encrypted.

{{ MastgTest_reversed.java }}

This means that the reverse-engineered code still makes it straightforward to identify that the app is checking for root manager packages and closing the app when one is found, despite having some obfuscation applied in the form of identifier renaming.

**Additional Context**:

In `AndroidManifest_reversed.xml`, lines 14 to 16 show the package visibility queries for the monitored root manager packages.

{{ AndroidManifest_reversed.xml }}
