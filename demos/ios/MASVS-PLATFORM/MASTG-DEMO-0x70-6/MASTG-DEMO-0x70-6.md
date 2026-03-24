---
platform: ios
title: Triggering Universal Links via Frida
id: MASTG-DEMO-0070-6
code: [swift]
test: MASTG-TEST-0070-6
tools: [MASTG-TOOL-0039]
---

## Sample

The code snippet below shows the target application code that is designed to handle and process incoming Universal Links.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida and load the URL triggering script.
4. Observe the terminal output as the script forces the app to open the target URL.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output contains the result of the URL routing API call, including the target URL and a boolean value indicating whether the OS accepted the routing request.

{{ output.txt }}

## Evaluation

The test fails if the OS returns `true`, confirming the routing request was accepted. The output shows:

- **`UIApplication.openURL_ executed`** — The non-public iOS routing API was successfully called.
- **`Target: https://attacker.example.com...`** — The injected malicious payload was passed to the OS.
- **`Result: true`** — The OS recognized the scheme and accepted the routing request.
