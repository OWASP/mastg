---
platform: android
title: Uses of Xposed/LSPosed Detection Techniques with Semgrep
id: MASTG-DEMO-0x48
code: [kotlin]
test: MASTG-TEST-0x48
tools: [MASTG-TOOL-0110]
kind: pass
---

## Sample

The snippet below shows sample code that performs three common Frida detection techniques used by Android apps as anti-instrumentation checks: a TCP scan of the default `frida-server` port (`127.0.0.1:27042`), a `/proc/<pid>/cmdline` walk for `frida-server`, `frida-helper`, `frida-agent`, `gum-js-loop`, `gmain`, and a `/proc/self/maps` read for injected Frida artifacts (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`, `/gum`).

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-frida-detection.yml }}

{{ run.sh }}

## Observation

The output contains the locations of all Frida detection checks in the code.

{{ output.txt }}

## Evaluation

The test case passes because the app statically implements three independent Frida detection mechanisms. Review each of the reported instances:

- Line 130 opens a TCP socket to `127.0.0.1:27042` — the default `frida-server` port-scan probe.
- Line 153 declares the process-name needle list (`frida-server`, `frida-helper`, `frida-agent`, `gum-js-loop`, `gmain`) consumed by the `/proc` enumeration.
- Lines 155 and 169 enumerate `/proc` and read each `/proc/<pid>/cmdline` to match running processes against those needles.
- Line 219 declares the injected-library needle list (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`, `/gum`) used to scan foreign mappings.
- Line 221 opens `/proc/self/maps` from Java to detect a Frida agent or any other foreign library mapped into the process.
