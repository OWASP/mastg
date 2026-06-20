---
platform: ios
title: Runtime Monitoring of Data Shared Between App Extensions and Containing Apps
id: MASTG-TEST-0x02
type: [dynamic]
weakness: MASWE-0053
threat: [app]
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0025]
profiles: [L1, L2]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x01.

When app extensions and their containing apps share data via [App Groups](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups), you can monitor the shared data at runtime to verify whether they exchange sensitive information. This test uses dynamic instrumentation to trace the use of shared storage APIs and capture the data read from or written to shared containers, user defaults, or databases.

## Steps

1. Identify the App Group identifier(s) used by the app and its extensions from the entitlements files or embedded provisioning profiles (see @MASTG-TEST-0x01).
2. Use @MASTG-TECH-0086 or write a Frida script to hook methods related to shared storage access:
   - For shared user defaults: [`UserDefaults.init(suiteName:)`](https://developer.apple.com/documentation/foundation/userdefaults/1409957-init), `UserDefaults.set*` and `UserDefaults.*forKey` methods.
   - For shared file containers: [`FileManager.containerURL(forSecurityApplicationGroupIdentifier:)`](https://developer.apple.com/documentation/foundation/filemanager/1412643-containerurl) and file I/O operations on paths within the shared container.
   - For shared Core Data storage: [`NSPersistentContainer`](https://developer.apple.com/documentation/coredata/nspersistentcontainer) initialization with App Group URLs.
3. Launch the app and trigger functionality that involves app extensions (e.g., share content using a share extension, display a widget, or use a custom keyboard).
4. Monitor the intercepted calls and log the data being accessed, including keys, file paths, and the content being read or written.

## Observation

The output should contain a trace of:

- Shared user defaults keys and values being set or retrieved.
- File paths within the shared container that are accessed and the corresponding data.
- Database queries or operations on shared Core Data stores.

## Evaluation

The test case fails if the app stores or retrieves sensitive data in the shared container, shared user defaults, or shared databases without adequate protection (e.g., encryption). 

Review the traced data to determine if it contains sensitive information and whether it is appropriately secured. Consider whether the data sharing is necessary for the extension's functionality and if access controls or encryption mechanisms are in place to protect sensitive data.
