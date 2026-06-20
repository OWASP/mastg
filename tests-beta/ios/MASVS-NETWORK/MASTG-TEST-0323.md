---
platform: ios
title: Uses of Low-Level Networking APIs for Cleartext Traffic
id: MASTG-TEST-0323
type: [static, code, manual]
weakness: MASWE-0050
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0073]
---

## Overview

App Transport Security (ATS) only applies to connections made via the [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system) (typically `URLSession`). Lower-level networking APIs bypass ATS entirely, meaning they can establish cleartext HTTP connections regardless of the app's ATS configuration.

The following low-level APIs are not affected by ATS:

- **[`Network` framework](https://developer.apple.com/documentation/network)**: A modern low-level networking API for socket-level communication using TCP and UDP.
- **[`CFNetwork`](https://developer.apple.com/documentation/cfnetwork)**: Core Foundation-based networking APIs including `CFSocketStream`, `CFHTTPStream`, and related functions.
- **BSD Sockets**: Low-level POSIX socket APIs accessed through functions like `socket()`, `connect()`, `send()`, and `recv()`.

Apple [recommends](https://developer.apple.com/documentation/security/preventing_insecure_network_connections) preferring high-level frameworks: "ATS doesn't apply to calls your app makes to lower-level networking interfaces like the `Network` framework or `CFNetwork`. In these cases, you take responsibility for ensuring the security of the connection. You can construct a secure connection this way, but mistakes are both easy to make and costly. It's typically safest to rely on the URL Loading System instead."

For more information on when ATS applies, see @MASTG-KNOW-0071.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of low-level networking API usages and their locations within the app binary.

## Evaluation

The test case fails if the app uses low-level networking APIs to establish cleartext connections.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine whether cleartext connections are established:

- Determine whether TLS is configured for `Network` framework connections, for example by checking whether `.tls` is included in `NWParameters`.
- Determine whether `CFNetwork` or BSD socket connections use any TLS wrapping.
