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

This sample uses the same code as @MASTG-DEMO-0x48, which implements three independent Frida detection routines: a TCP probe of the default `frida-server` port (`127.0.0.1:27042`), a `/proc/self/task/<tid>/comm` walk for Frida worker thread names (`gum-js-loop`, `gmain`, `gdbus`, `pool-frida`), and a `/proc/self/maps` scan for injected Frida artifacts. This demo demonstrates bypassing all three checks with a Frida script that hooks the Java APIs each routine depends on (`Socket.connect`, `File.listFiles`, `BufferedReader.readLine`), combined with reconfiguring `frida-server` to listen on a non-default port under a renamed binary so the default-port probe finds no listener even before the `Socket.connect` hook fires.

!!! note
    This is a series of correlated tests.
    - @MASTG-DEMO-0x48 is a successful test (successful defense/failed attack) against a Frida instrumentation attack.
    - This test is a failed test (failed defense/successful attack) against the defenses of @MASTG-DEMO-0x48 by using a more "complex" attack.

{{ ../MASTG-DEMO-0x48/MastgTest.kt # script.js }}

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Make sure you have @MASTG-TOOL-0031 installed on your machine; push and rename the `frida-server` binary on the device (e.g., to `notfrida`) and start it on a non-default port.
3. Run `run.sh` to spawn the app with the bypass script.
4. Click the **Start** button.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ script.js # run.sh }}

## Observation

The output contains the trace lines emitted by each hook as it intercepts a detection probe.

{{ output.txt }}

## Evaluation

The test case fails because every detection routine has been bypassed at runtime:

- The `Socket.connect` hook blocks the probe to `127.0.0.1:27042`, so the port check finds no `frida-server` listener.
- The `File.listFiles` hook hides Frida worker threads (`gmain`, `pool-frida`, `gdbus`) from the `/proc/self/task` scan.
- The `BufferedReader.readLine` hook drops the Frida `/proc/self/maps` lines.
