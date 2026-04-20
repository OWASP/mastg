---
platform: android
title: Overlay Attack Protection Implementation
id: MASTG-DEMO-0x01
code: [kotlin, java]
test: MASTG-TEST-0x01
tools: [semgrep]
---

### Sample

This sample demonstrates different approaches to protecting against overlay attacks in Android apps. It includes both a vulnerable implementation (without protection) and secure implementations using various overlay protection mechanisms.

{{ MastgTest.kt # MastgTest_reversed.java }}

The code shows three buttons:

1. **Vulnerable button** - A Compose button without any overlay protection, making it susceptible to tapjacking attacks
2. **Protected button** - A traditional Android View Button with `filterTouchesWhenObscured = true` to block touches when the window is obscured
3. **Custom protected button** - A button with a custom implementation that overrides `onFilterTouchEventForSecurity` to manually check for the `FLAG_WINDOW_IS_OBSCURED` flag

### Steps

Let's run our semgrep rule against the decompiled code to detect overlay protection mechanisms.

{{ ../../../../rules/mastg-android-overlay-protection.yml }}

{{ run.sh }}

### Observation

The semgrep rule detected three instances of overlay protection mechanisms in the code:

{{ output.txt }}

The output shows:

1. Line 59: `setFilterTouchesWhenObscured(true)` - enabling touch filtering on the protected button
2. Lines 73-79: `onFilterTouchEventForSecurity` - custom override implementation
3. Line 74: Check for `FLAG_WINDOW_IS_OBSCURED` flag - detecting when the window is obscured

### Evaluation

The test partially passes and partially fails:

**FAIL:** The first button (lines 38-48 in the Kotlin code, not shown in the output) does not implement any overlay protection. This button performs a sensitive action (payment confirmation) and should be protected against overlay attacks.

**PASS:** The second button (line 59 in the decompiled output) properly implements overlay protection using `setFilterTouchesWhenObscured(true)`, which will filter touch events when the view is obscured by another window.

**PASS:** The third button (lines 73-79 in the decompiled output) implements custom overlay protection by overriding `onFilterTouchEventForSecurity` and manually checking the `FLAG_WINDOW_IS_OBSCURED` flag. This provides fine-grained control over how the app responds to overlay attempts.

In a real-world assessment, the vulnerable button should be flagged as a finding. Sensitive UI elements such as payment confirmations, permission grants, or authentication buttons should implement overlay protection using one of the demonstrated mechanisms.
