---
title: Determining Whether Sensitive Stored Data Is Properly Protected in IPC Mechanisms
platform: android
id: MASTG-TEST-BXXX
weakness: MASWE-0064
type: [static, dynamic]
best-practices: [MASTG-BEST-XXXX]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0x07]
status: draft
---

## Overview

If the app exports an Android content provider without enforcing access restrictions, external callers may query database records or open private files through `content://` URIs. This test checks whether exported providers expose sensitive stored data to callers that don't hold the required permissions or satisfy the app's runtime authorization checks.

## Steps

1. Run @MASTG-TECH-0007 to inspect the Android manifest and identify each `<provider>` declaration, its authority, its exported state, and any read, write, or path based permissions.
2. Run @MASTG-TECH-0014 on the reversed code and confirm which provider classes expose database queries or file access and whether they enforce caller validation before returning data.
3. Run @MASTG-TECH-0002 from an external test context to query each exported provider URI and record whether access is granted or denied.
4. Record the authorities, the returned data, and any permission denial or `SecurityException` messages.

## Observation

The output should contain the exported provider authorities, the access control configured for each provider, and the result of each external access attempt.

## Evaluation

The test case fails if an external caller can query an exported content provider or open provider backed private files without the required permissions or equivalent runtime authorization checks.
