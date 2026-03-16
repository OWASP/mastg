---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
id: MASTG-TEST-0007
type: [static, dynamic]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: []
knowledge: []
---

## Overview

If the app exposes database-backed content providers without proper access restrictions, other apps on the device can use IPC to query sensitive stored data, such as credentials stored in internal databases. This can cause unauthorized disclosure of data that is intended to remain private to the application. This test case checks whether database-backed content providers can be accessed from outside the app and whether they return sensitive stored data.

## Steps

1. Inspect `AndroidManifest.xml` and identify all `<provider>` components.
2. For each provider, determine whether it is accessible to other apps:
   - Check `android:exported` and any `<intent-filter>` that could implicitly export the provider.
   - Check for access restrictions such as `android:permission`, `android:readPermission`, `android:writePermission`, or path-based permissions.
3. Inspect the source code and identify providers that handle database-backed sensitive stored data:
   - Look for subclasses of `android.content.ContentProvider`.
   - Look for database access patterns such as `SQLiteDatabase.query`, table lookups, and returned credential-like fields.
4. Perform dynamic verification from an external context:
   1. Enumerate the app’s content providers and their authorities.
   2. Identify database-backed content providers.
   3. Attempt to query provider data via their `content://` URIs.
5. Record the retrieved data and the URIs used to retrieve it.

## Observation

The output should include a list of content provider authorities and one or more proof-of-access results indicating that an external caller can query database-backed provider URIs, including any sensitive stored data returned, such as usernames, passwords, or other credential records.

## Evaluation

The test case will fail if an external caller is able to access one or more database-backed content providers and obtain sensitive stored data from internal databases without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
