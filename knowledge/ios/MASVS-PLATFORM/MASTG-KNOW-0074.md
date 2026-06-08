---
masvs_category: MASVS-PLATFORM
platform: ios
title: Enforced Updating
---

Enforced updating can be useful for maintaining security when critical components such as public key pins need to be rotated or when severe vulnerabilities must be patched quickly. In these cases, requiring users to install a new version can ensure that old, insecure builds are no longer in use.

The challenge with iOS however, is that Apple does not provide any APIs yet to automate this process, instead, developers will have to create their own mechanism, such as described at various [blogs](https://mobikul.com/show-update-application-latest-version-functionality-ios-app-swift-3/ "Updating version in Swift 3") which boil down to looking up properties of the app using `http://itunes.apple.com/lookup\?id\<BundleId>` or third party libraries, such as [Siren](https://github.com/ArtSabintsev/Siren "Siren") and [react-native-appstore-version-checker](https://github.com/kimxogus/react-native-version-check "Update checker for React"). Most of these implementations will require a certain given version offered by an API or just "latest in the appstore", which means users can be frustrated with having to update the app, even though no business/security need for an update is truly there.

The typical approach is to query the [App Store Lookup API](https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/index.html), for example using the endpoint:

```txt
https://itunes.apple.com/lookup?bundleId=<YourBundleId>
```

This returns metadata including the latest version number. The app can then compare that value with its current version (`CFBundleShortVersionString`) and, if necessary, prompt the user to update. Apple provides [`SKStoreProductViewController`](https://developer.apple.com/documentation/storekit/skstoreproductviewcontroller) in StoreKit, which allows developers to present the App Store product page directly within the app for a smoother update experience. Alternatively, the app can open the App Store page using its URL.

Open-source libraries such as [Siren](https://github.com/ArtSabintsev/Siren) (archived on Apr 2, 2025) or [react-native-appstore-version-checker](https://www.npmjs.com/package/react-native-appstore-version-checker) automate these lookups and comparisons. Regardless of the method, update prompts should be used carefully to avoid frustrating users when no real security or functional benefit exists.

For more details on managing builds and versions, see Apple's [App Store Connect documentation](https://developer.apple.com/help/app-store-connect/manage-builds/upload-builds/). To ensure compliance, review the [App Store Review Guidelines](https://developer.apple.com/app-store/review/guidelines/).

Keep in mind that updating the app does not resolve vulnerabilities that reside on backend systems. A secure update mechanism must be part of a broader API and service lifecycle management strategy. Likewise, if users are not forced to update, test older app versions against your backend and apply proper API versioning and deprecation policies to maintain overall platform security.
