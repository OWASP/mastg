---
platform: ios
title: References to Source Code Integrity Check APIs
id: MASTG-TEST-0x01
type: [static, code]
weakness: MASWE-0104
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0086]
best-practices: [MASTG-BEST-0x01]
---

## Overview

iOS apps can implement runtime source code integrity checks to detect if the binary has been tampered with. These checks typically parse the Mach-O binary structure to locate the `__TEXT/__text` section, compute a hash over it, and compare that hash against a reference value. If the app does not implement such checks, an attacker who patches the binary (see @MASTG-KNOW-0086) may go undetected.

This test verifies that the app references APIs commonly used to implement source code integrity checks, such as `dladdr` for resolving the binary base address, Mach-O header parsing structures (`mach_header`, `load_command`), and cryptographic hash functions applied to code sections (e.g., `CC_MD5`, `CC_SHA256`).

Note that [Apple's code signing](https://developer.apple.com/documentation/xcode/using-the-latest-code-signature-format) and DRM (FairPlay) provide some level of integrity protection at the OS level, but additional in-app runtime checks raise the bar for attackers who operate on jailbroken devices where these protections may be bypassed.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should include any references to source code integrity check APIs such as `dladdr`, `mach_header`, `LC_SEGMENT`, and cryptographic hash functions (e.g., `CC_MD5`, `CC_SHA256`, `CC_SHA512`) applied to code sections.

## Evaluation

The test case fails if the app contains no references to source code integrity check APIs.

Note that this test is not exhaustive and may not detect all source code integrity check implementations, especially if they are obfuscated or implemented using patterns not covered by the analysis.
