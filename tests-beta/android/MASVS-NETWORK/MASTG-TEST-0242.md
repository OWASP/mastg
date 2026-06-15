---
title: Missing Certificate Pinning in Network Security Configuration
platform: android
id: MASTG-TEST-0242
type: [static, code]
weakness: MASWE-0047
profiles: [L2]
knowledge: [MASTG-KNOW-0014, MASTG-KNOW-0015]
prerequisites:
- identify-first-party-domains
---

## Overview

Apps can configure [certificate pinning using the Network Security Configuration](../../../knowledge/android/MASVS-NETWORK/MASTG-KNOW-0015.md#pinning-via-network-security-configuration-api-24). For each domain, one or multiple digests can be pinned.

This test checks whether the app configures certificate pinning in the NSC for the relevant first-party domains it connects to. Relevant domains are remote endpoints under the developer's control that support the app's core or security-sensitive functionality. Third-party domains outside the developer's control **should not be reported** as missing pins only because they appear in app traffic. Note that the app may implement certificate pinning through other mechanisms covered in other tests.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml
3. Use @MASTG-TECH-0150 to check if a `networkSecurityConfig` is set in the `<application>` tag.
4. Use @MASTG-TECH-0151 to extract all domains from `<domain-config>` that have a pin set (`<pin-set>`) from the Network Security Configuration file.
5. Use @MASTG-TECH-0022 to identify the first-party domains the app connects to.

## Observation

The output should contain a list of domains that enable certificate pinning. The output should also identify any relevant first-party domains that were found in the app but do not have a pin set.

## Evaluation

The test case fails if the app connects to relevant first-party domains but no `networkSecurityConfig` is set, or if `networkSecurityConfig` is set but does not enable certificate pinning for those domains.

The test case should not fail only because unrelated third-party domains are not pinned.

If another certificate pinning implementation is identified for the same domains, such as a custom `TrustManager` or a third-party library, the result should be treated as not covered by NSC pinning rather than as a confirmed absence of certificate pinning.

**Further Validation Required:**

Before reporting a missing pin, confirm that the app actually establishes connections to the relevant first-party domains:

- Statically, follow the data references from the hardcoded URLs to the code that initiates the network connections (@MASTG-TECH-0023).
- Dynamically, capture and analyze the network traffic (@MASTG-TECH-0011) or hook the relevant network APIs at runtime to log the domains the app connects to.

Determining which domains are first-party and security-relevant typically requires information that is not present in the app binary and may require contact with the developers.
