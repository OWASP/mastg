---
platform: android
title: Common Uses of Insecure Random APIs
id: MASTG-DEMO-0007
code: [java]
test: MASTG-TEST-0204
---

## Sample

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-random-apis-insufficient-entropy.yml }}

{{ run.sh }}

## Observation

The rule has identified three instances in the code file where an insecure random number generator is used. The specified line numbers can be located in the original code for further investigation and remediation.

{{ output.txt }}

## Evaluation

The test case fails because the app generates random numbers using insecure APIs in security-relevant contexts.

Review each of the reported instances.

- Line 22 uses `new Random().nextDouble()` to generate random numbers for security purposes, in this case for generating authentication tokens.
- Line 23 uses `Math.random()`, but it's unclear how the value is used in the rest of the app. Review where it's used to ensure that the random number is not used in a security-relevant context.
- Line 27 is part of the password generation loop, which is a security-critical operation.

Note that line 31 did not trigger the rule because the random number is generated using `SecureRandom` which is a secure random number generator.
