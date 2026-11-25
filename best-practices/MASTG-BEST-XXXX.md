---
title: Pin TLS Certificate
alias: pin-tls-certificate
id: MASTG-BEST-XXXX
platform: generic
knowledge: [MASTG-KNOW-0015, MASTG-KNOW-0072, MASTG-KNOW-XXXX, MASTG-KNOW-YYYY, MASTG-KNOW-ZZZZ]
---

Use identity pinning (commonly called certificate pinning) to protect connections from Machine-in-the-Middle (MITM) attacks. Pinning checks that at least one public key in the server's certificate chain matches a key approved in advance by your app. If none match, the app rejects the connection, even if the operating system trusts the certificate. This helps prevent attackers from intercepting or modifying the app's traffic.

## When to Pin

Consider identity pinning when your threat model requires restricting trust beyond the platform's default certificate validation. It can protect against fraudulent server certificates issued by a compromised CA or by a malicious CA added to the operating system's trusted CAs (see @MASTG-TECH-0120).

Balance this protection against the risk of losing connectivity. If the server changes its key or certificate chain and the new chain doesn't contain any of the app's pinned keys, the app rejects the connection. Affected users may need an app update with new pins to reconnect. Because of this risk, [Google discourages certificate pinning for Android apps](https://developer.android.com/privacy-and-security/security-ssl#Pinning), and [Apple recommends using it only when necessary](https://developer.apple.com/news/?id=g9ejcf8y).

For examples of CA compromises and how pinning could protect connections in those scenarios, see @MASTG-KNOW-XXXX.

Adopt pinning only if your team can coordinate key and certificate-chain changes for the endpoint and maintain pins throughout the app's supported lifetime.

The following sections explain [what to pin against](#what-to-pin-against) and how to [pin safely](#pin-safely).

## What to Pin Against

You can pin the public key of a root CA, intermediate CA, or leaf certificate. Pin the public key (its SPKI hash) rather than the whole certificate, so that renewing a certificate with the same key does not invalidate the pin. Choose based on your app's threat model and the following security and operational tradeoffs:

- **Pinning a Root CA Public Key:** Allows server and intermediate keys to rotate, usually without requiring a pin update, which reduces the risk of outages during these changes. However, the app trusts every intermediate CA under the root, so if the root or any intermediate CA is compromised or issues an unauthorized certificate for your server, that certificate still passes the pin check. This broad trust makes root CA pinning the least restrictive option, but the app no longer trusts the rest of the CAs in the OS truststore and instead relies only on the single root CA you pin against, which already reduces the MitM attack surface significantly.
- **Pinning an Intermediate CA Public Key:** Allows the server key to change without updating the pin, but the app must trust that CA to remain secure and issue certificates only to authorized servers. Intermediate CA keys generally change more often than root CA keys, so pinning them requires more frequent pin maintenance and carries a greater risk of outages.
- **Pinning a Leaf Public Key:** Pins the server's public key directly. Unlike CA pinning, leaf public-key pinning prevents a compromised CA from impersonating your server by issuing a certificate with a different key. As with CA pinning, leaf pinning doesn't protect against compromise of the server's private key, so plan key rotation. Before rotating the key, ensure supported app versions already contain a pin for the replacement key to avoid breaking connectivity. The common lifespan of a leaf certificate can be [very short](https://letsencrypt.org/2025/12/02/from-90-to-45) and therefore introduce the largest operational risk compared to the other pins.

Pinning higher in the certificate chain favors operational stability, while pinning lower provides stronger protection against CA compromise.

Backup pins always need to be included in the identity pinning configuration to prepare for any changes in the serving CA chain from root, to intermediate to leave certificates.

See the [OWASP Pinning Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Pinning_Cheat_Sheet.html#what-should-be-pinned) for more information on choosing what to pin.

If your team needs control over certificate issuance and renewal, or independence from public CAs, consider issuing your own certificates. See @MASTG-KNOW-YYYY for details on self-signed certificates and internal CAs.

## Pin Safely

Plan pin management before release so you can rotate keys and recover from compromise without causing avoidable outages.

- Avoid pinning third-party services that may change their keys or certificate chains without advance notice, as these changes can prevent your app from connecting.
- Include backup pins that let the server switch to another key or certificate chain without requiring an app update. For **leaf public-key pinning**, you can generate a separate backup key pair, include its public-key pin in the app, and obtain a valid CA-issued certificate for that key before deploying it.
- For **intermediate or root CA public-key pinning**, include a pin for an alternative CA key and prepare a valid server certificate chain that contains it.
- Choose backup pins according to the same threat model as active pins. Including a backup intermediate CA public key alongside a leaf public-key pin allows replacement server keys under that CA. However, the app trusts that CA from the start. If the CA is compromised, an attacker could issue a fraudulent certificate for your server that the app would accept. Use this option only if the broader trust is acceptable for your app.
- If your threat model relies on certificate revocation, verify that your app rejects revoked server and intermediate CA certificates. Consider blocking connections when revocation information is unavailable
- Before releasing the app with backup pins, test planned rotation and emergency recovery, including rejection of connections that don't match the configured pins.
- Pinning can still be bypassed by an attacker who controls the device and can modify or instrument the app. See @MASVS-RESILIENCE for measures to increase the app's resilience to tampering and runtime instrumentation.
- Prefer networking APIs that use the platform's certificate validation and receive its trust-store and revocation updates. If a networking library bundles its own root certificates or implements its own validation, check how it receives these updates. Otherwise, it may continue trusting a compromised CA until the app or library is updated.
- Avoid using pin expiration as a recovery mechanism when your threat model requires continued pinning protection. In Android's [Network Security Configuration](https://developer.android.com/privacy-and-security/security-config#CertificatePinning), expiration disables pinning after the configured date. Treat it as an explicit tradeoff between availability and security, rather than a safe way to rotate pins.
- Pinning can still be bypassed by an attacker who controls the device and can modify or instrument the app. See [MASVS-RESILIENCE](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/) for measures to increase the app's resilience to tampering and runtime instrumentation.

For implementation details, see @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS.
