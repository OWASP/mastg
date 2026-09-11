---
platform: android
title: Extracting Sensitive Data from Cipher.doFinal via Frida Hooking
id: MASTG-DEMO-0106
code: [kotlin]
test: MASTG-TEST-0341
kind: fail
---

## Sample

This sample encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore. The app does not implement any runtime hook detection mechanisms. On the contrary, @MASTG-DEMO-0107 demonstrates a runtime hook detection mechanism.

!!! note
    This is a series of correlated tests.

    - This test is a failed test (failed defence/successful attack) against a data exfiltration attack.
    - @MASTG-DEMO-0107 is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0106.
    - @MASTG-DEMO-0108 is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0107 by using a more "complex" attack.

{{ MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ hooks.json # run.sh }}

## Observation

The output contains all instances of `Cipher.doFinal()` method calls found at runtime. A backtrace is also provided to help identify the location in the code. The first `doFinal` call reveals the sensitive API key in plaintext as the input parameter during encryption and the second call reveals it as the return value during decryption.

{{ output.json }}

## Evaluation

The test fails because the hook executes successfully and the sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` is extracted in plaintext from the `Cipher.doFinal()` calls. The app lacks runtime integrity verification, allowing instrumentation tools to intercept cryptographic operations without any defensive response.
