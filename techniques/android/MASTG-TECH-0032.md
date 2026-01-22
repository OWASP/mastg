---
title: Execution Tracing
platform: android
---

Performing execution tracing during Android reverse engineering allows you to observe control flow and runtime behavior across the entire stack, from managed Java code down to the Linux kernel. This is especially useful when dealing with obfuscation, dynamic loading, and anti-analysis defenses that limit the effectiveness of static inspection. The techniques described here are intended for behavioral analysis and inspection, not for application development or performance tuning.

## jdb

Besides being useful for debugging, the jdb command line tool offers basic execution tracing functionality that can be leveraged during reverse engineering. To trace an app right from the start, you can pause the app with the Android "Wait for Debugger" feature or a `kill -STOP` command and attach jdb to set a deferred method breakpoint on any initialization method. Once the breakpoint is reached, activate method tracing with the `trace go methods` command and resume execution. jdb will dump all method entries and exits from that point onwards.

```bash
$ adb forward tcp:7777 jdwp:7288
$ { echo "suspend"; cat; } | jdb -attach localhost:7777
Set uncaught java.lang.Throwable
Set deferred uncaught java.lang.Throwable
Initializing jdb ...
> All threads suspended.
> stop in com.acme.bob.mobile.android.core.BobMobileApplication.<clinit>()
Deferring breakpoint com.acme.bob.mobile.android.core.BobMobileApplication.<clinit>().
It will be set after the class is loaded.
> resume
All threads resumed.M
Set deferred breakpoint com.acme.bob.mobile.android.core.BobMobileApplication.<clinit>()

Breakpoint hit: "thread=main", com.acme.bob.mobile.android.core.BobMobileApplication.<clinit>(), line=44 bci=0
main[1] trace go methods
main[1] resume
Method entered: All threads resumed.
```

## Android Studio Profiler

Android Studio provides a built-in profiler that is the modern replacement for the deprecated DDMS and Android Device Monitor. The [Android Studio Profiler](https://developer.android.com/studio/profile) includes the CPU Profiler, which can be useful during reverse engineering when dealing with heavily obfuscated applications and unclear call graphs.

The CPU Profiler can record execution traces and present them as a zoomable hierarchical timeline of method calls, time spent in each method, and parent child relationships. This makes it possible to recover high level execution structure even when static analysis yields limited insight.

## strace

When Java level tracing is insufficient, analysis often moves down the stack toward native code and the operating system. At this level, behavior becomes visible through interactions with the Linux kernel via system calls.

`strace` is a standard Linux utility that is not included with Android by default, but can be built from source via the Android NDK. It monitors the interaction between a process and the kernel, making it a convenient way to observe low level behavior and bypass certain forms of application level obfuscation. A major limitation is that `strace` relies on the `ptrace` system call to attach to the target process, which causes it to fail once anti-debugging measures are enabled.

If the "Wait for Debugger" feature in **Settings > Developer options** is unavailable, a shell script can be used to launch the process and immediately attach `strace`.

```bash
while true; do pid=$(pgrep 'target_process' | head -1); if [[ -n "$pid" ]]; then strace -s 2000 - e "!read" -ff -p "$pid"; break; fi; done
```

## Ftrace

Ftrace is a tracing facility built directly into the Linux kernel. On rooted devices, it can trace kernel system calls and scheduling events more transparently than strace, which depends on ptrace and user space attachment.

The stock Android kernel on Lollipop and Marshmallow includes ftrace support. It can be enabled with the following command.

```bash
echo 1 > /proc/sys/kernel/ftrace_enabled
```

The `/sys/kernel/debug/tracing` directory contains all control and output files related to ftrace. Commonly used files include:

- `available_tracers`: lists the tracers compiled into the kernel.
- `current_tracer`: selects the active tracer.
- `tracing_on`: controls whether the ring buffer is updated.

## KProbes

The KProbes interface provides a more powerful mechanism for kernel level analysis by allowing probes to be inserted into almost arbitrary kernel code addresses. KProbes work by placing a breakpoint instruction at the target location and transferring control to a user defined handler when the breakpoint is hit.

This goes beyond passive tracing and into active kernel instrumentation, which can be necessary when user space tracing is blocked by defensive measures. In addition to function entry and exit tracing, KProbes can be used for more intrusive tasks such as altering kernel behavior or implementing rootkit like features.

Jprobes and Kretprobes are related probe types that hook function entries and exits.

The stock Android kernel does not support loadable kernel modules, which complicates KProbes deployment. Additionally, strict memory protection prevents patching certain kernel regions. For example, system call table hooking causes a kernel panic on stock Lollipop and Marshmallow because the table is not writable. KProbes can still be used in controlled environments by compiling a custom kernel with relaxed protections.
