---
platform: ios
title: Missing Certificate Pinning in Network Traffic
code: [swift]
id: MASTG-DEMO-0x04
test: MASTG-TEST-0x04
kind: fail
status: draft
---

## Sample

The sample below shows an app that makes HTTPS connections via `URLSession` with no certificate pinning configured. It doesn't implement `NSPinnedDomains` in `Info.plist` and doesn't use a custom `URLSessionDelegate` for server trust evaluation. As a result, any certificate issued by a CA trusted by the device (including a proxy CA certificate) is accepted:

{{ MastgTest.swift }}

## Steps

1. Set up an interception proxy on the testing device (@MASTG-TECH-0063).
2. Install and run the app on the device.
3. Trigger network communication (for example, by interacting with the app's UI).
4. Observe whether the proxy successfully intercepts HTTPS traffic from the app.

## Observation

The output should contain a list of HTTPS domains for which the interception was successful.

## Evaluation

The test fails because the proxy successfully intercepts HTTPS traffic to `api.example.com`. This confirms that the app doesn't enforce certificate pinning: it accepts a certificate issued by the proxy's CA, which is trusted by the device but not the app's intended server.
