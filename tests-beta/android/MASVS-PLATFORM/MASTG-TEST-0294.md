---
title: "`SecureOn` Not Used to Prevent Screenshots in Compose Dialogs"
platform: android
id: MASTG-TEST-0294
type: [static]
profiles: [L2]
best-practices: [MASTG-BEST-0014, MASTG-BEST-0018]
weakness: MASWE-0055
threat: [app, root]
---

## Overview

This test verifies whether an app references Android screen capture prevention APIs for components that don't support [`FLAG_SECURE`](https://developer.android.com/security/fraud-prevention/activities#flag_secure).

When this protection is enabled, Android blocks screenshots, prevents content from appearing on non-secure displays (including remote screen sharing), and blocks capture through MediaProjection or virtual displays. Screenshots are blocked (resulting images are blank or obscured and the system may show a policy message), and the app's content is not shown in the Recents screen when backgrounded.

In Compose dialogs, developers can be explicitly disabling `FLAG_SECURE` by setting the secure policy for the dialog window using [`SecureOff`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy#SecureOff) instead of enabling it with [`SecureOn`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy#SecureOn). A third option is [`Inherit`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy#Inherit), which applies the policy of the parent window.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test case fails if, for **any UI component that displays sensitive data**, the relevant API calls are missing, inconsistently applied, or the protection is explicitly cleared.
