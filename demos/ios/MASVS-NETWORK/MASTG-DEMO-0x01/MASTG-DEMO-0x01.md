---
platform: ios
title: Insecure App Transport Security Configuration
code: [xml]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
---

### Sample

The code snippet below shows an `Info.plist` configuration that disables App Transport Security (ATS) globally, allowing the app to make insecure HTTP connections and accept any certificate without proper validation.

{{ Info.plist }}

### Steps

1. Extract the app's `Info.plist` file from the IPA package as explained in @MASTG-TECH-0092.
2. Review the `NSAppTransportSecurity` configuration using the following command:

{{ run.sh }}

### Observation

The output shows the complete ATS configuration from the `Info.plist` file.

{{ output.txt }}

### Evaluation

The test fails because:

- **`NSAllowsArbitraryLoads` is set to `true`**, which globally disables ATS for all network connections. This allows the app to:
  - Make HTTP connections instead of requiring HTTPS
  - Accept any certificate without validation
  - Use weak TLS versions below 1.2
  - Bypass hostname verification

This configuration completely removes the security protections that ATS provides, making the app vulnerable to Machine-in-the-Middle (MITM) attacks.

**Best Practice**: Remove `NSAllowsArbitraryLoads` and use domain-specific exceptions only when absolutely necessary (@MASTG-BEST-0025). If you must connect to legacy servers, configure minimal exceptions for specific domains rather than disabling ATS globally.
