---
title: Use `SecureFlagPolicy.SecureOn` to Prevent Screenshots in Compose Components
alias: use-secureon-in-compose-components
id: MASTG-BEST-0018
parent: MASTG-BEST-0014
knowledge: [MASTG-KNOW-0053, MASTG-KNOW-0105, MASTG-KNOW-0106]
platform: android
component: androidx.compose.ui:ui
available_since: 1.0.0
---

Use [`SecureFlagPolicy.SecureOn`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy#SecureOn) for Jetpack Compose dialogs and popups that display sensitive information (via [`DialogProperties.securePolicy`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/DialogProperties#securePolicy()) or [`PopupProperties.securePolicy`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/PopupProperties#securePolicy()) respectively). This applies the underlying Window's [`FLAG_SECURE`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_SECURE), which blocks screenshots, screen recordings, display on insecure devices and prevents previews in the Recents screen.

Define the policy at creation time and avoid toggling it while visible. If the dialog or popup should follow the host window's state, use [`SecureFlagPolicy.Inherit`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy#Inherit). Avoid [`SecureFlagPolicy.SecureOff`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy#SecureOff) for sensitive content.

Be aware that the use of this API may interfere with workflows like screenshots or screen sharing. If your threat model allows, consider offering an explicit user option to disable secure mode for non-sensitive scenarios, but default to secure behavior.
