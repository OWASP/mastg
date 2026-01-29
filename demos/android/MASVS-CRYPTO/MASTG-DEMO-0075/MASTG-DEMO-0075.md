---
platform: android
title: Uses of Explicit Security Providers in Cryptographic APIs with semgrep
id: MASTG-DEMO-0075
code: [kotlin]
test: MASTG-TEST-0312
---

## Sample

The code snippet below shows sample code that demonstrates both insecure and secure usage of security providers in `getInstance` calls.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-hardcoded-security-provider.yaml }}

{{ run.sh }}

## Observation

The rule has identified instances in the code file where a security provider is explicitly specified. The specified line numbers can be located in the reverse-engineered code for further investigation and remediation.

{{ output.txt }}

## Evaluation

Review each of the reported instances:

- Line 71 uses the deprecated "BC" (BouncyCastle) provider with `Cipher.getInstance`. This is deprecated since Android 9 and removed in Android 12.
- Line 85 uses a custom provider "CustomProvider" which may not be regularly updated or patched.

All other cases are correctly handled and do not trigger the rule.
