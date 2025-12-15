---
title: Verifying Mandatory In-App Update Enforcement using MITM Proxy
platform: android
id: MASTG-TEST-xxxx
type: [dynamic]
weakness: MASWE-0075
profiles: [L2]
---

## Overview

The goal of the test is to verify whether the app properly enforces mandatory updates, When using a MITM proxy to send a version that the backend considers unsupported and verify if the app correctly blocks access and requires the user to update before continuing.

## Steps

1. Set up a MITM proxy using @MASTG-TECH-0011 to intercept network traffic. Launch the app and identify API calls that transmit version information (e.g., `X-App-Version`, `version`, `build`, `minVersion` in headers, parameters, or request body).
2. Modify the intercepted request to indicate that the current app version is unsupported (e.g., change `version` to an older version or set `minVersion` to a value higher than the current version). Forward the modified request to the backend.

## Observation

The output should contain the app's response when an unsupported version is sent to the backend:

- The backend's response indicating the version is outdated (e.g., a response code, JSON field, or message stating the update is required).
- Whether the app displays a blocking dialog or screen that prevents further use until the update is completed.
- Whether the user can dismiss the update prompt and continue using the app, or if the app completely blocks access to functionality.

## Evaluation

The test case fails if:

- The app does not block access to its main functionality when the backend indicates the version is unsupported.
- The app does not trigger a mandatory update flow through either the backend-gated mechanism or the Play In-App Updates API.
- The app proceeds normally without any enforcement action when presented with an unsupported version.
