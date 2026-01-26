---
platform: android
title: Sensitive User Data Sent to Firebase Analytics with Frida
id: MASTG-DEMO-0081
code: [kotlin]
test: MASTG-TEST-0319
---

## Sample

This sample collects the following [sensitive user data](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en#types&zippy=%2Cdata-types) and sends it to Firebase Analytics using the `logEvent` method:

- User ID (**Data type:** User IDs, **Category:** Personal info)
- Blood type (**Data type:** Health info, **Category:** Health and fitness)

For the sake of this demo, we pretend that the app is published on Google Play and that the data types collected are not disclosed in the [Data safety section](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en#types&zippy=%2Cdata-types).

{{ MainActivity.kt # MastgTest.kt # build.gradle.kts.libs }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Select a blood type from the dropdown
5. Click the **Start** button
6. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ hooks.js # run.sh }}

## Observation

The output shows all instances of `logEvent` calls to the Firebase Analytics SDK found at runtime, along with the parameters sent. A backtrace is also provided to help identify the location in the code.

{{ output.json }}

## Evaluation

This test **fails** because sensitive data (`blood_type` parameter) is being sent to Firebase Analytics via the `logEvent` method for a particular user (`user_id` parameter) and this data collection is not disclosed in the Data safety section on Google Play (as we indicated in the sample description).
