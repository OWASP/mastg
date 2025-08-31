---
title: References to Screen Capturing Prevention APIs
platform: android
id: MASTG-TEST-0291
type: [static]
profiles: [L2]
best-practices: [MASTG-BEST-0014]
weakness: MASWE-0055
---

## Overview

This test verifies whether an app references Android screen capture prevention APIs. On Android, developers can prevent screenshots and nonsecure display mirroring using [`FLAG_SECURE`](https://developer.android.com/security/fraud-prevention/activities#flag_secure). When set, Android blocks screenshots and prevents content from appearing on a nonsecure display, including remote screen sharing. Users see a blank screen if they attempt a screenshot or when the app moves to the background.

Developers typically apply the flag with [`addFlags()`](https://developer.android.com/reference/android/view/Window#addFlags(int)) or [`setFlags()`](https://developer.android.com/reference/android/view/Window#setFlags(int,int)). Common failure modes include not setting `FLAG_SECURE` on all sensitive screens or clearing the flag during transitions e.g., using [`clearFlags()`](https://developer.android.com/reference/android/view/Window#clearFlags(int)) or `setFlags()`.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test case fails if the relevant APIs are missing or inconsistently applied on any UI component that displays sensitive data, or if code paths clear the protection without an adequate justification.
