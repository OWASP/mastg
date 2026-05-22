---
platform: android
title: Uses of Frida Detection Techniques (Default Port, Process Enumeration and /proc/maps)
id: MASTG-DEMO-0x48
code: [kotlin]
test: MASTG-TEST-0x48
tools: [MASTG-TOOL-0110]
kind: pass
---

## Sample

The snippet below shows sample code that performs three common Frida detection techniques used by Android apps as anti-instrumentation checks (see @MASTG-KNOW-0030 for more information about Detection of Reverse Engineering Tools):

- Scanning the default `frida-server` TCP port (`127.0.0.1:27042`).
- Enumerating running processes via `/proc/<pid>/cmdline` looking for `frida-server`, `frida-helper`, `frida-agent`, `gum-js-loop`, etc.
- Reading `/proc/self/maps` looking for injected Frida artifacts (`frida-agent.so`, `libfrida`, `frida-gadget`, `gum`, `linjector`).

These checks are well-known and trivially bypassable at runtime.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Run the static analysis rule against the decompiled code.

{{ ../../../../rules/mastg-android-frida-detection.yaml }}

{{ run.sh }}

## Observation

The output shows the semgrep rule matching the three detection routines.

{{ output.txt }}

## Evaluation

The test passes because the app statically implements three independent Frida detection mechanisms — semgrep flags:

- `Socket.connect` with `127.0.0.1:27042` (the default-port probe).
- The `/proc` enumeration that walks `/proc/<pid>/cmdline` and the `frida-server`, `frida-helper`, `frida-agent`, `gum-js-loop`, `gmain` string literals it greps for.
- The `/proc/self/maps` reader paired with the injected-library substrings (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`, `/gum`).

!!! note

   This is a "kind: pass" demo for static presence only: the sample contains the detection mechanisms expected by the test. The dynamic counterpart @MASTG-DEMO-0x49 covers a runtime bypass that defeats all three of these checks.
