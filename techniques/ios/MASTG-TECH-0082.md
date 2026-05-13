---
title: Extracting Bundled Libraries
platform: ios
---

This technique describes how to identify dynamic libraries and framework binaries bundled with an iOS app. The static analysis steps inspect the IPA without running the app. Runtime-based approaches are covered in @MASTG-TECH-0x01 and require executing or instrumenting the app.

[Bundled libraries](https://developer.apple.com/library/archive/technotes/tn2435/_index.html) are included in the app's IPA and are commonly found in `Payload/YourApp.app/Frameworks/`. They may also appear in other bundled executable components, such as app extensions.

This technique does not fully cover statically linked code. Static libraries are usually linked into another Mach-O binary and do not appear as separate dynamic library dependencies. A `.framework` directory alone does not prove that the framework is dynamic, so inspect the contained Mach-O file when this distinction matters.

## Overview

When analyzing an iOS app's libraries, distinguish between the following categories:

- **App-bundled libraries and framework binaries**: executable code shipped inside the IPA. These are commonly located under `Payload/YourApp.app/Frameworks/`, and may include first-party frameworks, third-party frameworks, bundled `.dylib` files, and Swift runtime libraries.
- **Other bundled executable components**: executable code shipped in other app bundle locations, such as app extensions under `Payload/YourApp.app/PlugIns/`, watch content under `Payload/YourApp.app/Watch/`, app clips, or other Mach-O files inside the app bundle.
- **System libraries**: libraries provided by iOS, commonly referenced through paths such as `/System/Library/Frameworks/` or `/usr/lib/`. These are loaded from the operating system and are generally not part of the IPA.
- **Statically linked code**: code from static libraries, static frameworks, or mergeable libraries that has been linked into another Mach-O binary. This code will not appear as a separate dependency in `otool -L` or radare2 `il`.

The approaches below provide complementary information:

- **Inspecting the IPA contents** shows what files the developer shipped. This is the best starting point for identifying bundled frameworks, dylibs, app extensions, and other executable components.
- **Reading Mach-O load commands**, for example with `otool -L` or radare2 `il`, shows the dynamic libraries recorded as dependencies of a specific Mach-O binary. This includes system libraries and bundled libraries, but only for the binary being inspected.

When reviewing load command output, filter out paths that clearly refer to system libraries, such as `/System/Library/` and `/usr/lib/`. Entries using `@rpath`, `@executable_path`, or `@loader_path` should be resolved against the binary's load commands and then cross-checked against the IPA contents. In iOS apps, `@rpath` commonly resolves to the app's `Frameworks` directory, but this should not be assumed without verification.

Some Apple supplied Swift runtime libraries, such as `libswiftCore.dylib`, may be bundled in the app's `Frameworks` directory depending on the deployment target and toolchain. These are physically shipped in the IPA, even though they are not third-party libraries.

## Inspecting the Application Bundle

An IPA is a ZIP archive. Extract it and inspect the app bundle. Bundled dynamic libraries are commonly `.framework` bundles or `.dylib` files under `Payload/YourApp.app/Frameworks/`.

```bash
unzip -o MASTestApp.ipa -d MASTestApp
ls -1 MASTestApp/Payload/MASTestApp.app/Frameworks/
App.framework
Flutter.framework
libswiftCore.dylib
libswiftCoreAudio.dylib
...
```

To avoid missing bundled executable components, also inspect other common locations:

```bash
find MASTestApp/Payload -type d \( -name "*.framework" -o -name "*.appex" -o -name "*.app" \)
find MASTestApp/Payload -type f \( -name "*.dylib" -o -perm -111 \)
```

Use `file` to identify Mach-O files:

```bash
file MASTestApp/Payload/MASTestApp.app/MASTestApp
file MASTestApp/Payload/MASTestApp.app/Frameworks/App.framework/App
file MASTestApp/Payload/MASTestApp.app/Frameworks/libswiftCore.dylib
```

## Using @MASTG-TOOL-0060

Use the `otool -L` command on a Mach-O binary to list the dynamic libraries recorded in its load commands.

```bash
otool -L MASTestApp/Payload/MASTestApp.app/MASTestApp
MASTestApp:
    /System/Library/Frameworks/Foundation.framework/Foundation (compatibility version 300.0.0, current version 2503.1.0)
    /usr/lib/libobjc.A.dylib (compatibility version 1.0.0, current version 228.0.0)
    /usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 1345.120.2)
    /System/Library/Frameworks/CryptoKit.framework/CryptoKit (compatibility version 1.0.0, current version 1.0.0)
    @rpath/App.framework/App (compatibility version 1.0.0, current version 1.0.0)
    @rpath/Flutter.framework/Flutter (compatibility version 1.0.0, current version 1.0.0)
    ...
```

Run `otool -L` on each relevant Mach-O file, not only on the main app executable. The main executable's load commands do not necessarily include dependencies that belong only to bundled frameworks, app extensions, or other Mach-O binaries.

Examples:

```bash
otool -L MASTestApp/Payload/MASTestApp.app/MASTestApp
otool -L MASTestApp/Payload/MASTestApp.app/Frameworks/App.framework/App
otool -L MASTestApp/Payload/MASTestApp.app/Frameworks/Flutter.framework/Flutter
otool -L MASTestApp/Payload/MASTestApp.app/Frameworks/libswiftCore.dylib
```

To inspect runtime search paths used to resolve `@rpath` entries, use:

```bash
otool -l MASTestApp/Payload/MASTestApp.app/MASTestApp | grep -A2 LC_RPATH
```

Entries with absolute paths such as `/System/Library/Frameworks/` or `/usr/lib/` usually refer to system libraries. Entries using `@rpath`, `@executable_path`, or `@loader_path` may refer to bundled libraries, but they should be resolved and cross checked against the IPA contents.

## Using @MASTG-TOOL-0073

In radare2, the `il` command lists linked libraries for the currently opened binary.

```bash
r2 MASTestApp/Payload/MASTestApp.app/MASTestApp
[0x100006e9c]> il
[Linked libraries]
/System/Library/Frameworks/Foundation.framework/Foundation
/usr/lib/libobjc.A.dylib
/usr/lib/libSystem.B.dylib
/System/Library/Frameworks/CryptoKit.framework/CryptoKit
@rpath/App.framework/App
@rpath/Flutter.framework/Flutter
...
```

As with `otool -L`, run radare2 against each Mach-O file you want to analyze. The output reflects the linked libraries for that specific binary.
