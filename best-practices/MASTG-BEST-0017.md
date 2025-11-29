---
title: Use `setSecure` to Prevent Screenshots in SurfaceViews
alias: preventing-screenshots-and-screen-recording
id: MASTG-BEST-0017
parent: MASTG-BEST-0014
knowledge: [MASTG-KNOW-0053, MASTG-KNOW-0105, MASTG-KNOW-0106]
platform: android
component: android.view.SurfaceView
available_since: 17
---

`SurfaceView` creates a separate surface that is not protected by `FLAG_SECURE` even if the containing window has it set. To protect content in a `SurfaceView`, use [`setSecure(true)`](https://developer.android.com/reference/android/view/SurfaceView#setSecure(boolean)) before the surface view's containing window is attached to the window manager.
