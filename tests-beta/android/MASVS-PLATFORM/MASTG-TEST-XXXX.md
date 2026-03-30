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

1. Reverse engineer the app using @MASTG-TECH-0013 and extract the `AndroidManifest.xml` with @MASTG-TECH-0117 to identify all `<provider>` components.
2. Review each provider to determine whether it is exported explicitly or implicitly, and verify any applied permissions such as `android:permission`, `android:readPermission`, and related protection levels.
3. Use @MASTG-TECH-0014 and @MASTG-TECH-XXXX to inspect provider code for file-access behavior, including `openFile`, `ParcelFileDescriptor`, and reads from internal app storage.

## Observation

The output should include a list of content provider authorities and one or more proof-of-access results indicating that an external caller can read file-based provider URIs, including any sensitive stored data returned, such as the contents of internal application files.

## Evaluation

The test case fails if an external caller is able to access one or more file-based content providers and obtain sensitive stored data from internal/private files without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
