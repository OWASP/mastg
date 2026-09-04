---
platform: android
title: Uses of Broken Hashing Algorithms in MessageDigest with semgrep
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
---

## Sample

The code snippet below shows sample code that hashes data with MD5, SHA-1 (including the `SHA1` alias), and SHA-256.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-broken-hashing-algorithms.yaml }}

{{ run.sh }}

## Observation

The rule has identified several instances in the code file where broken hashing algorithms are used. The specified line numbers can be located in the reverse-engineered code for further investigation and remediation.

{{ output.txt }}

## Evaluation

The test case fails because the app hashes security-relevant data using broken algorithms, specifically MD5 and SHA-1.

Review each of the reported instances.

- Line 43 hashes a password with MD5.
- Line 50 hashes authentication token material with SHA-1.
- Line 57 hashes authentication token material with the `SHA1` JCA alias of SHA-1. This is the same algorithm as line 50; the alias is included because Android code often uses `SHA1` instead of `SHA-1`.
- Line 64 hashes `readme.txt` with MD5. This looks like a checksum of non-sensitive data, so further validation is required to decide whether it is a finding.

Note that line 71 did not trigger the rule because the hash is generated using SHA-256, which is a secure hashing algorithm.

See @MASTG-TEST-0x01 for more information.
