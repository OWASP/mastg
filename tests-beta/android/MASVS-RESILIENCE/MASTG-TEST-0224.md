---
title: Usage of Insecure APK Signature Version
platform: android
id: MASTG-TEST-0224
type: [static, code]
available_since: 24
weakness: MASWE-0104
best-practices: [MASTG-BEST-0006]
profiles: [R]
knowledge: [MASTG-KNOW-0003]
---

## Overview

Not using newer APK signing schemes means that the app lacks the enhanced security provided by more robust, updated mechanisms.

This test checks if the outdated v1 signature scheme is enabled. The v1 scheme is vulnerable to certain attacks, such as the "Janus" vulnerability ([CVE-2017-13156](https://nvd.nist.gov/vuln/detail/CVE-2017-13156)), because it does not cover all parts of the APK file, allowing malicious actors to potentially **modify parts of the APK without invalidating the signature**. Relying solely on v1 signing therefore increases the risk of tampering and compromises app security.

To learn more about APK Signing Schemes, see ["Signing Process"](../../../Document/0x05a-Platform-Overview.md#signing-process).

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0150 to obtain the `minSdkVersion` from the AndroidManifest.xml file.
3. Use @MASTG-TECH-0116 to list all used signature schemes.

## Observation

The output should contain the value of the `minSdkVersion` attribute and the used signature schemes (for example `Verified using v3 scheme (APK Signature Scheme v3): true`).

## Evaluation

The test case fails if the app has a `minSdkVersion` attribute of 24 and above, and only the v1 signature scheme is enabled.
