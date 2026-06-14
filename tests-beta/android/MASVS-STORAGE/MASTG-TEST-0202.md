---
platform: android
title: References to APIs and Permissions for Accessing External Storage
id: MASTG-TEST-0202
apis: [Environment#getExternalStoragePublicDirectory, Environment#getExternalStorageDirectory, Environment#getExternalFilesDir, Environment#getExternalCacheDir, MediaStore, WRITE_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE]
type: [static, code, manual]
weakness: MASWE-0007
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0042]
---

## Overview

This test uses static analysis to look for uses of APIs allowing an app to write to locations that are shared with other apps (@MASTG-TEST-0001) such as the External Storage APIs or the `MediaStore` API as well as the relevant Android manifest storage-related permissions.

Some APIs used to write to shared storage include `getExternalStoragePublicDirectory`, `getExternalStorageDirectory`, `getExternalFilesDir`, or `MediaStore`. Permissions include `WRITE_EXTERNAL_STORAGE`, and `MANAGE_EXTERNAL_STORAGE`. See @MASTG-KNOW-0042 for more information on these APIs and permissions.

!!! note
    This static test is great for identifying all code locations where the app is writing data to shared storage. However, it does not provide the actual data being written, and in some cases, the actual path in the device storage where the data is being written. Therefore, it is recommended to combine this test with others that take a dynamic approach, as this will provide a more complete view of the data being written to shared storage.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.
3. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
4. Use @MASTG-TECH-0126 to obtain the relevant permissions.

## Observation

The output should contain a list of APIs and storage-related permissions used to write to shared storage and their code locations.

## Evaluation

The test case fails if all of the following apply:

- the app has the proper permissions declared in the Android manifest (e.g. `WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`, etc.)
- the app uses APIs that write to shared storage (e.g. `getExternalStoragePublicDirectory`, `getExternalStorageDirectory`, `getExternalFilesDir`, `getExternalCacheDir`, `MediaStore`, etc.)
- the data being written to shared storage is sensitive and not encrypted.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the data is sensitive:

- Determine whether the data written to shared storage includes sensitive information (e.g., personal data, credentials, or tokens).
- Determine whether the data is stored without encryption.
