---
platform: android
title: Extracting Sensitive Data from Cipher.doFinal via Frida Hooking
id: MASTG-DEMO-0087
code: [kotlin]
test: MASTG-TEST-03te
kind: fail
---

## Sample

This sample encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore. The app does not implement any runtime hook detection mechanisms. On the contrary, @MASTG-DEMO-0088 demonstrates a runtime hook detection mechanism.

{{ MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Use @MASTG-TECH-0033 to target `Cipher.dofinal()` API
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ script.js # run.sh }}

## Observation

The output contains all instances of `Cipher` method calls found at runtime. A backtrace is also provided to help identify the location in the code. The `doFinal` calls reveal the sensitive API key in plaintext: as the input parameter during encryption and as the return value during decryption.

{{ output.txt }}

## Evaluation

The test fails because the hook executes successfully and the sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` is extracted in plaintext from the `Cipher.doFinal()` calls. The app lacks runtime integrity verification, allowing instrumentation tools to intercept cryptographic operations without any defensive response.
