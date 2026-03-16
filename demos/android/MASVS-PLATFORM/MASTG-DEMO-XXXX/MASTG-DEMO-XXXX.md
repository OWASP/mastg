---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
id: MASTG-DEMO-0007
code: [kotlin]
tools: [drozer]
status: new
kind: fail
---

## Sample

The following sample demonstrates an Android application that exposes sensitive stored data through a file-based exported content provider without enforcing access restrictions.

The application defines `FileLeakProvider`, which serves files stored under the application's private `filesDir` directory through the `openFile` method. The provider is exported and does not enforce any read or write permissions. As a result, external callers can read internal application files through IPC.

## Steps

Let's run the rules against the sample code.

{{ ../../../../rules/mastg-android-data-exposure-via-ipc-read.yml }}

{{ run.sh }}

To verify the leaked content 

{{ run app.provider.read content://org.owasp.mastestapp.files/files/secret.txt }}

## Observation

The external caller is able to read an internal application file through the exported content provider. The file-based provider returns the contents of a private file containing sensitive values such as a token and PIN. No permissions or access controls prevent external access to the file.

## Evaluation

The test fails because the application exposes sensitive stored data through an exported file-based content provider without enforcing appropriate access restrictions. External callers can read internal file contents via IPC, demonstrating that sensitive stored data is accessible outside the application sandbox.
