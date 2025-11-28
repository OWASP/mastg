---
platform: android
title: Sensitive Data Stored Unencrypted via Room Database
id: MASTG-DEMO-0070
code: [kotlin]
test: MASTG-TEST-0306
status: new
---

### Sample

The snippet below shows sample code that uses the Android Room Persistence Library to store sensitive data, including PII (email) and secrets (access token), in **plaintext** without any encryption.

{{ MastgTest.kt }}

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0004 installed on your machine
3. Click the **Start** button
4. Execute `run.sh`.

The script pulls the Room database (`PrivateUnencryptedRoomDB`) along with its WAL/SHM files and queries the `users` table content:

{{ run.sh }}

### Observation

The output contains the extracted content from the `users` table, showing the sensitive PII (email address) and the access token stored in **plaintext**.

{{ output.txt }}

### Evaluation

This test fails because the app uses Room without additional encryption, storing sensitive data such as an access token (secret) and the user's email address (PII) in its sandbox in **plaintext**.
