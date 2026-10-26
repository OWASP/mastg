---
platform: android
title: Sensitive Views Without Accessibility Data Sensitivity
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
---

## Sample

The sample creates a login and payment form with views that handle sensitive data, each using a different accessibility data sensitivity configuration. It does so by using Kotlin and the Android `View` APIs (instead of Android layout XML).

1. **Username field:** isn't marked as accessibility data sensitive.
2. **Password field:** is explicitly marked as accessibility data sensitive using `setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)`.
3. **One-time code field:** sets `filterTouchesWhenObscured`, which implies accessibility data sensitivity.
4. **Payment container:** a `LinearLayout` that is explicitly marked as accessibility data sensitive using `setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)`. It contains two views:
    - **Card number field:** keeps the default value, `ACCESSIBILITY_DATA_SENSITIVE_AUTO`, so it inherits the protection from the payment container.
    - **Confirm Payment button:** explicitly disables the protection using `setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_NO)`, which overrides the value inherited from the payment container.
5. **Cancel button:** is outside the payment container and keeps the default value, `ACCESSIBILITY_DATA_SENSITIVE_AUTO`. It doesn't handle sensitive data, so it isn't protected.

Marking a view as accessibility data sensitive only restricts accessibility services that aren't declared as accessibility tools. Accessibility tools, such as screen readers, can still read and interact with all of these views (see "Accessibility Tools" in @MASTG-KNOW-0x01).

When you tap **Start**, the app shows the form at the bottom of the screen and lists the value returned by `isAccessibilityDataSensitive` for each view. Tapping **Confirm Payment** or **Cancel** closes the form and shows a confirmation message.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reverse-engineered code.

{{ ../../../../rules/mastg-android-accessibility-data-sensitive.yaml }}

{{ run.sh }}

## Observation

The rule reports the calls to `setAccessibilityDataSensitive` and `setFilterTouchesWhenObscured` in the reverse-engineered code. In the decompiled code, the constants appear as their integer values: `1` for `ACCESSIBILITY_DATA_SENSITIVE_YES` and `2` for `ACCESSIBILITY_DATA_SENSITIVE_NO`.

{{ output.txt }}

- Line 64: the password field calls `setAccessibilityDataSensitive(1)`.
- Line 67: the one-time code field calls `setFilterTouchesWhenObscured(true)`.
- Line 71: the payment container calls `setAccessibilityDataSensitive(1)`.
- Line 82: the **Confirm Payment** button calls `setAccessibilityDataSensitive(2)`.

## Evaluation

The test case fails because two views that handle sensitive data aren't accessibility data sensitive:

- The username field (line 61) doesn't appear in the output because the app doesn't configure any protection for it.
- The **Confirm Payment** button (line 82) is reported by the `mastg-android-accessibility-data-sensitive-disabled` rule because the app calls `setAccessibilityDataSensitive(2)` on it, which overrides the protection inherited from the payment container.

The remaining views are protected:

- The password field (line 64) is explicitly marked as accessibility data sensitive.
- The one-time code field (line 67) sets `filterTouchesWhenObscured` and keeps the default `ACCESSIBILITY_DATA_SENSITIVE_AUTO` value, so it's treated as accessibility data sensitive.
- The card number field (line 72) doesn't appear in the output, but the payment container (line 71) is marked as accessibility data sensitive and the field keeps the default value, so it inherits the protection.

Note that the output only lists the views for which the app configures the protection. Views without any configuration, such as the username field (line 61) and the **Cancel** button (line 85), don't appear, so you have to find them by reviewing the reverse-engineered code. This is manageable in a small sample but hard in a real app. @MASTG-DEMO-0x02 shows how enumerating the views at runtime lists every view on the screen, including these, together with its effective value.

When you tap **Start**, the app confirms these results at runtime by listing the value returned by `isAccessibilityDataSensitive` for each view: `false` for the username field and the **Confirm Payment** button, and `true` for the other fields.
