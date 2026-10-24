---
platform: ios
title: Detecting Frida Hooks Before Sensitive Cryptographic Operations
code: [swift]
id: MASTG-DEMO-0118
test: MASTG-TEST-0354
kind: pass
---

## Sample

This sample encrypts and decrypts a sensitive API key using CommonCrypto's `CCCrypt`. Unlike the unprotected variant in @MASTG-DEMO-0117, this version checks whether a Frida control endpoint is reachable on the device before performing sensitive cryptographic operations. The detection logic attempts to connect to `127.0.0.1` on Frida's default local port (`27042`), which is commonly exposed by `frida-server` on jailbroken/rooted iOS devices, and sends a D-Bus `AUTH` probe. If the endpoint responds like a D-Bus service, the app treats it as a Frida runtime artifact and terminates before any cryptographic operations are performed.

!!! note "Environment"
    This demo was built using Xcode 26.2.9 and tested on an iPhone running iOS 16.7.10 (jailbroken with Dopamine 2.4.9).

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0117 is a failed test (failed defense/successful attack) against a data exfiltration attack.
    - This demo is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0117.
    - @MASTG-DEMO-0119 is a failed test (failed defense/successful attack) against the defenses of @MASTG-DEMO-0118 by using a more complex attack.

{{ MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Tap the **Start** button.
5. Observe that the app terminates before the hooks can capture any data.

{{ run.sh # script.js }}

## Observation

The output contains no `CCCrypt` calls found at runtime. The app terminated after the script loaded but before any hooks could capture the sensitive API key.

{{ output.txt }}

## Evaluation

The test passes because the hooking attempt fails due to the app's defensive response. In this setup, `frida-server` exposes a local Frida endpoint while the app is spawned with Frida. The app probes `127.0.0.1:27042` by sending a D-Bus `AUTH` message; when the endpoint returns a D-Bus authentication response, the app terminates before `CCCrypt` hooks execute, so no sensitive data is extracted.

!!! note
    Even if the test passes, it might still be possible to bypass the app's defensive response. For example, an attacker could run Frida on non-default ports or hook `connect`, `send`, or `recv` so the app cannot observe the D-Bus authentication response. @MASTG-DEMO-0119 demonstrates such a bypass. @MASTG-KNOW-0087 describes common reverse engineering tool detection techniques and their limitations.
