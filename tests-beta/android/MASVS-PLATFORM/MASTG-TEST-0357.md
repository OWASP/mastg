---
title: References to Oversharing of File-Based Content Providers
platform: android
id: MASTG-TEST-0357
weakness: MASWE-0064
type: [static, config, code, manual]
best-practices: [MASTG-BEST-0049]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0020, MASTG-KNOW-0117]
---

## Overview

If the app exports an Android content provider without enforcing access restrictions, external callers may open private files through `content://` URIs. This test checks whether exported providers expose sensitive stored data to callers that don't hold the required permissions.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0159 to identify exported file-based content providers and inspect their path configurations.
3. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of exported file-based content providers with their path configurations, and a list of code locations where provider-backed file access occurs.

## Evaluation

The test case fails if the app exports a `FileProvider` and if the provider's path configuration allows access outside the intended shared directory (for example, via `<root-path>`, `path="/"`, `path="."`, or `path=""`).

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the exposure is security-relevant:

- Determine whether `FileProvider.getUriForFile()` is called with attacker-controlled input (for example, values derived from URI query parameters or user input).
- Determine whether the provider enforces appropriate access control, by using in the Android Manifest `android:permission` and an adequate protection level like `dangerous` or `signature`.
