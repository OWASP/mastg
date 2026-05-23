---
title: lldb (Android)
platform: android
source: https://lldb.llvm.org/
hosts: [windows, linux, macOS]
---

## Overview

lldb is the LLVM debugger. On Android, you can use it to debug native code in apps and attach to processes for native-layer analysis.

## Capabilities and Use Cases

- Debug native libraries (JNI) and system calls.
- Attach to running processes to inspect memory and registers.
- Set breakpoints and step through native code paths.

## Installation

Use the lldb version bundled with Android Studio or the Android NDK (@MASTG-TOOL-0005). Refer to the official Android Studio and NDK debugging documentation for setup and host-specific steps.

The lldb server binary for the device is located under `$LLDB_ROOT/toolchains/llvm/prebuilt/$HOST_ARCH/lib/clang/$CLANG_VERSION/lib/linux/$ANDROID_ARCH/lldb-server`. You can upload it to `/data/local/tmp` and mark it executable.

## Usage

Use lldb to attach to a running process or launch the app under the debugger. Refer to the official Android debugging guides for more information on the required device-side components and commands.

### Debugging Example: Non-Debuggable App

Below are example steps to attach lldb to a running, non-debuggable app. Since we are targeting an application built without `android:debuggable="true"`, we will need root access to successfully attach a debugger to its process:

1. Spawn a root ADB shell using commands `adb shell` and `su`.
2. Run lldb with `lldb-server p --server --listen 0.0.0.0:1234`. This will start the lldb server, listening for connections from all addresses on port `1234`. Using any other accessible port is also correct.
3. Use ADB to forward connections to the `1234` port by running `adb forward tcp:1234 tcp:1234`.
4. From the host, connect to the lldb server by starting lldb and running the following commands inside, where `$TARGET_PID` is the pid of the process to debug:

```bash
(lldb) platform select remote-android
(lldb) platform connect connect://localhost:1234
(lldb) process attach -p $TARGET_PID
```

### Debugging Example: Debuggable App

If the target app is debuggable, you can also run `lldb-server` in the app context with `run-as` and debug without root.

Set the target package name first:

1. Copy `lldb-server` into the app data directory: `adb shell run-as "$PACKAGE_NAME" cp /data/local/tmp/lldb-server "/data/data/$PACKAGE_NAME/"`
2. Make it executable: `adb shell run-as "$PACKAGE_NAME" chmod 700 "/data/data/$PACKAGE_NAME/lldb-server"`
3. Start `lldb-server` from the app context: `adb shell run-as "$PACKAGE_NAME" "/data/user/0/$PACKAGE_NAME/lldb-server" platform --server --listen "*:1234"`
4. Forward the port from device to host: `adb forward tcp:1234 tcp:1234`
5. Spawn the target application.
6. From the host, connect and attach from lldb:

```bash
(lldb) platform select remote-android
(lldb) platform connect connect://localhost:1234
(lldb) process attach -p $TARGET_PID
```

### Android 14+ note for LLDB clients

As discussed [here](https://community.hex-rays.com/t/android-server-is-bad-since-android-14/474), some users report crashes when debugging Android 14+ processes with certain lldb clients. A workaround is to disable the JIT loader plugin and pass SIGSEGV/SIGBUS to the app while keeping the debugger attached. You can do this in LLDB before attaching:

```bash
(lldb) settings set plugin.jit-loader.gdb.enable off
(lldb) process handle SIGSEGV -s false -p true -n false
(lldb) process handle SIGBUS -s false -p true -n false
```

## Caveats and Limitations

- Requires having either a debuggable application or a rooted device.
- Apps can detect debugging or root and change behavior.
- Blocking the process on spawn until a debugger is attached is not possible without also debugging through JDWP.

## References

- [lldb](https://lldb.llvm.org/)
- [Android Debugging Docs](https://developer.android.com/studio/debug)
