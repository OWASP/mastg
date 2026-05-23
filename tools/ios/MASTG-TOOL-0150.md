---
title: xpcspy
platform: ios
source: https://github.com/ab-rizk/xpcspy
hosts: [macOS, linux, windows]
---

**xpcspy** is a Frida-based tool designed for **bidirectional XPC message interception** on iOS and macOS targets. It hooks into the XPC runtime to capture messages as they are sent and received, printing them to the console in a human-readable form. Unlike similar tools like XPoCe, xpcspy intercepts both incoming and outgoing messages and can parse XPC dictionary values that contain binary property list data, including `bplist00`, `bplist16`, and experimental support for `bplist15` and `bplist17`.

!!! note "Jailbreak required"
    xpcspy normally requires a jailbroken device with `frida-server` running when used against iOS system processes and daemons. Injecting Frida Gadget into a single app is not enough to inspect arbitrary system daemons or system-wide XPC traffic. On macOS, SIP must be disabled first.

## Installation

xpcspy is installed via pip and requires `frida-server` to be running on the target device:

```bash
pip3 install xpcspy
````

## Usage

To intercept all XPC messages for a running process by name:

```bash
xpcspy -U -n <ProcessName>
```

To intercept XPC messages for a running process by PID:

```bash
xpcspy -U -p <PID>
```

To spawn a process and intercept its XPC messages:

```bash
xpcspy -U -f <ExecutablePath>
```

To filter messages by service name substring or message direction, use `-t`. The direction prefix `i:` means incoming and `o:` means outgoing:

```bash
xpcspy -U -n <ProcessName> -t 'i:com.apple.*'
xpcspy -U -n <ProcessName> -t 'o:com.apple.apsd'
```

To parse XPC dictionary values that contain binary property list data:

```bash
xpcspy -U -n <ProcessName> -r
```

To print a timestamp before each intercepted message:

```bash
xpcspy -U -n <ProcessName> -d
```

## Real-World Example: Reversing AirTag Commands

See ["[0x0a] Reversing Shorts :: Apple's Cross-Process Communication (XPC)"](https://www.youtube.com/watch?v=eW-pq_aQPfQ) for a real-world example where xpcspy is used to trace how the **Find My** app communicates with various system daemons to trigger an AirTag sound:

1. **Identify Daemon Interaction:** By attaching to `bluetoothd`, you can observe how Bluetooth packets are forwarded internally via XPC to other processes.
2. **Trace Message Flow:** Using xpcspy on the `searchpartyd` daemon reveals messages sent from the Find My app, which are then forwarded to `locationd` (the daemon where AirTag commands, codenamed "Durian", are actually implemented).
3. **Inspect Data:** The tool allows you to read deep into long XPC messages to find specific commands like `DurianManagement` or `playSound`, helping to map out the protocol without intense manual code analysis.

## Real-World Example: Inspecting Location Services Traffic

The 8kSec article ["Advanced Frida Usage Part 4 - Sniffing location data from locationd in iOS"](https://8ksec.io/advanced-frida-usage-part-4-sniffing-location-data-from-locationd-in-ios/) describes using XPC tracing tools, including xpcspy and gxpc, to inspect communication involving the `locationd` daemon.

1. **Attach to a daemon:** Attach to `locationd`, the daemon responsible for location services.
2. **Observe XPC activity:** Inspect XPC function calls and dictionary values exchanged with other processes.
3. **Search parsed payloads:** Search the output for sensitive fields such as `longitude`, `latitude`, and `accuracy`.
4. **Review binary plist data:** The example shows location data inside a `bplist17` payload sent through `xpc_connection_send_notification`.

This type of analysis is useful when reviewing whether sensitive data is exposed through local daemon communication, and for understanding which processes participate in location-related workflows.
