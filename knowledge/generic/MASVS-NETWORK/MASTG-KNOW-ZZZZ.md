---
masvs_category: MASVS-NETWORK
platform: generic
title: Identity Pinning
---

Identity pinning restricts which certificates an app accepts for a server. It helps prevent Machine-in-the-Middle (MITM) attacks, even when the operating system trusts the attacker's certificate.

## How Pinning Works

Normally, an app accepts a server certificate if it passes certificate-chain, hostname, and validity checks. This can include certificates issued by any CA trusted by the operating system.

Pinning adds another requirement: the certificate chain must match an identity approved by the app. If it does not, the app rejects the connection.

Pinning does not replace normal certificate checks. It also does not establish trust in a self-signed certificate or internal CA. See @MASTG-KNOW-YYYY for configuring that trust.

## What Can Be Pinned?

An app can pin:

- **A certificate:** The app checks for a specific certificate. A replacement certificate needs a new pin, even if its public key stays the same.
- **A public key:** The app checks for a specific public key. Renewing a certificate with the same key does not change the pin.

Public-key pins commonly use a SHA-256 hash of the certificate's Subject Public Key Info (SPKI). SPKI contains the public key and its algorithm information.

The pinned key can belong to:

- **A root CA:** Allows certificate chains containing that root key.
- **An intermediate CA:** Allows certificate chains containing that intermediate key.
- **The server:** Requires the expected server public key.

An app can accept multiple pins. A backup pin can allow a replacement key before the current key is retired. The exact matching rules depend on the pinning API. See @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS.

Pins contain certificates, public keys, or their hashes, not private keys. Their protection does not depend on keeping them secret.

## Scope

Pinning usually applies to selected hostnames and connections. Pinning one API does not automatically protect every connection made by the app. Subdomains, redirects, and other networking libraries may need separate configuration.

## What Pinning Protects Against

Pinning can reject fraudulent certificates that would otherwise pass normal certificate checks. This includes certificates issued by compromised CAs or malicious CAs added to the device's trusted CAs.

Protection depends on the pins. For example, pinning a root CA still allows fraudulent certificates issued by an intermediate beneath that root.

See @MASTG-KNOW-XXXX for CA incidents and platform protections.

## Limitations

Server or CA key changes can prevent the app from connecting if its pins are not updated in advance.

An attacker who can modify or instrument the app may bypass its pinning checks. Pinning also does not protect against theft of the server's private key.

For recommendations on choosing and managing pins, see @MASTG-BEST-XXXX. For implementation details, see @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS.
