---
title: Getting Loaded Libraries Dynamically
platform: ios
---

This technique describes how to enumerate the dynamic libraries loaded into memory by a running iOS app. Unlike @MASTG-TECH-0082, which identifies bundled libraries statically, this approach requires the app to be running on a device or simulator.

## Using @MASTG-TOOL-0074

Objection's `ios bundles list_frameworks` command lists all framework bundles loaded by the running app.

```bash
...itudehacks.DVIAswiftv2.develop on (iPhone: 13.2.3) [usb] # ios bundles list_frameworks
Executable      Bundle                                     Version    Path
--------------  -----------------------------------------  ---------  -------------------------------------------
Bolts           org.cocoapods.Bolts                        1.9.0      ...8/DVIA-v2.app/Frameworks/Bolts.framework
RealmSwift      org.cocoapods.RealmSwift                   4.1.1      ...A-v2.app/Frameworks/RealmSwift.framework
                                                                      ...ystem/Library/Frameworks/IOKit.framework
...
```

The `ios bundles list_bundles` command lists all other application bundles that are not related to frameworks. The output includes the executable name, bundle ID, library version, and path.

```bash
...itudehacks.DVIAswiftv2.develop on (iPhone: 13.2.3) [usb] # ios bundles list_bundles
Executable    Bundle                                       Version  Path
------------  -----------------------------------------  ---------  -------------------------------------------
DVIA-v2       com.highaltitudehacks.DVIAswiftv2.develop          2  ...-1F0C-4DB1-8C39-04ACBFFEE7C8/DVIA-v2.app
CoreGlyphs    com.apple.CoreGlyphs                               1  ...m/Library/CoreServices/CoreGlyphs.bundle
```

## Using @MASTG-TOOL-0039

The `Process.enumerateModules()` function in Frida's REPL enumerates all modules (including system libraries) loaded into the process memory at runtime.

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

To filter out system libraries and focus on app-bundled libraries, exclude entries whose paths start with `/System/Library/Frameworks` or `/usr/lib`.
