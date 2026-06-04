---
title: References to Oversharing of File-Based Content Providers
platform: android
id: MASTG-TEST-0357
weakness: MASWE-0064
type: [static, config, code]
best-practices: [MASTG-BEST-0049]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0020, MASTG-KNOW-0117]
---

## Overview

If the app exports an Android content provider without enforcing access restrictions, external callers may open private files through `content://` URIs. This test checks whether exported providers expose sensitive stored data to callers that don't hold the required permissions.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 and @MASTG-TECH-0159 to confirm which provider classes expose file access, and whether they validate the caller before returning data.

## Observation

The output should contain each provider's authorities, the access control configured for each provider, and the result of each external access attempt.

## Evaluation

The test case fails if an external caller can open provider-backed private files without the required permissions.
