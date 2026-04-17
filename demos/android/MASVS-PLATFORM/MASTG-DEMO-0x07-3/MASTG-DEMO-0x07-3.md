---
platform: android
title: SQL Injection in Exported Content Provider with drozer
id: MASTG-DEMO-0x07-3
code: [kotlin, xml]
tools: [MASTG-TOOL-0015]
kind: fail
test: MASTG-TEST-0x07-3
---

## Sample

The sample below exposes sensitive stored data through exported `ContentProvider`s. `MastgTest.kt` creates a secret file and a credentials database, and `AndroidManifest.xml` exposes both providers without any read restrictions.

{{ MastgTest.kt # AndroidManifest.xml # MastgTest_reversed.java }}

## Steps

1. Install the app on a device.
2. Make sure you have drozer agent running on the device.
3. Run `run.sh` to enumerate the provider surface and read a private file through IPC.

{{ run.sh }}

## Observation

Drozer has identified that `org.owasp.mastestapp.files` is exported and that reading `content://org.owasp.mastestapp.files/files/secret.txt` returns the contents of the app's private file.

{{ output.txt }}

## Evaluation

The test fails because the application exposes sensitive stored data through an exported file based content provider without enforcing appropriate access restrictions. The returned TOP_SECRET_TOKEN and PIN values show that external callers can retrieve sensitive stored data from the app's private directory outside the application sandbox.
