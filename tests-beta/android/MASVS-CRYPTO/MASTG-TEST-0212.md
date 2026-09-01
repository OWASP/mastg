---
title: Use of Hardcoded Cryptographic Keys in Code
platform: android
id: MASTG-TEST-0212
type: [static, code]
weakness: MASWE-0003
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0012]
---

## Overview

In this test case, we will look for the use of hardcoded cryptographic keys in Android applications. Hardcoded key material may be passed to different cryptographic APIs or represented using different key classes and specifications. For example, the Java Cryptography Architecture (JCA) provides several [`KeySpec`](https://developer.android.com/reference/java/security/spec/KeySpec) implementations and key factory APIs for constructing cryptographic keys from supplied key material.

The use of a key-related API or class, such as [`SecretKeySpec`](https://developer.android.com/reference/javax/crypto/spec/SecretKeySpec), does not by itself indicate that a key is hardcoded. The source of the key material must be traced to determine whether it is embedded in the application code.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to identify cryptographic key construction, import, derivation, or use, and trace the corresponding key material back to its source.

## Observation

The output should contain a list of locations where cryptographic keys are used together with the source of their key material, identifying any key material that is hardcoded in the application code.

## Evaluation

The test case fails if you find any hardcoded cryptographic key material that is used in security-sensitive contexts.
