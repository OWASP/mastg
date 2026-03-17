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

The following sample demonstrates an Android application that exposes sensitive stored data through an exported content provider without enforcing access restrictions.

The application defines `CredentialProvider`, which exposes records stored in an internal SQLite database through the `query` method. The provider is exported and does not enforce any read or write permissions. As a result, external callers can retrieve sensitive data such as usernames and passwords through IPC.

## Steps

Let's run the rules against the sample code.

{{ ../../../../rules/mastg-android-data-exposure-via-ipc-query.yml }}

{{ run.sh }}

Run the following command in the drozer to check for leaked content. 

```bash
dz> run app.provider.query content://org.owasp.mastestapp.credentials/credentials
```

## Observation

The external caller is able to query the exported content provider and retrieve sensitive database records. The provider returns credential data including usernames and passwords. No permissions or access controls prevent external access to the database-backed provider.

## Evaluation

The test fails because the application exposes sensitive stored data through an exported database-backed content provider without enforcing appropriate access restrictions. External callers can query credential records via IPC, demonstrating that sensitive stored data is accessible outside the application sandbox.
