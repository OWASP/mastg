---
platform: ios
title: References to APIs for Event-Bound Biometric Authentication
id: MASTG-TEST-0266
apis: [LAContext.evaluatePolicy]
type: [static, code]
weakness: MASWE-0044
profiles: [L2]
knowledge: [MASTG-KNOW-0056, MASTG-KNOW-0057]
---

## Overview

This test checks if the app insecurely accesses sensitive resources (e.g., tokens, keys) that should be protected by user authentication relying **solely** on the LocalAuthentication API for access control instead of using the Keychain API and requiring user presence.

The **LocalAuthentication** API (e.g., `LAContext`) provides user authentication (Touch ID, Face ID, device passcode), returning only a success or failure result. However, it **does not** securely store secrets or enforce any security. This makes it susceptible to logic manipulation (e.g., bypassing an `if authenticated { ... }` check).

In contrast, the **Keychain** API securely stores sensitive data, and can be configured with access control policies (e.g., require user presence such as biometrics) via `kSecAccessControl` flags. This ensures authentication is not just a one-time boolean, but part of a **secure data retrieval path (out-of-process)**, so bypassing authentication becomes significantly harder.

The Keychain APIs include `SecItemAdd`, `SecItemCopyMatching`, and `SecAccessControlCreateWithFlags` (with flags like `kSecAccessControlUserPresence`) to enforce user authentication on sensitive data access. See @MASTG-KNOW-0057 for more details.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain the locations where the `LAContext.evaluatePolicy` and Keychain APIs are used in the codebase (or the lack of their use).

## Evaluation

The test case fails if for each sensitive data resource worth protecting:

- `LAContext.evaluatePolicy` is used explicitly.
- There are no calls to `SecAccessControlCreateWithFlags` requiring user presence with [any of the possible flags](https://developer.apple.com/documentation/security/secaccesscontrolcreateflags).
