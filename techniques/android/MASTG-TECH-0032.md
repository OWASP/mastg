---
title: Execution Tracing
platform: android
---

Besides being useful for debugging, the jdb command line tool offers basic execution tracing functionality. To trace an app right from the start, you can pause the app with the Android "Wait for Debugger" feature or a `kill -STOP` command and attach jdb to set a deferred method breakpoint on any initialization method. Once the breakpoint is reached, activate method tracing with the `trace go methods` command and resume execution. jdb will dump all method entries and exits from that point onwards.

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

Android Studio provides a built-in profiler that is the modern replacement for the deprecated DDMS (Dalvik Debug Monitor Server) and Android Device Monitor. The [Android Studio Profiler](https://developer.android.com/studio/profile/android-profiler) includes the CPU Profiler, which is essential for analyzing method execution and is particularly useful for analyzing obfuscated bytecode.

The CPU Profiler provides several ways to record execution traces, offering a zoomable hierarchical timeline of all method calls, time spent in each method, and the relationships between methods (parents and children).

## Recording with Android Studio Profiler

To record an execution trace in Android Studio:

1. Open **View** -> **Tool Windows** -> **Profiler** (or click the **Profiler** tab at the bottom).
2. Select your app's process from the device and process dropdown.
3. In the CPU timeline, click anywhere to open the CPU profiler.
4. Click **Record** and choose a recording configuration:
   - **Sampled (Java/Kotlin Methods)**: Lower overhead, good for most cases
   - **Instrumented (Java/Kotlin Methods)**: More accurate but higher overhead
   - **System Trace**: Records system-level information
5. Interact with your app to capture the behavior you want to analyze.
6. Click **Stop** to end the recording.

The trace view will display in the profiler window, showing method calls in a flame chart, call chart, or top-down/bottom-up tree view. You can zoom, scroll, and inspect individual method calls to understand execution flow and timing.

## Tracing System Calls

Moving down a level in the OS hierarchy, you arrive at privileged functions that require the powers of the Linux kernel. These functions are available to normal processes via the system call interface. Instrumenting and intercepting calls into the kernel is an effective method for getting a rough idea of what a user process is doing, and often the most efficient way to deactivate low-level tampering defenses.

Strace is a standard Linux utility that is not included with Android by default, but can be easily built from source via the Android NDK. It monitors the interaction between processes and the kernel, being a very convenient way to monitor system calls. However, there's a downside: as strace depends on the `ptrace` system call to attach to the target process, once anti-debugging measures become active, it will stop working.

If the "Wait for debugger" feature in **Settings > Developer options** is unavailable, you can use a shell script to launch the process and immediately attach strace (not an elegant solution, but it works):

```bash
while true; do pid=$(pgrep 'target_process' | head -1); if [[ -n "$pid" ]]; then strace -s 2000 - e "!read" -ff -p "$pid"; break; fi; done
```

## Ftrace

Ftrace is a tracing utility built directly into the Linux kernel. On a rooted device, ftrace can trace kernel system calls more transparently than strace can (strace relies on the ptrace system call to attach to the target process).

Conveniently, the stock Android kernel on both Lollipop and Marshmallow includes ftrace functionality. The feature can be enabled with the following command:

```bash
echo 1 > /proc/sys/kernel/ftrace_enabled
```

The `/sys/kernel/debug/tracing` directory holds all control and output files related to ftrace. The following files are found in this directory:

- available_tracers: This file lists the available tracers compiled into the kernel.
- current_tracer: This file sets or displays the current tracer.
- tracing_on: Echo "1" into this file to allow/start update of the ring buffer. Echoing "0" will prevent further writes into the ring buffer.

## KProbes

The KProbes interface provides an even more powerful way to instrument the kernel: it allows you to insert probes into (almost) arbitrary code addresses within kernel memory. KProbes inserts a breakpoint instruction at the specified address. Once the breakpoint is reached, control passes to the KProbes system, which then executes the user-defined handler function(s) and the original instruction. Besides being great for function tracing, KProbes can implement rootkit-like functionality, such as file hiding.

Jprobes and Kretprobes are other KProbes-based probe types that allow hooking of function entries and exits.

The stock Android kernel comes without loadable module support, which is a problem because Kprobes are usually deployed as kernel modules. The strict memory protection that the Android kernel is compiled with is another issue because it prevents the patching of some parts of Kernel memory. Elfmaster's system call hooking method causes a Kernel panic on stock Lollipop and Marshmallow because the sys_call_table is non-writable. You can, however, use KProbes in a sandbox by compiling your own, more lenient Kernel (more on this later).
