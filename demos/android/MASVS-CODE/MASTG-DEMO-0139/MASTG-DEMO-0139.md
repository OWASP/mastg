---
id: MASTG-DEMO-0139
title: Path Traversal via Malicious ContentProvider Filename
platform: android
code: [kotlin, xml]
test: MASTG-TEST-0375
kind: fail
---

## Sample

The following sample app requests a file using a custom implicit intent (`org.owasp.mastestapp.REQUEST_FILE`) and handles the result in `onActivityResult`. The selected app controls the returned `Intent` data and the provider metadata that the app reads through `ContentResolver.query`.

The app reads `OpenableColumns.DISPLAY_NAME` from the returned `ContentProvider` and uses it directly as the filename for a `File` under `filesDir/public/`. A malicious app can return a `content://` URI with a display name such as `../private/secret.txt`, causing the victim app to write outside the intended `public/` directory.

{{ MastgTest.kt # AndroidManifest.xml }}

!!! note
    @MASTG-DEMO-0141 provides an example attacker app that handles `org.owasp.mastestapp.REQUEST_FILE` and returns a `content://` URI with a provider-controlled display name. It demonstrates how an attacker-controlled app can control data returned to this sample app's implicit intent result.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Make sure @MASTG-TOOL-0145 can connect to the app, for example via frida-server or Frida Gadget.
3. Use @MASTG-TECH-0043 by running `run.sh` with @MASTG-TOOL-0145 to hook the relevant API calls.
4. Exercise the app to trigger a flow that requests data from another app through an implicit intent.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ hooks.json # run.sh }}

## Observation

The output shows runtime file API calls reached while the app handles the activity result:

- `java.io.File.$init` is called from `VulnerableActivity.onActivityResult` with base directory `/data/user/0/org.owasp.mastestapp/files` and filename `public`.
- `java.io.File.$init` is called from `VulnerableActivity.onActivityResult` with base directory `/data/user/0/org.owasp.mastestapp/files/public` and filename `../private/secret.txt`.
- `java.io.FileOutputStream.$init` is called from `VulnerableActivity.onActivityResult` with `/data/user/0/org.owasp.mastestapp/files/public/../private/secret.txt`.

{{ output.json }}

## Evaluation

The test case fails because data returned from an external intent result reaches a file write operation without validation or sanitization.

The returned filename comes from `OpenableColumns.DISPLAY_NAME`, which is provider-controlled metadata obtained through `ContentResolver.query`. The app uses this value directly in `File(publicDir, fileName)` and then writes to the resulting path with `FileOutputStream`.

The hook output shows this untrusted value as `../private/secret.txt`, which causes the destination path to become `files/public/../private/secret.txt`. No validation is performed before use: the app does not verify the returned URI/provider, reject path separators, normalize and check the canonical destination path, or otherwise constrain the filename to the intended `public/` directory, amongst other possible validations.
