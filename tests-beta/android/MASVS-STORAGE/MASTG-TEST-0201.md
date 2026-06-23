---
platform: android
title: Runtime Use of APIs to Access External Storage
id: MASTG-TEST-0201
apis: [Environment#getExternalStorageDirectory, Environment#getExternalStorageDirectory, Environment#getExternalFilesDir, Environment#getExternalCacheDir, FileOutputStream]
type: [dynamic, hooks, manual]
weakness: MASWE-0007
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0042]
---

## Overview

Android apps use a variety of APIs to access the external storage (@MASTG-KNOW-0042). Collecting a comprehensive list of these APIs can be challenging, especially if an app uses a third-party framework, loads code at runtime, or includes native code.

The most effective approach to testing applications that write to device storage is usually dynamic analysis, and specifically method hooking. You can use it to hook into the relevant APIs such as `getExternalStorageDirectory`, `getExternalStoragePublicDirectory`, `getExternalFilesDir` or `FileOutPutStream`. You could also use `open` as a catch-all for file interactions. However, this won't catch all file interactions, such as those that use the `MediaStore` API and should be done with additional filtering as it can generate a lot of noise.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of files that the app wrote to the external storage during execution and the APIs used to write them including function names and backtraces.

## Evaluation

The test case fails if the files found above are not encrypted and leak sensitive data.

**Further Validation Required:**

Inspect the content of each reported file to determine whether the data is sensitive:

- Determine whether the file contains sensitive information (e.g., personal data, credentials, or tokens).
- Determine whether the data is stored without encryption.

Use @MASTG-TECH-0023 to inspect the code locations from the backtraces if you want to determine the exact code paths that lead to the file creation and whether they are security-relevant.
