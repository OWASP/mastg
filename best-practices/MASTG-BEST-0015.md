---
title: Use `setRecentsScreenshotEnabled` to Prevent Screenshots When Backgrounded
alias: preventing-screenshots-when-backgrounded
id: MASTG-BEST-0015
parent: MASTG-BEST-0014
knowledge: [MASTG-KNOW-0053]
platform: android
component: android.app.Activity
available_since: 33
---

Preventing screenshots when an app is backgrounded helps protect sensitive data from being exposed in system-generated snapshots used by the [Recents screen](https://developer.android.com/guide/components/activities/recents). This is especially important for apps that handle sensitive information, such as banking, healthcare, or enterprise apps. For example, a banking app should ensure that account balances, transaction details, or card numbers never appear in the Recents screen, unless the user explicitly chooses to allow it using an in-app setting.

Even though **system-generated snapshots** are sandboxed, they may still be accessible on rooted or compromised devices or through forensic acquisition.

Using `FLAG_SECURE` consistently on all UI components as indicated in @MASTG-BEST-0014 helps prevent system-generated snapshots from showing sensitive data in the [Recents screen](https://developer.android.com/guide/components/activities/recents) when the app is backgrounded. However, getting it right can be challenging, as there's no global setting to enforce it across the entire app.

Starting with Android 13 (API level 33), you can use [`setRecentsScreenshotEnabled(false)`](https://developer.android.com/reference/android/app/Activity#setRecentsScreenshotEnabled(boolean)) to ensure that the app's content is not shown in the Recents screen when backgrounded.

- The snapshots saved to `/data/system_ce/<user_id>/snapshots` will be blank/obscured.
- The previews in the Recents screen will be blank after the user switches away from the app completely, similar to the behavior of `FLAG_SECURE`.

!!! warning "What `setRecentsScreenshotEnabled` Does Not"

    Setting `setRecentsScreenshotEnabled(false)` does not prevent user-initiated screenshots or screen recordings.

    - Users can take screenshots using the hardware shortcut and the content will appear in those screenshots.
    - Tapping "Screenshot" in the Recents screen will not work (the system may show a policy message depending on device/ROM).
