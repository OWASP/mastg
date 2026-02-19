---
platform: ios
title: Excessive Permission Purpose Strings in Info.plist
id: MASTG-TEST-0x69
type: [static]
weakness: MASWE-0117
profiles: [P]
---

## Overview

If an app declares purpose strings for permissions that are not justified by its core functionality, it gains unnecessary access to sensitive user data such as location, health records, contacts, or camera. This can lead to privacy violations, excessive data collection, or abuse by malicious insiders.

On iOS, apps must declare purpose strings (keys ending in `UsageDescription`) in `Info.plist` to request access to protected resources. Since iOS 10, each resource access requires a corresponding purpose string. Declaring more purpose strings than necessary indicates the app may be over-privileged, requesting access to data it does not need to function.

## Steps

1. Extract the app package contents using @MASTG-TECH-0058 and locate the `Info.plist` file at `Payload/<appname>.app/Info.plist`.
2. Convert the `Info.plist` to a readable format using @MASTG-TECH-0x69.
3. Search for all keys ending with `UsageDescription` to identify all declared purpose strings.

## Observation

The output should contain a list of all purpose strings (keys ending in `UsageDescription`) declared in the app's `Info.plist` file, along with their associated description values.

## Evaluation

The test case fails if the app declares purpose strings for permissions that are not justified by its core functionality, indicating excessive access to sensitive user data.
