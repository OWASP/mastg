---
platform: ios
title: Runtime Tracing of Permission API Calls with Frida
id: MASTG-DEMO-0x69-3
code: [swift]
test: MASTG-TEST-0x69-3
tools: [MASTG-TOOL-0039]
---

## Sample

The snippet below shows sample code that requests access to protected resources requiring permissions.

{{ ../MASTG-DEMO-0x69/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida and load the permission tracing script.
4. Interact with the app to trigger permission requests.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output contains each intercepted permission API call with the permission name, authorization status (GRANTED or DENIED), and additional details such as the specific status value returned by the system.

{{ output.txt }}

## Evaluation

The test fails because the app requests excessive permissions at runtime not justified by its core functionality. The output confirms the following permissions are actively requested and granted:

- **Location** — requests both `WhenInUse` and `Always` authorization
- **Camera** — granted access
- **Microphone** — granted access
- **Contacts** — granted access
- **Calendar** — granted access
- **HealthKit** — granted access
- **Motion** — granted access
- **PhotoLibrary** — granted access
- **Notifications** — granted access

This confirms that the statically declared permissions in `Info.plist` and entitlements are actually exercised during app execution.
