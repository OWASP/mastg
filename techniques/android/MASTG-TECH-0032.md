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

The CPU Profiler can record execution traces and present them as a zoomable hierarchical timeline of method calls, time spent in each method, and parent-child relationships. This makes it possible to recover high-level execution structure even when static analysis yields limited insight.

## strace

When Java-level tracing is insufficient, analysis often moves down the stack toward native code and the operating system. At this level, behavior becomes visible through interactions with the Linux kernel via system calls.

`strace` is a standard Linux utility that is not included with Android by default, but can be built from source via the Android NDK. It monitors the interaction between a process and the kernel, making it a convenient way to observe low-level behavior and bypass certain forms of application-level obfuscation. A major limitation is that `strace` relies on the `ptrace` system call to attach to the target process, which causes it to fail once anti-debugging measures are enabled.

You can try different syscall filters depending on your analysis goals. For example, focus on syscalls that correlate with behavior you care about, like file and path access, network, process injection attempts, anti-debug checks, and dynamic loading. For this, you can use `-e trace=` followed by a comma-separated list of syscall names. For example, to trace file access, networking, and process management syscalls, you could use:

```bash
strace -ff -s 2000 -p `pgrep -f 'org.owasp.mastestapp' | head -1` -e trace=openat,access,fstat,newfstatat,readlinkat,unlinkat,renameat,mkdirat,connect,sendto,recvfrom,ptrace,prctl,mmap,mprotect,execve
```

Where:

- `-ff` follows and traces all threads and any child processes created via fork or clone, writing separate output streams per thread or process.
- `-s 2000` tells strace to capture up to 2000 bytes of data for string arguments, which matters for paths, socket data, and ioctl payloads.
- `-p` specifies the process ID (pid) of the target app to attach to. In this case, we use `pgrep` to find the pid of the app by its package name (`-f` for full match).
- `-e trace=` specifies which syscalls to trace, reducing noise and focusing on relevant behavior.

See the [`strace` man page](https://man7.org/linux/man-pages/man1/strace.1.html) for more options and details.

Example output:

```bash
strace: Process 27524 attached with 19 threads
[pid 27524] recvfrom(82, "\1\0\0\0\364\22\0\0\376DI&...", 2472, MSG_DONTWAIT, NULL, NULL) = 312
[pid 27524] recvfrom(74, 0x7fd71751a0, 21600, MSG_DONTWAIT, NULL, NULL) = -1 EAGAIN (Try again)
[pid 27524] sendto(82, "\2\0\0\0\364\22\0\0\1\0\0\0\0\0\0\0\26\34AI\t\207\0\0", 24, MSG_DONTWAIT|MSG_NOSIGNAL, NULL, 0) = 24
[pid 27524] recvfrom(74, "nysv\0\0\0\0\0{+zj\354\34@]\327\304K\t\20...", 21600, MSG_DONTWAIT, NULL, NULL) = 216
...
[pid 27591] openat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml", O_RDONLY) = 97
...
```

Here you can see how the target app is receiving and sending data over sockets, as well as opening a shared preferences XML file that may contain sensitive data.

If you want to focus on files only, you can use `-e trace=file` which is an abbreviation for `-e trace=open,openat,creat,link,unlink,...` (see more options in the "Filtering" section of the man page):

```bash
strace -ff -s 2000 -p `pgrep -f 'org.owasp.mastestapp' | head -1` -e trace=file
```

Example output:

```bash
[pid 27592] faccessat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml", F_OK) = 0
[pid 27592] faccessat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml.bak", F_OK) = -1 ENOENT (No such file or directory)
[pid 27592] renameat2(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml", AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml.bak", 0) = 0
[pid 27592] openat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml", O_WRONLY|O_CREAT|O_TRUNC, 0666) = 99
[pid 27592] --- SIGSEGV {si_signo=SIGSEGV, si_code=SEGV_MAPERR, si_addr=NULL} ---
[pid 27592] fchmodat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml", 0660) = 0
[pid 27592] newfstatat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml", {st_mode=S_IFREG|0660, st_size=1926, ...}, 0) = 0
[pid 27592] unlinkat(AT_FDCWD, "/data/user/0/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml.bak", 0) = 0
```

Some things you can see here include the app accessing `MasSharedPref_Sensitive_Data.xml` with `faccessat`, renaming it to create a backup with `renameat2`, opening it for writing with `openat`, and finally deleting the backup with `unlinkat`.

**Early Attachment:**

To capture behavior that occurs very early in the app's lifecycle, it's important to attach `strace` as soon as the process starts. The best way to do this is to enable the "Wait for Debugger" option in the Android Developer Options for the target app. This causes the system to pause the app right after launch, allowing you to attach `strace` before any significant code executes. Once attached, you can then resume the app from the device or via adb.

If the "Wait for Debugger" feature in **Settings -> Developer options** is unavailable, a shell script can be used to wait for the process to appear and immediately attach to it. The process must be started separately, either by the system, the launcher, or another trigger.

```bash
while true; do pid=$(pgrep -f 'org.owasp.mastestapp' | head -1); if [[ -n "$pid" ]]; then strace -s 2000 -e "!read" -ff -p "$pid"; break; fi; done
```

This script polls the process table until pgrep finds a matching pid, then immediately attaches `strace` as soon as the process becomes visible. This approximates early attachment, but it is not true process launch tracing. True start of process tracing would require tracing the zygote or using kernel-level tracing like ftrace.

## Ftrace

Ftrace is a tracing facility built directly into the Linux kernel. On rooted devices, it can trace kernel system calls and scheduling events more transparently than `strace`, which depends on `ptrace` and user-space attachment.

The stock Android kernel on Lollipop and Marshmallow includes ftrace support. It can be enabled with the following command.

```bash
echo 1 > /proc/sys/kernel/ftrace_enabled
```

The `/sys/kernel/debug/tracing` directory contains all control and output files related to ftrace. Commonly used files include:

- `available_tracers`: lists the tracers compiled into the kernel.
- `current_tracer`: selects the active tracer.
- `tracing_on`: controls whether the ring buffer is updated.

## KProbes

The KProbes interface provides a more powerful mechanism for kernel-level analysis by allowing probes to be inserted into almost arbitrary kernel code addresses. KProbes work by placing a breakpoint instruction at the target location and transferring control to a user-defined handler when the breakpoint is hit.

This goes beyond passive tracing and into active kernel instrumentation, which can be necessary when user-space tracing is blocked by defensive measures. In addition to function entry and exit tracing, KProbes can be used for more intrusive tasks such as altering kernel behavior or implementing rootkit-like features.

Jprobes and Kretprobes are related probe types that hook function entries and exits.

The stock Android kernel does not support loadable kernel modules, which complicates KProbes deployment. Additionally, strict memory protection prevents patching certain kernel regions. For example, system call table hooking causes a kernel panic on stock Lollipop and Marshmallow because the table is not writable. KProbes can still be used in controlled environments by compiling a custom kernel with relaxed protections.
