---
platform: android
title: Using File APIs to Write Sensitive Data Unencrypted to the App Sandbox
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
tools: [MASTG-TOOL-0110]
---

## Sample

The code below stores sensitive data to the app's internal storage using Java File APIs, both without encryption:

- A password is stored unencrypted using `openFileOutput`
- An API key is stored unencrypted using `FileOutputStream`

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-unencrypted-internal-file-storage.yml }}

{{ run.sh }}

## Observation

The rule has identified 2 locations that indicate use of File APIs to write data to internal storage.

{{ output.txt }}

## Evaluation

The test fails because the app uses File APIs to write sensitive data to internal storage without encryption.

After reviewing the decompiled code at the locations specified in the output:

- Line 33: `openFileOutput("secret_token.txt", ...)` is followed by writing `password` in plaintext and no preceding `Cipher` calls, so the data is stored unencrypted.
- Lines 42-43: `new File(context.getFilesDir(), "api_key.txt")` is stored in `apiKeyFile` and passed to `new FileOutputStream(apiKeyFile)`, which is followed by writing `apiKey` in plaintext. Again, no preceding `Cipher` calls.

You can confirm the dynamic counterpart in @MASTG-DEMO-0x02.
