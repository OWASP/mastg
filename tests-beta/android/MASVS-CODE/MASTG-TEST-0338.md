---
title: Integrity and Authenticity Validation of Local Storage Data
platform: android
id: MASTG-TEST-0338
type: [static]
weakness: MASWE-0082
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0036]
---

## Overview

Apps may store sensitive data in local storage and later use it in security-relevant decisions. If that data can be modified by an attacker and the app does not verify its integrity and authenticity before using it, the app may trust tampered input.

This test applies to local storage broadly, including `SharedPreferences`, files, databases, and other app managed storage locations.

For example, when using `SharedPreferences` specifically, the data is stored in the app's private sandbox and normally cannot be modified by other apps. However, it can still be tampered with in local attack scenarios, such as on rooted devices, during dynamic analysis, through backups, or by directly manipulating the app's data directory after obtaining privileged access, as described in @MASTG-KNOW-0036. Because of that, apps should not blindly trust security-relevant data loaded from local storage.

When performing this test, look not only for storage read APIs, but also for nearby integrity and authenticity validation logic. Depending on the implementation, this may include APIs and patterns related to HMACs, MAC comparison, cryptographic initialization, signature verification, checksums, or other mechanisms intended to detect tampering.

## Steps

1. Reverse engineer the app with @MASTG-TECH-0017.
2. Run a static analysis tool with @MASTG-TECH-0014 on the reverse engineered app to identify APIs that read data from local storage and, where possible, related integrity and authenticity validation APIs.

## Observation

The output should contain code locations where the app reads data from local storage. Depending on the storage API and the analysis rule, these code locations may include APIs such as `SharedPreferences.getString`, file reads, database queries, or nearby comparison and verification logic such as HMAC or MAC related operations.

## Evaluation

The test case fails if the app doesn't verify the integrity and authenticity of data loaded from local storage before being used in security-relevant decisions.

This means that the presence of the target APIs, for example `SharedPreferences.getString`, does not inherently fail the test. Each reported code location must be carefully analyzed by reverse engineering it and checking whether the app performs the proper validation.

When evaluating reported code locations, determine:

1. What value is being loaded from local storage.
2. Whether that value can influence a security-relevant decision, such as authentication state, authorization, feature access, configuration, or trust decisions.
3. Whether the app verifies the integrity and authenticity of the loaded value before using it, for example with an HMAC, MAC, signature, or similar verification mechanism.
4. Whether that validation is effective for the attacker model in scope.
