---
masvs_category: MASVS-PLATFORM
platform: ios
title: Enforced Updating
---

Forcing a user to update the application can be necessary in multiple cases:

- A client-side vulnerability was discovered which needs to be fixed
- Cryptographical key material that needs to be rotated (e.g. public key pinning)
- Migrating to a new API so that the old API can be decommissioned more quickly
- Updating a dependency to ensure compatibility with third-party backend systems

Apple does not provide a public API to force install or silently update an App Store app. Developers must implement their own update gating mechanism, commonly done by querying the App Store Lookup API, using a remotely configured minimum supported version, or both.

The typical lookup-based approach is to query the [App Store Lookup API](https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/index.html) using the app's bundle ID or numeric App Store ID:

```txt
https://itunes.apple.com/lookup?bundleId=<YourBundleId>
https://itunes.apple.com/lookup?id=<NumericAppId>
```

An optional `country` parameter (for example, `&country=us`) can be appended to target a specific App Store region. The response is a JSON object with a `results` array; the key fields are `results[0].version` (the current App Store version string) and `results[0].trackViewUrl` (the direct App Store URL used to redirect the user).

The app compares `results[0].version` with its installed marketing version, read from `CFBundleShortVersionString` via `Bundle.main.infoDictionary`. Do not compare this value with `CFBundleVersion`, which identifies a build rather than the App Store version. Also handle empty `results` responses, regional App Store differences, phased releases, and propagation delays.

If an update is required, the app can block usage and redirect the user. Two common redirection approaches are:

- **[`SKStoreProductViewController`](https://developer.apple.com/documentation/storekit/skstoreproductviewcontroller)** (StoreKit): presents the App Store product page in-app for a smoother user experience.
- **[`UIApplication.shared.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/open(_:options:completionhandler:))**: opens the App Store URL (for example, `results[0].trackViewUrl`) in the App Store app.

Open-source libraries such as [Siren](https://github.com/ArtSabintsev/Siren), archived on Apr 2, 2025, or [react-native-appstore-version-checker](https://www.npmjs.com/package/react-native-appstore-version-checker) automate these lookups and comparisons. Regardless of the method, update prompts should be used carefully to avoid frustrating users when no real security or functional benefit exists.

For security critical enforcement, the App Store Lookup API should not be the only source of truth. Prefer a backend-controlled minimum supported version, and where the app uses backend services, reject requests from unsupported app versions server side. This is especially important when the update is required to address a vulnerability, remove trust in compromised material, or retire an unsafe API contract.

For more details on managing builds and versions, see Apple's [App Store Connect documentation](https://developer.apple.com/help/app-store-connect/manage-builds/upload-builds/). To ensure compliance, review the [App Store Review Guidelines](https://developer.apple.com/app-store/review/guidelines/).

Keep in mind that updating the app does not resolve vulnerabilities that reside on backend systems. A secure update mechanism must be part of a broader API and service lifecycle management strategy. Likewise, if users are not forced to update, test older app versions against your backend and apply proper API versioning and deprecation policies to maintain overall platform security.
