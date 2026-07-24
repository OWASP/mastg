---
platform: android
title: Sensitive Data Stored Unencrypted via DataStore
id: MASTG-DEMO-0069
code: [kotlin]
test: MASTG-TEST-0305
status: new
---

### Sample

The snippet below shows sample code that uses Jetpack DataStore to store sensitive data, including PII (email) and secrets (access token or password), in **plaintext** without encryption.

{{ MastgTest.kt # user_preferences.proto }}

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0004 installed on your machine
3. Click the **Start** button
4. Execute `run.sh`.

The script pulls the DataStore files (`sensitive_datastore_proto.pb` for Proto DataStore and `sensitive_datastore_prefs.preferences_datastore` for Preferences DataStore) from the app sandbox and queries their content:

{{ run.sh }}

### Observation

The output contains the extracted sensitive data, showing PII (email address) and secrets (password or access token) stored in **plaintext** in the DataStore files.

{{ output.txt }}

### Evaluation

This test fails because the app uses DataStore without encryption, storing sensitive data such as an access token (secret) and the user's email address (PII) in **plaintext** within the sandbox.
