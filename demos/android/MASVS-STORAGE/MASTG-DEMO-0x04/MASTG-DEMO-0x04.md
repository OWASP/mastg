---
platform: android
title: Hardcoded API Keys in Code and Resources
id: MASTG-DEMO-0x04
code: [kotlin]
test: MASTG-TEST-0x03
kind: fail
---

## Sample

This sample embeds credentials in the two places they most often appear in an Android app package: as constants in the code, and as string resources in `res/values/strings.xml`.

The credentials below are not real. They match the format of the providers they imitate so that the detection patterns are exercised realistically.

{{ MastgTest.kt # MastgTest_reversed.java # strings.xml }}

## Steps

Let's run @MASTG-TOOL-0110 against the reversed Java code and the string resources.

{{ ../../../../rules/mastg-android-hardcoded-api-keys.yml }}

{{ run.sh }}

## Observation

The rule identifies four locations across both files:

- Two credentials matching well-known provider formats in the decompiled code (a Google API key and an AWS access key ID).
- Two credential-named string resources.

The endpoint URL and the non-credential resource on the surrounding lines are not reported.

{{ output.txt }}

## Evaluation

The test fails because credentials that authenticate the app to third-party services are present in the app package and can be recovered by anyone who downloads it.

Note that the key in `strings.xml` is reported independently of the one in the code. Resource values are not affected by code obfuscation, so stripping or renaming the code constant alone would leave the key exposed. In the built APK these values end up in the resource table, merged with the string resources contributed by the app's libraries.

!!! warning "Pattern matching alone under-reports"
    The sample also contains a fifth credential, `s3cr3t-not-a-real-value-9f2b`, which none of the rules report. In the Kotlin source it is a local variable named `clientSecret`, but the compiler inlines the constant, so the decompiled code contains only the bare literal inside a `StringBuilder.append` call with no identifier attached to it. The credential is still fully recoverable from the package.

    This is why the test does not rely on automated patterns alone: identifier-based rules operate on names that often do not survive compilation, and format-based rules only cover providers whose credentials have a recognizable shape. Review the extracted strings (@MASTG-TECH-0019) as well.

See @MASTG-BEST-0x02 for mitigations, including delivering keys over the air after app and device attestation and proxying third-party calls through a backend.
