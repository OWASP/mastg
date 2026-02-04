---
platform: android
title: Runtime Use of Root Detection Techniques
id: MASTG-TEST-0502
type: [dynamic]
weakness: MASWE-0097
best-practices: [MASTG-BEST-0028]
profiles: [R]
knowledge: [MASTG-KNOW-0027]
---

## Overview

Android apps may implement root detection to identify whether the device has been rooted at runtime. If the app does not implement effective root detection, it becomes easier for attackers to perform dynamic analysis, hook into sensitive methods, bypass security controls, or extract sensitive data on rooted devices.

This test verifies whether an application implements runtime root detection by attempting to bypass common root detection mechanisms using automated tools. If the bypass tools successfully identify and neutralize root detection checks, it confirms that the app is performing root detection.

The test works by running the app on a rooted device or in an environment with root artifacts, then using dynamic instrumentation tools to hook common root detection methods and observe the app's behavior.

## Steps

1. Run @MASTG-TECH-0142 to bypass root detection checks in the application.
2. Observe the console output and the app's behavior to identify which root detection mechanisms were triggered and bypassed.

## Observation

The output should include any instances of root detection checks that were intercepted and bypassed, along with the methods or APIs that were hooked.

## Evaluation

The test passes if the bypass tool indicates that the application is testing for known root artifacts or behaviors (e.g., presence of `su` binary, root management apps, or suspicious processes).

The test fails if root detection is not implemented. However, note that this test relies on the bypass tool's ability to detect common root checks. More sophisticated or obfuscated detection mechanisms may not be caught by automated tools and may require manual reverse engineering and custom Frida scripts to identify and bypass.
