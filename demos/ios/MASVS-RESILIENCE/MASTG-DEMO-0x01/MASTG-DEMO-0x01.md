---
platform: ios
title: Running Security-Sensitive Code Without Source Code Integrity Checks
code: [swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

## Sample

The sample makes a security-sensitive decision (a license check) but never computes or verifies a hash over its own code at runtime. As a result, the app cannot detect whether its binary has been patched.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ source_integrity.r2 }}

{{ run.sh }}

## Observation

The output shows that the app contains a security-sensitive routine (a license check), identifiable by the `MAS-PREMIUM-2025` string literal in the `__TEXT.__cstring` section. The import table does reference source code integrity APIs such as `dladdr` and `CC_SHA256`, but these belong to a separate routine and are never applied to guard the security-sensitive decision itself, so the license check can still be patched without detection.

{{ output.asm }}

## Evaluation

The test case fails because the security-sensitive decision (the license check) is made without computing or verifying a hash over the app's own `__TEXT/__text` section. Although the import table references integrity APIs such as `dladdr` and `CC_SHA256`, they are only used by an unrelated routine and are never wired into the license check, so the app cannot detect if that decision's code has been patched.
