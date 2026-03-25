---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
id: MASTG-DEMO-XXXX
code: [kotlin, xml]
tools: [MASTG-TOOL-0015]
status: draft
kind: fail
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

## Evaluation

The test fails because the application exposes sensitive stored data through an exported file based content provider without enforcing appropriate access restrictions. The returned TOP_SECRET_TOKEN and PIN values show that external callers can retrieve sensitive stored data from the app's private directory outside the application sandbox.

```bash
dz> run app.package.attacksurface org.owasp.mastestapp
dz> run app.provider.info -a org.owasp.mastestapp
dz> run app.provider.read content://org.owasp.mastestapp.files/files/secret.txt
