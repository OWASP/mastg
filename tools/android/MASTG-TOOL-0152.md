---
title: lldb (Android)
platform: android
source: https://lldb.llvm.org/
hosts: [windows, linux, macOS]
---

[lldb](https://lldb.llvm.org/) is the LLVM debugger. On Android, it can be used to debug native code in apps, including JNI libraries, native crashes, memory, registers, system calls, and compiled control flow.

Use lldb to attach to a running process or launch an app under the debugger. Command line workflows typically use the lldb client from Android Studio or the Android NDK (@MASTG-TOOL-0005), together with a matching `lldb-server` binary running on the device.

## Requirements and Usage Notes

- Requires a debuggable app, a suitable test build, or sufficient device privileges such as root.
- Apps may detect debugger attachment, root, ptrace usage, timing changes, or hooking frameworks.
- Blocking a process on spawn until a native debugger is attached usually requires combining lldb with Java or JDWP based launch control.
- Use a device-side `lldb-server` version that matches the host-side lldb client when possible.

The device-side `lldb-server` binary is usually located under a path similar to:

```bash
$LLDB_ROOT/toolchains/llvm/prebuilt/$HOST_ARCH/lib/clang/$CLANG_VERSION/lib/linux/$ANDROID_ARCH/lldb-server
````

This path can vary between Android Studio, NDK, host platform, and LLVM versions. To locate it, search inside the Android SDK or NDK directory:

```bash
find "$ANDROID_HOME" "$ANDROID_NDK_HOME" -name lldb-server 2>/dev/null
```

After locating the matching binary, upload it to the device and mark it executable, for example:

```bash
adb push lldb-server /data/local/tmp/
adb shell chmod +x /data/local/tmp/lldb-server
```

## Android 14 (API level 34) Compatibility Note

Some users report crashes when debugging Android 14 (API level 34) and later processes with certain lldb clients. A reported workaround is to disable the JIT loader plugin and pass `SIGSEGV` and `SIGBUS` to the app while keeping the debugger attached:

```bash
(lldb) settings set plugin.jit-loader.gdb.enable off
(lldb) process handle SIGSEGV -s false -p true -n false
(lldb) process handle SIGBUS -s false -p true -n false
```

For setup and usage instructions, refer to the official Android documentation for [debugging apps](https://developer.android.com/studio/debug), [NDK debugging](https://developer.android.com/ndk/guides/ndk-gdb), and [Android Studio run/debug configurations](https://developer.android.com/studio/run/rundebugconfig). For step-by-step examples of attaching lldb to debuggable and non-debuggable apps, see @MASTG-TECH-0031.
