---
masvs_category: MASVS-RESILIENCE
platform: android
title: Runtime Application Self-Protection (RASP)
---

Runtime Application Self-Protection (RASP) is a security technology embedded in mobile apps to detect and prevent real-time attacks. Unlike server-side or network-based security solutions, RASP integrates directly into the app's runtime environment, enabling the app to monitor its own execution and respond to threats from within the device.

## Core Capabilities

RASP implementations typically include several defensive mechanisms:

- **Environment Detection**: Identifying rooted devices, emulators, debuggers attached to the process, or the presence of hooking frameworks.
- **Code Integrity Verification**: Ensuring the app's code has not been modified at runtime, including detecting hooks on methods and functions.
- **Anti-Tampering**: Detecting modifications to the app's binary, resources, or configuration files.
- **Anti-Debugging**: Preventing or detecting when a debugger is attached to the app's process.
- **Response Mechanisms**: Taking action when threats are detected, such as terminating the app, clearing sensitive data, or alerting a backend server.

## Implementation Approaches

RASP can be implemented in several ways:

### Self-Implemented Checks

Developers can implement their own RASP checks directly in the application code. This includes:

- Checking for root indicators (see @MASTG-KNOW-0031 and @MASTG-KNOW-0027)
- Detecting reverse engineering tools (see @MASTG-KNOW-0030)
- Verifying runtime integrity (see @MASTG-KNOW-0032)
- Implementing file integrity checks (see @MASTG-KNOW-0029)

### Commercial RASP Vendors

Several commercial solutions provide RASP capabilities as SDKs or compiler toolchains. Both may offer:

- Obfuscated detection logic that is harder to bypass
- Continuous updates to detect new threats
- Server-side attestation and threat intelligence
- Tamper-resistant implementation in native code

The biggest difference is that SDKs operate as standalone frameworks delivered within the application. They sit alongside the app's core functionality, and the app must 'call out' to the security SDK. By contrast, compiler toolchains inject detections between user code and randomly obfuscate the resulting code at each compilation.

### Google Play Integrity API

For Android apps distributed through Google Play, the Play Integrity API (see @MASTG-KNOW-0035) provides device and app attestation. It can verify:

- The app binary is the original, unmodified version from Google Play
- The app is running on a genuine Android device
- The device passes basic integrity checks

## Limitations

RASP provides defense-in-depth but has inherent limitations:

- **Bypassable**: Determined attackers with sufficient time and resources can typically bypass RASP protections, especially on rooted devices where they have full control.
- **Cat-and-Mouse**: RASP detection methods and bypass techniques evolve continuously, requiring ongoing updates.
- **Performance Impact**: Extensive runtime checks can degrade app performance and battery life.
- **False Positives**: Overly aggressive detection may incorrectly flag legitimate user environments (e.g., custom ROMs, accessibility tools).

RASP should be considered one layer in a defense-in-depth strategy, not a complete security solution. Critical security logic should also be protected server-side, where possible.
