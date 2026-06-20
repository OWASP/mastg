---
platform: ios
title: Storing Data Without File Storage Integrity Checks
code: [swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

## Sample

The sample writes a sensitive value to a file in the app's Documents directory and later reads it back, trusting it without computing or verifying any HMAC or signature. As a result, the app cannot detect whether the stored data was modified.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ storage_integrity.r2 }}

{{ run.sh }}

## Observation

The output shows that the app stores data in a file (`user_profile.json`) but contains no references to file storage integrity APIs such as `CCHmac`, `CC_SHA256`, `CC_SHA512`, or `SecKeyCreateSignature`.

{{ output.asm }}

## Evaluation

The test case fails because the app stores sensitive data on the device but never computes or verifies an HMAC or signature over it. The import table contains no file storage integrity APIs, so the app cannot detect if the stored data has been tampered with before reading it back.
