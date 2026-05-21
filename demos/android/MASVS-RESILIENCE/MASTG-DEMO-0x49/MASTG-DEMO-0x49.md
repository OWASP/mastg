---
platform: android
title: Bypassing Frida Detection via API Hooking and Server Reconfiguration
id: MASTG-DEMO-0x49
code: [kotlin]
test: MASTG-TEST-0x48
tools: [MASTG-TOOL-0031]
kind: fail
---

## Sample

This sample uses the same code as @MASTG-DEMO-0x48, which implements three Frida detection mechanisms: a default-port (`127.0.0.1:27042`) scan, a `/proc/<pid>/cmdline` walk for `frida-server` / `frida-helper` / `frida-agent` / `gum-js-loop` / `gmain`, and a `/proc/self/maps` read for injected Frida libraries. This demo defeats all three by combining **server reconfiguration** — `frida-server` is renamed to `notfrida` on the device and bound to a non-default port — with **return-value tampering** — a Frida script hooks `Socket.connect`, `File.listFiles`, `BufferedReader.readLine`, and `FileReader` so each detection routine sees the values it would see on a clean device.

See @MASTG-KNOW-0030 and @MASTG-KNOW-0032 for more context on bypassing runtime detection mechanisms.

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0x48 is a successful test (successful defense) that defend against Frida-detection mechanisms 
    - This test is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0x48 by using a runtime API-hooking bypass.

{{ ../MASTG-DEMO-0x48/MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Push and rename `frida-server` on the device, then start it on a non-default port and forward it to the host:

    ```sh
    adb push frida-server /data/local/tmp/notfrida
    adb shell "chmod +x /data/local/tmp/notfrida && /data/local/tmp/notfrida -l 0.0.0.0:12345 &"
    adb forward tcp:12345 tcp:12345
    ```

3. Run `run.sh` to spawn the app with the bypass script attached.
4. Click the **Start** button in the app and observe the in-app result and the Frida console output.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ script.js # run.sh }}

## Observation

The frida console shows each hook firing — `Socket.connect` to `127.0.0.1:27042` is blocked, `File.listFiles("/proc")` returns a list with every Frida-named pid removed, and `BufferedReader.readLine` returns a sanitized view of `/proc/self/maps` — while the app reports **PASS** for all three Frida-detection checks.

{{ output.txt }}

## Evaluation

The test fails because every detection routine has been neutralized at runtime:

- **Default-port scan.** The `java.net.Socket.connect` hook intercepts connect attempts to `127.0.0.1:27042` and raises `ConnectException`, so `checkFridaDefaultPort()` reports the port closed. Server reconfiguration also moves `frida-server` to port `12345`, so the hardcoded probe would miss it even without the hook.
- **Process enumeration.** The `File.listFiles` hook filters `/proc` listings to drop any pid whose `cmdline` contains a Frida-related substring, and the `frida-server` binary has been renamed to `notfrida`, so `checkFridaProcesses()` finds nothing.
- **`/proc/self/maps` parsing.** The `BufferedReader.readLine` hook transparently drops any line that mentions `frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, or `linjector`, so `checkFridaLibraries()` returns an empty list even though the agent is mapped into the process.

The script also installs a top-level exception handler so any anti-tamper crash raised by the detection code is swallowed. This demonstrates why these three Frida-detection mechanisms must never be relied on as a single line of defence and must be combined with integrity-protected native checks and remote attestation.
