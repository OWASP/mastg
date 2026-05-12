---
platform: ios
title: Triggering Universal Links via Frida
id: MASTG-DEMO-0x70-6
code: [swift]
test: MASTG-TEST-0x70-6
tools: [MASTG-TOOL-0039]
---

## Sample

The code snippet below shows the target application code that is designed to handle and process incoming Universal Links.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida and load the URL triggering script.
4. Observe the terminal output as the script forces the app to open the target URL and look for evidence that the app's Universal Link receiver executed, or that iOS bypassed the association and used a fallback handler instead.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output should contain the target URL, the result of the `UIApplication.openURL_` call, and additional evidence showing what happened next: either the app's Universal Link handling callback executed, or the request fell back to another handler such as Safari. `Result: true` alone is not enough to prove that the Universal Link was delivered to the app.

{{ output.txt }}

## Evaluation

The test case fails because the injected `https://` URL reaches the app's Universal Link receiver logic, or because the output shows that the Universal Link association was bypassed and the request was handled by a fallback path instead.

Review the output as follows:

- **`UIApplication.openURL_ executed`** shows that the script invoked the routing API, but this alone does not prove Universal Link delivery.
- **`Target: https://attacker.example.com...`** shows that the injected payload was passed into the routing request.
- **`Result: true`** only shows that iOS accepted the request in some form, for example by opening Safari. Treat this as supporting context, not as the deciding signal.
- Evidence that a Universal Link receiver callback executed, such as `application:continueUserActivity:restorationHandler:` or `scene:continueUserActivity:`, shows that the app processed the link.
- Evidence of fallback handling instead of receiver execution shows that the Apple App Site Association (AASA) relationship was not used for app delivery in this attempt.

If the output contains only `Result: true` without receiver-side execution or fallback evidence, the result is inconclusive for this test.
