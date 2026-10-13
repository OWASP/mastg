---
title: Choose and Manage Identity Pinning for Mobile Apps
alias: choose-and-manage-identity-pinning-for-mobile-apps
id: MASTG-BEST-0075
platform: generic
knowledge: [MASTG-KNOW-0015, MASTG-KNOW-0072]
---

Identity pinning can strengthen TLS server authentication by making the app accept a server only if it presents a public key the developer approved in advance. The app compares the key in the server's certificate with its stored pin and rejects the connection if they do not match, even if the certificate chains to a CA trusted by the operating system.

## What to Pin Against

Pinning can target one of three public keys in the server's certificate chain. The higher the pinned key is in the chain, the more key changes the app can tolerate, but the more certificate authorities it trusts:

- **Root CA:** Allows the server and intermediate keys to change without updating the pin. However, the app trusts every intermediate CA under the root. If the root CA or any of its intermediate CAs is compromised or issues an unauthorized certificate for the server, the app will accept it.
- **Intermediate CA:** Allows the server key to change without updating the pin, but the app must trust that CA to remain secure and issue certificates only to authorized servers. Intermediate CA certificates usually have shorter validity periods than root CA certificates and are replaced more often. If the intermediate public key changes, the app needs a matching backup pin.
- **Leaf certificate:** This is the server's certificate. Pinning its public key makes the app accept only the pinned server keys. Leaf certificates usually have shorter validity periods than CA certificates to limit how long a compromised or unauthorized certificate can be used. They must therefore be rotated more often. Before rotating the server key, app versions must contain a backup pin for the replacement key. Otherwise, the app can no longer connect, causing an outage.

See the [OWASP Pinning Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Pinning_Cheat_Sheet.html#what-should-be-pinned) for this tradeoff.

One possible way to reduce the risk of an outage is to pin the leaf public key and include the intermediate CA public key as a backup. If an unexpected problem requires the leaf key to be replaced, the server can deploy a replacement certificate under the same intermediate CA without waiting for an app update. However, this broadens the app's trust to that CA from the start.

## Decide Whether to Pin

Google's [Android security guidance](https://developer.android.com/privacy-and-security/security-ssl#Pinning) states that "certificate pinning ... is not recommended for Android apps." If the server changes to a CA or key that is not in the app's pinset, existing app versions cannot connect until users install an update.

[MAS-L1](https://mas.owasp.org/Profiles/MAS-L1/) requires correct TLS certificate and hostname validation, but does not require pinning. [MAS-L2](https://mas.owasp.org/Profiles/MAS-L2/) uses a stronger threat model in which an attacker may add a CA certificate to the operating system trust store and actively intercept the network connection. The attacker can then present a certificate for the app's server that the operating system accepts. Identity pinning gives the app an independent check that rejects this certificate. It is therefore a best practice for developer-controlled endpoints in MAS-L2.

By default, the operating system trusts many root CAs. Pinning a specific root or intermediate CA narrows the authorities the app accepts and reduces its exposure if another trusted CA is compromised or issues an unauthorized certificate. The app still depends on the security of the pinned CA and its certificate chain.

### Self-Signed Certificates

Some apps reduce the number of third parties they trust because a compromised CA could allow an attacker to obtain an unauthorized certificate for the app's server. When the app team controls both the app and server, pinning a self-signed server certificate allows the app to trust only a developer-controlled server key.

Past incidents show the impact of compromised or misused CA infrastructure. In the [DigiNotar breach](https://security.googleblog.com/2011/08/update-on-attempted-man-in-middle.html), an unauthorized certificate for Google domains was used in active MITM attacks. In the [CNNIC/MCS incident](https://security.googleblog.com/2015/03/maintaining-digital-certificate-security.html), an intermediate CA installed in a MITM proxy generated unauthorized certificates for Google domains.

For apps using the [MAS-R](https://mas.owasp.org/Profiles/MAS-R/) resilience profile, a self-signed certificate may be a natural choice when the app's threat model excludes trust in third-party CAs. MAS-R controls can also make it harder to remove or replace the embedded pin.

Self-signed certificates give the app team full control over issuance and validity periods. The team can issue a replacement immediately without waiting for a CA. In an emergency unrelated to key compromise, it can reuse the same key as a last resort so the existing public-key pin continues to work and the app remains available. Key reuse should not replace planned key rotation. This flexibility is a double-edged sword: the team also assumes full responsibility for protecting private keys, choosing safe validity periods, rotating keys, and recovering from a compromise. Poor certificate management can introduce security weaknesses or cause outages.

## Pin Safely

- Generate a separate backup key and include its public-key pin in the app before rotating the active server key. Test planned rotation and emergency recovery before release.
- Pinning supplements TLS certificate validation. The app must still verify that the certificate is valid for the requested server.
- On Android, pinning is disabled after the pinset expires. Configure an expiration only when this loss of protection is an accepted part of the recovery plan.

Pinning does not protect against an attacker who controls the device and can modify or instrument the app. For implementation details, see @MASTG-KNOW-0015 for Android and @MASTG-KNOW-0072 for iOS.
