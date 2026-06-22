---
platform: ios
title: References to the App-Wide Restriction of Custom Keyboards
id: MASTG-TEST-0389
type: [static, code, manual]
weakness: MASWE-0061
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0069]
profiles: [L2]
knowledge: [MASTG-KNOW-0082, MASTG-KNOW-0141]
---

## Overview

iOS lets users install custom keyboards, which are app extensions that replace the system keyboard across all apps (see @MASTG-KNOW-0141). Once granted "Full Access", a custom keyboard can transmit what the user types off the device. An app that collects sensitive input, such as a banking PIN or a one-time passcode, keeps using whichever keyboard the user has selected unless it opts out.

An app can reject custom keyboards across the whole app by implementing [`application:shouldAllowExtensionPointIdentifier:`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623122-application) in its `UIApplicationDelegate` and returning `false` for the keyboard extension point (`UIApplicationKeyboardExtensionPointIdentifier`). High-assurance apps, for example banking apps, use this app-wide control so that no field ever receives input from a third-party keyboard.

This test checks whether an app that handles sensitive keyboard input restricts custom keyboards app-wide. It complements the field-level control covered by @MASTG-TEST-0346 and @MASTG-TEST-0347, where `isSecureTextEntry` keeps individual sensitive fields on the system keyboard.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain whether the app implements `application:shouldAllowExtensionPointIdentifier:` in its app delegate and the value it returns for the keyboard extension point (`UIApplicationKeyboardExtensionPointIdentifier`).

## Evaluation

The test case fails if the app handles sensitive data entered through the keyboard and does not reject the custom keyboard extension point app-wide, that is, it does not implement `application:shouldAllowExtensionPointIdentifier:` or returns `true` for the keyboard extension point.

**Further Validation Required:**

Inspect the app delegate implementation using @MASTG-TECH-0076 to determine the value returned for the keyboard extension point (`UIApplicationKeyboardExtensionPointIdentifier`) and whether the app handles sensitive data entered through the keyboard.

An app may instead keep individual sensitive fields on the system keyboard with `isSecureTextEntry` rather than restricting custom keyboards app-wide; that field-level control is covered by @MASTG-TEST-0346 and @MASTG-TEST-0347.
