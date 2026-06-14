---
masvs_category: MASVS-NETWORK
platform: ios
title: iOS Network APIs
---

On iOS, you can create network connections through multiple API layers. These layers differ in abstraction level, supported protocols, and how much of the connection lifecycle they manage. See ["TN3151: Choosing the right networking API"](https://developer.apple.com/documentation/Technotes/tn3151-choosing-the-right-networking-api) for advice on selecting the appropriate API for your use case.

## URL Loading System (High-Level)

The [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system) is the highest-level networking stack in Foundation. It is the primary API surface for HTTP and HTTPS.

Common APIs in this layer include:

- [`URLSession`](https://developer.apple.com/documentation/foundation/urlsession)
- [`URLRequest`](https://developer.apple.com/documentation/foundation/urlrequest)
- [`URLAuthenticationChallenge`](https://developer.apple.com/documentation/foundation/urlauthenticationchallenge)

`URLSession` supports a broad set of HTTP features (for example redirects, caching, cookies, and proxies) and integrates with the system trust store and server trust evaluation (@MASTG-KNOW-0072). See this example of use for ["Fetching website data into memory"](https://developer.apple.com/documentation/foundation/fetching-website-data-into-memory) from the official Apple documentation.

`URLSessionConfiguration` exposes two TLS version properties for each session:

- [`tlsMinimumSupportedProtocolVersion`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsminimumsupportedprotocolversion): sets the minimum TLS version the session accepts. Accepts `tls_protocol_version_t` values such as `tls_protocol_version_TLSv10`, `tls_protocol_version_TLSv11`, `tls_protocol_version_TLSv12`, and `tls_protocol_version_TLSv13`.
- [`tlsMaximumSupportedProtocolVersion`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsmaximumsupportedprotocolversion): sets the maximum TLS version the session uses.

The predecessors [`tlsMinimumSupportedProtocol`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsminimumsupportedprotocol) and [`tlsMaximumSupportedProtocol`](https://developer.apple.com/documentation/foundation/urlsessionconfiguration/tlsmaximumsupportedprotocol) are deprecated. They accept `SSLProtocol` enum values, for example `kSSLProtocol3` and `kTLSProtocol1`.

These properties are **independent from ATS**: ATS enforces its own TLS minimum requirements in addition to whatever value is set here. Setting `tlsMinimumSupportedProtocolVersion` to TLS 1.0 in code does not bypass ATS; if no matching `Info.plist` exception is present, ATS still blocks the connection. When an ATS exception is present, a lower minimum TLS version configured in code can weaken the effective policy for that connection. See @MASTG-KNOW-0071 for ATS behavior.

## Network Framework (Low-Level)

The [Network](https://developer.apple.com/documentation/network) framework, introduced in iOS 12, provides direct access to protocols like TLS, TCP and UDP. It exposes connection and listener primitives and lets you configure transports and protocol stacks explicitly. See the WWDC2018 session ["Introducing Network.framework: A modern alternative to Sockets"](https://developer.apple.com/videos/play/wwdc2018/715/) for an overview.

Common APIs in this layer include:

- [`NWConnection`](https://developer.apple.com/documentation/network/nwconnection)
- [`NWListener`](https://developer.apple.com/documentation/network/nwlistener)
- [`NWParameters`](https://developer.apple.com/documentation/network/nwparameters)
- [`NWProtocolTLS`](https://developer.apple.com/documentation/network/nwprotocoltls)

See this example of use for ["Implementing netcat with Network Framework"](https://developer.apple.com/documentation/network/implementing-netcat-with-network-framework) from the official Apple documentation.

TLS settings for a Network.framework connection are configured through `NWProtocolTLS.Options`, which exposes the underlying `sec_protocol_options_t`. These are the key functions for TLS version configuration:

- [`sec_protocol_options_set_min_tls_protocol_version(_:_:)`](https://developer.apple.com/documentation/security/sec_protocol_options_set_min_tls_protocol_version(_:_:)): sets the minimum TLS version. Accepts `tls_protocol_version_t` values such as `tls_protocol_version_TLSv10`, `tls_protocol_version_TLSv12`, etc.
- [`sec_protocol_options_set_max_tls_protocol_version(_:_:)`](https://developer.apple.com/documentation/security/sec_protocol_options_set_max_tls_protocol_version(_:_:)): sets the maximum TLS version.

ATS is the safety net for `URLSession`. `Network.framework` bypasses that safety net because ATS doesn't apply to it. Any weak TLS configuration in code (e.g. setting the minimum to TLS 1.0) takes effect directly.

In iOS 26 the framework adds new Swift structured concurrency oriented APIs that provide a modern, declarative interface for connections, listeners, and endpoint discovery. See the WWDC2025 session ["Use structured concurrency with Network framework"](https://developer.apple.com/videos/play/wwdc2025/250/) for details. These APIs include:

- [`NetworkConnection`](https://developer.apple.com/documentation/Network/NetworkConnection)
- [`NetworkListener`](https://developer.apple.com/documentation/Network/NetworkListener)
- [`NetworkBrowser`](https://developer.apple.com/documentation/Network/NetworkBrowser)

Apple advises developers to keep using the URL Loading System for HTTP- and URL-based networking. Use the Network framework only when you need lower-level access to protocols or custom transports not supported by the URL Loading System.

## CFNetwork and Core Foundation Streams (Low-Level)

[CFNetwork](https://developer.apple.com/documentation/cfnetwork) is a lower-level networking framework that provides Core Foundation APIs and stream abstractions.

Common APIs in this layer include:

- [`CFReadStream`](https://developer.apple.com/documentation/corefoundation/cfreadstream) and [`CFWriteStream`](https://developer.apple.com/documentation/corefoundation/cfwritestream)
- [`CFStream`](https://developer.apple.com/documentation/corefoundation/cfstream)
- [`CFSocket`](https://developer.apple.com/documentation/corefoundation/cfsocket)
- HTTP-related primitives such as `CFHTTPMessage` and `CFReadStreamCreateForHTTPRequest`

You may encounter CFNetwork-based networking both in apps and in third-party libraries, especially where a C or Core Foundation interface is preferred.

Apple's archived documentation is available at ["Using Sockets and Socket Streams"](https://developer.apple.com/library/archive/documentation/NetworkingInternet/Conceptual/NetworkingTopics/Articles/UsingSocketsandSocketStreams.html).

## BSD Sockets (Lowest-Level)

At the lowest level, iOS supports POSIX/BSD socket APIs for TCP and UDP.

Common functions include:

- [`socket`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/socket.2.html), [`connect`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/connect.2.html), [`bind`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/bind.2.html), [`listen`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/listen.2.html), [`accept`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/accept.2.html)
- [`send`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/send.2.html), [`recv`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/recv.2.html), [`sendto`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/sendto.2.html), [`recvfrom`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/recvfrom.2.html)

## Relationship to App Transport Security (ATS)

[App Transport Security (ATS)](https://developer.apple.com/documentation/bundleresources/information_property_list/nsapptransportsecurity) is enforced for connections made through the URL Loading System. Lower-level APIs (such as Network framework, CFNetwork/Core Foundation streams, and BSD sockets) are outside the scope of ATS enforcement.

For details about ATS behavior and configuration, see @MASTG-KNOW-0071.
