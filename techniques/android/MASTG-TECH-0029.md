---
title: Extracting Loaded Native Libraries
platform: android
---

This technique describes how to identify and extract the native libraries loaded into memory by a running Android app. Unlike @MASTG-TECH-0x02, which identifies bundled native libraries statically from the APK, this approach requires the app to be running on a device.

## Using Process Memory Maps

The file `/proc/<pid>/maps` contains the currently mapped memory regions and their access permissions. Using this file, we can get the list of the libraries loaded in the process.

```bash
## Using Process Memory Maps

The Linux kernel exposes the memory map of every process through the virtual file `/proc/<pid>/maps`. Each line describes one mapped region and contains: the virtual address range, memory permissions (`r`ead/`w`rite/e`x`ecute/`p`rivate or `s`hared), the offset within the backing file, the device, the inode, and the pathname.

Use @MASTG-TOOL-0004 to read this file for the target process (root is required on production builds):

12c00000-52c00000 rw-p 00000000 00:04 14917                              /dev/ashmem/dalvik-main space (region space) (deleted)
6f019000-6f2c0000 rw-p 00000000 fd:00 1146914                            /data/dalvik-cache/arm64/system@framework@boot.art
...
7327670000-7329747000 r--p 00000000 fd:00 1884627                        /data/app/com.google.android.gms-4FJbDh-oZv-5bCw39jkIMQ==/oat/arm64/base.odex
..
733494d000-7334cfb000 r-xp 00000000 fd:00 1884542                        /data/app/com.google.android.youtube-Rl_hl9LptFQf3Vf-JJReGw==/lib/arm64/libcronet.80.0.3970.3.so
...
```

## Using Frida

You can retrieve process-related information straight from the Frida CLI by using the `Process` command. Within the `Process` command, the function `enumerateModules` lists the libraries loaded into the process memory.

```bash
[Huawei Nexus 6P::sg.vantagepoint.helloworldjni]-> Process.enumerateModules()
[
    {
        "base": "0x558a442000",
        "name": "app_process64",
        "path": "/system/bin/app_process64",
        "size": 32768
    },
    {
        "base": "0x78bc984000",
        "name": "libandroid_runtime.so",
        "path": "/system/lib64/libandroid_runtime.so",
        "size": 2011136
    },
...

```
