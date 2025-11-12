
---
platform: android
title: Overlay attack via malicious app overlay
id: MASTG-TEST-XXXX
weakness: MASWE-0056

---

### Overview

Overlay attacks, also known as `Tapjacking` or `UI redressing`, occur when a malicious app draws an overlay on top of a legitimate app to trick the user into performing unintended actions

### Steps

1. Run a static analysis tool such as @MASTG-TOOL-0110 on the xml layout files to check for presence of the `android:filterTouchesWhenObscured` attribute on sensitive views like buttons or input fields, and verify it is set to `true`.

### Observation

The output shows the declaration in the `Layout file` that is vulnerable to overlay attacks.

### Evaluation

The test fails if sensitive views (e.g., buttons for granting permissions, confirming purchases, or entering credentials) within the application's XML layout files do not include the attribute `android:filterTouchesWhenObscured="true"`.

### Context Consideration:

When evaluating overlay attack prevention at the layout or view level, the context shifts from an application-wide setting to a `fine-grained`, `view-specific defense`. Developers should apply the `android:filterTouchesWhenObscured="true"` attribute directly to individual sensitive views within a layout, such as a "Grant Access" button, a "Confirm Purchase" button, or an field used for entering passwords.
