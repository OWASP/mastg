---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via Database-Backed IPC Mechanisms
id: MASTG-DEMO-AXXX
code: [kotlin]
tools: [MASTG-TOOL-0015]
kind: fail
---

## Sample

The code snippet below shows sample code that insecurely exposes database records through an exported `ContentProvider`.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

1. Install the app on a device.
2. Make sure you have drozer agent running on the device.
3. Run `run.sh` to spawn the app with drozer.

{{ run.sh }}

## Observation

The output should contain the database records returned through the exported content provider.

{{ output.txt }}

## Evaluation

The test case fails because the application exposes sensitive stored data through an exported database-backed content provider without enforcing appropriate access restrictions.

- The exported provider allows external callers to access the database-backed IPC entry point.
- The returned credential records show that the provider exposes sensitive stored data from the app's internal database.
- External callers can query usernames, passwords, and related notes through IPC.
- This demonstrates that sensitive stored data is accessible outside the application sandbox.
