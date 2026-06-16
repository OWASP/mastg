---
masvs_v1_id:
- MSTG-RESILIENCE-5
masvs_v2_id:
- MASVS-RESILIENCE-1
platform: ios
title: Testing Emulator Detection
masvs_v1_levels:
- R
profiles: [R]
status: deprecated
covered_by: [MASTG-TEST-0367]
deprecation_note: "New version available in MASTG V2"
---

## Overview

In order to test for emulator detection you can try to run the app on different emulators as indicated in @MASTG-KNOW-0088 and see what happens.

The app should respond in some way. For example by:

- Alerting the user and asking for accepting liability.
- Preventing execution by gracefully terminating.
- Reporting to a backend server, e.g, for fraud detection.

You can also reverse engineer the app using ideas for strings and methods from @MASTG-KNOW-0088.

Next, work on bypassing this detection and answer the following questions:

- Can the mechanisms be bypassed trivially (e.g., by hooking a single API function)?
- How difficult is identifying the detection code via static and dynamic analysis?
- Did you need to write custom code to disable the defenses? How much time did you need?
- What is your assessment of the difficulty of bypassing the mechanisms?
