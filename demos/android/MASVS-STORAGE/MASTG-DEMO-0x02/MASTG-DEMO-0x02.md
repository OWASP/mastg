---
platform: android
title: Runtime Use of File APIs to Write Sensitive Data Unencrypted to the App Sandbox
id: MASTG-DEMO-0x02
code: [kotlin]
test: MASTG-TEST-0x02
tools: [MASTG-TOOL-0145]
---

## Sample

This demo uses the same app sample as @MASTG-DEMO-0x01.

{{ ../MASTG-DEMO-0x01/MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Click the **Start** button.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

These are the relevant methods we are hooking to detect the use of File APIs to write data to the app sandbox:

- [`Context.openFileOutput(String, int)`](https://developer.android.com/reference/android/content/Context#openFileOutput(java.lang.String,%20int))
- [`FileOutputStream.write(byte[])`](https://developer.android.com/reference/java/io/FileOutputStream#write(byte[]))

Our hooks also trace calls to cryptographic methods to help determine whether the written data is encrypted or not, and whether an AndroidKeyStore-backed key is used:

- [`javax.crypto.Cipher.init(int, Key)`](https://developer.android.com/reference/javax/crypto/Cipher#init(int,java.security.Key))
- [`javax.crypto.Cipher.doFinal(...)`](https://developer.android.com/reference/javax/crypto/Cipher#doFinal())
- [`java.security.KeyStore.getKey(String, char[])`](https://developer.android.com/reference/java/security/KeyStore#getKey(java.lang.String,char[]))

{{ hooks.json # run.sh }}

## Observation

The output shows all instances of data written via File APIs that were found at runtime. A backtrace is also provided to help identify the corresponding locations in the code.

{{ output.json }}

## Evaluation

The test fails because sensitive data is written to the app sandbox via File APIs without encryption.

In `output.json` we can identify entries that use the File APIs to write data to the app's internal storage.

After slightly processing the output using `jq`, we can get a high-level view of the relevant calls, which can help us identify unencrypted data writes.

{{ evaluation.txt # evaluate.sh }}

Here we can see that:

- `openFileOutput` was called with `secret_token.txt` and the subsequent `FileOutputStream.write` call writes the plaintext value `MyS3cr3tP4ssw0rd` — no preceding `Cipher` calls, so this is unencrypted.
- A second `FileOutputStream.write` call writes `AKIAABCDEFGHIJKLMNOP` — also no preceding `Cipher` calls, so this is unencrypted.
- The remaining entries correspond to the PASS case: `KeyStore.getKey` retrieves the AndroidKeyStore-backed key, followed by `Cipher.init` and `Cipher.doFinal` (which shows `SensitiveDataToEncrypt` as input and returns ciphertext), and then two `FileOutputStream.write` calls — one for the IV and one for the ciphertext — writing to `encrypted_data.bin`. The data is encrypted before being stored.

You can confirm the code locations responsible by reviewing the `stackTrace` of each hook entry and cross-referencing with the static counterpart @MASTG-DEMO-0x01.
