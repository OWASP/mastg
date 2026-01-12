---
platform: ios
title: Uses of Network Framework Bypassing ATS
code: [swift]
id: MASTG-DEMO-0085
test: MASTG-TEST-0323
kind: fail
---

### Sample

The code sample code uses the `Network` framework to establish a connection to `httpbin.org` on port `80`. The demo doesn't send any data over the connection, but for the purposes of this demo, assume that it does.

{{ MastgTest.swift }}

Note that we do not modify ATS so such a connection should be blocked. However, ATS does not apply to the `Network` framework so the connection will succeed.

### Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for low-level networking APIs in the binary. In this case, we'll focus on `NWEndpoint.Port.integerLiteral`, which is used to specify the port number when creating a network endpoint, but you could also look for `NWConnection` and `NWParameters`.

{{ low_level_network.r2 }}

{{ run.sh }}

### Observation

The output contains references to the `NWEndpoint.Port.integerLiteral` function, including the locations where it is used in the binary and the value passed to it:

{{ output.asm }}

### Evaluation

The test fails because the app uses `NWEndpoint.Port.integerLiteral` to specify port `80` for a network connection by loading the value `0x50` (which is `80` in decimal) into register `w0` before calling the function.
