---
platform: ios
title: Data Protection Classes for Files in Private Storage
id: MASTG-TEST-0299
type: [dynamic, filesystem]
prerequisites:
- identify-sensitive-data
profiles: [L1]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0024]
knowledge: [MASTG-KNOW-0091, MASTG-KNOW-0108]
---

## Overview

This test retrieves the data protection classes of files created or modified in the app's private storage during typical app usage. The goal is to ensure that files containing sensitive data are assigned appropriate data protection classes to safeguard them when the device is locked.

## Steps

1. Ensure the device / simulator is in a clean state (no prior test artifacts). Terminate the app if running.
2. Launch and exercise the app to trigger typical workflows (authentication, profile loading, messaging, caching, offline usage, cryptographic operations).
3. Retrieve the list of files from the app's private storage (sandbox) directory tree (`/var/mobile/Containers/Data/Application/<UUID>/`) including the data protection classes (@MASTG-TECH-0059).

## Observation

The output should contain:

- List of files in private storage including at least path and data protection class.

## Evaluation

The test case fails if **files containing sensitive data** have the data protection class set to `NSFileProtectionNone`.
