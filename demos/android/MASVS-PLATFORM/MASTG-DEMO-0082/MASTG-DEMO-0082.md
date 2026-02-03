---
platform: android
title: WebView WebStorage Cleanup
id: MASTG-DEMO-0082
code: [kotlin]
test: MASTG-TEST-0320
---

## Sample

The code snippet below shows a sample that uses a WebView to store sensitive data in the cache and then performs a cleanup with `WebStorage` API.

The sensitive data is stored using `localStorage.setItem` in the inline HTML loaded into the WebView. The values used are:

- `'sensitive_token'` with value `'SECRET_TOKEN_123456'`.
- `'driving_license_id'` with value `'DL-987654321'`.

{{ MainActivityWebView.kt # MastgTestWebView.kt # AndroidManifest.xml }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button in the app
5. Wait for the Frida script to capture the WebView cleanup calls
6. Stop the script by quitting the Frida CLI
7. Use @MASTG-TECH-0002 to search the contents of the `/data/data/<app_package>/app_webview/` directory for the sensitive data used in the WebView.

{{ script.js # run.sh }}

## Observation

In the output we can see all instances of `deleteAllData()` of `WebStorage` called at runtime:

{{ output.json }}

We can also see that the output of the `adb shell grep` command shows no matches for the sensitive data used in the WebView:

{{ output_adb_deletion_succeeded.txt }}

## Evaluation

The test **passes** as the application properly cleans up all storage data from the WebView cache using the `WebStorage` API.

If you want to demonstrate the failure case, you can comment out the `WebStorage.getInstance().deleteAllData()` line in the code and rerun the test. In this case, the test **fails** because the sensitive data remains in the WebView storage directory after closing the app.

{{ output_adb_deletion_failed.txt }}
