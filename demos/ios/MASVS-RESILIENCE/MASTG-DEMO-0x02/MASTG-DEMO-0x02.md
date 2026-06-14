---
platform: ios
title: File Storage Integrity Check using CCHmac with r2
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: pass
status: draft
---

## Sample

The sample code demonstrates file storage integrity checking by using `CCHmac` with SHA-256 to compute an HMAC over stored data and verify it before use.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ storage_integrity.r2 }}

{{ run.sh }}

## Observation

The output shows a reference to `CCHmac` in the binary's import table along with its cross-reference, indicating a call from the app's file storage integrity check routine.

{{ output.asm }}

## Evaluation

The test passes because the app contains references to file storage integrity check APIs (`CCHmac`).
