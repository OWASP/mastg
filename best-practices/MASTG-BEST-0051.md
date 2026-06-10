---
title: Minimize iOS Permissions and Entitlements
alias: minimize-ios-permissions-and-entitlements
id: MASTG-BEST-0051
platform: ios
knowledge: [MASTG-KNOW-0077]
---

Request only the iOS permissions and [app capabilities](https://developer.apple.com/documentation/xcode/adding-capabilities-to-your-app) that the app actually needs, and prefer the narrowest Apple-supported access model for each feature. This reduces unnecessary exposure of personal data and limits the blast radius if the app, an extension, or a shared container is later abused.

Review `Info.plist` purpose strings, the signed entitlements, and any provisioning-profile entitlements together before release. A permission prompt or capability should map to a concrete feature that users can understand and justify. Remove unused or speculative entries instead of keeping them "just in case".

## Prefer Narrower Access Paths

When Apple provides a user-selected or limited-access API, prefer it over broad library or background access. For example, use [`PHPickerViewController`](https://developer.apple.com/documentation/photokit/phpickerviewcontroller) or [`PhotosPicker`](https://developer.apple.com/documentation/photosui/photospicker) when users only need to choose specific photos, and request full photo library access only when the app genuinely needs it.

For location, request [`when in use`](https://developer.apple.com/documentation/corelocation/requesting-authorization-to-use-location-services) access before considering broader background access. For Bluetooth, HealthKit, HomeKit, Siri, or other protected services, enable only the capabilities and usage-description keys that are strictly required for the implemented feature set.

## Minimize Data-Sharing Capabilities

Treat entitlements such as [App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.security.application-groups), [iCloud containers](https://developer.apple.com/library/archive/documentation/General/Conceptual/iCloudDesignGuide/Chapters/iCloudFundametals.html), and [Associated Domains](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.developer.associated-domains) as sensitive design decisions, not default conveniences. They can create new paths for sharing, syncing, or exposing personal data across apps, extensions, websites, or devices.

Before enabling one of these capabilities, document the exact data flow it unlocks, the minimum set of targets that need it, and the security controls that protect the data afterward. If the same feature can be implemented without broad shared containers or external associations, prefer the narrower design.

!!! note
    Newer Apple privacy mechanisms such as [privacy manifests](https://developer.apple.com/documentation/bundleresources/privacy-manifest-files) and required-reason APIs complement these checks but do not replace purpose strings or entitlements. You still need to minimize and review both.
