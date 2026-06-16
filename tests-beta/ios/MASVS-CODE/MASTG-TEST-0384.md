---
platform: ios
title: Runtime Use of Enforced Updating APIs
id: MASTG-TEST-0384
type: [dynamic, network, hooks, manual]
weakness: MASWE-0075
profiles: [L2]
knowledge: [MASTG-KNOW-0074]
---

## Overview

On iOS, apps implementing enforced updating typically read the app version, for example `CFBundleShortVersionString` via `Bundle.main.infoDictionary`, and send it to a backend that returns a minimum version policy. Apps may also read `CFBundleVersion` when the backend policy is based on build numbers. Alternatively, they may query the App Store using the iTunes Search API, for example `https://itunes.apple.com/lookup?bundleId=<bundleId>` or `https://itunes.apple.com/lookup?id=<appId>`, with an optional `country` parameter, to compare `results[0].version` against the installed `CFBundleShortVersionString`. If an update is required, the app should block usage and redirect to the App Store via `UIApplication.shared.open`, typically using `results[0].trackViewUrl`, or by presenting a [`SKStoreProductViewController`](https://developer.apple.com/documentation/storekit/skstoreproductviewcontroller). If the app does not perform these checks or if the enforcement can be bypassed, the app fails to properly enforce the update.

This test checks whether the app triggers the expected update enforcement behavior at runtime by capturing version-related network traffic where applicable and hooking update-related API calls (@MASTG-KNOW-0074).

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0062 to capture the app traffic.
3. Use @MASTG-TECH-0095 to hook the relevant APIs.
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- a network traffic trace showing version values in requests and the corresponding backend responses, where a backend-gated update flow is used
- any requests to the iTunes lookup endpoint and the parsed fields, for example `results[0].version` and `results[0].trackViewUrl`, where an App Store lookup flow is used
- a method trace showing which APIs were called, for example version retrieval from `Bundle`, network calls, iTunes lookup parsing, and any redirection via `UIApplication.shared.open` or `SKStoreProductViewController`

## Evaluation

The test case fails if the app does not perform a runtime update check, or if the update is not enforced at runtime.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0076:

- Determine whether the update check executes before access to protected functionality or backend services and cannot be bypassed.
- For backend-gated flows, determine whether modifying the version value in network requests, for example lowering `version`, `versionCode`, or `build`, results in an update-required response that the app properly enforces.
- For App Store lookup flows, determine whether stubbing the iTunes lookup response to advertise a higher `results[0].version` results in update enforcement, for example by blocking usage and redirecting to the App Store via `UIApplication.shared.open` using `results[0].trackViewUrl`, or by presenting `SKStoreProductViewController`.
