---
platform: ios
title: Tracing Outgoing App Links with Frida
id: MASTG-DEMO-0070-9
code: [swift]
test: MASTG-TEST-0070-9
tools: [MASTG-TOOL-0039]
---

## Sample

The snippet below shows sample code that blindly passes an unvalidated URL into `UIApplication.shared.open`, allowing the application to trigger external apps or URI schemes.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida and load the outgoing URL tracing script.
4. Interact with the app to trigger the logic that routes the user to an external link.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output alerts the tester that the application is actively attempting to hand off an external URL to the operating system, capturing the exact destination scheme.

{{ output.txt }}

## Evaluation

The test fails because the application dynamically executes an outgoing link containing an untrusted custom scheme without sanitization. The output confirms the following behavior:

- **`[!] DYNAMIC ALERT (Outgoing): App is executing an outgoing link:`** — Confirms the `-[UIApplication openURL:options:completionHandler:]` API was invoked by the application.
- **`-> malicious-app://steal-data?payload=123`** — The app blindly attempted to trigger a dangerous, unregistered URI scheme, making it vulnerable to URI Scheme Hijacking.
