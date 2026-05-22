---
title: Network Security Configuration Allowing Trust in User-Provided CAs
platform: android
id: MASTG-TEST-0286
type: [static, code]
weakness: MASWE-0052
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0014]
---

## Overview

This test evaluates whether an Android app **explicitly** trusts user-added CA certificates by including [`<certificates src="user"/>`](https://developer.android.com/privacy-and-security/security-config#certificates) in its [Network Security Configuration](https://developer.android.com/privacy-and-security/security-config#CustomTrust) which is defined [`android:networkSecurityConfig`](https://developer.android.com/guide/topics/manifest/application-element#networkSecurityConfig) attribute is set in the `<application>` tag. Even though starting with Android 7.0 (API level 24) apps no longer trust user-added CAs by default, this configuration overrides that behavior.

Such trust can expose the application to [MITM attacks](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm), as malicious CAs installed by users could intercept secure communications.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to check if the `android:networkSecurityConfig` attribute is present.
4. Use @MASTG-TECH-0151 to extract all uses of `<certificates src="user" />` from the Network Security Configuration file.

## Observation

The output should contain all the `<trust-anchors>` from the Network Security Configuration file along with any defined `<certificates>` entries, if present.

## Evaluation

The test case fails if `<certificates src="user" />` has been defined as part of the `<trust-anchors>` in the Network Security Configuration file.
