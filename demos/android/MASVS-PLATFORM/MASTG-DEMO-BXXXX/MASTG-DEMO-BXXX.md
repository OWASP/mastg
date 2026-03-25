---
platform: android
title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
id: MASTG-DEMO-0x07
code: [kotlin]
tools: [MASTG-TOOL-0015]
status: new
kind: fail
---

## Sample

The code snippet below shows sample code that insecurely exposes database records through an exported `ContentProvider`.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

1. Install the app on a device using @MASTG-TECH-0005.
2. Access the ADB shell on the device using @MASTG-TECH-0001.
3. Run `run.sh` to query the exported content provider from an external context.

{{ run.sh }}

## Observation

The output should contain the database records returned through the exported content provider.

{{ output.txt }}

## Evaluation

The test fails because the application exposes sensitive stored data through an exported database-backed content provider without enforcing appropriate access restrictions. External callers can query credential records via IPC, demonstrating that sensitive stored data is accessible outside the application sandbox.
