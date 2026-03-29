---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via File-Based IPC Mechanisms
id: MASTG-TEST-XXXX
type: [dynamic]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-XXXX]
knowledge: [MASTG-KNOW-0020]
---

## Overview

If the app exposes file system-based content providers without proper access restrictions, other apps on the device can use IPC to read sensitive stored data, such as internal files kept in the app sandbox. This can cause unauthorized disclosure of data that is intended to remain private to the application. This test case checks whether file-based content providers can be accessed from outside the app and whether they return sensitive stored data.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the `AndroidManifest.xml` file and identify all `<provider>` components.
3. For each provider, check if `android:exported` is set to `true` (or implicitly exported via `<intent-filter>`) and review applied permissions (`android:permission`, `android:readPermission`, `android:protectionLevel`).
4. Use @MASTG-TECH-0014 to search for file access patterns such as `openFile`, `ParcelFileDescriptor`, and references to internal storage directories in the provider classes.
5. Run @MASTG-TECH-XXXX on the app and look for provider that read files from app storage.

## Observation

The output should include a list of content provider authorities and one or more proof-of-access results indicating that an external caller can read file-based provider URIs, including any sensitive stored data returned, such as the contents of internal application files.

## Evaluation

he test case fails if an external caller is able to access one or more file-based content providers and obtain sensitive stored data from internal/private files without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
