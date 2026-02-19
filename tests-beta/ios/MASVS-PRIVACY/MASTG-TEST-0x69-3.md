---
platform: ios
title: Runtime Use of Permission APIs
id: MASTG-TEST-0x69-3
type: [dynamic]
weakness: MASWE-0117
profiles: [P]
---

## Overview

If an app requests permissions at runtime that are not justified by its core functionality, it may be collecting sensitive user data (location, contacts, camera, notifications) beyond what is necessary. This can lead to privacy violations or indicate hidden data collection behavior not apparent from static analysis alone.

Dynamic analysis reveals which permissions are actually requested at runtime and under what conditions — exposing cases where declared permissions are exercised unnecessarily or without proper user context.

## Steps

1. Use @MASTG-TOOL-0039 to hook iOS permission APIs.
2. Interact with the app to trigger permission requests and observe the hooked API calls.

## Observation

The output should contain a list of permission API calls made at runtime, including the permission type and the authorization status.

## Evaluation

The test case fails if the app requests permissions at runtime that are not justified by its core functionality or the current user context.
