---
platform: ios
title: Bypassing Frida D-Bus Port Detection to Extract Sensitive Data
code: [swift]
id: MASTG-DEMO-0119
test: MASTG-TEST-0354
kind: fail
---

## Sample

This sample uses the same code as @MASTG-DEMO-0118, which encrypts and decrypts a sensitive API key using CommonCrypto's `CCCrypt`. The code includes a runtime hook detection mechanism that probes `127.0.0.1:27042` with a D-Bus `AUTH` message and terminates via `exit(0)` if a D-Bus endpoint responds. This demo demonstrates bypassing the detection by hooking `connect()` to block connections to Frida's default port, causing `detectHooking()` to return `false` so the termination path is never reached.

See @MASTG-KNOW-0087 for more context on bypassing runtime detection mechanisms.

!!! note "Environment"
    This demo was built using Xcode 26.2.9 and tested on an iPhone running iOS 16.7.10 (jailbroken with Dopamine 2.4.9).

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0117 is a failed test (failed defense/successful attack) against a data exfiltration attack.
    - @MASTG-DEMO-0118 is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0117.
    - This demo is a failed test (failed defense/successful attack) against the defenses of @MASTG-DEMO-0118 by using a more complex attack.

{{ ../MASTG-DEMO-0118/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and frida-server running on the device.
3. Run `run.sh` to spawn the app with the bypass script.
4. Tap the **Start** button.
5. Stop the script by pressing `Ctrl+C`.

{{ bypass.js # run.sh }}

## Observation

The output shows that the `connect()` call to a Frida port was blocked, followed by two `CCCrypt` calls found at runtime. The encryption call reveals the sensitive API key as plaintext input, and the decryption call reveals the same API key as plaintext output.

{{ output.txt }}

## Evaluation

The test case fails because the `connect()` hook successfully prevented the D-Bus port detection from reaching Frida's endpoint, causing `detectHooking()` to return `false`. With detection bypassed, the app proceeded with its cryptographic operations, which were intercepted by the `CCCrypt` hooks to extract the sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` in plaintext.
