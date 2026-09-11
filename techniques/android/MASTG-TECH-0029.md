---
title: Listing Loaded Native Libraries
platform: android
---

This technique describes how to identify and extract the native libraries loaded into memory by a running Android app. Unlike @MASTG-TECH-0157, which identifies bundled native libraries statically from the APK, this approach requires the app to be running on a device.

## Using @MASTG-TOOL-0004

The Linux kernel exposes the memory map of every process through the virtual file `/proc/<pid>/maps`. Each line describes one mapped region and contains: the virtual address range, memory permissions (`r`ead/`w`rite/e`x`ecute/`p`rivate or `s`hared), the offset within the backing file, the device, the inode, and the pathname.

Use adb to read this file for the target process (`adb root` is required):

```bash
adb shell cat /proc/23796/maps | grep "/data/.*\.so"
7619ca3000-7619e68000 r-xp 00000000 fe:27 352366                         /data/data/org.owasp.mastestapp/code_cache/startup_agents/dced2491-agent.so
7619e6b000-7619e79000 r--p 001c8000 fe:27 352366                         /data/data/org.owasp.mastestapp/code_cache/startup_agents/dced2491-agent.so
7619e7c000-7619eb8000 rw-p 001d5000 fe:27 352366                         /data/data/org.owasp.mastestapp/code_cache/startup_agents/dced2491-agent.so
...
```

## Using @MASTG-TOOL-0001

You can retrieve process-related information straight from the Frida CLI by using the `Process.enumerateModules` lists the libraries loaded into the process memory.

```bash
[Android Emulator 5554::MASTestApp ]-> Process.enumerateModules()
[
   {
        "base": "0x766af82000",
        "name": "libcutils.so",
        "path": "/apex/com.android.vndk.v34/lib64/libcutils.so",
        "size": 204800,
        "version": null
    },
    {
        "base": "0x7668523000",
        "name": "libc++.so",
        "path": "/apex/com.android.vndk.v34/lib64/libc++.so",
        "size": 827392,
        "version": null
    },
...
]
```
