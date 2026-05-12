---
title: Extracting Bundled Native Libraries
platform: android
---

This technique describes how to identify native libraries (`.so` files) that are packaged inside an Android app's APK using static analysis (without running the app). Native libraries are stored in the `lib/` directory of the APK, organized by CPU architecture (ABI).

## Using `unzip`

An APK is a ZIP archive. You can extract it with standard tools and list the native libraries in the `lib/` directory.

```bash
unzip -o YourApp.apk "lib/*" -d YourApp
find YourApp/lib -name "*.so"
YourApp/lib/arm64-v8a/libnative-lib.so
YourApp/lib/armeabi-v7a/libnative-lib.so
...
```

## Using @MASTG-TOOL-0011

@MASTG-TOOL-0011 unpacks the APK and preserves the directory structure, making it easy to inspect the `lib/` folder.

```bash
apktool d YourApp.apk -o YourApp
ls -1 YourApp/lib/arm64-v8a/
libnative-lib.so
libsqlcipher.so
...
```
