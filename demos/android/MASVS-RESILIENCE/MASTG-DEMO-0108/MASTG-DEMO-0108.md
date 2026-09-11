---
platform: android
title: Bypassing Frida Detection in /proc/self/maps to Extract Sensitive Data
id: MASTG-DEMO-0108
code: [kotlin]
test: MASTG-TEST-0341
kind: fail
---

## Sample

This sample uses the same code as @MASTG-DEMO-0107, which encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore. The code includes a runtime hook detection mechanism that scans `/proc/self/maps` for Frida-related libraries and terminates the process via `Process.killProcess()` if any are found. This demo demonstrates bypassing the detection by hooking `BufferedReader.readLine()` to hide Frida entries from `/proc/self/maps`, causing `detectHooking()` to return `false` so the termination path is never reached.

See @MASTG-KNOW-0030 and @MASTG-KNOW-0032 for more context on bypassing runtime detection mechanisms.

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0106 is a failed test (failed defence/successful attack) against a data exfiltration attack.
    - @MASTG-DEMO-0107 is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0106.
    - This test is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0107 by using a more "complex" attack.

{{ ../MASTG-DEMO-0107/MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Run `run.sh` to spawn the app with the bypass script
3. Click the **Start** button
4. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ bypass.js # run.sh }}

## Observation

The output contains eight `frida-agent-64.so` memory segments filtered from `/proc/self/maps` across two scans. Two `Cipher.doFinal()` calls are captured: one in `ENCRYPT_MODE` with the plaintext input `sk-OWASP-MAS-SuperSecretKey-1234567890`, and another in `DECRYPT_MODE` with the same plaintext as output.

{{ output.txt }}

## Evaluation

The test fails because the `BufferedReader.readLine()` hook successfully concealed all Frida memory segments from `/proc/self/maps`, causing `detectHooking()` to return `false`. With detection bypassed, the app proceeded with its cryptographic operations, which were intercepted by the `Cipher.doFinal()` hooks to extract the sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` in plaintext.
