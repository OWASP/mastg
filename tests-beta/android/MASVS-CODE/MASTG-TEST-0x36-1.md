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

If the app relies solely on client-side version checks without backend enforcement, an attacker running an outdated app can bypass mandatory update mechanisms using a MITM proxy. By intercepting outgoing API requests and replacing the reported app version with a higher value (e.g., changing `X-App-Version: 1.0` to `X-App-Version: 3.0`), the backend receives the spoofed version, considers the app up to date, and returns a normal response, the update required signal is never sent and no blocking screen appears. This allows the attacker to continue using a vulnerable outdated version without restriction.

This test checks whether the backend independently enforces version restrictions by downgrading the reported version to a known unsupported value and verifying that the backend rejects the request and the app blocks the user in response.

## Steps

1. Set up a MITM proxy using @MASTG-TECH-0011 to intercept network traffic.
2. Launch the app and identify API calls that transmit version information (e.g., `X-App-Version`, `version`, `build`, `minVersion` in headers, parameters, or request body). If no such calls are found, this test is not applicable.
3. Modify the intercepted request to indicate an unsupported app version (e.g., change `version` to an older version).
4. Forward the modified request to the backend and observe the response.

## Observation

The output should contain the backend's response when a downgraded version number is sent, including the HTTP status code, response headers, and body specifically any fields indicating whether the backend accepted or rejected the request based on the version number, and whether the app displays a blocking dialog or screen preventing further use.

## Evaluation

The test case fails if:

- The backend returns no version related error when an unsupported version is reported.
- The app does not block access to application functionality when the backend indicates the version is unsupported.
- The app continues to function normally despite the backend response indicating an update is required.
