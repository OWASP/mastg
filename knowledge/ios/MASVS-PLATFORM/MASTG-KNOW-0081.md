---
masvs_category: MASVS-PLATFORM
platform: ios
title: UIActivity Sharing
---

Starting with iOS 6, apps can share data (items) via the system-wide Share Activity Sheet using the [`UIActivityViewController`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller) API. From a user perspective, this is the familiar "Share" button available throughout iOS.

## UIActivityViewController

You create a `UIActivityViewController` by calling [`init(activityItems:applicationActivities:)`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/init(activityitems:applicationactivities:)), passing:

- `activityItems`: An array of data objects to share. Items can be of any type that conforms to `UIActivityItemSource` or is directly shareable (for example, `String`, `URL`, `UIImage`).
- `applicationActivities`: An optional array of custom [`UIActivity`](https://developer.apple.com/documentation/uikit/uiactivity) subclass instances representing app-specific services.

```swift
let activityVC = UIActivityViewController(
    activityItems: ["Hello, World!", URL(string: "https://example.com")!],
    applicationActivities: nil
)
present(activityVC, animated: true)
```

## Built-in Activity Types

The system provides a set of built-in activity types defined in [`UIActivity.ActivityType`](https://developer.apple.com/documentation/uikit/uiactivity/activitytype):

- `airDrop`
- `assignToContact`
- `copyToPasteboard`
- `mail`
- `message`
- `postToFacebook`
- `postToTwitter`
- `saveToCameraRoll`
- `addToReadingList`

A full list is available in the [UIActivity.ActivityType documentation](https://developer.apple.com/documentation/uikit/uiactivity/activitytype). The set of available activity types may grow with each iOS release.

## Excluding Activity Types

You can restrict which activity types are presented to the user by setting the [`excludedActivityTypes`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/excludedactivitytypes) property on the `UIActivityViewController` instance before presenting it:

```swift
activityVC.excludedActivityTypes = [
    .postToFacebook,
    .postToTwitter,
    .airDrop,
]
```

If `excludedActivityTypes` is not set or is `nil`, all available activity types are presented to the user.

## Custom Activities

Apps can provide custom activity types by subclassing `UIActivity` and passing instances to the `applicationActivities` parameter. Custom activities appear alongside the system activities in the share sheet.
