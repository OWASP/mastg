---
title: "`setSecure` Not Used to Prevent Screenshots in SurfaceViews"
platform: android
id: MASTG-TEST-0293
type: [static]
profiles: [L2]
best-practices: [MASTG-BEST-0014, MASTG-BEST-0017]
weakness: MASWE-0055
threat: [app, root]
---

## Overview

This test verifies whether an app references Android screen capture prevention APIs for components that don't support [`FLAG_SECURE`](https://developer.android.com/security/fraud-prevention/activities#flag_secure).

When this protection is enabled, Android blocks screenshots, prevents content from appearing on non-secure displays (including remote screen sharing), and blocks capture through MediaProjection or virtual displays. Screenshots are blocked (resulting images are blank or obscured and the system may show a policy message), and the app's content is not shown in the Recents screen when backgrounded.

**SurfaceViews:**

`SurfaceView` creates a separate surface that is not protected by `FLAG_SECURE` even if the containing window has it set. Developers could be explicitly using [`setSecure(false)`](https://developer.android.com/reference/android/view/SurfaceView#setSecure(boolean)) or not calling it at all, which results in the default behavior of allowing screenshots.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test case fails if, for **any UI component that displays sensitive data**, the relevant API calls are missing, inconsistently applied, or the protection is explicitly cleared.
