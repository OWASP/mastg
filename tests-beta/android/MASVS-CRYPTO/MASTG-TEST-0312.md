---
title: References to Explicit Security Provider in Cryptographic APIs
platform: android
id: MASTG-TEST-0312
type: [static]
weakness: MASWE-0020
best-practices: [MASTG-BEST-0020]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0011]
---

## Overview

Android cryptography APIs based on the Java Cryptography Architecture (JCA) allow developers to specify a [security provider](https://developer.android.com/reference/java/security/Provider.html) when calling `getInstance` methods. However, explicitly specifying a provider can cause security issues and break compatibility because several providers have been deprecated or removed in recent versions. For example:

- Apps targeting Android 9 (API level 28) or above [fail when a provider is specified](https://android-developers.googleblog.com/2018/03/cryptography-changes-in-android-p.html).
- The _Crypto_ provider was deprecated in Android 7.0 (API level 24) and [removed in Android 9 (API level 28)](https://developer.android.com/about/versions/pie/android-9.0-changes-all#conscrypt_implementations_of_parameters_and_algorithms).
- The _BouncyCastle_ provider was [deprecated in Android 9 (API level 28) and removed in Android 12 (API level 31)](https://developer.android.com/about/versions/12/behavior-changes-all#bouncy-castle).

This test identifies cases where an app explicitly specifies a security provider when using JCA APIs that is not the default provider, `AndroidOpenSSL` ([Conscrypt](https://github.com/google/conscrypt)), which is actively maintained and should generally be used. It examines `getInstance` calls and flags any use of a named provider other than legitimate exceptions such as `KeyStore.getInstance("AndroidKeyStore")`.

## Steps

1. Run @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 on the app binary to look for calls to `getInstance` that explicitly specify a security provider.

## Observation

The output should contain a list of locations where a security provider is explicitly specified in `getInstance` calls.

## Evaluation

The test case fails if any `getInstance` call explicitly specifies a security provider other than `AndroidKeyStore` for `KeyStore` operations. Review each occurrence to determine whether the provider is actually required and whether its use could introduce security or compatibility issues on modern Android versions.
