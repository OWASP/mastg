---
title: User-Initiated Screenshots and Screen Recording
platform: android
masvs_category: MASVS-PLATFORM
---

Users can take screenshots or screen recordings at any time using [system shortcuts](https://support.google.com/android/answer/9075928).

User-initiated screenshots are stored in the **shared external storage**, typically under `/sdcard/Pictures/Screenshots/` or a similar public media directory depending on the device and Android version. These files are accessible to the user and to any app with storage or media access permissions. They may also be synced automatically to cloud services such as Google Photos. Because they are outside the app sandbox, once a screenshot is taken the app has no control over its lifecycle, distribution, or deletion.

A malicious app with screen-capture capabilities, or malware on a rooted or compromised device, can also capture the display contents without the user's knowledge. See @MASTG-KNOW-0106 for details on app-initiated screen capture.
