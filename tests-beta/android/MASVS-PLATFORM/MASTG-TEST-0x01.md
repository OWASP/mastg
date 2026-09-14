---
platform: android
title: Runtime Exposure of Sensitive Data Through Accessibility Services
id: MASTG-TEST-0x01
type: [dynamic, logs, manual]
weakness: MASWE-0040
profiles: [L2]
knowledge: [MASTG-KNOW-0x02]
---

## Overview

If an app exposes sensitive values through its accessibility representation, an enabled accessibility service may be able to retrieve those values even when they are not visibly displayed in plain text.

Sensitive data can appear in accessibility event data or node properties such as text, content descriptions, hints, or state descriptions. This test verifies what a running app actually exposes to an accessibility service rather than relying on the UI implementation used by the app.

This makes the test applicable to Android Views, Jetpack Compose, and cross-platform frameworks that expose their UI through Android accessibility APIs. See @MASTG-KNOW-0x02.

## Steps

1. Use @MASTG-TECH-0005 to install the app on a device.
2. Install and enable a test `AccessibilityService` that:
    - declares `android:canRetrieveWindowContent="true"`.
    - declares `android:isAccessibilityTool="false"`.
    - limits its observations to the package being tested.
    - records relevant `AccessibilityEvent` and `AccessibilityNodeInfo` properties.
3. Use @MASTG-TECH-0009 to monitor the output produced by the test accessibility service.
4. Exercise each relevant flow and enter a unique known value into fields that handle sensitive data, such as passwords, PINs, and verification codes.
5. Observe the accessibility data while each value is being entered and after input has completed. If the app supports states such as showing and hiding a password, exercise each state separately.

Relevant accessibility data includes `AccessibilityEvent.getText()`, `AccessibilityEvent.getContentDescription()`, `AccessibilityNodeInfo.getText()`, `AccessibilityNodeInfo.getContentDescription()`, `AccessibilityNodeInfo.getHintText()`, `AccessibilityNodeInfo.getStateDescription()`, `AccessibilityNodeInfo.isPassword()`, and, on Android 14 (API level 34) and later, `AccessibilityNodeInfo.isAccessibilityDataSensitive()`.

A UI Automator hierarchy dump may additionally be used to inspect the accessibility representation, but it should not replace observation through the test `AccessibilityService`.

## Observation

The output should contain the accessibility events and node properties that the test `AccessibilityService` can observe while the sensitive input flows are exercised.

Record whether the known test value, or any part of it, appears in the observed accessibility data. Also record relevant node properties such as `isPassword()` and, on Android 14 and later, `isAccessibilityDataSensitive()`.

## Evaluation

The test case fails if sensitive plaintext that is intended to remain concealed from non-tool accessibility services is exposed through accessibility events or node metadata. For example:

- The complete password, PIN, verification code, or other sensitive value appears in node text.
- Sensitive plaintext appears in `contentDescription`, `hintText`, `stateDescription`, or equivalent accessibility metadata.
- Individual plaintext characters are exposed through accessibility while the corresponding sensitive input is intended to remain concealed.
- A visually masked field exposes its underlying sensitive value through its accessibility representation.

**Further Validation Required:**

Determine whether each observed value is sensitive in the context of the tested flow and whether its exposure is intentional. For example, a value deliberately revealed after the user activates a show-password control should be evaluated separately from a value exposed while the field is in its concealed state.

`AccessibilityNodeInfo.isPassword()` should be treated as supporting information rather than a pass or fail condition by itself. A node reporting `isPassword() == true` does not establish that no sensitive value is exposed through other accessibility properties. Likewise, `isPassword() == false` does not by itself establish a failure.

`TextObfuscationMode.RevealLastTyped` should not automatically result in a failure, since it intentionally reveals the latest character visually. The relevant result for this test is whether sensitive plaintext is actually delivered to the test accessibility service while the value is intended to remain inaccessible to that service.

On Android 14 (API level 34) and later, a node protected by `accessibilityDataSensitive` may be unavailable to a service whose `isAccessibilityTool` property is `false`. In that case, absence of the protected data from the service output is the expected platform behavior.

**Expected False Negatives:**

This test may produce false negatives if:

- Relevant app flows or input states are not exercised.
- Sensitive information is exposed only through accessibility event types or node properties that the test service does not record.
- The app generates accessibility content conditionally based on focus, timing, or other runtime state.
- A custom UI framework exposes additional accessibility semantics that are not inspected by the test service.
