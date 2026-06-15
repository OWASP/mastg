---
title: Restrict Activity Types in UIActivityViewController
alias: restrict-uiactivity-types
id: MASTG-BEST-0x71
platform: ios
knowledge: [MASTG-KNOW-0081]
---

Where possible, avoid sharing sensitive data via [`UIActivityViewController`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller) altogether. If sharing is required, limit the shared content to the minimum necessary, and prefer activity types that the user explicitly chooses from a restricted list.

When using `UIActivityViewController` to share content, by default, all system activity types are available, which may allow sensitive content to be shared through channels the app does not intend to support.

Use the [`excludedActivityTypes`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/excludedactivitytypes) property to remove activity types that are not appropriate for sensitive data. For example, if the shared content should not be sent to social networks or saved to a photo library, explicitly exclude those activity types:

```swift
let activityVC = UIActivityViewController(
    activityItems: [shareContent],
    applicationActivities: nil
)

activityVC.excludedActivityTypes = [
    .postToFacebook,
    .postToTwitter,
    .postToWeibo,
    .saveToCameraRoll,
    .addToReadingList,
    .airDrop,
    .mail,
    .message,
]
```

Keep the exclusion list up to date. New activity types may be added by the system in newer iOS versions and are not automatically excluded. Test on each supported iOS version to ensure no unintended activity types are available.

If you implement and use a custom `UIActivity`, make sure that potentially sensitve data is handled securely.
