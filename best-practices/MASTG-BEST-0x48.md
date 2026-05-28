---
title: Detecting Frida Instrumentation
alias: detecting-frida-instrumentation
id: MASTG-BEST-0x48
platform: android
knowledge: [MASTG-KNOW-0030]
---

Implement multiple independent Frida detection mechanisms to increase the effort required for an attacker to successfully instrument the application. Frida detection should be treated as a layer of defense-in-depth rather than a foolproof solution, as sophisticated attackers can bypass most user-space checks.

## Frida Detection Techniques

### TCP Port Scan
Frida's default `frida-server` listens on TCP port `27042`. Attempting to connect to this port on `127.0.0.1` can detect a running server. However, attackers often change the default port.

### Procfs Enumeration
Scanning `/proc` for artifacts related to Frida is a common technique:
- **Process names**: Enumerate running processes by walking `/proc/<pid>/cmdline` and look for strings like `frida-server`, `frida-helper`, or `frida-agent`.
- **Thread names**: Look for Frida worker threads in `/proc/self/task/<tid>/comm` such as `gum-js-loop`, `gmain`, or `pool-frida`.
- **Memory Maps**: Scan `/proc/self/maps` for injected libraries or artifacts like `frida-agent.so`, `libfrida`, `frida-gadget`, or `linjector`.

### Frida Gadget Detection
On non-rooted devices, Frida is often used by embedding the `frida-gadget` shared library into the APK. This can be detected by:
- **Scanning Memory Maps**: Looking for `libfrida-gadget.so` (or any renamed version of the gadget library) in `/proc/self/maps`.
- **Native Library Enumeration**: Using `System.loadLibrary` hooks or `dladdr` in native code to inspect loaded libraries for Frida-related symbols or names.

### Memory Scanning for Artifacts
Scan the process memory for known Frida strings, such as "LIBFRIDA", which is present in various versions of Frida's libraries. This can be done by iterating through memory mappings and performing a signature-based search.

### Detecting Hooking Trampolines
Frida's `Interceptor` works by inserting trampolines (indirect jump vectors) at the beginning of functions. Detecting these jumps in critical native functions can reveal that they have been hooked.

## Countermeasures and Limitations

Since these checks rely on user-space APIs controlled by the attacker, they can be silently disabled by hooking the underlying Java or system calls to return spoofed clean values.

To improve resilience:
- **Use Native Implementation**: Perform these checks in C/C++ via the NDK to make them harder to hook than Java/Kotlin APIs.
- **Direct System Calls**: Use `syscall()` to bypass libc wrappers that are easily hooked.
- **Combine with Integrity Checks**: Use code integrity checks to detect if the detection logic itself has been tampered with.
- **Silent Detection**: Instead of crashing immediately upon detection, change the app's behavior subtly or report the detection to a backend server to avoid tipping off the attacker.
