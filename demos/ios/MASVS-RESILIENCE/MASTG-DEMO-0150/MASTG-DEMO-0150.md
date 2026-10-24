---
platform: ios
title: References to Storage Integrity Check APIs with radare2
code: [swift]
id: MASTG-DEMO-0150
test: MASTG-TEST-0387
kind: fail
---

## Sample

The sample contains two storage flows, so the difference is visible in the disassembly:

- An **insecure** flow writes a sensitive value to `user_profile.json` in the Documents directory and later reads it back, trusting it without computing or verifying any HMAC or signature.
- A **secure** flow (`storeWithIntegrity`) writes the same data to `user_profile_protected.json` with an appended HMAC-SHA256 and verifies the HMAC when reading it back.

Because the insecure flow stores data the app trusts without any integrity check, the app cannot detect whether that data was modified on disk.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ storage_integrity.r2 }}

{{ run.sh }}

## Observation

The output shows that the app references both storage APIs (`Foundation.Data.write(to:)` and `Foundation.Data(contentsOf:)`) and storage-integrity APIs (`CryptoKit.HMAC.authenticationCode` and `CryptoKit.HMAC.isValidAuthenticationCode`).

Cross-referencing `Data.write(to:)` reveals two distinct storage flows, and the disassembly of each shows how the storage and integrity calls relate:

- **Flow A** (`fcn.0x1000050f8`) calls `Data.write(to:)` and `Data(contentsOf:)` with no HMAC call on that data path.
- **Flow B** (`fcn.0x1000045bc`) calls `HMAC.authenticationCode` before `Data.write(to:)` and `HMAC.isValidAuthenticationCode` after `Data(contentsOf:)`.

{{ output.asm }}

## Evaluation

The test case fails because the app stores data it later trusts without verifying its integrity. Although the binary references HMAC APIs, those references alone do not prove that all stored data is protected: the disassembly shows that flow A writes `user_profile.json` and reads it back with no HMAC computation or verification on that path, so the app cannot detect if that file is tampered with.

For contrast, flow B (`storeWithIntegrity`) is a passing path: it computes an HMAC over the data before writing it and verifies the HMAC after reading it back, so tampering with `user_profile_protected.json` would be detected. The presence of this protected flow is exactly why the references found in step 2 require manual validation: only by inspecting how each storage path uses the integrity APIs can you tell which stored data is actually protected.
