---
id: MASTG-BEST-0317
title: Protect Sensitive UI Elements from Overlay and Accessibility Abuse
platform: android
---

## Overview

Android apps that display sensitive UI elements can be exposed to overlay-based
attacks and accessibility abuse if interaction is not properly restricted.

## Best Practices

- Enable `filterTouchesWhenObscured` on sensitive UI elements to prevent user
  interaction when the app window is obscured by overlays.
- Apply UI protections selectively to security-critical views such as login,
  OTP, and transaction confirmation screens.
- Validate UI behavior under overlay and accessibility abuse scenarios to ensure
  that unauthorized interaction is blocked as expected.
  ## References

- Android View Security: `View#setFilterTouchesWhenObscured`

