---
title: Verify Usage of File-Based Content Providers
alias: verify-usage-of-file-based-content-providers
id: MASTG-TECH-0159
platform: android
---

See @MASTG-KNOW-0117 for an overview of Android ContentProviders, including URI structure, access control, and query handling.

This technique describes how to identify file based content providers.

## Using the AndroidManifest

Use @MASTG-TECH-0117, to identify exported activities that are set to `android:exported="true"`.

Extract `res/xml/*.xml` and check for `FileProvider` path declarations. Flag any usage of:

- `path="."`,
- `path=""`,
- `path="/"` or
- `<root-path>`.

## Using the Decompiled Source Code

Use @MASTG-TECH-0017 to decompile the APK and for each `FileProvider.getUriForFile(...)` call in the reversed code, trace the File argument to its source. Flag calls where the argument is derived from attacker controlled input like URI query parameters.
