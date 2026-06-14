---
masvs_category: MASVS-RESILIENCE
platform: ios
title: App Attest
available_since: 14
---

[App Attest](https://developer.apple.com/documentation/devicecheck/validating-apps-that-connect-to-your-server "Validating apps that connect to your server") is part of the DeviceCheck framework and provides hardware-backed app attestation on iOS 14+. It allows a server to cryptographically verify that a request originates from a legitimate, unmodified instance of the app running on a genuine Apple device.

## How It Works

The process uses a device-bound key pair generated in the Secure Enclave:

1. The app generates an attestation key via `DCAppAttestService.generateKey()`.
2. It requests an attestation certificate from Apple's servers, binding the key to the app's App ID and the device's hardware identifier.
3. For each sensitive request, the app generates an assertion (a signature over a hash of the request payload) using `DCAppAttestService.generateAssertion()`.
4. The server verifies the assertion against the attested public key and checks the App ID, receipt freshness, and risk metric returned by Apple.

For more detailed information, refer to the [WWDC 2021](https://developer.apple.com/videos/play/wwdc2021/10244 "WWDC 2021").

## Limitations

- **iOS 14+ only**: App Attest is unavailable on older OS versions. DeviceCheck (see @MASTG-KNOW-0x02) can serve as a fallback.
- **Unavailable on simulators**: The Secure Enclave is not present in the iOS simulator. Use the debug provider (e.g., via Firebase App Check) during development.
- **Not jailbreak detection**: App Attest attests the app binary and device hardware but does not detect a compromised operating system. A jailbroken device may still pass attestation.
- **Requires network**: Attestation and assertion verification require a live connection to Apple's servers.

Both App Attest and DeviceCheck are used as iOS attestation providers for Firebase App Check (see @MASTG-KNOW-0x01).
