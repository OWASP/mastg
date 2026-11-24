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

In this demo, the app responds to root detection by skipping a sensitive operation. It encrypts and decrypts a sensitive API key on the unrooted device and omits that operation on the rooted device. In both cases, it displays the results of every check.

!!! note
    The sample displays individual check results, writes them and the app's reaction to the system log, and uses descriptive method names to make the detection logic easy to follow. This detail is included for learning, not as an example of production behavior. In a production app, detection diagnostics should stay internal rather than appear in the UI or logs accessible to users or third parties. Exposing them can help an attacker understand the checks, reverse engineer the app's response, and defeat those checks.

{{ ../MASTG-DEMO-0087/MastgTest.kt }}

## Steps

1. Use @MASTG-TECH-0005 to install the app on a clean, unrooted device.
2. Open the app, tap **Start**, and run `run.sh` to capture the app's log output using @MASTG-TECH-0009. Save it as `output_unrooted.txt`.
3. Use @MASTG-TECH-0005 to install the same app on a device rooted with @MASTG-TOOL-0021.
4. Open the app, tap **Start**, and run `run.sh` again. Save it as `output_rooted.txt` and compare both captures.

{{ run.sh }}

## Observation

The app reports no root indicators on the unrooted device, but reports detected indicators on the rooted device.

### Unrooted Device

No check reports a root indicator, the overall result is `false`, and the app logs that the sensitive operation was performed.

{{ output_unrooted.txt }}

### Rooted Device

The `which su` command finds `su`, the package check finds a root management app, and the overall result is `true`. Because the app detects root, it logs that the sensitive operation was not performed.

{{ output_rooted.txt }}

## Evaluation

The test passes because the app reacts to the root indicators it detects by not performing the sensitive operation on a rooted device. The unrooted capture logs `Completed checks: rooted=false` followed by `Sensitive operation performed: decryption successful=true`, while the rooted capture logs `su found via which: path=/system_ext/bin/su`, `root package detected: package=com.topjohnwu.magisk`, `Completed checks: rooted=true` and `Sensitive operation skipped: root detected`.
