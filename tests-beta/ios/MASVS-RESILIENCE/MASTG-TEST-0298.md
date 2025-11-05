---
platform: ios
title: Runtime Detection of Reverse Engineering Tools
id: MASTG-TEST-0298
type: [dynamic]
weakness: MASWE-0104
profiles: [R]
---

## Overview

This test case verifies whether an iOS app can detect the presence of reverse engineering tools at runtime, specifically focusing on @MASTG-TOOL-0031 (Frida). Apps implementing resilience measures may include runtime checks to detect dynamic instrumentation frameworks and respond appropriately (e.g., alerting users, terminating execution, or reporting to backend servers).

The detection mechanisms tested include:

- **Dynamic Library Scanning**: Checking loaded libraries via `_dyld_image_count()` and `_dyld_get_image_name()` for Frida-related names
- **Port Detection**: Scanning for Frida's default ports (27042, 27043)
- **Thread Analysis**: Detecting abnormal thread counts via `task_threads()`, as Frida typically adds additional threads
- **File System Artifacts**: Checking for Frida-related temporary files in `/tmp` and `/var/tmp`

Note that these detection methods can be bypassed using Frida's Interceptor API to hook the detection functions and manipulate their return values.

## Steps

1. Launch the app with @MASTG-TOOL-0031 attached using `frida -U -n <app-name>` or spawn mode.
2. Exercise the app's functionality to trigger any detection mechanisms.
3. Monitor the app's response to the presence of Frida.
4. Use @MASTG-DEMO-0091 to test the detection and bypass capabilities.

## Observation

The output should contain evidence of detection mechanisms being triggered, such as:

- Security alerts displayed to the user
- App termination or graceful exit
- Log messages indicating detection of reverse engineering tools
- Network requests to backend fraud detection systems

## Evaluation

The test case evaluates the effectiveness of the detection mechanisms:

- **Detection Capability**: The test passes if the app successfully detects Frida's presence through one or more of the implemented methods.
- **Bypass Difficulty**: Document how easily the detection can be bypassed (e.g., single function hook, multiple hooks required, custom code needed).
- **Response Appropriateness**: Verify that the app's response to detection is appropriate for the app's security requirements.

Note: For assessment purposes, document the time and expertise required to bypass the detection mechanisms. Simple boolean checks that can be bypassed with trivial Frida hooks provide minimal security value, while more sophisticated detection schemes requiring custom code and significant reverse engineering effort provide better resilience.
