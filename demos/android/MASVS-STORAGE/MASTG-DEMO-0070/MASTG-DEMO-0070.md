---
platform: android
title: Sensitive Data Stored Unencrypted via Room Database
id: MASTG-DEMO-0070
code: [kotlin]
test: MASTG-TEST-0x02
status: new
---

## Sample

The snippet below shows sample code that uses the Android Room Persistence Library
to store sensitive data, including PII (email) and secrets (access token),
in **plaintext** without any encryption.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0004 installed on your machine
3. Click the **Start** button
4. Execute `run.sh`.

The script pulls the Room database (`PrivateUnencryptedRoomDB`) along
with its WAL/SHM files and queries the `users` table content:

{{ run.sh }}

## Observation

The output contains the extracted content from the `users` table,
showing the sensitive PII (email address) and the access token stored in **plaintext**.

{{ output.txt }}

## Evaluation

The test case fails because the application persists sensitive information
in a Room database without any form of encryption,
making it accessible in plaintext to anyone with access to the app's private storage.

Reviewing the evidence in `output.txt`:

- Each row represents a record from the `users` table, formatted as `id|username|email|token`.
- The third field contains the user's **email address** (e.g., `john.doe@maswe.com`),
which is considered PII and should be protected.
- The fourth field contains the **access token** (e.g., `ghp_123456...`),
which is a critical secret.

Since these values are clearly readable via a standard SQLite query
and no encryption layer (like SQLCipher) is present in the `AppDatabase` configuration,
the security requirements for sensitive data storage are not met.
