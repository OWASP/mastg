---
masvs_category: MASVS-NETWORK
platform: generic
title: CA Compromises and Platform Protections
---

Apps normally trust server certificates issued by CAs trusted by the operating system. If a CA is compromised or misuses its authority, it can issue fraudulent certificates that the app accepts. Attackers can use these certificates to impersonate servers and intercept or modify traffic.

The following incidents illustrate these risks and how pinning and platform protections can help.

## CA Compromise and Misuse

- **[eMudhra (2026)](https://blog.google/security/protecting-chrome-and-android-users-from-compromised-emudhra-intermediate-ca-certificates/):** Intermediate CA private keys were generated with insufficient randomness, allowing attackers to reconstruct them. These keys could be used to forge certificates for arbitrary servers.
- **[CNNIC/MCS Holdings (2015)](https://security.googleblog.com/2015/03/maintaining-digital-certificate-security.html):** MCS Holdings installed a CNNIC-issued intermediate CA's private key in a MITM proxy. The proxy issued unauthorized certificates for Google domains.
- **[DigiNotar (2011)](https://security.googleblog.com/2011/08/update-on-attempted-man-in-middle.html):** Attackers used a fraudulent DigiNotar-issued certificate in attempted MITM attacks against Google users, primarily in Iran. The certificate could pass normal validation on devices that still trusted DigiNotar.

Pinning the legitimate server's leaf public key would protect against these attacks. CA public-key pinning would also protect the connection if none of the keys in the fraudulent chain matched the app's accepted pins, including backup pins.

## Platform Protections Against Fraudulent Certificates

Pinning is not the only protection against fraudulent certificates. Apps using platform certificate validation can also benefit from:

- **Certificate revocation and blocklists:** Platforms can reject certificates or CA keys identified as compromised or misused. Google and Apple distribute certificate blocklist updates without requiring a full OS update, but protection depends on the device receiving the update and the app's TLS stack applying it.
- **Certificate Transparency (CT):** Clients can require evidence that a publicly trusted server certificate has been submitted to accepted public logs. CT enforcement rejects certificates that do not meet this requirement; it does not reject every fraudulent certificate, since a misissued certificate may also be logged.

The following table compares resilience to CA compromise across platform versions and app configurations, using eMudhra to illustrate potential exposure and when pinning provides additional protection. In that incident, CT enforcement protected clients because the [fraudulent certificates lacked the required CT log entries](https://blog.google/security/protecting-chrome-and-android-users-from-compromised-emudhra-intermediate-ca-certificates/).

| Platform / configuration | eMudhra exposure | Pinning benefit |
| --- | --- | --- |
| iOS 12.1+, CT enforced | Blocked by CT | None needed for this incident |
| iOS <12.1 | Until certificate block | Covers exposure period |
| Android ≤15 | Until certificate block | Covers exposure period |
| Android 16, CT enabled | Blocked by CT | None needed for this incident |
| Android 16, CT disabled by default | Until certificate block | Covers exposure period |
| Android 17+, target API 37+, CT enabled by default | Blocked by CT | None needed for this incident |
| Android 17+, CT disabled | Until certificate block | Covers exposure period |

**Exposure period:** Potentially from [April 30, 2025](https://bugzilla.mozilla.org/show_bug.cgi?id=2083449), until the device's TLS stack applied an effective certificate block. [Google](https://blog.google/security/protecting-chrome-and-android-users-from-compromised-emudhra-intermediate-ca-certificates/) and [Apple](https://support.apple.com/en-us/149555) began distributing blocks on October 19, 2026; devices without remediation could remain exposed.

**Pinning benefit:** Applies only when none of the app's accepted pins, including backup pins, match the fraudulent chain.

Android's documentation confirms [CT availability](https://developer.android.com/privacy-and-security/security-config#CertificateTransparency) and the [target SDK requirement](https://developer.android.com/about/versions/17/behavior-changes-17#enable-ct).

Custom HTTPS libraries or certificate checks may behave differently. Verify their trust store, CT enforcement, and certificate-block updates; the OS version alone does not establish protection.

For guidance on when to pin and how to manage pins, see @MASTG-BEST-0075. For platform-specific implementation details, see @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS.
