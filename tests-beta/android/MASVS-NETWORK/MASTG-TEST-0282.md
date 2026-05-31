---
title: Unsafe Custom Trust Evaluation
platform: android
id: MASTG-TEST-0282
type: [static, code, manual]
weakness: MASWE-0052
best-practices: [MASTG-BEST-0021]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0010]
---

## Overview

This test evaluates whether an Android app uses [`checkServerTrusted(...)`](https://developer.android.com/reference/javax/net/ssl/X509TrustManager#checkServerTrusted%28java.security.cert.X509Certificate[],%20java.lang.String%29) [in an unsafe manner](https://developer.android.com/privacy-and-security/risks/unsafe-trustmanager) as part of a custom `TrustManager`, causing any connection configured to use that `TrustManager` to skip certificate validation.

Such unsafe implementations can allow an attacker to run a [MITM attack](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) with a valid (or self-signed) certificate and intercept or tamper with the app's traffic.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where `checkServerTrusted(...)` is used.

## Evaluation

The test case fails if `checkServerTrusted(...)` is implemented in a custom `X509TrustManager` and does **not** properly validate server certificates.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023, looking for cases such as:

- **Using `checkServerTrusted(...)` which is error prone, when NSC would be enough.
- **Trust manager that does nothing:** overriding `checkServerTrusted(...)` to accept all certificates without any validation, for example by returning immediately without verifying the certificate chain or by always returning `true`.
- **Ignoring errors:** failing to [throw proper exceptions](https://support.google.com/faqs/answer/6346016) (e.g. [`CertificateException`](https://developer.android.com/reference/java/security/cert/CertificateException.html) or [`IllegalArgumentException`](https://developer.android.com/reference/java/lang/IllegalArgumentException)) on validation failure, or catching and suppressing them.
- **Using [`checkValidity()`](https://developer.android.com/reference/java/security/cert/X509Certificate#checkValidity()) instead of full validation:** relying only on `checkValidity()` checks whether the certificate is expired or not yet valid, but does **not** verify trust or hostname matching.
- **Explicitly loosening trust:** disabling trust checks to accept self-signed or untrusted certificates for convenience during development or testing.
- **Misusing [`getAcceptedIssuers()`](https://developer.android.com/reference/javax/net/ssl/X509TrustManager#getAcceptedIssuers())**: Returning `null` or an empty array without proper handling may effectively disable issuer validation.
