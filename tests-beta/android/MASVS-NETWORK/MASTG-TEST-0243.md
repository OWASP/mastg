---
title: Expired Certificate Pins in the Network Security Configuration 
platform: android
id: MASTG-TEST-0243
type: [static, code]
weakness: MASWE-0047
profiles: [L2]
knowledge: [MASTG-KNOW-0014, MASTG-KNOW-0015]
---

## Overview

Apps can configure expiration dates for pinned certificates in the Network Security Configuration (NSC) (@MASTG-KNOW-0014) by using the `expiration` attribute. When a pin expires, the app no longer enforces certificate pinning and instead relies on its configured trust anchors. This means the connection will still succeed if the server presents a valid certificate from a trusted CA (such as a system CA or a custom CA defined in the app's configuration). However, if no trusted certificate is available, the connection will fail.

If developers assume pinning is still in effect but don't realize it has expired, the app may start trusting CAs it was never intended to.

> Example: A financial app previously pinned to its own private CA but, after expiration, starts trusting publicly trusted CAs, increasing the risk of compromise if a CA is breached.

This test checks whether any certificate pin configured for a relevant first-party domain has expired. Relevant domains are remote endpoints under the developer's control that support the app's core or security-sensitive functionality.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to check if `android:networkSecurityConfig` is set in the `<application>` tag.
4. Use @MASTG-TECH-0151 to extract the expiration dates for all certificate pins from the Network Security Configuration file.
5. Use @MASTG-TECH-0022 to identify the first-party domains the app connects to.

## Observation

The output should contain a list of expiration dates for pinned certificates, along with the domains they apply to. The output should also identify which of those domains are relevant first-party domains the app connects to.

## Evaluation

The test case fails if any certificate pin configured for a relevant first-party domain has an expiration date in the past.

The test case should not fail only because pins for unrelated third-party domains have expired.

**Further Validation Required:**

Before reporting an expired pin, confirm that the app actually establishes connections to the affected first-party domains:

- Statically, follow the data references from the hardcoded URLs to the code that initiates the network connections (@MASTG-TECH-0023).
- Dynamically, capture and analyze the network traffic (@MASTG-TECH-0011) or hook the relevant network APIs at runtime to log the domains the app connects to.

Determining which domains are first-party and security-relevant typically requires information that is not present in the app binary and may require contact with the developers.
