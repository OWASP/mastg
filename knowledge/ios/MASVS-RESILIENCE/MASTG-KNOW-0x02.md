---
masvs_category: MASVS-RESILIENCE
platform: ios
title: DeviceCheck
---

[DeviceCheck](https://developer.apple.com/documentation/devicecheck "DeviceCheck documentation") is an Apple framework that lets apps persistently store two bits of data per device on Apple's servers. The stored state survives app reinstallation, device transfers, and factory resets, and can optionally be cleared periodically by the developer.

DeviceCheck is typically used to mitigate fraud by restricting access to sensitive resources — for example, limiting a promotional offer to once per device or flagging suspicious devices. Because the flags are stored server-side by Apple and keyed to the device hardware, they cannot be reset by the user without Apple's involvement.

However, DeviceCheck does not attest the app's identity or verify that the app binary is unmodified. It provides no cryptographic proof that the request came from a genuine, unaltered app running on a real device. For stronger app-identity guarantees, use App Attest (see @MASTG-KNOW-0x03) instead.

Both DeviceCheck and App Attest serve as iOS attestation providers for Firebase App Check (see @MASTG-KNOW-0x01).