---
title: Purpose String Accuracy for Reachable Protected Resource Access
platform: ios
id: MASTG-TEST-0360
type: [static, config, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0051]
knowledge: [MASTG-KNOW-0077]
---

## Overview

Purpose strings are user-facing explanations that iOS displays when an app requests [access to protected resources](https://developer.apple.com/documentation/uikit/requesting-access-to-protected-resources) such as location, camera, microphone, contacts, photos, health data, Bluetooth, motion, or speech recognition. Unlike entitlements, purpose strings are tied to privacy-sensitive protected resources and runtime authorization prompts.

Usage description keys and runtime APIs are separate parts of the iOS permission model. Usage description keys provide the explanation in `Info.plist`, while framework APIs request, check, or access the protected resource.

| Protected Resource | Usage Description Key | Representative APIs |
| --- | --- | --- |
| Location | `NSLocationWhenInUseUsageDescription`, `NSLocationAlwaysAndWhenInUseUsageDescription` | `CLLocationManager.requestWhenInUseAuthorization()`, `CLLocationManager.requestAlwaysAuthorization()`, `CLLocationManager.authorizationStatus()` |
| Camera | `NSCameraUsageDescription` | `AVCaptureDevice.requestAccess(for:completionHandler:)`, `AVCaptureDevice.authorizationStatus(for:)` |
| Contacts | `NSContactsUsageDescription` | `CNContactStore.requestAccess(for:completionHandler:)`, `CNContactStore.authorizationStatus(for:)` |
| Photos | `NSPhotoLibraryUsageDescription`, `NSPhotoLibraryAddUsageDescription` | `PHPhotoLibrary.requestAuthorization(for:handler:)`, `PHPhotoLibrary.authorizationStatus(for:)` |
| Health | `NSHealthShareUsageDescription`, `NSHealthUpdateUsageDescription` | `HKHealthStore.requestAuthorization(toShare:read:completion:)`, `HKHealthStore.authorizationStatus(for:)` |
| Bluetooth | `NSBluetoothAlwaysUsageDescription` | `CBManager.authorization`, `CBCentralManager`, `CBPeripheralManager` |

A declared purpose string is not a privacy violation by itself. The risk exists when the app has a reachable code path that requests or accesses a protected resource without a reasonable connection to a real user-visible feature, or when the purpose string shown to the user is vague, deceptive, inaccurate, or inconsistent with the actual access.

This test verifies whether reachable protected resource access is justified by app functionality and whether the corresponding purpose string accurately, meaningfully, and specifically explains that access to the user.

See @MASTG-KNOW-0077 for the mapping between purpose string keys and framework APIs that request or check protected resource access.

!!! note "Out of Scope"

    This test does not check for declared purpose strings where no corresponding protected resource API use is present. A declared purpose string is not a privacy violation by itself.

    This test does not check for reachable protected resource access without a matching required purpose string. That issue is treated separately as an App Store blocker. Missing purpose strings can cause access to fail or the app to exit, and App Store Connect may report this as `ITMS-90683: Missing purpose string in Info.plist`.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` file to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect all `*UsageDescription` keys.
5. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
6. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain:

- The usage description keys declared by the app together with their user-facing purpose strings.
- The protected resource authorization or access APIs referenced by the app binaries.

## Evaluation

The test case fails if there is evidence that the app has a reachable code path that requests or accesses a protected resource and the purpose string does not meaningfully, accurately, and specifically explain why the app needs that protected resource.

The test case also fails if a reachable code path requests or accesses a protected resource without a matching required purpose string.

**Further Validation Required:**

Use the declared purpose strings, referenced APIs, app metadata and App Store information, visible app features, and runtime behavior to determine whether each protected resource access path is justified and accurately explained.

Consider the following when evaluating:

- Is the protected resource access reachable during normal or reasonably expected app use, and is it connected to a real user-visible feature?
- Does the purpose string accurately and specifically explain why the app needs the protected resource, without being vague, generic, deceptive, or inconsistent with the actual access?

Static analysis can find unused code, SDK code, dead code, weak-linked frameworks, dynamically resolved APIs, obfuscated code, native libraries, loaded frameworks, or feature-flagged code paths. Treat missing API references as absence of evidence, not proof that the app never requests the protected resource. Treat referenced APIs without matching purpose strings as a failure only when they can reasonably be connected to reachable protected resource access.

Use dynamic analysis to complement static analysis and identify authorization APIs that are actually reached at runtime. See @MASTG-TEST-0361.
