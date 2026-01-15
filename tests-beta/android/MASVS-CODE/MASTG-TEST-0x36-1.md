---
title: Testing Mandatory Update Enforcement via Version Downgrade using MITM Proxy
platform: android
id: MASTG-TEST-0x36-1
type: [dynamic]
weakness: MASWE-0075
profiles: [L2]
---

## Overview

The goal of the test is to verify whether the app properly enforces mandatory version checks when the version number of the app is downgraded to an older version while intercepting the traffic using a MITM proxy. The objective is to determine whether the backend correctly verifies unsupported application versions and prevents further application usage.

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
