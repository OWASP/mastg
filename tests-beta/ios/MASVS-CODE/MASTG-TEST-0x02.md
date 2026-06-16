---
title: Integrity and Authenticity Validation of Local Storage Data
platform: ios
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0082
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0091]
best-practices: [MASTG-BEST-0x02]
---

## Overview

This test checks whether the app verifies the integrity and authenticity of data loaded from local storage before using it in security-relevant decisions. It looks for storage read APIs alongside nearby validation logic such as HMACs, signature verification, or other tamper-detection mechanisms. 

For background on iOS local storage APIs, see @MASTG-KNOW-0091 and @MASTG-KNOW-0093.

## Steps

1. Use @MASTG-TECH-0066 to look for APIs that read data from local storage (such as `UserDefaults.object(forKey:)`, `FileManager`, `Data(contentsOf:)`, Core Data fetch requests, or Realm queries) and, where possible, nearby integrity and authenticity validation APIs such as HMAC or cryptographic operations.

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
