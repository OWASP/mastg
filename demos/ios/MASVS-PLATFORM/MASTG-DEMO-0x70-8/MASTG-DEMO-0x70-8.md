---
platform: ios
title: Tracing the Universal Link Receiver Method with Frida
id: MASTG-DEMO-0070-8
code: [swift]
test: MASTG-TEST-0070-8
tools: [MASTG-TOOL-0039]
---

## Sample

The code snippet below shows sample code that blindly extracts and processes an incoming Universal Link payload from the operating system without validation.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida and load the receiver tracing script.
4. Interact with the app (e.g., triggering an inbound Universal Link) to trigger the routing logic.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output contains the intercepted Objective-C method call, confirming that the application actively read from the `NSUserActivity` object to extract the raw URL payload.

{{ output.txt }}

## Evaluation

The test fails because the application dynamically receives an untrusted Universal Link and immediately pulls the attacker-controlled payload into active memory for processing. The output confirms the following behavior:

- The `[!] DYNAMIC ALERT (Receiver): App is reading incoming Universal Link:`, confirms that the `-[NSUserActivity webpageURL]` selector was executed by the app.
- The app accepts the following URL `https://attacker.example.com/reset_password?token=malicious_123`. This confirms that at runtime, the app accepted the attacker controlled domain and malicious token without rejecting them.
