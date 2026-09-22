---
masvs_category: MASVS-PLATFORM
platform: ios
title: App Notifications
---

iOS provides developers with _local_ and _remote_ [app notifications](https://developer.apple.com/notifications/ "Notifications") to communicate relevant information to the user. Notifications can display an alert, play a sound, or badge the app's icon.

While useful, app notifications have privacy implications that can be noted:

- For remote notifications, the [APNs payload](https://developer.apple.com/documentation/usernotifications/generating-a-remote-notification "APNs payload") can define user-visible content such as the notification's `title`, `subtitle`, and `body`. When notification previews are hidden, notification categories can use the [`hiddenPreviewsShowTitle`](https://developer.apple.com/documentation/usernotifications/unnotificationcategoryoptions/hiddenpreviewsshowtitle "hiddenPreviewsShowTitle") and [`hiddenPreviewsShowSubtitle`](https://developer.apple.com/documentation/usernotifications/unnotificationcategoryoptions/hiddenpreviewsshowsubtitle "hiddenPreviewsShowSubtitle") options to keep the title or subtitle visible, while [`hiddenPreviewsBodyPlaceholder`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory/hiddenpreviewsbodyplaceholder "hiddenPreviewsBodyPlaceholder") defines placeholder text to display instead of the notification body.

- Notifications can appear on the device's Lock Screen and display their content according to the user's notification preview settings. iOS allows users to control when [notification previews](https://support.apple.com/guide/iphone/change-notification-settings-iph7c3d96bab/ios#:~:text=Prevent%20notification%20previews%20from%20appearing%20on%20the%20Lock%20Screen "Prevent notification previews from appearing on the Lock Screen") are shown, while apps can customize the content displayed when previews are hidden using notification categories.
