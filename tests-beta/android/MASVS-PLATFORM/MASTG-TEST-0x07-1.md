---
platform: android
title: References to Unauthorized Database Access through Content Providers
id: MASTG-TEST-0x07-1
type: [dynamic]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x07]
knowledge: [MASTG-KNOW-0020]
---

## Overview

If the app exposes database backed content providers without proper access restrictions, other apps on the device can use IPC to query sensitive stored data, such as credentials stored in internal databases. This can cause unauthorized disclosure of data that is intended to remain private to the application. This test case checks whether database backed content providers can be accessed from outside the app and whether they return sensitive stored data.

## Steps

1. Reverse engineer the app using @MASTG-TECH-0013 and extract the `AndroidManifest.xml` with @MASTG-TECH-0117 to identify all `<provider>` components.
2. Review each provider to determine whether it is exported explicitly or implicitly, and verify any applied permissions such as `android:permission`, `android:readPermission`, and related protection levels.
3. Use @MASTG-TECH-0014 and @MASTG-TECH-0x07-2 to enumerate and query exported content provider URIs from outside the app, and verify whether they expose database-backed sensitive stored data to an external caller.

## Observation

The output should contain each provider authority, the access controls configured for each provider, and the result of each external access attempt.

## Evaluation

The test case fails if an external caller is able to access one or more database backed content providers and obtain sensitive stored data from internal databases without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
