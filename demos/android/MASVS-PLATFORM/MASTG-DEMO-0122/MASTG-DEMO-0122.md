---
platform: android
title: Oversharing via FileProvider with Unrestricted Path Configuration
id: MASTG-DEMO-0122
code: [xml, kotlin]
kind: fail
test: MASTG-TEST-0357
---

## Sample

The code below sets up a `FileProvider` to share lab report PDFs with external apps (e.g., email clients or document viewers). While the provider is not directly exported (`android:exported="false"`), it enables URI grants via `android:grantUriPermissions="true"`. The `filepaths.xml` resource uses `path="."`, which exposes the entire internal `filesDir`, including sensitive files such as `session_token.txt`, to any app that receives a URI grant.

The Android Manifest exports the activity `ShareReportActivity` that can be queried by any other app.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml # filepaths.xml # filepaths_reversed.xml}}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the `filepaths.xml` resource.

{{ ../../../../rules/mastg-android-fileprovider-broad-scope.yml }}

{{ run.sh }}

## Observation

The rule flags the `files-path` element with `path="."`.

{{ output.txt }}

## Evaluation

The test case fails because the `FileProvider` path configuration exposes the entire `filesDir` instead of only the intended `reports/` subdirectory.

The rule flags the `files-path` element in `filepaths.xml`:

- `path="."` is an overly broad scope that grants URI-grant access to every file under `filesDir`, not just the intended `reports/` subdirectory. Any app that receives a URI grant from the victim's `ShareReportActivity` can request any filename — including sensitive files such as `session_token.txt`.

Additionally, `ShareReportActivity` is declared with `android:exported="true"` in the AndroidManifest, meaning any external app can send it a crafted intent with an arbitrary `file_name` extra and receive back a valid `content://` URI.

> This attack cannot be reproduced with `adb` alone. `adb shell am start` can launch the exported activity, but it never receives the returned result intent or the temporary URI permission grant, so it can't read the file — and reading it via `su -c 'content read …'` only works because root can read any app's storage directly, which bypasses the provider rather than exploiting it. Demonstrating the real vulnerability therefore requires a separate attacker app that calls the activity with `startActivityForResult()` and reads the granted `content://` URI.
