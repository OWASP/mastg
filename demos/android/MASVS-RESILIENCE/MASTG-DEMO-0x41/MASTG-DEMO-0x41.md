---
platform: android
title: Native Anti-Debugging Checks with TracerPid and ptrace
id: MASTG-DEMO-0x41
code: [kotlin, cpp]
test: MASTG-TEST-0x01
tools: [MASTG-TOOL-0110, MASTG-TOOL-0073, MASTG-TOOL-0x42]
kind: pass
---

## Sample

This sample demonstrates native anti-debugging checks that inspect `TracerPid` in `/proc/self/status` and use a `ptrace` self-check flow.

{{ MastgTest.kt # MastgTest_reversed.java }}
{{ tracerpid_check.cpp # tracerpid_ptrace_self.cpp }}
{{ tracerpid_inline_syscall.cpp # CMakeLists.txt }}

## Steps

Run the native anti-debugging Semgrep rule against the decompiled Java file, then run the r2 script against the compiled native library (`libtracerpidcheck.so`).

{{ ../../../../rules/mastg-android-native-debugger-checks.yml }}
{{ native_debugger_checks.r2 }}
{{ run.sh }}

## Observation

The output should contain detections for native anti-debugging indicators such as testing for `TracerPid` in `/proc/self/status`, and testing for usage of `ptrace(PTRACE_SEIZE)`. This demo does not use the alternative `PTRACE_ATTACH`, but it should also be taken into account when looking for indicators of anti-debugging checks.

{{ output.txt }}

## Evaluation

The test passes because the app implements native debugger detection checks, for example `getTracerPidFromProcStatus()` at `MastgTest_reversed.java` line 56, native `TracerPid` JNI checks invoked at lines 79 and 91, and `/proc/self/status` parsing at lines 126 and 133. The output also reports native `ptrace` anti-debugging logic in the compiled library: `mov w0, 0x4206` loads the `PTRACE_SEIZE` request before calling `sym.imp.ptrace`.
