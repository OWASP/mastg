---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
id: MASTG-DEMO-0x07
code: [kotlin, xml]
tools: [MASTG-TOOL-0015]
status: draft
kind: fail
note: Tested on Android 15 (API level 35) with Android Studio Ladybug and a Pixel 8 emulator.
---

## Sample

The sample below exposes sensitive stored data through exported `ContentProvider`s. `MastgTest.kt` creates a secret file and a credentials database, and `AndroidManifest.xml` exposes both providers without any read restrictions.

{{ MastgTest.kt # AndroidManifest.xml # MastgTest_reversed.java }}

## Steps

1. Install the sample app on the device by following @MASTG-TECH-0005.
2. Use @MASTG-TECH-0001 to confirm that the app is installed and that you can interact with the device.
3. Review `AndroidManifest.xml` and confirm that both providers are exported and do not declare read permissions.
4. Run `run.sh` to enumerate the provider surface and read a private file through IPC.

{{ run.sh }}

## Observation

The output should contain evidence that `org.owasp.mastestapp.files` is exported and that reading `content://org.owasp.mastestapp.files/files/secret.txt` returns the contents of the app's private file.

{{ output.txt }}

In `output.txt`, the lines containing `TOP_SECRET_TOKEN=tok_live_12345` and `PIN=9876` show that an external caller can read sensitive values from private storage through IPC.

## Evaluation

Apply the evaluation from @MASTG-TEST-0x07 to this output.
- The exported provider information shows that the app exposes a file-based IPC entry point without any read restrictions.
- The returned `TOP_SECRET_TOKEN` and `PIN` values prove that the provider can disclose sensitive stored data from the app's private directory.
- This demo is therefore a failing case because external callers can retrieve sensitive stored data outside the app sandbox.

```bash
dz> run app.package.attacksurface org.owasp.mastestapp
dz> run app.provider.info -a org.owasp.mastestapp
dz> run app.provider.read content://org.owasp.mastestapp.files/files/secret.txt
```