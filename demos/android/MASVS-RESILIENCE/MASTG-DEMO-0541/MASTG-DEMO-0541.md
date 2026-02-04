---
platform: android
title: Runtime Detection and Bypass of Root Detection
id: MASTG-DEMO-0541
code: [kotlin]
test: MASTG-TEST-0502
tools: [MASTG-TOOL-0031]
---

### Sample

This demo shows how to detect and bypass root detection mechanisms at runtime using Frida. The sample app from @MASTG-DEMO-0540 implements multiple root detection checks.

{{ ../MASTG-DEMO-0540/MastgTest.kt }}

### Steps

1. Ensure the target app is installed on the device and frida-server is running.
2. Run the Frida script using @MASTG-TECH-0142 to bypass root detection.

{{ run.sh # frida_script.js }}

The Frida script hooks common root detection methods to:

1. Monitor when root checks are performed
2. Bypass the checks by returning safe values
3. Log the detected checks for analysis

### Observation

The output shows all root detection mechanisms that were detected and bypassed during app execution.

{{ output.txt }}

### Evaluation

The test passes because the output confirms the app implements multiple root detection checks:

- **File.exists() checks** (lines 7-19): The app checks for su binaries at common locations (/system/xbin/su, /sbin/su, /system/bin/su)
- **PackageManager.getPackageInfo() checks** (lines 21-28): The app looks for root management packages (SuperSU, Magisk)
- **Build.TAGS check** (lines 30-33): The app verifies if the device has a test-keys build
- **Runtime.exec() calls** (lines 35-41): The app executes getprop commands to read system properties (ro.debuggable, ro.secure)

The successful bypass and detection of these checks demonstrates that the app implements runtime root detection, which aligns with resilience best practices. However, the fact that these checks can be bypassed with standard tools like Frida indicates that more sophisticated anti-tampering mechanisms would be needed for high-security applications.
