---
platform: android
title: Overlay attack via malicious app overlay
id: MASTG-TEST-XXXX
type: [static]
weakness: MASWE-0056
profiles: [L2]
---

### Overview

An Overlay Attack on Android is a user-interface (UI) based attack where a malicious application displays a visual layer on top of another legitimate application’s interface. This overlay can deceive the user into interacting with elements that appear to be part of the genuine app, but actually belong to the malicious overlay. The goal of the testcase is to verify whether the application layouts using `android:filterTouchesWhenObscured` is enabled in the xml layouts.

### Steps

1. Inspect `XML` layout files using @MASTG-TECH-0014 for the presence of the `android:filterTouchesWhenObscured` attribute on sensitive views like buttons or input fields, and verify it is set to `true`.

### Observation

The output shows the declaration of a sensitive activity in the `Layout file` that is vulnerable to overlay attacks.

### Evaluation

The test fails if sensitive views (e.g., buttons for granting permissions, confirming purchases, or entering credentials) within the application's XML layout files do not include the attribute `android:filterTouchesWhenObscured="true"`.

**Context Consideration**:

When evaluating overlay attack prevention at the layout or view level, the context shifts from an application-wide setting to a `fine-grained`, `view-specific defense`. Developers should apply the `android:filterTouchesWhenObscured="true"` attribute directly to individual sensitive views within a layout, such as a "Grant Access" button, a "Confirm Purchase" button, or an field used for entering passwords.
