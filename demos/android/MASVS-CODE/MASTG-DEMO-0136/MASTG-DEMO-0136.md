---
id: MASTG-DEMO-0136
title: Internal Activity Communication via Implicit Intent
platform: android
code: [kotlin, xml]
kind: fail
---

## Sample

The following code implements an application that attempts to invoke an implicit intent to open an internal activity. This approach is insecure because a system dialog (picker) will open if multiple apps handle the action, and a user might accidentally choose the wrong app, leading to intent hijacking. For more details on the intent resolution mechanism, see @MASTG-KNOW-0137.

The intent in this sample is implicit because it relies solely on `setAction` without explicitly defining the target component. For the secure alternative, see @MASTG-BEST-0056. In this demo, we use a custom @MASTG-TOOL-0110 rule to statically check for these insecure patterns.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

### Static Analysis

Let's run our @MASTG-TOOL-0110 rule against the sample code to detect the use of implicit intents for internal communication.

{{ rule.yaml }}

{{ run.sh }}

### Observing the System Picker Dialog (Optional)

To observe the implicit intent resolution in action, you can install a second app that registers for the same action and trigger the picker dialog on a real device:

1. Build and install the attacker app from @MASTG-DEMO-0140 on the device (@MASTG-TECH-0005).
2. Install the main app on the same device (@MASTG-TECH-0005).
3. Launch the main app and tap **Start**.
4. The system presents a chooser dialog listing both apps as candidates for handling `INTERNAL_ACTION`.

<img src="implicit-intent-choose-app.png" width="50%" />

## Observation

The @MASTG-TOOL-0110 scan identifies the vulnerable code block where the implicit intent is created and used to start an activity.

{{ output.txt }}

## Evaluation

The test case fails because the app uses an implicit intent (`INTERNAL_ACTION`) to start a component that is intended to be internal. Static analysis confirms the insecure code pattern. The intent resolution mechanism that enables this issue is described in @MASTG-KNOW-0137.
