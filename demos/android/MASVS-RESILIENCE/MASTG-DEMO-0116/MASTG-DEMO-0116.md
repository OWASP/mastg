---
platform: android
title: Native Anti-Debugging Checks with TracerPid and ptrace
id: MASTG-DEMO-0116
code: [kotlin, cpp]
test: MASTG-TEST-0352
kind: pass
---

## Sample

This sample demonstrates native anti-debugging checks that inspect `TracerPid` in `/proc/self/status` and use a `ptrace` self-check flow.

{{ MastgTest.kt # MastgTest_reversed.java }}
{{ tracerpid_check.cpp # tracerpid_ptrace_self.cpp }}
{{ tracerpid_inline_syscall.cpp # CMakeLists.txt }}

## Steps

Let's run @MASTG-TOOL-0110 against the decompiled code and @MASTG-TOOL-0073 against the compiled native library:

{{ ../../../../rules/mastg-android-native-debugger-checks.yml }}
{{ native_debugger_checks.r2 }}
{{ run.sh }}

## Observation

The output contains detections for native anti-debugging indicators, including `TracerPid` checks in `/proc/self/status` and `ptrace(PTRACE_SEIZE)` usage.

{{ output.txt }}

## Evaluation

The test passes because the app implements native debugger detection checks, for example `getTracerPidFromProcStatus()` at `MastgTest_reversed.java` line 56, native `TracerPid` JNI checks invoked at lines 79 and 91, and `/proc/self/status` parsing at lines 126 and 133. The output also reports native `ptrace` anti-debugging logic in the compiled library: `mov w0, 0x4206` loads the `PTRACE_SEIZE` request before calling `sym.imp.ptrace`.

Note that this demo uses `ptrace(PTRACE_SEIZE)` for its self-check, but you should also check for `ptrace(PTRACE_ATTACH)`, which is an alternative approach used in real-world apps.
