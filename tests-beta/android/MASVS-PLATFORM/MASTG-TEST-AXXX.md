---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via Database Backed IPC Mechanisms
id: MASTG-TEST-AXXX
type: [dynamic]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-XXXX]
knowledge: [MASTG-KNOW-0020]
---

## Overview

If the app exposes database backed content providers without proper access restrictions, other apps on the device can use IPC to query sensitive stored data, such as credentials stored in internal databases. This can cause unauthorized disclosure of data that is intended to remain private to the application. This test case checks whether database backed content providers can be accessed from outside the app and whether they return sensitive stored data.

## Steps

1. Reverse engineer the app using @MASTG-TECH-0013 and extract the `AndroidManifest.xml` with @MASTG-TECH-0117 to identify all `<provider>` components.
2. Review each provider to determine whether it is exported explicitly or implicitly, and verify any applied permissions such as `android:permission`, `android:readPermission`, and related protection levels.
3. Use @MASTG-TECH-0014 and @MASTG-TECH-XXXX to inspect provider code for database-access behavior, including `SQLiteDatabase`, `query`, `android.database.sqlite`, and any exported providers exposing sensitive stored data.

## Observation

The output must contain a list of content provider authorities and results demonstrating that an external caller can query database-backed provider URIs, including any sensitive stored data retrieved, such as usernames, passwords, or other credential records.

## Evaluation

The test case fails if an external caller is able to access one or more database backed content providers and obtain sensitive stored data from internal databases without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
