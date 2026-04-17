---
platform: android
title: References to SQL Injection in Content Providers
id: MASTG-TEST-0x07-3
type: [dynamic]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x07]
knowledge: [MASTG-KNOW-0020]
---

## Overview

If the app exposes file system-based content providers without proper access restrictions, other apps on the device can use IPC to read sensitive stored data, such as internal files kept in the app sandbox. This can cause unauthorized disclosure of data that is intended to remain private to the application. This test case checks whether file-based content providers can be accessed from outside the app and whether they return sensitive stored data.

## Steps

1. Reverse engineer the app using @MASTG-TECH-0013 and extract the `AndroidManifest.xml` with @MASTG-TECH-0117 to identify all `<provider>` components.
2. Review each provider to determine whether it is exported explicitly or implicitly, and verify any applied permissions such as `android:permission`, `android:readPermission`, and related protection levels.
3. Use @MASTG-TECH-0014 and @MASTG-TECH-0x07-2 to query the identified providers from outside the app and test file-based provider URIs to determine whether an external caller can access internal app files or other sensitive stored data.

## Observation

The output should contain a list of content provider authorities and one or more proof-of-access results indicating that an external caller can read file-based provider URIs, including any sensitive stored data returned, such as the contents of internal application files.

## Evaluation

The test case fails if an external caller is able to access one or more file-based content providers and obtain sensitive stored data from internal/private files without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
