---
platform: android
title: Runtime Verification of Accessibility Data Sensitivity
id: MASTG-TEST-0x02
apis: [View.isAccessibilityDataSensitive, View.setAccessibilityDataSensitive, View.getFilterTouchesWhenObscured, View.setFilterTouchesWhenObscured]
type: [dynamic, hooks, manual]
maswe: [MASWE-0040]
best-practices: [MASTG-BEST-0x01]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0x01]
available_since: 34
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x01.

If the views that display or accept sensitive data aren't accessibility data sensitive at runtime, any enabled accessibility service can read their content or perform interactions on them, regardless of how the app's layout files and code appear to configure them. For example, the app may change the setting of a view at runtime, or a view may lose the protection inherited from a parent.

This test combines two approaches:

- Hook `setAccessibilityDataSensitive` and `setFilterTouchesWhenObscured` to capture when and where the app changes these settings.
- Obtain the UI hierarchy of each screen that handles sensitive data, including the effective state of each view as returned by `isAccessibilityDataSensitive` and `getFilterTouchesWhenObscured`. This reveals views that inherit the protection from a parent and views for which the app never configures the protection, which the hooks alone don't show.

Run this test on a device running Android 14 (API level 34) or higher, since the protection isn't available on earlier versions.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
4. Use @MASTG-TECH-0x01 to obtain the UI hierarchy of each screen that handles sensitive data, including the effective accessibility data sensitivity of each view.
5. Use @MASTG-TECH-0x02 to take a screenshot of each screen that handles sensitive data.

## Observation

The output should contain:

- The calls to `setAccessibilityDataSensitive` and `setFilterTouchesWhenObscured` with their argument values and backtraces.
- The UI hierarchy of each screen that handles sensitive data, including the class, resource ID, and hint or text of each view, and the values returned by `isAccessibilityDataSensitive` and `getFilterTouchesWhenObscured`.
- A screenshot of each screen that handles sensitive data.

## Evaluation

The test case fails if any view that handles sensitive data returns `false` from `isAccessibilityDataSensitive`.

**Further Validation Required:**

Inspect the screenshots of the screens that handle sensitive data to determine which views display or accept sensitive information, such as credentials, one-time codes, payment details, or personal information. Match these views with the UI hierarchy of the same screen to determine whether they're protected.

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023 to determine whether the app disables the protection on sensitive views, for example, by calling `setAccessibilityDataSensitive` with `ACCESSIBILITY_DATA_SENSITIVE_NO`.

Without a tester monitoring the screens, there's no reliable way to determine whether the displayed information is sensitive. Automated classification, for example with an LLM, can help, but it may miss the screens that display the data this API is intended to protect, so you must confirm the results manually.
