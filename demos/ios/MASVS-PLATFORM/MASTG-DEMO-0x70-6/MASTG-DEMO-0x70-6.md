---
platform: ios
title: Triggering Universal Links via Frida
id: MASTG-DEMO-0070-6
code: [swift]
test: MASTG-TEST-0070-6
tools: [MASTG-TOOL-0039]
---

## Sample

The snippet below shows the target application code that is designed to handle and process incoming Universal Links. You can use dynamic instrumentation to programmatically trigger the operating system's routing mechanisms to hit this entry point.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida and load the URL triggering script.
4. Observe the terminal output as the script forces the app to open the target URL.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output shows the execution of the URL routing API call along with the target URL and the resulting boolean value indicating if the OS accepted the routing request.

{{ output.txt }}

## Evaluation

The test confirms that the application's URL handling mechanism can be successfully triggered by an external payload using dynamic instrumentation. The output confirms the following behavior:

- **`UIApplication.openURL_ executed`** — The script successfully called the non-public iOS routing API.
- **`Target: https://attacker.example.com...`** — The exact malicious payload being injected into the OS.
- **`Result: true`** — The operating system successfully received the URL, recognized the scheme, and accepted the responsibility of handling it.

This establishes the necessary runtime conditions to proceed with tracing the app's internal receiver methods to see how it handles the injected data.