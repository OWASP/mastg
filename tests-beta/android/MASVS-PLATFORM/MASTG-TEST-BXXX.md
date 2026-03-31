---
title: Determining Whether Sensitive Stored Data Is Properly Protected in IPC Mechanisms
platform: android
id: MASTG-TEST-BXXX
weakness: MASWE-0064
type: [dynamic]
best-practices: [MASTG-BEST-XXXX]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0020]
---

## Overview

If the app exports an Android content provider without enforcing access restrictions, external callers may query database records or open private files through `content://` URIs. This test checks whether exported providers expose sensitive stored data to callers that don't hold the required permissions or satisfy the app's runtime authorization checks.

## Steps

1. Run @MASTG-TECH-AXXX on the app to identify each `<provider>`, including its authority, exported state, and any read, write, or path-based permissions.
2. Use @MASTG-TECH-0014 to review the reversed code and confirm which provider classes expose database or file access, and whether they validate the caller before returning data.
3. Use @MASTG-TECH-AXXX from an external test context to query each exported provider URI, then record the authority, returned data, and any denial messages such as permission errors or `SecurityException`.

## Observation

The output should contain the each provider authorities, the access control configured for each provider, and the result of each external access attempt.

## Evaluation

The test case fails if an external caller can query an exported content provider or open provider backed private files without the required permissions or equivalent runtime authorization checks.
