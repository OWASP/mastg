---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
id: MASTG-DEMO-BXXX
code: [kotlin]
tools: [drozer]
status: new
kind: pass
---

## Sample

The following sample demonstrates an Android application that prevents exposure of sensitive stored data via IPC by protecting exported content providers.

The application defines two exported content providers:

- `CredentialProvider`: accesses an internal SQLite database.
- `FileLeakProvider`: serves files from the application’s private `filesDir`.

The providers are protected with a signature-level permission and enforce runtime verification that the calling package is signed with the same certificate. The file provider additionally validates canonical paths to prevent traversal.

{{ MastgTest.kt }}

{{ AndroidManifest.xml }}

---

## Steps

1. Install and launch the application on a device or emulator.
2. Initialize sample data so the database and internal file are created.
3. Enumerate the application’s exported content providers.
4. Attempt to query the database-backed provider from an external context.
5. Attempt to read an internal file via the file-based provider from an external context.

## Observation

The external caller can enumerate the exported content providers, but attempts to query the database provider or read internal files fail due to enforced access restrictions. Provider metadata indicates a signature-level read/write permission, and runtime verification prevents callers that are not signed with the same certificate.

{{ output.txt }}

## Evaluation

The test case passes because the exported content providers enforce appropriate access restrictions, including signature-level permissions and runtime caller validation.

- The exported providers do not allow unauthorized external callers to access the IPC entry points.
- Signature-level permission enforcement restricts access to apps signed with the same certificate.
- Runtime caller validation blocks requests from callers that are not signed with the same certificate.
- Sensitive stored data such as credential records and internal file contents cannot be retrieved through IPC by unauthorized apps.
