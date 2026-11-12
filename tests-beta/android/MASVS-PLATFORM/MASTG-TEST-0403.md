---
platform: android
title: References to Accessibility Data Sensitivity APIs
id: MASTG-TEST-0403
apis: [View.setAccessibilityDataSensitive, View.setFilterTouchesWhenObscured, ACCESSIBILITY_DATA_SENSITIVE_YES, ACCESSIBILITY_DATA_SENSITIVE_NO, ACCESSIBILITY_DATA_SENSITIVE_AUTO]
type: [static, code, manual]
maswe: [MASWE-0040]
best-practices: [MASTG-BEST-0075]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0143]
available_since: 34
---

## Overview

Accessibility services can read the content of the views an app displays and perform interactions on them. Malware often tricks users into enabling its accessibility service so it can capture typed credentials or one-time codes, read sensitive data shown on screen, or tap buttons such as payment confirmations on the user's behalf.

On Android 14 (API level 34) and higher, apps can restrict access to sensitive views to accessibility services declared as accessibility tools by marking those views as accessibility data sensitive (@MASTG-KNOW-0143). Accessibility tools, such as screen readers, can still read and interact with protected views. If the app doesn't mark the views that display or accept sensitive data, or explicitly disables the protection on them, any enabled accessibility service can access them.

This test checks whether the app marks the views that handle sensitive data as accessibility data sensitive, either explicitly or through `filterTouchesWhenObscured`. It looks for:

- The `android:accessibilityDataSensitive` and `android:filterTouchesWhenObscured` attributes in layout files.
- Calls to `setAccessibilityDataSensitive` and the value passed to it.
- Calls to `setFilterTouchesWhenObscured`.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.
3. Use @MASTG-TECH-0007 to extract the layout files from the app package.

## Observation

The output should contain:

- All `android:accessibilityDataSensitive` and `android:filterTouchesWhenObscured` XML attributes, if using XML for the UI.
- All calls to `setAccessibilityDataSensitive` and the values passed to it.
- All calls to `setFilterTouchesWhenObscured` and the values passed to it.

## Evaluation

The test case fails if any view that handles sensitive data isn't marked as accessibility data sensitive, either on the view itself or on one of its parents, or if the app sets `ACCESSIBILITY_DATA_SENSITIVE_NO` (or `android:accessibilityDataSensitive="false"`) on such a view.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the protected and unprotected views handle sensitive data:

- Determine which views handle sensitive data, such as credentials, one-time codes, payment details, or personal information, based on the app's sensitive data definitions.
- Determine whether each of these views, or one of its parents, sets `android:accessibilityDataSensitive="true"`, calls `setAccessibilityDataSensitive` with `ACCESSIBILITY_DATA_SENSITIVE_YES`, or sets `filterTouchesWhenObscured` to `true` without setting `ACCESSIBILITY_DATA_SENSITIVE_NO`.

Static analysis can only show where the protection is configured. Views for which the app doesn't configure anything don't appear in the output at all, even though they're the ones most likely to be missing the protection. To find them statically, you'd need to review every view the app creates in its layout files and code and resolve the values they inherit from their parents. Static analysis also can't reliably determine whether a view displays sensitive data or whether the protection is active at runtime. For these reasons, use the results of this test as a starting point and confirm them with @MASTG-TEST-0404, which lists every view on a screen together with its effective accessibility data sensitivity.

**Expected False Negatives:**

This test may produce false negatives if the app builds its UI with frameworks that don't use Android `View` attributes for this purpose (for example, Jetpack Compose, Flutter, or game engines), or if views are created or configured dynamically in ways that static analysis can't resolve, for example, through reflection or values computed at runtime.
