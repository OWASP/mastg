---
platform: ios
title: Uses of Insecure Random Number Generation with frida-trace
code: [swift]
id: MASTG-DEMO-0074
test: MASTG-TEST-0311
---

## Sample

This demo uses the same sample code as in @MASTG-DEMO-0073. It demonstrates the use of insecure random number generation using various APIs.

{{ ../MASTG-DEMO-0073/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056)
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn your app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C`

{{ run.sh }}

## Observation

{{ output.txt }}

This output contains both insecure and secure APIs. For this test case the interesting calls are:

- `rand` and `srand`, which expose the insecure libc PRNG.
- `drand48`, which also uses an insecure linear congruential generator.

## Evaluation

The test fails because insecure PRNGs are used in a security relevant context. See the evaluation section in @MASTG-DEMO-0073 for more details.

The same output also shows calls to secure sources such as `SecRandomCopyBytes`, `CCRandomGenerateBytes`, `SystemRandomNumberGenerator`, and the Swift standard library's `FixedWidthInteger.random` implementation. These are present in the sample for contrast, but they are not the reason the test fails.
