---
platform: ios
title: Uses of BSD Sockets Bypassing ATS
code: [swift]
id: MASTG-DEMO-0086
test: MASTG-TEST-0323
kind: fail
---

## Sample

The code sample code uses BSD sockets directly to establish a connection to `httpbin.org` on port `80`. The demo doesn't send any data over the connection, but for the purposes of this demo, assume that it does.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for BSD socket APIs in the binary.

{{ bsd_sockets.r2 }}

{{ run.sh }}

## Observation

The output contains references to BSD socket APIs found in the binary:

{{ output.asm }}

## Evaluation

The test fails because the app uses BSD sockets directly, including `socket`, `connect`, `send`, `recv`, and `getaddrinfo`. The binary imports these symbols and calls them to create a cleartext network connection that bypasses ATS. The `getaddrinfo` call resolves the hostname `httpbin.org` and is supplied with a service value representing port `80`.

The signature of the `getaddrinfo` function from the [Darwin libc man pages](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/getaddrinfo.3.html) is as follows:

```c
int getaddrinfo(
    const char *restrict nodename,
    const char *restrict servname,
    const struct addrinfo *restrict hints,
    struct addrinfo **restrict res
);
```

Where `nodename` is the host (e.g., `"example.com"`) and `servname` is the service name or port string (e.g., `"http"` or `"80"`).

At `0x100004188` the code loads `w8` with `0x50` and stores it as a halfword at `var_30h`, then sets `x20` to `sp + 0x30` and later calls `utf8CString.ContiguousArray.setter`. This sequence uses the numeric value `0x50`, which is `80` in decimal, and converts it into a NUL terminated UTF8 string buffer.

At `0x1000041c4` the code sets `x1` to `x24 + 0x20`, which points to the previous string buffer, and passes it into `getaddrinfo` as the `servname` parameter. The `nodename` parameter in `x0` points to the literal `httpbin.org`. The `hints` and `res` pointers are passed via stack addresses `sp + 0x50` and `sp + 0x48`.
