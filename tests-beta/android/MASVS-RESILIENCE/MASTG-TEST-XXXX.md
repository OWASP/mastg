---
title: References to Integrity Validation of Local Storage Data
platform: android
id: MASTG-TEST-0x01
weakness: MASWE-0082
type: [static, code, manual]
profiles: [L1, L2, R]
knowledge: [MASTG-KNOW-0041]
best-practices: [MASTG-BEST-0x01]
---

## Overview

Android apps frequently persist user state, role flags, feature gates, and configuration values as files in private storage (`context.filesDir`). If the app reads these files back without verifying their integrity, an attacker with on-device write access — root, malware, accessibility abuse, or a debuggable build via `run-as` — can rewrite the file contents and the app will accept the modified value without verification.

A common defense is to compute an HMAC over the file bytes and store it in a companion `.hmac` file (or similar) so that a tamper-after-write attack is detected at read time. The HMAC's strength collapses entirely if the key used to compute it is hardcoded in the dex: the same reverse engineer who can rewrite the file can read the hardcoded key and recompute a valid HMAC over the tampered payload. This test checks whether the app verifies the integrity and authenticity of data loaded from local storage before trusting it, and whether the HMAC secret is generated inside AndroidKeyStore rather than embedded as a constant.

### Example Attack Scenario

Suppose a banking app stores the user's role (`user`, `admin`) in a file under `getFilesDir()` and grants admin features based on the value read at app launch.

1. An attacker reverse engineers the app using @MASTG-TECH-0013 and locates the role file path and the file-loading code using @MASTG-TECH-0014.
2. The attacker rewrites the file from `user` to `admin` using `adb shell run-as` on a debuggable build, or via a malicious app on a rooted device.
3. On next launch, the app reads `admin` from disk without verifying integrity and unlocks admin features.
4. The attacker now has elevated privileges without authenticating.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of code locations where local file content is read (for example, `FilesKt.readBytes`, `FilesKt.readText`, `new FileInputStream(...)`) and references to HMAC validation logic (for example, `Mac.getInstance("HmacSHA256")`, `new SecretKeySpec(...)`, `MessageDigest.isEqual`).

## Evaluation

The test case fails if local file reads are not paired with HMAC validation, or if the HMAC key is hardcoded as a constant in the source rather than generated inside AndroidKeyStore.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the integrity validation is correctly implemented:

- Determine whether the loaded file content is passed through an HMAC validation step (for example, `Mac.doFinal(...)` followed by `MessageDigest.isEqual(...)`) before being trusted.
- Determine whether the HMAC key is generated via `KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")` with `KeyGenParameterSpec` rather than constructed from a hardcoded `SecretKeySpec(byte[], "HmacSHA256")`.
- Determine whether the comparison uses `MessageDigest.isEqual` (constant-time) rather than `String.equals` (timing side-channel).

**Expected False Negatives:**

This test may produce false negatives if the app implements integrity validation through unusual API paths not covered by the rules (for example, JNI calls into a native library performing HMAC, or a custom obfuscated wrapper around `javax.crypto.Mac`). In such cases, the absence of findings does not guarantee the absence of integrity validation, and additional manual reverse engineering may be required.
