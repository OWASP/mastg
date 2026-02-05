---
platform: android
title: Detecting Accessibility Data Sensitivity in Layouts with semgrep
id: MASTG-DEMO-0083
code: [xml, kotlin]
test: MASTG-TEST-0321
---

## Sample

This sample demonstrates how to detect the use of `accessibilityDataSensitive` and `filterTouchesWhenObscured` attributes in Android layout files using Semgrep. The sample includes both properly protected views and views that lack protection.

{{ activity_login.xml # MastgTest.kt }}

### activity_login.xml

The layout file contains several EditText fields for user credentials:

1. `usernameField` - Has no protection (neither `accessibilityDataSensitive` nor `filterTouchesWhenObscured`)
2. `passwordField` - Protected with `android:accessibilityDataSensitive="true"`
3. `pinField` - Protected with `android:filterTouchesWhenObscured="true"` (which provides implicit accessibility protection on Android 16+)
4. `creditCardField` - Protected with both `android:accessibilityDataSensitive="true"` and `android:filterTouchesWhenObscured="true"`

### MastgTest.kt

The Kotlin code demonstrates programmatic configuration of accessibility data sensitivity:

1. Sets `accessibilityDataSensitive` on a button using the View API
2. Checks the current state of `accessibilityDataSensitive` on views
3. Sets `filterTouchesWhenObscured` which provides implicit protection

## Steps

Let's run our @MASTG-TOOL-0110 rule against the layout file and decompiled code.

{{ ../../../../rules/mastg-android-accessibility-data-sensitive.yaml }}

{{ run.sh }}

## Observation

The rule has identified:

1. Views with explicit `accessibilityDataSensitive` attributes in XML
2. Views with `filterTouchesWhenObscured` which provide implicit accessibility protection
3. Programmatic calls to set or check accessibility data sensitivity
4. Potentially sensitive input fields that lack any protection

{{ output.txt }}

## Evaluation

Based on the Semgrep output:

**Failures (views lacking protection):**

- `usernameField` (line 12 in activity_login.xml): An EditText for username input lacks both `accessibilityDataSensitive` and `filterTouchesWhenObscured` protection. While usernames may be less sensitive than passwords, credential fields should generally be protected.

**Passes (properly protected views):**

- `passwordField` (line 23): Protected with `android:accessibilityDataSensitive="true"` - malicious accessibility services cannot read password input
- `pinField` (line 34): Protected with `android:filterTouchesWhenObscured="true"` - gains implicit accessibility protection on Android 16+ while also preventing tapjacking
- `creditCardField` (line 45): Protected with both attributes - defense in depth approach
- `confirmButton` (line 18 in MastgTest.kt): Programmatically sets `ACCESSIBILITY_DATA_SENSITIVE_YES` for a payment confirmation button
- Runtime checks (lines 22-23): The code properly checks current sensitivity settings

**Recommendations:**

1. Add `android:accessibilityDataSensitive="true"` or `android:filterTouchesWhenObscured="true"` to `usernameField`
2. Consider using `filterTouchesWhenObscured` on all credential fields for broader protection across Android versions
3. Review all payment and transaction confirmation buttons to ensure they are protected programmatically or in XML
4. Test with legitimate accessibility tools (TalkBack, Voice Access) to ensure protected views remain accessible when needed
