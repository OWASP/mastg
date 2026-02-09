---
title: Working with XAPK Files
platform: android
---

When downloading apps from alternative stores such as APKPure or APKMirror, you may receive XAPK files instead of a single APK. XAPK is not an Android standard format. It is simply a ZIP archive used by third-party stores to bundle one or more APKs together with optional additional data.

## What an XAPK Contains

An XAPK file is a regular ZIP archive that typically includes:

- A base APK
- Optional split APKs generated from an Android App Bundle
- Optional OBB data files
- A manifest.json file describing the package contents

## Extracting an XAPK

Because XAPK is just a ZIP file, it can be extracted using standard tools.

```bash
unzip app.xapk -d app_extracted
```

After extraction you may see a single APK or multiple APK files. For example:

```sh
ls -1 app_extracted
base.apk
config.ar.apk
config.arm64_v8a.apk
...
config.xxxhdpi.apk
icon.png
manifest.json
```

## Installing Apps from an XAPK

### Single APK Case

If the extracted directory contains only one APK, it can be installed normally.

```bash
adb install app_extracted/*.apk
```

### Split APK Case

If multiple APKs are present, the app was built as an Android App Bundle. There is no reliable or supported way to convert these splits into one universal APK. The correct approach is to install the base APK together with the splits that match the target device.

```bash
adb install-multiple -r app_extracted/*.apk
```

### OBB Data

If the XAPK contains OBB files, install the APKs first, then push the OBB directory to the device.

```bash
adb push app_extracted/Android/obb/<package.name> /sdcard/Android/obb/
```

## Reverse Engineering

For reverse engineering and static analysis, you can open the base APK and all relevant split APKs together using @MASTG-TOOL-0018 like this:

```bash
jadx app_extracted/*.apk
```

See @MASTG-TECH-0017 for more details on decompiling Android apps.
