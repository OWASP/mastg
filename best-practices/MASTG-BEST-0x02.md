---
title: Mitigate the Risk of API Keys Hardcoded in the App Package
alias: mitigate-hardcoded-api-keys
id: MASTG-BEST-0x02
platform: generic
knowledge: [MASTG-KNOW-0015, MASTG-KNOW-0035, MASTG-KNOW-0043, MASTG-KNOW-0057, MASTG-KNOW-0072, MASTG-KNOW-0118, MASTG-KNOW-0120, MASTG-KNOW-0x01, MASTG-KNOW-0x02, MASTG-KNOW-0x03, MASTG-KNOW-0x04]  # TODO: resolve attestation ID references (MASTG-KNOW-0x01, 0x02, and 0x03 come from the iOS attestation PR)
---

Treat every API key that ships with the app, or that the app receives at runtime, as public. Keys in the app package can be extracted with static analysis, and keys delivered at runtime can be read from memory or traffic on a device the attacker controls. Whether an exposed key is a problem depends on what the service lets it do (see @MASTG-KNOW-0x04). A key that only identifies the project for quota and billing is acceptable in the app. A key that unlocks privileged functionality, such as billed APIs or writes to the app owner's data, is not.

Keep privileged keys on your backend, and limit every key that must ship with the app to what any anonymous client may do.

## Keep Privileged Keys on Your Backend

Have the app call your own backend (an API proxy or gateway), which authenticates the user and then calls the third-party API with a server-stored key. The key never reaches the app, and you keep control over auditing, rate limiting, and rotation.

