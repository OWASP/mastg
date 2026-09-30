---
platform: android
title: Runtime Detection of Root Detection Mechanisms
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
---

## Sample

This demo shows how to detect the presence of root detection mechanisms behaviorally by running inside a rooted device the sample app from @MASTG-DEMO-0087, which implements multiple root detection checks.

{{ ../MASTG-DEMO-0087/MastgTest.kt }}

## Steps

1. Ensure the target app is installed on the device and that @MASTG-TOOL-0021 is properly set up.
2. Run the demo.

## Observation

The demo output shows that both the `su` binary and the manager app for @MASTG-TOOL-0021 were found.

```
Root Detection Results:

✗ No su binary found
✔ Found su via which Command
✔ Found root management apps
✗ Device has release-keys build
✗ No dangerous system properties

Root Detection Results:
Device appears to be rooted: true
```

## Evaluation

The test passes because the output confirms the app implements root detection checks that effectively detect the presence of a rooting solution such as @MASTG-TOOL-0021 in the system.
