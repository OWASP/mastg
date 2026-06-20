---
platform: ios
title: Runtime Monitoring of UIActivityViewController with Frida
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: fail
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0x01.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the `frida-server` running on the device.
3. Launch the MASTestApp on the device.
4. Run `run.sh` to attach Frida to the running app.
5. Click the **Start** button in the app to trigger the UIActivityViewController presentation.
6. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

The Frida script hooks two `UIActivityViewController` APIs at runtime and logs their arguments when invoked:

- `UIActivityViewController initWithActivityItems:applicationActivities:` to capture the items passed to the share sheet and any custom activity services.
- `UIActivityViewController setExcludedActivityTypes:` to capture any activity types the app explicitly excludes.

## Observation

The output shows the activity items passed to `UIActivityViewController`. There is no log entry for `excludedActivityTypes`, indicating that the property was not set.

{{ output.txt }}

## Evaluation

The test case fails because the app initializes a `UIActivityViewController` with sensitive data (an account token string and a private URL) and does not set `excludedActivityTypes`. The absence of any `excludedActivityTypes` log entry confirms that no activity types are excluded, meaning all system activities (AirDrop, Mail, Messages, social network posting, etc.) are available to the user.
