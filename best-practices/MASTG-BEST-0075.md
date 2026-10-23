---
title: Choose and Manage Identity Pins
alias: choose-and-manage-identity-pins
id: MASTG-BEST-0075
platform: generic
knowledge: [MASTG-KNOW-0015, MASTG-KNOW-0072]
---

Use identity pinning (commonly called SSL pinning) to protect connections from Machine-in-the-Middle (MITM) attacks. Pinning checks that at least one public key in the server's validated certificate chain matches a key approved in advance by your app. If none match, the app rejects the connection, even if the operating system trusts the certificate.

## Security Benefits and Operational Risks

Identity pinning prevents attackers from using a fraudulent certificate trusted by the operating system to intercept API traffic, steal credentials or session tokens, or modify requests and responses.

An [intermediate CA compromise in October 2026](https://blog.google/security/protecting-chrome-and-android-users-from-compromised-emudhra-intermediate-ca-certificates/) illustrates the value of identity pinning as protection against CA compromise. Attackers could forge certificates for arbitrary servers, putting apps relying on the platform's default CA trust at risk of traffic interception. Apple and Google distributed updates to block the compromised CAs, but [these protections can take days to reach devices](https://blog.google/security/protecting-chrome-and-android-users-from-compromised-emudhra-intermediate-ca-certificates/). Some devices may never receive the necessary trust-store or revocation updates. Pinning can protect connections during this gap, provided the app's pinset excludes the compromised CA chains.

Identity pinning also carries a risk of outages: if the server changes to a CA or key that is not in the app's pinset, existing app versions cannot connect until users install an update. Because of this risk, Google's [Android security guidance](https://developer.android.com/privacy-and-security/security-ssl#Pinning) states that "certificate pinning ... is not recommended for Android apps." The risk depends on what you pin and how you manage key rotation. The following sections explain [what to pin against](#what-to-pin-against) and how to [pin safely](#pin-safely) to reduce the risk of outages while maintaining the protection your threat model requires.

## What to Pin Against

You can pin the public key of a root CA, intermediate CA, or leaf certificate. Pinning only a root CA public key is sufficient to implement identity pinning, while pinning an intermediate CA or leaf public key narrows trust further. Choose based on your app's threat model and the following security and operational tradeoffs:

- **Pinning a Root CA Public Key:** Allows server and intermediate keys to rotate, usually without requiring a pin update, which reduces the risk of outages during these changes. However, the app trusts every intermediate CA under the root. If the root or any intermediate CA is compromised or issues an unauthorized certificate for your server, that certificate can pass the pin check. This broad trust is why root CA pinning is generally discouraged.
- **Pinning an Intermediate CA Public Key:** Allows the server key to change without updating the pin, but the app must trust that CA to remain secure and issue certificates only to authorized servers. Intermediate CA keys generally change more often than root CA keys, so pinning them requires more frequent pin maintenance and carries a greater risk of outages. Include backup pins to prepare for these changes.
- **Pinning a Leaf Public Key:** Pins the server's public key directly. Unlike CA pinning, leaf public-key pinning prevents a compromised CA from impersonating your server by issuing a certificate with a different key. As with CA pinning, leaf pinning doesn't protect against compromise of the server's private key, so plan key rotation. Before rotating the key, ensure supported app versions already contain a pin for the replacement key to avoid breaking connectivity.

Pinning higher in the certificate chain favors operational stability, while pinning lower provides stronger protection against CA compromise.

See the [OWASP Pinning Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Pinning_Cheat_Sheet.html#what-should-be-pinned) for more information on choosing what to pin.

## Self-Signed Certificates

Your team can use a self-signed server certificate or operate a private CA to issue server certificates. In either case, your team controls the private keys, certificate issuance, and validity periods.

This setup offers two benefits:

**Independence from third-party CA security.** Pinning to your own server or private CA keys avoids relying on third-party CAs for server authentication. This protects against fraudulent certificates issued through compromised or misused CA infrastructure, as illustrated by the [DigiNotar breach](https://security.googleblog.com/2011/08/update-on-attempted-man-in-middle.html), the [CNNIC/MCS incident](https://security.googleblog.com/2015/03/maintaining-digital-certificate-security.html), and the [eMudhra intermediate CA compromise](https://blog.google/security/protecting-chrome-and-android-users-from-compromised-emudhra-intermediate-ca-certificates/).

You can achieve the same protection against CA misissuance by pinning the leaf public key of a third-party CA-issued certificate. In both cases, only your team controls the corresponding private key.

**Control over certificate management.** You can issue replacement certificates immediately without waiting for a third-party CA. If an operational mistake causes a certificate to expire, you can issue a replacement using the same uncompromised key so existing public-key pins continue to work. This can restore connectivity without an app update, but shouldn't replace planned key rotation.

This control also makes your team responsible for choosing secure cryptographic algorithms and key sizes, generating keys with a cryptographically secure random number generator, protecting private keys, choosing appropriate validity periods, rotating keys, and recovering from compromise. The eMudhra incident shows that even established CAs can fail to generate keys securely.

## Pin Safely

Plan pin management before release so you can rotate keys and recover from compromise without causing avoidable outages.

- Avoid pinning third-party services that may change their keys or certificate chains without advance notice, as these changes can prevent your app from connecting.
- Include backup pins that let the server switch to another key or certificate chain without requiring an app update. For **leaf public-key pinning**, you can generate a separate backup key pair, include its public-key pin in the app, and obtain a valid CA-issued certificate for that key before deploying it.
- For **intermediate or root CA public-key pinning**, include a pin for an alternative CA key and prepare a valid server certificate chain that contains it. Either approach can also use certificates issued by your own private CA or self-signed certificates, provided supported app versions are configured to trust them.
- Choose backup pins according to the same threat model as active pins. Including a backup intermediate CA public key alongside a leaf public-key pin allows replacement server keys under that CA. However, the app trusts that CA from the start. If the CA is compromised, an attacker could issue a fraudulent certificate for your server that the app would accept. Use this option only if the broader trust is acceptable for your app.
- Before releasing the app with backup pins, test planned rotation and emergency recovery, including rejection of connections that don't match the configured pins.
- Before deploying a replacement server key or certificate chain, verify that supported app versions can connect using it.
- Prefer networking APIs that use the platform's certificate validation and receive its trust-store and revocation updates. If a networking library bundles its own root certificates or implements its own validation, check how it receives these updates. Otherwise, it may continue trusting a compromised CA until the app or library is updated.
- Avoid using pin expiration as a recovery mechanism when your threat model requires continued pinning protection. In Android's [Network Security Configuration](https://developer.android.com/privacy-and-security/security-config#CertificatePinning), expiration disables pinning after the configured date. Treat it as an explicit tradeoff between availability and security, rather than a safe way to rotate pins.
- Pinning can still be bypassed by an attacker who controls the device and can modify or instrument the app. See [MASVS-RESILIENCE](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/) for measures to increase the app's resilience to tampering and runtime instrumentation.

For implementation details, see @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS.
