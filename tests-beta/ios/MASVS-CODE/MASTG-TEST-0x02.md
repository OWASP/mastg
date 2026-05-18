---
title: Integrity and Authenticity Validation of Local Storage Data
platform: ios
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0082
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0091, MASTG-KNOW-0093]
best-practices: [MASTG-BEST-0x02]
---

## Overview

Apps may store sensitive data in local storage, such as files, `UserDefaults`, Core Data, or Realm, and later use it in security-relevant decisions. If that data can be modified by an attacker and the app does not verify its integrity and authenticity before using it, the app may act on tampered input.

Although the app's private sandbox is protected from other apps under normal conditions, stored data can be tampered with on jailbroken devices, through device backups, or by directly modifying the app's data container after obtaining privileged access. For this reason, apps must not blindly trust security-relevant data read from local storage.

This test looks not only for storage read APIs, but also for nearby integrity and authenticity validation logic. Depending on the implementation, this may include APIs and patterns related to HMACs, MAC comparisons, cryptographic initialization, signature verification, checksums, or other mechanisms intended to detect tampering.

This test applies broadly to local storage, including file system access, `UserDefaults`, Core Data, Realm, and other app-managed storage locations. For background on iOS local storage APIs, see @MASTG-KNOW-0091 and @MASTG-KNOW-0093.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0058).
2. Run static analysis (@MASTG-TECH-0076) on the reverse-engineered app to identify APIs that read data from local storage and, where possible, nearby integrity and authenticity validation APIs.

## Observation

The output should contain code locations where the app reads data from local storage. Depending on the storage API and the analysis rule, these locations may include calls to `UserDefaults.object(forKey:)`, `FileManager`, `Data(contentsOf:)`, Core Data fetch requests, or Realm queries, along with nearby comparison or verification logic such as HMAC or cryptographic operations.

## Evaluation

The test case fails if the app does not verify the integrity and authenticity of data loaded from local storage before using it in security-relevant decisions.

The presence of a target API, for example `UserDefaults.object(forKey:)`, does not inherently fail the test. Each reported code location must be carefully analyzed by reverse engineering to check whether the app performs the proper validation.

When evaluating reported code locations, determine:

1. What value is being loaded from local storage.
2. Whether that value can influence a security-relevant decision, such as authentication state, authorization, feature access, configuration, or trust decisions.
3. Whether the app verifies the integrity and authenticity of the loaded value before using it, for example with an HMAC, MAC, signature, or similar verification mechanism.
4. Whether that validation is effective for the attacker model in scope.
