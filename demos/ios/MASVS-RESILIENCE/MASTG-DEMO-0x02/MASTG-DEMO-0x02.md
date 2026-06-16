---
platform: ios
title: Storing Data Without File Storage Integrity Checks
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
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

The output shows that the import table references the hash function `CC_SHA256`, but it belongs to an unrelated routine and is never used to protect the stored data; there are no references to file storage integrity APIs such as `CCHmac` or `SecKeyCreateSignature` guarding the file. As a result, the app reads its stored data back without verifying any HMAC or signature over it.

{{ output.asm }}

## Evaluation

The test case fails because the app stores sensitive data on the device but never computes or verifies an HMAC or signature over it before reading it back. Although the import table references `CC_SHA256`, it is only used by an unrelated routine and is never wired into the read/write path of the stored file, so the app cannot detect if that data has been tampered with.
