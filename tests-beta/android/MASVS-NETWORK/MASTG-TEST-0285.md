---
title: Outdated Android Version Allowing Trust in User-Provided CAs
platform: android
id: MASTG-TEST-0285
type: [static, code]
deprecated_since: 24
weakness: MASWE-0052
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0014]
---

## Overview

This test evaluates whether an Android app **implicitly** trusts user-added CA certificates by [default](https://developer.android.com/privacy-and-security/security-config#CustomTrust), which is the case for apps that can be installed to devices running API level 23 or lower.

Those apps rely on the default Network Security Configuration that trusts both system and user-installed Certificate Authorities (CAs). Such trust can expose the app to [MITM attacks](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm), as malicious CAs installed by users could intercept secure communications.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to read the value of the `minSdkVersion` attribute from the `<uses-sdk>` element.

## Observation

The output should contain the value of `minSdkVersion`.

## Evaluation

The test case fails if `minSdkVersion` is less than 24.
