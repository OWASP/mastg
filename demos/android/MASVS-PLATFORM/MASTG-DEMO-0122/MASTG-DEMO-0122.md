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

### Exploitation

@MASTG-DEMO-0x01 demonstrates the full exploit as a self-contained attacker app. Install the attacker APK, tap **Start**, and it sends a crafted intent to `ShareReportActivity` requesting `session_token.txt`. The exfiltrated token appears in a dialog and in logcat:

```bash
adb logcat -s EXFIL
--------- beginning of main
06-05 08:17:34.993 12771 12771 E EXFIL   : Exfiltrated from victim: sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b
```

## Fix

There are two independent fixes, which can be combined for defense-in-depth.

**Option 1: Restrict the `FileProvider` path scope (recommended)**

In filepaths.xml, replace `path="."` with the specific subdirectory the app intends to share:

```xml
<files-path name="app_files" path="reports/" />
```

After this change, any call to `FileProvider.getUriForFile()` with a path outside `reports/` throws an error. You can confirm by re-running the attacker app from @MASTG-DEMO-0x01, the app will not show the session token anymore.

Run `adb logcat | grep -A20 "Failed to find configured root"` to validate it:

```bash
Caused by: java.lang.IllegalArgumentException: Failed to find configured root that contains /data/data/org.owasp.mastestapp/files/session_token.txt
```

**Option 2: Restrict or remove the export of `ShareReportActivity`**

If `ShareReportActivity` doesn't need to be reachable by arbitrary third-party apps, set `android:exported="false"` or remove the activity completely from the Android Manifest:

```xml
<activity
    android:name="org.owasp.mastestapp.MastgTest$ShareReportActivity"
    android:exported="false" />
```

This prevents any external app from sending a crafted intent.
