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

### Exploitation

**Option 1: Using an attacker app (@MASTG-DEMO-0x01)**

@MASTG-DEMO-0x01 demonstrates the full exploit as a self-contained attacker app. Install the attacker APK, tap **Start**, and it sends a crafted intent to `ShareReportActivity` requesting `session_token.txt`. The exfiltrated token appears in a dialog and in logcat:

```bash
adb logcat -s EXFIL
--------- beginning of main
06-05 08:17:34.993 12771 12771 E EXFIL   : Exfiltrated from victim: sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b
```

**Option 2: Using `adb` on a rooted device**

Building an attacker app is the best way to demonstrate the real-world attack, because it simulates a malicious app installed on the device. However, you can also demonstrate the broad `FileProvider` exposure without building a separate app by using a rooted device.

First, note that simply calling the exported activity with an arbitrary filename is not enough:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\\$ShareReportActivity' --es file_name 'session_token.txt'
```

This only starts the activity. It does not print the file contents, because `adb shell am start` does not receive the result intent, hold the URI permission grant, or read the returned `content://` URI.

To demonstrate the exposure with commands only, **use a rooted device** and directly read the exposed provider URI:

```bash
adb shell su -c 'content read --uri content://org.owasp.mastestapp.fileprovider/app_files/session_token.txt'
```

Expected output before the fix:

```bash
sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b
```

After restricting the provider path from `path="."` to `path="reports/"`, the same command should fail:

```bash
adb shell su -c 'content read --uri content://org.owasp.mastestapp.fileprovider/app_files/session_token.txt'
```

Expected output after the fix:

```text
Error while accessing provider:org.owasp.mastestapp.fileprovider
java.io.FileNotFoundException: open failed: ENOENT (No such file or directory)
```

Of course, since you have root access, you can also directly read the file without going through the provider. However, that would not demonstrate the vulnerability.

## Fix

There are two independent fixes, which can be combined for defense-in-depth.

**Option 1: Restrict the `FileProvider` path scope (recommended)**

In filepaths.xml, replace `path="."` with the specific subdirectory the app intends to share:

```xml
<files-path name="app_files" path="reports/" />
```

After this change, any call to `FileProvider.getUriForFile()` with a path outside `reports/` throws `IllegalArgumentException: Failed to find configured root that contains …`. You can confirm by re-running the attacker app from @MASTG-DEMO-0x01: the dialog will show `No URI returned` instead of the token.

**Option 2: Restrict or remove the export of `ShareReportActivity`**

If `ShareReportActivity` doesn't need to be reachable by arbitrary third-party apps, set `android:exported="false"`:

```xml
<activity
    android:name="org.owasp.mastestapp.MastgTest$ShareReportActivity"
    android:exported="false" />
```

This prevents any external app from sending a crafted intent. Confirm with:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\\$ShareReportActivity' --es file_name "session_token.txt"
# Expected: Security exception — Permission Denial
```

If `ShareReportActivity` must remain exported for legitimate cross-app use (for example, to allow a trusted document viewer to request files), add a signature-level permission instead:

```xml
<permission
    android:name="org.owasp.mastestapp.permission.SHARE_REPORT"
    android:protectionLevel="signature" />

<activity
    android:name="org.owasp.mastestapp.MastgTest$ShareReportActivity"
    android:exported="true"
    android:permission="org.owasp.mastestapp.permission.SHARE_REPORT" />
```

**Why isn't restricting the path alone sufficient in every case?**

`path="reports/"` prevents `FileProvider` from constructing URIs for files outside `reports/`. However, if `ShareReportActivity` were ever changed to resolve paths differently (for example, accepting absolute paths or `../` sequences), the broad `path="."` scope would silently re-enable the exposure. Combining both fixes (a narrow path scope and a restricted or permission-guarded activity) provides defense-in-depth.
