---
platform: android
title: Using File APIs to Write Sensitive Data Unencrypted to the App Sandbox
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
tools: [MASTG-TOOL-0110]
---

## Sample

The code below uses Java File APIs to write to the app's internal storage:

- A password is stored **unencrypted** using `openFileOutput`.
- An API key is stored **unencrypted** using `FileOutputStream`.
- An encrypted secret is stored using `FileOutputStream` after AES/GCM encryption with an AndroidKeyStore-backed key.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-unencrypted-internal-file-storage.yml }}

{{ run.sh }}

## Observation

The rule has identified 3 locations that indicate use of File APIs to write data to internal storage.

{{ output.txt }}

## Evaluation

The test fails because the app uses File APIs to write sensitive data to internal storage without encryption.

After reviewing the decompiled code at the locations specified in the output:

- Line 69: `openFileOutput("secret_token.txt", ...)` is followed by writing `password` in plaintext and no preceding `Cipher` calls, so the data is stored unencrypted.
- Lines 78-79: `new File(context.getFilesDir(), "api_key.txt")` is stored in `apiKeyFile` and passed to `new FileOutputStream(apiKeyFile)`, which is followed by writing `apiKey` in plaintext. Again, no preceding `Cipher` calls.

The test **passes** only for the `encrypted_data.bin` file written in lines 95-96: the data is encrypted using `AES/GCM` with a key generated in the AndroidKeyStore before being written to internal storage.

You can confirm the dynamic counterpart in @MASTG-DEMO-0x02.
