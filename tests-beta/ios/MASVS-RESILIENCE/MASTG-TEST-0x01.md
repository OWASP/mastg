---
platform: ios
title: References to Source Code Integrity Check APIs
id: MASTG-TEST-0x01
type: [static, code, manual]
weakness: MASWE-0104
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0086]
best-practices: [MASTG-BEST-0x01]
---

## Overview

iOS apps can implement runtime source code integrity checks to detect if the binary has been tampered with. These checks typically parse the Mach-O binary structure to locate the `__TEXT/__text` section, compute a hash over it, and compare that hash against a reference value (see @MASTG-KNOW-0086). If the app does not implement such checks, an attacker who patches the binary can go undetected.

This test verifies that the app references APIs commonly used to implement source code integrity checks, such as `dladdr` for resolving the binary base address, Mach-O header parsing structures (`mach_header`, `load_command`), and cryptographic hash functions applied to code sections (e.g., `CC_SHA256`).

Note that [Apple's code signing](https://developer.apple.com/documentation/xcode/using-the-latest-code-signature-format) and DRM (FairPlay) provide some level of integrity protection at the OS level, but additional in-app runtime checks raise the bar for attackers who operate on jailbroken devices where these protections may be bypassed.

**Example Attack Scenario:**

Suppose a banking app relies only on OS-level code signing and implements no runtime integrity check.

1. An attacker reverse engineers the app and patches the binary to disable a security control (for example, jailbreak detection) using @MASTG-TECH-0090.
2. The attacker re-signs the modified app with their own certificate using @MASTG-TECH-0092.
3. The attacker installs the patched app on a jailbroken device, where OS-level code signing no longer protects it.
4. Because the app never verifies the integrity of its own code at runtime, the tampering goes undetected and the attacker can run the app with the security control removed.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should include any references to source code integrity check APIs such as `dladdr`, `mach_header`, `LC_SEGMENT`, and cryptographic hash functions (e.g., `CC_MD5`, `CC_SHA256`, `CC_SHA512`) applied to code sections.

## Evaluation

The test case fails if the app contains no references to source code integrity check APIs.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine whether the referenced APIs actually implement a source code integrity check:

- Determine whether the hash is computed over the `__TEXT/__text` section (rather than, for example, hashing unrelated data for caching or networking).
- Determine whether the computed hash is compared against a stored reference value and the app reacts when the comparison fails.

**Expected False Negatives:**

This test may produce false negatives if the integrity check is built using patterns not covered by the analysis (for example, a custom hash function instead of `CC_SHA256`). In such cases, the absence of findings does not guarantee the absence of an integrity check, and additional manual reverse engineering may be required.
