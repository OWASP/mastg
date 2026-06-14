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

**Example Attack Scenario:**

Suppose an app exports a `FileProvider` with a `files-path` element using `path="."`, exposing the entire internal `filesDir`.

1. An attacker reverse engineers the app and finds the exported `FileProvider` authority and a `files-path` entry with `path="."`, which maps the entire internal filesDir into the provider's shareable root.
2. The attacker identifies an exported component in the victim app (e.g. an Activity or Service) that accepts a filename or path from the caller and uses it to build a URI via `FileProvider.getUriForFile(context, authority, new File(filesDir, attackerInput))`.
3. The attacker crafts a malicious app that invokes that component with a traversal payload such as `../databases/auth.db`, causing the victim app to construct a `content://` URI pointing outside the intended shared subdirectory and return it with `FLAG_GRANT_READ_URI_PERMISSION`.
4. The malicious app calls `ContentResolver.openInputStream()` on the returned `content://` URI to access any file under `filesDir`, including sensitive files such as tokens or private databases.
5. The `FileProvider` serves the file without restricting which paths are accessible, exposing data beyond the intended shared directory.

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
