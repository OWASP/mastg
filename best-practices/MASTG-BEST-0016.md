---
title: Use `SECURE_FLAG` to Prevent Screenshots and Screen Recording
alias: use-secure-flag-to-prevent-screenshots-and-screen-recording
id: MASTG-BEST-0016
parent: MASTG-BEST-0014
knowledge: [MASTG-KNOW-0053, MASTG-KNOW-0105, MASTG-KNOW-0106]
platform: android
component: android.view.Window
available_since: 1
refs:
- https://developer.android.com/security/fraud-prevention/activities#flag_secure
- https://support.google.com/googleplay/android-developer/answer/16273414
- https://support.google.com/googleplay/android-developer/answer/14638385
- https://proandroiddev.com/multitasking-intrusion-and-preventing-screenshots-in-android-app-15bd8757c24d
---

Setting [`FLAG_SECURE`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_SECURE) on a window prevents screenshots (or makes them appear black), blocks screen recording, and hides content on non-secure displays and ensures that the app's content is not shown in the [Recents screen](https://developer.android.com/guide/components/activities/recents) when backgrounded.

<div style="display:flex; flex-wrap:wrap; gap:16px; align-items:flex-start; margin:16px 0;">
  <figure style="flex:1 1 220px; margin:0; text-align:center;">
    <img src="Images/Chapters/0x05d/task-switcher-without-flag-secure.png" width="200" alt="Task switcher without FLAG_SECURE">
    <figcaption>Without <code>FLAG_SECURE</code></figcaption>
  </figure>
  <figure style="flex:1 1 220px; margin:0; text-align:center;">
    <img src="Images/Chapters/0x05d/task-switcher-with-flag-secure.png" width="200" alt="Task switcher with FLAG_SECURE">
    <figcaption>With <code>FLAG_SECURE</code></figcaption>
  </figure>
</div>

The flag is applied with [`addFlags()`](https://developer.android.com/reference/android/view/Window#addFlags(int)) or [`setFlags()`](https://developer.android.com/reference/android/view/Window#setFlags(int,int)). Common best practices include:

- This flag cannot be applied globally. Apply the flag to all windows that can show sensitive data, including `Activity`, `Dialog`, `DialogFragment`, `AlertDialog`, or any custom window added via `WindowManager`. Sometimes apps **use a base UI class and use inheritance** to ensure all sensitive screens have it set.
- Set the flag as early as possible in the lifecycle, before sensitive content is rendered. For example, setting it in `onCreate()` is better than `onResume()`, and trying to add the flag in `onPause()` is not effective, as the preview is created before this method is called.
- Be careful with fragments and their lifecycle, as they can be added or removed dynamically.
- Avoid clearing the flag during transitions (e.g., using [`clearFlags()`](https://developer.android.com/reference/android/view/Window#clearFlags(int)) or `setFlags()` without reapplying), as sensitive previews for the Recents screen can be captured before callbacks like `onPause()`.

Note that you cannot set `FLAG_SECURE` directly on components that do not expose `getWindow()`, such as `android.widget.PopupWindow` or `android.widget.Toast`.

- `android.view.View` and subclasses like `android.widget.TextView`, `android.widget.EditText`, `android.webkit.WebView`, `android.view.TextureView`, etc. don't support `FLAG_SECURE`. You must secure their containing Window instead. Alternatively, you can mask passwords and other sensitive by following the recommendations in @MASTG-BEST-0019.

For more information, refer to the official documentation.

**Important:** Google warns that this approach isn't reliable for protecting against overlay attacks (@MASTG-KNOW-0022). See ["Secure sensitive activities - Best practices"](https://developer.android.com/security/fraud-prevention/activities#best-practices).

