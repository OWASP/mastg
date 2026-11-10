---
masvs_category: MASVS-RESILIENCE
platform: ios
title: App Attest
available_since: 14
best-practices: [MASTG-BEST-0x03]
---

[App Attest](https://developer.apple.com/documentation/devicecheck/validating-apps-that-connect-to-your-server "Validating apps that connect to your server") is part of the DeviceCheck framework and provides hardware-backed app attestation on iOS 14+. It allows a server to cryptographically verify that a request originates from a legitimate, unmodified instance of the app running on a genuine Apple device.

## How It Works

The process uses a device-bound key pair generated in the Secure Enclave:

1. The app generates an attestation key via [`DCAppAttestService.generateKey()`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generatekey%28completionhandler%3A%29).
2. It requests an attestation certificate from Apple's servers via [`DCAppAttestService.attestKey()`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/attestkey%28_%3Aclientdatahash%3Acompletionhandler%3A%29), passing a hash of a one-time challenge issued by the server (the `clientDataHash`). Apple certifies that the key belongs to the app's App ID (its team identifier and bundle identifier) and was generated on genuine Apple hardware, where the private key cannot leave the Secure Enclave.
3. For each sensitive request, the app generates an assertion (a signature over a hash of the request payload and a new server-issued challenge) using [`DCAppAttestService.generateAssertion()`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generateassertion%28_%3Aclientdatahash%3Acompletionhandler%3A%29).
4. The server confirms that each challenge is one it issued and has not seen before, verifies the assertion against the attested public key, and checks the App ID, receipt freshness, and risk metric returned by Apple.

For more detailed information, refer to the [WWDC 2021 session "Mitigate fraud with App Attest and DeviceCheck"](https://developer.apple.com/videos/play/wwdc2021/10244 "WWDC 2021").

## Limitations

- **iOS 14+ only**: App Attest is unavailable on older OS versions, where only DeviceCheck (see @MASTG-KNOW-0x02) is available.
- **Unavailable on simulators**: The Secure Enclave is not present in the iOS simulator, so `DCAppAttestService.shared.isSupported` returns `false`. Debug providers, such as the one offered by Firebase App Check, cover this case during development.
- **Not jailbreak detection**: App Attest attests the app's identity and the device hardware but does not detect a compromised operating system. A jailbroken device may still pass attestation.
- **Attestation requires network**: `attestKey` contacts Apple's servers, so a key cannot be attested offline. Assertions are generated on the device and verified by the app's server with the stored public key, without contacting Apple.

Both App Attest and DeviceCheck are used as iOS attestation providers for Firebase App Check (see @MASTG-KNOW-0x01).
