---
platform: ios
title: References to Sensitive Data Stored Unprotected in Shared App Group Containers
id: MASTG-TEST-0388
type: [static, code, manual]
weakness: MASWE-0006
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0068]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0082]
---

## Overview

An iOS app and its [app extensions](https://developer.apple.com/app-extensions/) can share data through an [App Group](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups) container (see @MASTG-KNOW-0082). Every member of the App Group has equal read/write access to everything in that container, and the container has no per-item access control between members. When the app or any of its extensions stores credentials, tokens, or other secrets there, all members of the App Group can read them, even those that do not need the data, and the values are only as protected as their file protection class allows when the device is locked.

For secrets, iOS offers a more suitable channel: a shared Keychain (a [Keychain Access Group](https://developer.apple.com/documentation/security/sharing-access-to-keychain-items-among-a-collection-of-apps) declared via the `keychain-access-groups` entitlement), which provides dedicated, access-controlled key storage. Storing secrets in shared `UserDefaults` or in a shared file container instead of the shared Keychain leaves them unprotected.

This test checks whether the app or any of its extensions writes sensitive data to an App Group shared container (shared `UserDefaults` or a shared file container) instead of using a shared Keychain, and whether that data is left unencrypted. The analysis covers the app's main binary and each extension binary (the `.appex` bundles under `PlugIns/`).

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, including the extension binaries in the `PlugIns/*.appex` bundles.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app and extension binaries.

## Observation

The output should contain a list of locations where the app or its extensions access an App Group shared container, in particular:

- `UserDefaults(suiteName:)` for shared user defaults.
- `FileManager.containerURL(forSecurityApplicationGroupIdentifier:)` for the shared file container.
- An `NSPersistentContainer` configured with an App Group store.

It should also indicate whether the app declares the `keychain-access-groups` entitlement and uses the Keychain to store secrets.

## Evaluation

The test case fails if sensitive data is written to an App Group shared container (shared `UserDefaults`, the shared file container, or a shared Core Data store) without adequate protection, for example when secrets are stored in plaintext or in shared `UserDefaults`, instead of being kept in a shared Keychain.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine:

- Whether the value written to the shared container is sensitive (for example, credentials, authentication tokens, or API keys).
- Whether the value is encrypted before being written, or whether files are written with `NSFileProtectionComplete` (see @MASTG-TEST-0299).
- Whether the data is a secret that should have been stored in a shared Keychain (`keychain-access-groups`) rather than the shared container.
