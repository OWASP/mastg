---
platform: ios
title: References to Deprecated Custom URL Scheme Handler Methods
id: MASTG-TEST-0x75-1
type: [static, code]
weakness: MASWE-0058
profiles: [L1, L2]
best-practices: [MASTG-BEST-0045]
knowledge: [MASTG-KNOW-0079]
apis: [application:handleOpenURL:, application:openURL:sourceApplication:annotation:, openURL:]
---

## Overview

If the app uses deprecated URL scheme handler methods, it loses access to the security context provided by the modern `application:openURL:options:` API (@MASTG-KNOW-0079). The deprecated methods lack the `options` dictionary, which carries the source application identifier (`UIApplicationOpenURLOptionsSourceApplicationKey`). Without this information, the app cannot perform source-based validation to restrict which apps can trigger actions via its custom URL scheme.

The following methods are deprecated since iOS 9.0:

- [`application:handleOpenURL:`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1622964-application)
- [`application:openURL:sourceApplication:annotation:`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623073-application)

The following method is deprecated since iOS 10.0:

- [`openURL:`](https://developer.apple.com/documentation/uikit/uiapplication/1622961-openurl) on `UIApplication`

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations in the binary where deprecated URL scheme handler methods are used.

## Evaluation

The test case fails if any of the following deprecated URL handler methods is found in the app:

- `application:handleOpenURL:`
- `application:openURL:sourceApplication:annotation:`
- `openURL:` (on `UIApplication`)
