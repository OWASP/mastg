---
platform: android
title: App Leaking Sensitive Data via Notifications
id: MASTG-DEMO-0078
code: [kotlin]
test: MASTG-TEST-0315
tools: [MASTG-TOOL-0110]
---

## Sample

The following sample code contains:

- the Kotlin code that creates a notification with the `NotificationManager` class and exposes sensitive data.
- the AndroidManifest.xml that declares the runtime permission `POST_NOTIFICATIONS` permission that allows the app to post notifications (Android API 33 and higher).

!!! note
    To execute the test on a device, we must ensure that the app has the `POST_NOTIFICATIONS` permission granted. This can be done either via the @MASTG-TOOL-0004 command, such as `adb shell pm grant org.owasp.mastestapp android.permission.POST_NOTIFICATIONS` or by navigating to the app settings on the device and manually enabling the permission.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed Java code.

{{ ../../../../rules/mastg-android-sensitive-data-in-notifications.yml }}

And another one against the sample manifest file.

{{ ../../../../rules/mastg-android-sensitive-data-in-notifications-manifest.yml }}

{{ run.sh }}

## Observation

The rule detected two instances in the code where the `setContentTitle` API is used to set the notification title, and 2 cases where the `setContentText` API is used to set the notification text. It also identified the location in the manifest file where the `POST_NOTIFICATIONS` permission is declared.

{{ output.txt # output2.txt }}

## Evaluation

After reviewing the decompiled code at the location specified in the output (file and line number), we can conclude that the **test fails** because the notification contains sensitive data, specifically a first and a last name (PII).
