---
title: Extracting Bundled Libraries
platform: ios
---

This technique describes how to identify the dynamic libraries bundled with an iOS app using static analysis (without running the app). Bundled libraries are included in the app's IPA and are typically found in the `Frameworks` directory (`YourApp.app/Frameworks`).

Note that this technique doesn't cover static libraries, which are linked directly into the app's main binary and don't appear as separate files.

## Overview

When analyzing an iOS app's libraries it is important to distinguish between two categories:

- **App-bundled libraries**: included in the app's IPA, typically under `Payload/YourApp.app/Frameworks/`. They are either first-party (custom code) or third-party dependencies that the developer explicitly shipped with the app. These are the primary target for security assessments.
- **System libraries**: part of the iOS SDK, located in directories such as `/System/Library/Frameworks/` or `/usr/lib/`. They are provided by the OS and shared across all apps; they are generally not of interest unless there is a specific reason to examine them.

The two approaches below — inspecting the `Frameworks` directory and reading the binary's load commands — provide complementary information:

- **`Frameworks` directory**: shows what is physically bundled inside the IPA. This is the definitive list of libraries that the developer shipped.
- **Load commands (`otool -L`, `radare2 il`)**: show all libraries that the binary is linked against, including system libraries not present in the IPA. This may include entries that are provided at runtime by the OS and are not bundled.

When reviewing results from the load commands, filter out paths starting with `/System/Library/`, `/usr/lib/`, or `/usr/lib/swift/` to focus on app-bundled libraries. Note that some system libraries may also be bundled with the app to ensure compatibility with a specific iOS SDK version, so cross-referencing both approaches is useful.

## Inspecting the Application Bundle

An IPA is a ZIP archive. Extract it and navigate to the `Frameworks` directory to list all bundled dynamic libraries. They are typically `.framework` bundles or `.dylib` files.

```bash
unzip -o MASTestApp.ipa -d MASTestApp
ls -1 Frameworks/
App.framework
Flutter.framework
libswiftCore.dylib
libswiftCoreAudio.dylib
...
```

## Using @MASTG-TOOL-0060

Use the `otool -L` command on the app binary to list all dynamic libraries recorded in its Mach-O load commands.

```bash
otool -L MASTestApp
MASTestApp:
    /System/Library/Frameworks/Foundation.framework/Foundation (compatibility version 300.0.0, current version 2503.1.0)
    /usr/lib/libobjc.A.dylib (compatibility version 1.0.0, current version 228.0.0)
    /usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 1345.120.2)
    /System/Library/Frameworks/CryptoKit.framework/CryptoKit (compatibility version 1.0.0, current version 1.0.0)
    @rpath/App.framework/App (compatibility version 1.0.0, current version 1.0.0)
    @rpath/Flutter.framework/Flutter (compatibility version 1.0.0, current version 1.0.0)
    ...
```

Entries with `@rpath` refer to libraries resolved via the runtime search path, which typically points to the `Frameworks` directory — these are the app-bundled ones.

## Using @MASTG-TOOL-0073

In radare2, the `il` command lists the same load command entries as `otool -L`.

```bash
r2 MASTestApp
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

## @MASTG-TOOL-0074

You can use Objection's command `list_frameworks` to list all the app's bundles that represent Frameworks.

```bash
...itudehacks.DVIAswiftv2.develop on (iPhone: 13.2.3) [usb] # ios bundles list_frameworks
Executable      Bundle                                     Version    Path
--------------  -----------------------------------------  ---------  -------------------------------------------
Bolts           org.cocoapods.Bolts                        1.9.0      ...8/DVIA-v2.app/Frameworks/Bolts.framework
RealmSwift      org.cocoapods.RealmSwift                   4.1.1      ...A-v2.app/Frameworks/RealmSwift.framework
                                                                      ...ystem/Library/Frameworks/IOKit.framework
...
```

The `list_bundles` command lists all the application's bundles **that are not related to frameworks**. The output includes the executable name, bundle ID, library version, and path to the library.

```bash
...itudehacks.DVIAswiftv2.develop on (iPhone: 13.2.3) [usb] # ios bundles list_bundles
Executable    Bundle                                       Version  Path
------------  -----------------------------------------  ---------  -------------------------------------------
DVIA-v2       com.highaltitudehacks.DVIAswiftv2.develop          2  ...-1F0C-4DB1-8C39-04ACBFFEE7C8/DVIA-v2.app
CoreGlyphs    com.apple.CoreGlyphs                               1  ...m/Library/CoreServices/CoreGlyphs.bundle
```

## @MASTG-TOOL-0039

The `Process.enumerateModules()` function in Frida's REPL enumerates modules loaded into memory at runtime.

```bash
[iPhone::com.iOweApp]-> Process.enumerateModules()
[
    {
        "base": "0x10008c000",
        "name": "iOweApp",
        "path": "/private/var/containers/Bundle/Application/F390A491-3524-40EA-B3F8-6C1FA105A23A/iOweApp.app/iOweApp",
        "size": 49152
    },
    {
        "base": "0x1a1c82000",
        "name": "Foundation",
        "path": "/System/Library/Frameworks/Foundation.framework/Foundation",
        "size": 2859008
    },
    {
        "base": "0x1a16f4000",
        "name": "libobjc.A.dylib",
        "path": "/usr/lib/libobjc.A.dylib",
        "size": 200704
    },

    ...
```
