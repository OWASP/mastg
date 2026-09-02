---
platform: android
title: Static Detection of Frida using Semgrep
id: MASTG-DEMO-0x48
code: [kotlin]
test: MASTG-TEST-0x48
tools: [MASTG-TOOL-0110]
kind: pass
---

## Sample

The snippet below shows sample code that performs three common Frida detection techniques used by Android apps as anti-instrumentation checks: a TCP scan of the default `frida-server` port (`127.0.0.1:27042`), a `/proc/self/task/<tid>/comm` walk for Frida worker thread names (`gum-js-loop`, `gmain`, `gdbus`, `pool-frida`, `frida`), and a `/proc/self/maps` read for injected Frida artifacts (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`, `/gum`).

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
- Line 152 declares the thread-name needle list (`gum-js-loop`, `gmain`, `gdbus`, `pool-frida`, `frida`) consumed by the `/proc/self/task` enumeration.
- Lines 154 and 165 enumerate `/proc/self/task` and read each `/proc/self/task/<tid>/comm` to match the process's own thread names against those needles.
- Line 213 declares the injected-library needle list (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`, `/gum`) used to scan foreign mappings.
- Line 215 opens `/proc/self/maps` from Java to detect a Frida agent or any other foreign library mapped into the process.
