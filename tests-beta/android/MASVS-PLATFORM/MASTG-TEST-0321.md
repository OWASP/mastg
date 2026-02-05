---
title: References to Accessibility Data Sensitivity APIs
platform: android
id: MASTG-TEST-0321
type: [static]
profiles: [L2]
best-practices: [MASTG-BEST-0029]
weakness: MASWE-0055
knowledge: [MASTG-KNOW-0108]
apis: [View, setAccessibilityDataSensitive, filterTouchesWhenObscured]
available_since: 35
---

## Overview

This test verifies whether an app references Android's accessibility data sensitivity APIs. Starting with Android 16 (API level 35), developers can use the [`accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) attribute to protect sensitive views from malicious accessibility services. When set to `true` on a view, it restricts non-tool accessibility services from reading or interacting with sensitive content.

Developers can apply this protection in two ways:

1. **Explicit XML declaration**: Setting `android:accessibilityDataSensitive="true"` in layout files
2. **Programmatic API**: Calling [`setAccessibilityDataSensitive(int)`](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int)) with `View.ACCESSIBILITY_DATA_SENSITIVE_YES`
3. **Implicit via filterTouchesWhenObscured**: Views with `android:filterTouchesWhenObscured="true"` automatically inherit accessibility data sensitivity protection on Android 16+

Common failure modes include:

- Not marking sensitive views (login fields, payment buttons, personal data forms) with `accessibilityDataSensitive`
- Inconsistently applying protection across related sensitive screens
- Explicitly disabling protection on sensitive views by setting `View.ACCESSIBILITY_DATA_SENSITIVE_NO`
- Not leveraging automatic protection from existing `filterTouchesWhenObscured` flags

## Steps

1. Extract the app's layout XML files from the APK (@MASTG-TECH-0117).
2. Run a static analysis tool (@MASTG-TECH-0014) to identify:
    - Views with `android:accessibilityDataSensitive` set to `true` or `false`
    - Views with `android:filterTouchesWhenObscured="true"` (which provide implicit protection)
    - Calls to `setAccessibilityDataSensitive()` in decompiled code
    - Calls to `setFilterTouchesWhenObscured()` in decompiled code
3. Identify sensitive views based on their ID, type, or context (e.g., password fields, payment confirmation buttons).

## Observation

The output should include:

1. A list of views with `android:accessibilityDataSensitive` explicitly set in XML layouts.
2. A list of views with `android:filterTouchesWhenObscured="true"` (which gain implicit protection).
3. A list of programmatic calls to `setAccessibilityDataSensitive()` in the codebase.
4. A list of programmatic calls to `setFilterTouchesWhenObscured()` in the codebase.
5. A mapping of identified sensitive views (by ID, type, or context) that should be protected.

## Evaluation

The test case fails if sensitive views that handle or display confidential information are not protected by `accessibilityDataSensitive` or `filterTouchesWhenObscured`. Specifically:

- Password fields, PINs, credit card inputs, or other credential fields lack protection
- Payment confirmation or transaction approval buttons are unprotected
- Personal information forms (SSN, passport numbers, etc.) are not marked sensitive
- The app explicitly disables protection on sensitive views using `View.ACCESSIBILITY_DATA_SENSITIVE_NO`

The test passes if:

- All sensitive views have `android:accessibilityDataSensitive="true"` in XML or call `setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)` programmatically
- Or, sensitive views use `android:filterTouchesWhenObscured="true"` to gain implicit protection on Android 16+
- Protection is applied consistently across all screens handling sensitive data
- No code paths explicitly disable the protection without justification

**Note**: This test is applicable only for apps targeting Android 16 (API level 35) or higher. For apps targeting lower API levels, the attribute is ignored by the system, and alternative protections like `filterTouchesWhenObscured` should be evaluated separately.
