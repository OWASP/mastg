---
platform: ios
title: Implementation Details Exposed in App Logs
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
---

## Sample

This demo uses the sample from @MASTG-DEMO-0x01.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

## Steps

1. Install and launch the application on an iOS device or simulator.
2. Start monitoring system logs as described in @MASTG-TECH-0060.
3. Interact with the application and trigger the vulnerable logging flow from the UI.
4. Capture the emitted runtime logs.

## Observation

Monitoring system logs during runtime reveals that the application emits verbose debug and error messages containing internal implementation details. The captured logs are stored in `system_log.txt`, while `output.txt` contains the PID used during the run.

{{ system_log.txt # output.txt }}

## Evaluation

The test fails because runtime log monitoring shows that the application emits internal implementation details through multiple logging APIs.

The observed logs reveal:

- **Internal endpoints and routes**: The logs disclose an internal authentication endpoint, `https://internal-api.example.com/v2/auth/login`, and the route `/v2/auth/login`.
- **Detailed error information**: The logs expose error descriptions, domains, codes, `userInfo` contents, retry-related flags, and module-level context.
- **Internal implementation details**: The logs reveal authentication flow behavior, offline fallback handling, validation logic, and internal module names such as `AuthenticationService.validateCredentials()` and `NetworkManager`.
- **Execution context**: The logs include stack trace information that exposes internal symbols, function names, and code paths, for example `$s10MASTestApp9MastgTestC20handleRealisticErroryyFZ`.
- **Debug state**: The logs indicate that a debug configuration is active, `[DEBUG] Debug configuration active`.

The logs also expose a username, `testuser`, and a request identifier, `REQ-12345`, in logged metadata. Those elements are better treated as sensitive data and should be assessed under @MASTG-TEST-0296 instead.
