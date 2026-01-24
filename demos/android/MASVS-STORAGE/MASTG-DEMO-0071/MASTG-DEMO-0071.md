---
platform: android
title: Serialization of Sensitive Data
id: MASTG-DEMO-0071
code: [kotlin]
test: MASTG-TEST-0307
---

### Sample

The snippet below shows sample code that serializes a user object containing sensitive data (username and password) using Gson and stores it in internal storage without encryption.

{{ MastgTest.kt }}

### Steps

1. Install and run the app.
2. Trigger the serialization by interacting with the app (e.g., login or save data).
3. Use @MASTG-TECH-0002 to retrieve files from internal storage.
4. Inspect the serialized data.

### Observation

The file `user_data.json` contains:

{{ output.txt }}

Which is plaintext JSON with sensitive information.

### Evaluation

This demo fails because sensitive data is stored unencrypted via serialization. In a secure implementation, the data should be encrypted before serialization or the serialized data should be encrypted.