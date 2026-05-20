---
masvs_category: MASVS-RESILIENCE
platform: generic
title: Firebase App Check
---

[Firebase App Check](https://firebase.google.com/docs/app-check) is a Google service that protects backend resources (Firebase services and custom backends) from abuse by verifying that requests originate from legitimate, unmodified instances of the app running on genuine devices. It delegates the actual attestation to platform-specific providers and forwards a short-lived token to the backend, which validates it against Google's App Check servers.

## Attestation Providers

App Check is provider-agnostic. The app registers a provider at startup. Both platforms offer a debug provider that can be used for development and CI environments.

### Android

- **Play Integrity API** (recommended) - issues a verdict covering device integrity, app authenticity, and installation source (see @MASTG-KNOW-0035).

### iOS

- **App Attest** (recommended, iOS 14+) - Apple's hardware-backed attestation service using the Secure Enclave (see @MASTG-KNOW-0123).
- **DeviceCheck** (fallback, iOS 11+) - an older Apple service providing per-device flags persisted by Apple, without cryptographic app-identity guarantees (see @MASTG-KNOW-0122).

## Limitations

- **Server-side enforcement required**: App Check tokens are only meaningful if the backend validates them. Skipping server-side checks renders the integration ineffective.
- **Depends on underlying provider availability**: All limitations of the underlying provider apply (see @MASTG-KNOW-0035 for Play Integrity limitations). App Attest requires iOS 14+ and is unavailable on simulators without the debug provider.
- **Token replay**: App Check tokens have a short TTL but are bearer tokens. They should be transmitted over TLS, not logged or caches locally.