Enforce authorization for privileged operations on the remote endpoint, not in the app. Verify the backend side using [OWASP ASVS V8 Authorization](https://github.com/OWASP/ASVS/blob/master/5.0/en/0x17-V8-Authorization.md) and the [OWASP API Security Top 10](https://owasp.org/API-Security/editions/2023/en/0x11-t10/).

## Restrict Keys That Must Ship with the App

Some services issue keys that are meant to be used from client apps. Limit them on the service side:

- **Restrict the APIs a key can call.** On Google Cloud, add [API restrictions](https://cloud.google.com/docs/authentication/api-keys#adding-api-restrictions) so the key only works with the APIs the app uses. A key without them works with every API that accepts Google Cloud keys, including APIs enabled on the project later.
- **Use a separate key per service.** Firebase [recommends](https://firebase.google.com/docs/projects/api-keys#apply-restrictions) using its auto-created keys only for Firebase APIs and a separate, restricted key for anything else, such as Maps. Never allow a generative AI API on a key that ships in an app.
- **Do not rely on app restrictions for authentication.** Google Cloud can [restrict a key](https://cloud.google.com/docs/authentication/api-keys#adding-application-restrictions) to an Android package name and signing-certificate fingerprint, or to an iOS bundle ID. The app proves these by sending the `X-Android-Package`, `X-Android-Cert`, or `X-Ios-Bundle-Identifier` headers, which any client can set. Google notes that "bypassing this restriction is straightforward". These restrictions stop casual reuse of the key, not deliberate abuse.
- **Protect Firebase data with Security Rules.** Firebase API keys [only identify the project](https://firebase.google.com/docs/projects/api-keys) and do not control access to data. [Firebase Security Rules](https://firebase.google.com/docs/rules) do. Require an authenticated user for every read and write that is not meant to be public, and validate the data that clients write.
- **Cap and monitor usage.** Requests made with a key are billed to your project, whoever sends them, and Google [holds you financially responsible](https://developers.google.com/maps/api-security-best-practices) for charges caused by abuse of unrestricted keys. Set quota limits on each API so that abuse is capped, and budget alerts so that it's noticed. A budget alert alone [doesn't cap spending](https://cloud.google.com/billing/docs/how-to/budgets).
- **Check the keys that SDKs bring with them.** Some third-party SDKs ship a key of their own, with the same value in every app that bundles them, or ask you to add a shared key to the app's manifest or `Info.plist`. Treat these like your own keys, and prefer vendors that can issue a credential per installation.

## Issue Per-Installation Credentials Instead of a Shared Key

Fetching a shared key from your backend or a remote configuration service at runtime keeps it out of the package, but not away from attackers. Any installation can be made to receive it, and once read from one device it works for everyone. With a shared key, every installation is only as safe as the most vulnerable device that holds it.

Do for installations what you already do for users. A user gets a token after logging in, the token is short-lived, and it can be revoked without affecting anyone else. Have the backend issue each installation its own short-lived credential in the same way:

- Issue the credential once the installation has attested itself, as described in @MASTG-BEST-0x01 (Android) and @MASTG-BEST-0x03 (iOS), and use the result as a risk signal for what the credential may do. At the time of writing, the IETF draft [OAuth 2.0 Attestation-Based Client Authentication](https://datatracker.ietf.org/doc/draft-ietf-oauth-attestation-based-client-auth/) (version 12, October 2026) is standardizing this for OAuth clients. <!-- TODO: resolve attestation ID references (MASTG-BEST-0x01 comes from the Android attestation PR, MASTG-BEST-0x03 from the iOS one) -->
- Issue dynamically generated, short-lived tokens instead of static keys, as required by [ASVS 7.2.2](https://github.com/OWASP/ASVS/blob/master/5.0/en/0x16-V7-Session-Management.md). Rotate refresh tokens on every use.
- Bind tokens to a key in the platform keystore where possible, for example with [Demonstrating Proof of Possession (DPoP)](https://datatracker.ietf.org/doc/html/rfc9449) or by signing each request with the attested key, so that a copied token cannot be used from another client. See [ASVS 10.4.5](https://github.com/OWASP/ASVS/blob/master/5.0/en/0x19-V10-OAuth-and-OIDC.md).
- Store the credential encrypted at rest, for example with the Android KeyStore (@MASTG-KNOW-0043) or the iOS Keychain (@MASTG-KNOW-0057), and decrypt it only to use it.
- Scope each token to the operations the app needs at that point.
- Enforce per-installation quotas and rate limits, and revoke credentials that are abused. Because the backend issues them, credentials can be rotated or revoked without an app release.

An attacker's own installation can still obtain a valid credential. The value of per-installation credentials is containment: abuse can be traced to one installation and cut off without affecting the others.

## Use Attestation as a Risk Signal, Not as a Gate for Secrets

Platform attestation, such as Play Integrity (@MASTG-KNOW-0035), Key Attestation (@MASTG-KNOW-0120), App Attest (@MASTG-KNOW-0x03), or Firebase App Check (@MASTG-KNOW-0x01), helps the backend tell whether a request likely comes from a genuine app on a genuine device. Use it to adjust trust, rate limits, and step-up requirements, as described in @MASTG-BEST-0x01 (Android) and @MASTG-BEST-0x03 (iOS). <!-- TODO: resolve attestation ID references (MASTG-KNOW-0x01, MASTG-KNOW-0x03, and MASTG-BEST-0x03 come from the iOS attestation PR, MASTG-BEST-0x01 from the Android one) -->

Do not make attestation the only control that keeps a key or a privileged operation away from attackers. A rooted or jailbroken device running the genuine app can pass it, and leaked attestation keys have been used to forge verdicts (@MASTG-KNOW-0120). RASP checks (@MASTG-KNOW-0118, @MASTG-BEST-0029) raise the cost further but can also be bypassed.

## Protect Credentials in Transit

Send keys and tokens only over a pinned channel, so that a network-level attacker cannot observe or replay them:

- **Android**: configure pinning in the Network Security Configuration (@MASTG-KNOW-0015).
- **iOS**: implement server trust evaluation (@MASTG-KNOW-0072).

## Plan for Key Rotation

- A key shipped in the app only changes when users update the app. Be prepared to revoke and reissue it, knowing that older installations keep the old value until they update.
- Use distinct keys per platform and per environment (development, staging, production) so that a leak in one does not affect the others.
- Rotate keys immediately on any suspected compromise.
