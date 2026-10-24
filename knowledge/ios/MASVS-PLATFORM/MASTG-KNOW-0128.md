---
masvs_category: MASVS-PLATFORM
platform: ios
title: Bonjour
---

[Bonjour](https://developer.apple.com/bonjour/) is Apple's zero-configuration networking implementation based on multicast DNS (mDNS) and DNS Service Discovery (DNS-SD). It lets apps advertise and discover services on a local network without requiring manual configuration or a central directory server.

## How App Store Apps Use Bonjour

App Store apps can use Bonjour to discover and connect to other devices and services on the same local network, including other iOS devices, Macs, smart home accessories, and third-party hardware. Common use cases include:

- Discovering and connecting to a companion Mac app or a local server.
- Peer-to-peer communication between iOS devices on the same local network.
- Integration with network-accessible devices, such as printers, media players, or IoT hardware.

## APIs

iOS provides two main sets of APIs for Bonjour:

- **[`NetService`](https://developer.apple.com/documentation/foundation/netservice) / [`NetServiceBrowser`](https://developer.apple.com/documentation/foundation/netservicebrowser)**: The older Foundation API for advertising and browsing services. Apple DTS notes that the old Bonjour APIs are deprecated and recommends the Network framework for new development.
- **[`NWBrowser`](https://developer.apple.com/documentation/network/nwbrowser) / [`NWListener`](https://developer.apple.com/documentation/network/nwlistener)**: The modern Network framework APIs. `NWListener` advertises a service; `NWBrowser` discovers services by type.

A service type follows the format `_<name>._tcp` or `_<name>._udp`, for example `_myapp._tcp`.

## Local Network Access Permission

Since iOS 14, apps that access the local network, including via Bonjour, must include [`NSLocalNetworkUsageDescription`](https://developer.apple.com/documentation/bundleresources/information_property_list/nslocalnetworkusagedescription) in their `Info.plist` to explain why they need access. The system presents a permission prompt to the user.

Apps that use Bonjour should also declare the service types they browse in `Info.plist` under the [`NSBonjourServices`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsbonjourservices) key:

```xml
<key>NSBonjourServices</key>
<array>
    <string>_myapp._tcp</string>
</array>
````

Without the required local network privacy declarations, Bonjour browsing or local network access may fail or be blocked by the system.

## Scope and Constraints

- Bonjour is intended for local network service discovery. It does not provide internet-scale discovery or traversal by itself.
- Bonjour discovery does not encrypt or authenticate the subsequent connection. Apps are responsible for applying transport security, such as TLS, and authenticating the peer.
- The local network permission grants local network access to the app, not to a single Bonjour service type. Apps should still declare the Bonjour service types they browse and minimize local network use.
