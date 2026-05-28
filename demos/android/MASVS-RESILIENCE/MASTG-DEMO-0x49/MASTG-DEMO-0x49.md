---
platform: android
title: Bypassing Frida Detection via API Hooking and Frida-Server Reconfiguration
id: MASTG-DEMO-0x49
code: [kotlin]
test: MASTG-TEST-0x48
tools: [MASTG-TOOL-0031]
kind: fail
---

## Sample

This sample uses the same code as @MASTG-DEMO-0x48, which implements three independent Frida detection routines: a TCP probe of the default `frida-server` port (`127.0.0.1:27042`), a `/proc/<pid>/cmdline` walk for Frida process-name needles, and a `/proc/self/maps` scan for injected Frida artifacts. This demo demonstrates bypassing all three checks with a Frida script that hooks the Java APIs each routine depends on (`Socket.connect`, `File.listFiles`, `BufferedReader.readLine`) and reconfigures `frida-server` to listen on a non-default port under a renamed binary, so the port probe fails, the `/proc` enumeration sees no Frida-named pids, and `/proc/self/maps` reports no Frida mappings.

!!! note
    This is a series of correlated tests.
    - @MASTG-DEMO-0x48 is a successful test (successful defense/failed attack) against a Frida instrumentation attack.
    - This test is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0x48 by using a more "complex" attack.

{{ ../MASTG-DEMO-0x48/MastgTest.kt # script.js }}

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Make sure you have @MASTG-TOOL-0031 installed on your machine; push and rename the `frida-server` binary on the device (e.g., to `notfrida`) and start it on a non-default port.
3. Run `run.sh` to spawn the app with the bypass script.
4. Click the **Start** button.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ script.js # run.sh }}

## Observation

The output contains the trace lines emitted by each hook as it intercepts a detection probe

{{ output.txt }}

## Evaluation

The test case fails because every detection routine has been bypassed at runtime:

- The `Socket.connect` hook raises `ConnectException` for `127.0.0.1:27042`, and `frida-server` was moved to a non-default port so no fallback scan succeeds.
- The `File.listFiles` hook filters `/proc` to drop Frida-named pids, and the `frida-server` binary was renamed (e.g., to `notfrida`) so a name-based scan would also miss it.
- The `BufferedReader.readLine` hook drops `/proc/self/maps` lines mentioning Frida artifacts (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`), including the four `/memfd:frida-agent-64.so` entries shown in the output.
