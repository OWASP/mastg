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

The goal of this test is to check if any expiration date is in the past.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to check if `android:networkSecurityConfig` is set in the `<application>` tag.
4. Use @MASTG-TECH-0151 to extract the expiration dates for all certificate pins from the Network Security Configuration file.

## Observation

The output should contain a list of expiration dates for pinned certificates.

## Evaluation

The test case fails if any expiration date is in the past.
