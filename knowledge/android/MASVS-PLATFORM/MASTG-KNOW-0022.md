---
masvs_category: MASVS-PLATFORM
platform: android
title: Overlay Attacks
---

Screen overlay attacks occur when a malicious application places itself on top of another application which continues to function normally in the foreground. The malicious app can create UI elements that mimic the appearance of the legitimate app or the Android system UI. The goal is typically to deceive users into believing they are interacting with the legitimate app to elevate privileges (for example, by getting permissions granted), conduct phishing, or capture user taps and keystrokes.

There are several types of overlay attacks affecting different Android versions:

- [**Tapjacking**](https://developer.android.com/privacy-and-security/risks/tapjacking) (historically affecting Android 6.0 (API level 23) and lower) exploits the screen overlay feature by listening for taps and intercepting information passed to underlying activities.
- [**Cloak & Dagger**](https://cloak-and-dagger.org/) attacks affected apps targeting Android 5.0 (API level 21) to Android 7.1 (API level 25). They abused the `SYSTEM_ALERT_WINDOW` ("draw on top") and/or `BIND_ACCESSIBILITY_SERVICE` ("a11y") permissions. When apps were installed from the Play Store, users did not need to explicitly grant these permissions and were not even notified.
- [**Toast Overlay**](https://unit42.paloaltonetworks.com/unit42-android-toast-overlay-attack-cloak-and-dagger-with-no-permissions/) was similar to Cloak & Dagger but did not require specific Android permissions from users. It was patched with CVE-2017-0752 in Android 8.0 (API level 26).

Android provides several defensive mechanisms that apps can use to protect against overlay attacks:

**Prevention Mechanisms:**

- [`HIDE_OVERLAY_WINDOWS`](https://developer.android.com/reference/android/Manifest.permission#HIDE_OVERLAY_WINDOWS) permission and [`setHideOverlayWindows`](https://developer.android.com/reference/android/view/Window#setHideOverlayWindows(boolean)) (since API level 31): Declare this permission in the manifest and call the method on the window to hide all non-system overlay windows while the activity is in the foreground. This provides the strongest protection by preventing overlays entirely.
- [`android:filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#attr_android:filterTouchesWhenObscured) attribute and [`setFilterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) method: Set this layout attribute to `true` in XML or call the method programmatically to filter touch events when the view is obscured by another visible window.
- [`onFilterTouchEventForSecurity`](https://developer.android.com/reference/android/view/View#onFilterTouchEventForSecurity(android.view.MotionEvent)): Override this method for fine-grained control to implement custom security policies for views.

**Detection Mechanisms:**

- [`FLAG_WINDOW_IS_OBSCURED`](https://developer.android.com/reference/android/view/MotionEvent#FLAG_WINDOW_IS_OBSCURED) (since API level 9): Check this flag to detect if the window is obscured.
- [`FLAG_WINDOW_IS_PARTIALLY_OBSCURED`](https://developer.android.com/reference/android/view/MotionEvent#FLAG_WINDOW_IS_PARTIALLY_OBSCURED) (since API level 29): Check this flag to detect if the window is partially obscured.

Many overlay attacks are inherent to specific Android system versions due to vulnerabilities or design issues. Modern Android versions have introduced system-level protections that make these attacks more difficult, but apps targeting older API levels may remain vulnerable.

Over the years, malware such as MazorBot, BankBot, and MysteryBot have exploited screen overlays to target business-critical applications, particularly in the banking sector.
