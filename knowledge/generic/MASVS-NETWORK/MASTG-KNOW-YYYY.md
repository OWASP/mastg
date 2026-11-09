---
masvs_category: MASVS-NETWORK
platform: generic
title: Self-Signed Certificates and Internal CAs
---

Self-signed certificates and internal CAs let your team issue certificates without relying on a public CA. This page explains how to configure app trust and the benefits and responsibilities of this approach.

Your team can use either approach:

- **Self-signed server certificate:** The server's own private key signs its certificate.
- **Internal CA:** Your company's CA signs certificates for its servers. Its root certificate is usually self-signed.

## Configuring App Trust

These certificates are not trusted by default. Configure the app to trust the self-signed server certificate or your internal CA. Adding a pin alone does not establish trust.

On Android, use [Network Security Configuration](https://developer.android.com/privacy-and-security/security-config#ConfigCustom) to configure custom trust anchors. For Apple platforms, see [HTTPS Server Trust Evaluation](https://developer.apple.com/library/archive/technotes/tn2232/_index.html) for an explanation of custom CAs and self-signed certificates.

See @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS implementation details.

## Benefits

- **Protection against third-party CA misuse:** Trusting only your company's internal CA prevents the app from accepting fraudulent certificates issued by public CAs. This can suit apps whose threat models include CA compromise, misissuance, or deliberate misuse of CA authority. The [CNNIC/MCS Holdings incident](https://security.googleblog.com/2015/03/maintaining-digital-certificate-security.html) illustrates this misuse of CA authority; see @MASTG-KNOW-XXXX.
- **Control over certificate management:** Your team can issue and renew certificates when needed, without waiting for a public CA. Renewing with the same public key keeps existing pins working. Public CAs support this too, but issuing certificates yourself gives you control over the timing.

Pinning a server certificate issued by a public CA provides the same protection against CA misissuance as pinning one issued by your internal CA. In both cases, your team controls the server's private key.

## Risks and Responsibilities

- **Weak key generation:** Keys generated with insufficient randomness may be recoverable by attackers. The eMudhra compromise shows that this can happen even to established CAs. Use a cryptographically secure random number generator and established cryptographic libraries. See @MASTG-KNOW-XXXX for details.
- **Internal CA compromise:** If your internal CA's private key is stolen, attackers can issue certificates that your app trusts. Your team must protect that key and plan how to replace it if compromised.

Plan and test certificate replacement and recovery before release. See @MASTG-BEST-0075 for pin-management guidance.
