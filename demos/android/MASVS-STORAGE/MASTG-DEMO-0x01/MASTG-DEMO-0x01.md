---
platform: android
title: Using File APIs to Write Sensitive Data Unencrypted to the App Sandbox
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
---

## Sample

The code below stores sensitive data to the app's internal storage using the Java File APIs, both with and without encryption:

- A password is stored unencrypted using `openFileOutput`
- An API key is stored unencrypted using `FileOutputStream`
- An API key is stored encrypted using `FileOutputStream` with AES-GCM encryption (key managed by the Android KeyStore)

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Click the **Start** button.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

These are the relevant methods we are hooking to detect the use of File APIs to write data to the app sandbox:

- [`Context.openFileOutput(String, int)`](https://developer.android.com/reference/android/content/Context#openFileOutput(java.lang.String,%20int))
- [`FileOutputStream.write(byte[])`](https://developer.android.com/reference/java/io/FileOutputStream#write(byte[]))

Our hooks also trace calls to cryptographic methods to help determine whether the written data is encrypted or not; whether the Android KeyStore is used; and whether Base64 encoding is used to convert binary data to strings:

- [`javax.crypto.Cipher.*(...)`](https://developer.android.com/reference/javax/crypto/Cipher)
- [`java.security.KeyStore.*(...)`](https://developer.android.com/reference/java/security/KeyStore)
- [`javax.crypto.KeyGenerator.*(...)`](https://developer.android.com/reference/javax/crypto/KeyGenerator)
- [`android.util.Base64.*(...)`](https://developer.android.com/reference/android/util/Base64)

{{ hooks.json # run.sh }}

## Observation

The output shows all instances of data written via File APIs that were found at runtime. A backtrace is also provided to help identify the corresponding locations in the code.

{{ output.json }}

## Evaluation

The test fails because sensitive data is written to the app sandbox via File APIs without encryption.

In `output.json` we can identify entries that use the File APIs to write data to the app's internal storage.

Determining if data is encrypted or not may require careful analysis. After slightly processing the output using `jq`, we can get a high-level view of the relevant calls, which can help us identify unencrypted data writes.

{{ evaluation.txt # evaluate.sh }}

Here we can see that:

- `openFileOutput` was called with `secret_token.txt` and the subsequent `FileOutputStream.write` call writes the plaintext value `MyS3cr3tP4ssw0rd` — no preceding Cipher calls, so this is unencrypted.
- `FileOutputStream.write` writes `AKIAABCDEFGHIJKLMNOP` without any preceding Cipher calls — this is also unencrypted.
- The third `FileOutputStream.write` call writes `obLDpJw/Gi+km0wdLj9KW2x9jp8KGy1KW3B9kJ8=`, but this is preceded by `Cipher.getInstance`, `KeyStore.getEntry`, `Cipher.init`, `Cipher.doFinal`, and `Base64.encodeToString` calls, confirming that the data was encrypted before being written.

You can confirm the unencrypted writes by reverse engineering the app and inspecting the code at the locations identified in the `stackTrace` of each hook entry.
