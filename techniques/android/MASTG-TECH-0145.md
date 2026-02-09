---
title: Converting XAPK to APK
platform: android
---

When downloading APKs from alternative app stores like APKPure or APKMirror, you may encounter XAPK files instead of standard APK files. XAPK is a package format that bundles an APK along with additional files such as OBB data files or split APKs.

## Understanding XAPK Format

An XAPK file is essentially a ZIP archive containing:

- The main APK file (or split APKs for app bundles)
- OBB files (additional data files, if any)
- A manifest JSON file describing the contents

## Extracting XAPK Files

The simplest way to work with XAPK files is to extract them as ZIP archives:

```bash
unzip app.xapk -d app_extracted
```

After extraction, you'll find:

- The main APK file (usually named like `com.example.app.apk`)
- Split APK files (if the app uses App Bundles), typically in a folder or with names like `split_config.arm64_v8a.apk`
- OBB files in an `Android/obb` directory structure (if present)
- A `manifest.json` file describing the package contents

## Installing from XAPK

If you need to install the app on a device:

### For Single APK

If the XAPK contains a single APK file:

```bash
unzip app.xapk
adb install com.example.app.apk
```

### For Split APKs (App Bundles)

If the XAPK contains multiple split APKs:

```bash
unzip app.xapk -d app_extracted
adb install-multiple app_extracted/*.apk
```

### With OBB Files

If the XAPK includes OBB files, you need to:

1. Install the APK(s) as described above
2. Push the OBB files to the correct location on the device:

```bash
adb push Android/obb/com.example.app /sdcard/Android/obb/com.example.app
```

## Converting to Standard APK Bundle

For analysis purposes, if you have split APKs, you can work with them individually or use @MASTG-TOOL-0004 to analyze the extracted APKs directly. Each split APK can be decompiled and analyzed separately.

!!! note
    When analyzing apps distributed as XAPK files with split APKs, remember that the app's functionality may be distributed across multiple APK files. You should analyze all split APKs together to get the complete picture of the app's behavior.
