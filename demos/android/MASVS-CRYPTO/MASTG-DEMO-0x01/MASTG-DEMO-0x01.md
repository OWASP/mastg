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

Review each of the reported instances:

- Line 43 (FAIL): Hashes a password with MD5. In addition to being broken for collision resistance, raw MD5 is vulnerable to rapid offline dictionary and rainbow table attacks.
- Line 50 (FAIL): Hashes authentication token material with SHA-1.
- Line 57 (FAIL): Hashes authentication token material with the `SHA1` JCA alias of SHA-1. This is the same algorithm as line 50; the alias is included because Android code often uses `SHA1` instead of `SHA-1`.
- Line 64 (Requires Contextual Validation): Hashes `readme.txt` using MD5 for a checksum. While flagged by static analysis, using MD5 for non-security checksums (where collision resistance is not a security requirement) is generally acceptable; contextual validation is required rather than treating it as an unconditional failure.

Note that line 71 did not trigger the rule because the hash is generated using SHA-256 on ordinary file data (`readme.txt`), which passes this algorithm check. Note that while raw SHA-256 is appropriate for data integrity and hashing non-sensitive inputs, it is [unsuitable for password storage](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html) (which requires salted, memory-hard key derivation functions such as Argon2id, scrypt, or PBKDF2).

See @MASTG-TEST-0x01 for more information.
