---
platform: android
title: Behavioral Detection of Root Detection Mechanisms
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
kind: pass
---

## Sample

This demo compares the behavior of the sample app from @MASTG-DEMO-0087 on an unrooted device and a rooted device.

In this demo, the app responds to root detection by displaying the check results. It does not block any functionality or close the app. Apps that treat root as a threat may instead block sensitive actions or close the app when they detect it.

!!! note
    The sample displays individual check results, writes diagnostic logs, and uses descriptive method names to make the detection logic easy to follow. This detail is included for learning, not as an example of production behavior. In a production app, detection diagnostics should stay internal rather than appear in the UI or logs accessible to users or third parties. Exposing them can help an attacker understand the checks and reverse engineer the app's response.

{{ ../MASTG-DEMO-0087/MastgTest.kt }}

## Steps

1. Use @MASTG-TECH-0005 to install the app on a clean, unrooted device.
2. Open the app.
3. Tap **Start** and observe the displayed results.
4. Use @MASTG-TECH-0005 to install the same app on a device rooted with @MASTG-TOOL-0021.
5. Open the app.
6. Tap **Start** and compare the app's response with the results from the unrooted device.

## Observation

The app behaves differently on the two devices. It reports no root indicators on the unrooted device, but reports detected indicators on the rooted device.

### Unrooted Device

No checks detect root indicators, and the overall result is `false`.

{{ output-clean.txt }}

### Rooted Device

The `which su` command finds `su`, and the package check finds a root management app. The overall result is `true`.

{{ output-rooted.txt }}

## Evaluation

The test passes because the app reports no root indicators on the unrooted device and detects root indicators on the rooted device. In this demo, displaying the results is the app's response to root detection. In a real app that treats root as a threat, that response could instead be blocking a sensitive action or closing the app.
