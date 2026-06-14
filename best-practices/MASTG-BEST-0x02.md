---
title: Mitigate the Risk of API Keys Hardcoded in the App Package
alias: mitigate-hardcoded-api-keys
id: MASTG-BEST-0x02
platform: generic
knowledge: [MASTG-KNOW-0015, MASTG-KNOW-0035, MASTG-KNOW-0072, MASTG-KNOW-0118, MASTG-KNOW-0x02, MASTG-KNOW-0x03, MASTG-KNOW-0x04]
---

API keys embedded in the app package - whether in source code, resource files, or build artifacts - can be extracted through static analysis or binary inspection, even without a rooted device or special tooling. The ideal solution is to move the key server-side entirely so it never ships with the app binary. Often, that is not possible, so the following measures reduce the window of exploitation.

In all cases, never expose sensitive data such as API keys to clients whose integrity has not been verified.

## Deliver API Keys Over-The-Air After Integrity Verification

Instead of embedding the key in the binary, fetch it at runtime from your backend only after the device and app have been attested:

1. At app startup, perform app and device attestation (see @MASTG-BEST-0043).
2. If attestation passes, the backend issues an API key (or a short-lived derivative token) over a secure, pinned channel.
3. The app holds the key in memory for the session. If persistence is required, store it in platform-provided secure storage (Keychain on iOS, Android Keystore) rather than plain files.

This approach keeps the key out of the binary entirely, allows rotation without a new app release, and ensures only verified app instances on genuine devices ever receive it. It combines the protections described in the sections below into a single cohesive architecture.

### Prefer a Server-Side Proxy for Third-Party API Calls

Where the architecture allows it, do not ship the API key in the app at all. Instead:

- Have the mobile app call your own backend (sometimes known as API proxy or API Gateway), which authenticates the request, then calls the third-party API using a server-stored key.
- This eliminates the key from the app package entirely and gives you full control over auditing and rotation.

### Enforce App and Device Integrity Verification Before Use

Require the client to pass app and device integrity verification before any API key or scoped token is issued or accepted. Do not treat attestation as an optional layer on top of a hardcoded key - make it a precondition enforced on the server:

- **Android**: Play Integrity API (@MASTG-KNOW-0035) or Firebase App Check (@MASTG-KNOW-0x02).
- **iOS**: App Attest (@MASTG-KNOW-0x04) or DeviceCheck (@MASTG-KNOW-0x03) via Firebase App Check (@MASTG-KNOW-0x02).

See @MASTG-BEST-0043 for server-side enforcement requirements.

### Enforce Network Integrity with Certificate Pinning

A network-level attacker who intercepts traffic can observe or replay API keys even if they were never in the binary. Pin the server certificate so that the key is only ever transmitted over a channel the app explicitly trusts:

- **Android**: configure pinning via the Network Security Configuration (@MASTG-KNOW-0015).
- **iOS**: implement server trust evaluation (@MASTG-KNOW-0072).

Security controls such as Certificate Pinning can only be trusted if the app's integrity has been verified and the device's controls can be trusted.

### Apply RASP Controls Before and Between Attestations

Attestation is a point-in-time check - it verifies the environment at session start but does not detect changes that occur during execution. RASP (see @MASTG-KNOW-0118) fills this gap by running continuous in-process checks throughout the app's lifetime:

- **Before attestation**: run environment checks (debugger detection, emulator detection, hooking framework detection) before initiating the attestation flow. Abort early if the environment is hostile - do not expose network calls or key requests to an already-compromised process.
- **Between attestations**: monitor for runtime changes that could indicate an active attack - hook injection, memory tampering, or the appearance of reverse engineering tools after a previously clean attestation. Respond by revoking the locally held key, clearing it from memory, and requiring re-attestation before resuming sensitive operations.

See @MASTG-BEST-0029 for RASP signal implementation guidance.

### Limit Credential Lifetime

Credentials - whether static API keys or issued tokens - should be treated as perishable. The shorter their effective lifetime, the smaller the window of exploitation if they are extracted or intercepted.

**If you control the API and issue tokens to the app**, do not issue long-lived static keys. Issue short-lived tokens instead:

- Use short-lived JWTs with an `exp` claim of minutes to a few hours, depending on the sensitivity of the operation. A stolen token becomes useless once it expires.
- Issue a refresh token alongside the access token and invalidate it on each use (refresh token rotation). This limits the damage window if a refresh token is intercepted and makes reuse detectable.
- Bind tokens to the attested client by including attestation claims in the JWT payload (e.g., device integrity verdict, app version, platform). The server can then reject tokens presented from a context that no longer passes integrity checks.
- Scope tokens narrowly to the minimum set of operations the app needs at that point in the session.
- Support revocation via a server-side denylist for high-risk scenarios where waiting for natural expiry is unacceptable.

**If you rely on third-party API keys that cannot be moved server-side**, treat them as perishable static secrets:

- Rotate keys on a defined schedule and immediately upon any suspected compromise.
- Use distinct keys per platform and per environment (development, staging, production) so that a leak in one context does not affect others.
- Automate rotation where the API provider supports it, and ensure the app can receive updated keys without a forced upgrade (e.g., via a remote configuration service backed by attestation).
