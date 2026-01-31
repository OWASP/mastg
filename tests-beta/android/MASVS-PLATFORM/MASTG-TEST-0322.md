---
title: Runtime Verification of Accessibility Data Sensitivity
platform: android
id: MASTG-TEST-0322
type: [dynamic]
profiles: [L2]
best-practices: [MASTG-BEST-0029]
weakness: MASWE-0055
knowledge: [MASTG-KNOW-0108]
apis: [View, getAccessibilityDataSensitive, setAccessibilityDataSensitive, isFilterTouchesWhenObscured]
available_since: 35
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0321. It verifies that sensitive views in an Android app are properly configured with accessibility data sensitivity protection at runtime.

At runtime, you can inspect view properties to determine:

- Whether `accessibilityDataSensitive` is enabled using [`getAccessibilityDataSensitive()`](https://developer.android.com/reference/android/view/View#getAccessibilityDataSensitive())
- Whether `filterTouchesWhenObscured` is enabled using [`isFilterTouchesWhenObscured()`](https://developer.android.com/reference/android/view/View#isFilterTouchesWhenObscured()), which provides implicit accessibility protection on Android 16+
- Whether views handling sensitive data are protected appropriately

This test focuses on observing the actual runtime configuration of views to ensure protection is active when the app is running.

## Steps

1. Install the app on a device running Android 16 (API level 35) or higher (@MASTG-TECH-0005).
2. Use a dynamic analysis tool like @MASTG-TOOL-0031 (Frida) to:
    - Enumerate all active View instances in the app's UI hierarchy
    - For each view, call `getAccessibilityDataSensitive()` to check the current sensitivity setting
    - For each view, call `isFilterTouchesWhenObscured()` to check if implicit protection is enabled
    - Identify views by their resource ID, type, and context to determine if they handle sensitive data
3. Navigate through sensitive screens in the app (login, payment, personal information forms) and collect runtime view configurations.

## Observation

The output should include:

1. A list of all View instances found in the app's UI hierarchy with their:
    - Resource ID (e.g., `@id/passwordField`)
    - View type (e.g., `EditText`, `Button`)
    - `accessibilityDataSensitive` setting (from `getAccessibilityDataSensitive()`)
    - `filterTouchesWhenObscured` setting (from `isFilterTouchesWhenObscured()`)
2. A classification of views as sensitive or non-sensitive based on their ID, type, and context.
3. Any views where sensitivity settings change during app execution (e.g., via programmatic calls to `setAccessibilityDataSensitive()`).

## Evaluation

The test case fails if sensitive views are not properly protected at runtime:

- Password fields, PIN inputs, or credential entry views have `ACCESSIBILITY_DATA_SENSITIVE_NO` or `ACCESSIBILITY_DATA_SENSITIVE_AUTO` without implicit protection from `filterTouchesWhenObscured`
- Payment confirmation buttons, transaction approval actions lack accessibility sensitivity protection
- Personal information forms (SSN, credit card details) are not marked as sensitive
- Protection is inconsistently applied across related sensitive screens

The test passes if:

- All sensitive views return `View.ACCESSIBILITY_DATA_SENSITIVE_YES` from `getAccessibilityDataSensitive()`
- Or, sensitive views return `true` from `isFilterTouchesWhenObscured()`, providing implicit accessibility protection on Android 16+
- Protection is active and consistent across all screens handling sensitive data during runtime navigation
- No runtime code paths dynamically disable the protection on sensitive views without justification

**Note**: Views with `ACCESSIBILITY_DATA_SENSITIVE_AUTO` may inherit protection from parent views or from `filterTouchesWhenObscured`. Verify the effective protection by checking both the view and its parent hierarchy.

**Additional Guidance**:

If the app dynamically creates views or modifies their sensitivity settings at runtime, use method tracing or hooks to capture calls to `setAccessibilityDataSensitive()` and `setFilterTouchesWhenObscured()` to understand when and why protection is applied or removed. See @MASTG-TECH-0033 for method tracing techniques.
