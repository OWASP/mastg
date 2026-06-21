---
platform: ios
title: References to Enforced Updating APIs
id: MASTG-TEST-0383
type: [static, code, manual]
weakness: MASWE-0075
profiles: [L2]
knowledge: [MASTG-KNOW-0074]
---

## Overview

iOS apps may fail to enforce updates when critical security patches or minimum version requirements are needed. Apple does not provide a public API to force install or silently update an App Store app, so apps must implement their own mechanism: either querying the App Store using the iTunes Search API (for example, `https://itunes.apple.com/lookup?bundleId=<bundleId>` or `https://itunes.apple.com/lookup?id=<appId>`, with an optional `country` parameter) and comparing `results[0].version` against the installed `CFBundleShortVersionString` from `Bundle.main.infoDictionary`, or checking a backend-supplied minimum version over `URLSession`. Apps may also read `CFBundleVersion` when the backend policy is based on build numbers. If an update is required, the app should block usage, for example via a non-dismissible `UIAlertController` or gating view controller, and redirect to the App Store, for example via `UIApplication.shared.open`, typically using `results[0].trackViewUrl`, or [`SKStoreProductViewController`](https://developer.apple.com/documentation/storekit/skstoreproductviewcontroller). If these mechanisms are absent, implemented incorrectly, or do not block app usage when required, the app fails to enforce the update.

This test checks whether the app contains code that implements update enforcement, either through App Store lookup or through a custom version-based gating mechanism (@MASTG-KNOW-0074).

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of code locations where the app retrieves its version (for example, `CFBundleShortVersionString` or `CFBundleVersion`), compares it against a backend-supplied minimum or the latest App Store version (via `results[0].version` from the iTunes lookup), and enforces the update (for example, via a non-dismissible `UIAlertController`, a gating view controller, `UIApplication.shared.open` with `results[0].trackViewUrl`, or `SKStoreProductViewController`).

## Evaluation

The test case fails if no code locations show an update enforcement mechanism.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine whether the update enforcement is correct:

- Determine whether the update check executes before access to protected functionality or backend services and cannot be bypassed (for example, by checking the call graph or app startup entry points such as `AppDelegate.application(_:didFinishLaunchingWithOptions:)` or `SceneDelegate.scene(_:willConnectTo:)`).
- Determine whether the enforcement uses a non-dismissible blocking UI (for example, a `UIAlertController` without a dismiss action, or a gating view controller) or redirects to the App Store (via `UIApplication.shared.open` using `results[0].trackViewUrl`, or `SKStoreProductViewController`) when an update is required.
- For App Store lookup flows, determine whether the app parses `results[0].version` from the iTunes lookup response and correctly enforces an update when the published version exceeds the installed version and the flow is intended to require that update.
