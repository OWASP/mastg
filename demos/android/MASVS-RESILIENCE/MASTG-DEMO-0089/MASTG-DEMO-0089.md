---
platform: android
title: Bypassing Frida Detection in /proc/self/maps to Extract Sensitive Data
id: MASTG-DEMO-0089
code: [kotlin]
test: MASTG-TEST-03te
kind: fail
---

## Sample

This sample uses the same code as @MASTG-DEMO-0088, which encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore and includes a runtime hook detection mechanism that scans `/proc/self/maps` for Frida-related libraries, terminating via `Process.killProcess()` if any are found. This demo demonstrates bypassing both the detection and the response by hooking `BufferedReader.readLine()` to hide Frida entries from the detection logic and `Process.killProcess()` to neutralize the self-termination call. 

See @MASTG-KNOW-0030 and @MASTG-KNOW-0032 for more context on bypassing runtime detection mechanisms.

{{ MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Run `run.sh` to spawn the app with the bypass script
3. Click the **Start** button
4. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ bypass.js # run.sh }}

## Observation

The output contains eight `frida-agent-64.so` memory segments filtered from `/proc/self/maps` across two scans. No `Process.killProcess()` calls appear in the output. Two `Cipher.doFinal()` calls are captured: one in `ENCRYPT_MODE` with the plaintext input `sk-OWASP-MAS-SuperSecretKey-1234567890`, and one in `DECRYPT_MODE` with the same plaintext as output.

{{ output.txt }}

## Evaluation

The test fails because the bypass script defeats both the detection and response mechanisms. The absence of `Process.killProcess()` in the output confirms that `detectHooking()` returned `false`, meaning the `BufferedReader.readLine()` hook successfully hid all Frida memory segments. With the detection neutralized, the `Cipher.doFinal()` hooks intercepted the cryptographic operations and extracted the sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` in plaintext.
