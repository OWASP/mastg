---
platform: android
title: Sensitive Data in Unencrypted SQLite
id: MASTG-DEMO-0068
code: [kotlin]
test: MASTG-TEST-0304
status: new
---

### Sample

The snippet below shows sample code that uses the default SQLite API (`context.openOrCreateDatabase`) to store sensitive data, including PII and an access token, without any encryption.

{{ MastgTest.kt }}

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0004 installed on your machine
3. Click the **Start** button
4. Execute `run.sh`.

The script `run.sh` pulls the database file (`PrivateUnencryptedData.db`) and queries its content:

{{ run.sh }}

### Observation

The output contains the extracted content from the `Users` table, showing the sensitive PII (email address) and the access token stored in **plaintext**.

{{ output.txt }}

### Evaluation

This test fails because the app uses the standard SQLite API to store sensitive data, specifically an access token (secret) and the user's email address (PII), in its sandbox without additional encryption.
