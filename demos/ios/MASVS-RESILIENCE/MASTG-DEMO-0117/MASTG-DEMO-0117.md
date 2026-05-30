---
platform: ios
title: Extracting Sensitive Data from CCCrypt via Frida Hooking
code: [swift]
id: MASTG-DEMO-0117
test: MASTG-TEST-0354
kind: fail
---

## Sample

This sample encrypts and decrypts a sensitive API key using CommonCrypto's `CCCrypt`. The app does not implement any runtime hook detection mechanisms. On the contrary, @MASTG-DEMO-0118 demonstrates a runtime hook detection mechanism.

!!! note "Environment"
    This demo was built using Xcode 26.2.9 and tested on an iPhone running iOS 16.7.10 (jailbroken with Dopamine 2.4.9).

!!! note
    This is a series of correlated tests.

    - This demo is a failed test (failed defense/successful attack) against a data exfiltration attack.
    - @MASTG-DEMO-0118 is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0117.
    - @MASTG-DEMO-0119 is a failed test (failed defense/successful attack) against the defenses of @MASTG-DEMO-0118 by using a more complex attack.

{{ MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Tap the **Start** button.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output contains two `CCCrypt` calls found at runtime. The encryption call reveals the sensitive API key as plaintext input, and the decryption call reveals the same API key as plaintext output. Backtraces are also provided to help identify the locations in the code.

{{ output.txt }}

## Evaluation

The test case fails because the hook executes successfully and the sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` is extracted in plaintext from the `CCCrypt` calls. The app lacks runtime integrity verification, allowing instrumentation tools to intercept cryptographic operations without any defensive response.
