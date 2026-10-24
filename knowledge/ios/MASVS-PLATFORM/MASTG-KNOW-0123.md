---
masvs_category: MASVS-PLATFORM
platform: ios
title: Handoff
available_since: 8
---

Handoff is an Apple continuity feature that lets users start an activity on one device and continue it on another nearby Apple device. It relies on [`NSUserActivity`](https://developer.apple.com/documentation/foundation/nsuseractivity) to capture enough state to resume the activity later.

## How Handoff Works

An app creates an `NSUserActivity` object describing the current activity and makes it current. Handoff advertises eligible user activities to nearby devices signed into the same iCloud account. When the user accepts the handoff on another device, the system launches or resumes the app on that device and delivers the `NSUserActivity` to it.

`NSUserActivity` is also used by related platform features, including Universal Links (see @MASTG-KNOW-0080), Siri Suggestions, and Spotlight integration. This means the same activity data may affect more than one system feature, depending on the eligibility flags set by the app.

Apps adopt Handoff by:

1. Creating and configuring an `NSUserActivity` with an `activityType` declared in `Info.plist`.
2. Populating `userInfo` with the data needed to restore the activity.
3. Ensuring the activity is eligible for Handoff through [`isEligibleForHandoff`](https://developer.apple.com/documentation/foundation/nsuseractivity/iseligibleforhandoff).
4. Calling [`becomeCurrent()`](https://developer.apple.com/documentation/foundation/nsuseractivity/becomecurrent()) to make the activity current.
5. Implementing [`scene(_:continue:)`](https://developer.apple.com/documentation/uikit/uiscenedelegate/scene(_:continue:)) to receive and restore the activity in scene-based apps. Apps that do not use scenes may handle continuation through the deprecated [`application(_:continue:restorationHandler:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/application(_:continue:restorationhandler:)).

## Scope and Constraints

- Handoff works between nearby Apple devices signed into the same iCloud account.
- The `activityType` must be declared in the app's `Info.plist` under `NSUserActivityTypes`.
- Data transferred via `userInfo` should be minimal. Apple recommends transferring as small a payload as possible, preferably 3 KB or less. For larger state, store the data elsewhere and include only enough information to retrieve or reconstruct it.
- If the activity contains file URLs, the receiving app may need to call `startAccessingSecurityScopedResource()` before accessing them.
- Apps can mark an activity as eligible for search ([`isEligibleForSearch`](https://developer.apple.com/documentation/foundation/nsuseractivity/iseligibleforsearch)) and prediction ([`isEligibleForPrediction`](https://developer.apple.com/documentation/foundation/nsuseractivity/iseligibleforprediction)), which exposes the activity to Spotlight and Siri Suggestions respectively.
