---
platform: android
title: Missing Overlay Protection on a Sensitive View
id: MASTG-DEMO-0103
code: [kotlin, java]
test: MASTG-TEST-0340
tools: [semgrep]
---

## Sample

The sample demonstrates three buttons performing sensitive actions, each using a different overlay protection approach:

1. **Vulnerable button:** A button without any overlay protection, making it susceptible to tapjacking attacks.
2. **Protected button:** A button with `filterTouchesWhenObscured = true` to block touches when the window is obscured.
3. **Custom protected button:** A button with a custom `onFilterTouchEventForSecurity` override that manually checks for the `FLAG_WINDOW_IS_OBSCURED` flag.

{{ MastgTest.kt # MastgTest_reversed.java }}

To demonstrate this vulnerability in a live environment, you can use @MASTG-DEMO-0104, which shows an attacker app that requests the `SYSTEM_ALERT_WINDOW` permission to draw overlays over other apps. Running the attacker app while the victim app is in the foreground lets you verify whether the unprotected button accepts touch events through an overlay.

## Steps

Let's run our @MASTG-TOOL-0110 rule against the decompiled code to detect overlay protection mechanisms.

{{ ../../../../rules/mastg-android-overlay-protection.yml }}

{{ run.sh }}

## Observation

The rule detected two instances of overlay protection mechanisms in the decompiled code:

{{ output.txt }}

- Line 54: `setFilterTouchesWhenObscured(true)` is set on the protected button.
- Lines 64-72: `onFilterTouchEventForSecurity` is overridden to implement custom overlay protection, including a check for the `FLAG_WINDOW_IS_OBSCURED` flag (literal value `1`) at line 67.

## Evaluation

The test partially passes and partially fails:

**FAIL:** The first button ("Vulnerable: Confirm Payment") doesn't appear in the output, meaning it implements no overlay protection. This button performs a sensitive action (payment confirmation) and is susceptible to tapjacking attacks.

**PASS:** The second button (line 54) correctly implements overlay protection using `setFilterTouchesWhenObscured(true)`, which filters out touch events when the view is obscured by another window.

**PASS:** The third button (lines 64-72) implements custom overlay protection by overriding `onFilterTouchEventForSecurity` and checking the `FLAG_WINDOW_IS_OBSCURED` flag. This provides fine-grained control over how the app responds to overlay attempts.

In a real-world assessment, the unprotected button should be flagged as a finding. Sensitive UI elements such as payment confirmations, permission grants, or authentication controls should implement overlay protection using one of the demonstrated mechanisms.
