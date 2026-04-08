---
title: Testing Mandatory Update Enforcement via Version Downgrade using MITM Proxy
platform: android
id: MASTG-TEST-0x36-1
type: [dynamic]
weakness: MASWE-0075
profiles: [L2]
knowledge: [MASTG-KNOW-0023]
---

## Overview

If the app relies solely on client-side version checks without backend enforcement, an attacker can bypass mandatory update mechanisms by intercepting and modifying network traffic. By downgrading the reported app version via a MITM proxy, an attacker can determine whether the backend properly validates the application version and blocks access for unsupported versions. If the backend does not enforce version restrictions, users may continue using outdated app versions with known vulnerabilities.

## Steps

1. Set up a MITM proxy using @MASTG-TECH-0011 to intercept network traffic.
2. Launch the app and identify API calls that transmit version information (e.g., `X-App-Version`, `version`, `build`, `minVersion` in headers, parameters, or request body).
3. Modify the intercepted request to indicate an unsupported app version (e.g., change `version` to an older version).
4. Forward the modified request to the backend and observe the response.

## Observation

The output should contain the backend's response when a modified version number is sent:

- The backend's response indicating the version is outdated (e.g., a response code, JSON field, or message stating an update is required).
- Whether the app displays a blocking dialog or screen that prevents further use until the update is completed.

## Evaluation

The test case fails if:

- The app does not block access to application functionality when the backend indicates the version is unsupported.
- The app continues to function normally despite the backend response indicating an update is required.
