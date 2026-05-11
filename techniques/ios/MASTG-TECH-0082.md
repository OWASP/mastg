---
title: Extracting Bundled Libraries
platform: ios
---

This technique describes how to identify the dynamic libraries bundled with an iOS app using static analysis (without running the app). Bundled libraries are included in the app's IPA and are typically found in the `Frameworks` directory (`YourApp.app/Frameworks`). They include first-party and third-party libraries that the developer intentionally incorporated into the app.

Note that this technique doesn't cover static libraries, which are linked directly into the app's main binary and don't appear as separate files.

When reviewing the results, keep in mind that some **system libraries** may also be bundled with the app to ensure compatibility with specific iOS SDK versions. You'll need to filter those out to focus on the app's own libraries.

## Inspecting the Application Bundle

Extract the IPA (which is a ZIP archive) and navigate to the `Frameworks` directory to list bundled dynamic libraries. They are typically `.framework` bundles or `.dylib` files.

```bash
unzip -o YourApp.ipa -d YourApp
ls -1 YourApp/Payload/YourApp.app/Frameworks/
App.framework
Flutter.framework
libswiftCore.dylib
libswiftCoreAudio.dylib
...
```

## Using @MASTG-TOOL-0060

Use the `otool -L` command on the app binary to list all linked dynamic libraries as recorded in the binary's load commands.

```bash
otool -L MASTestApp
MASTestApp:
        /System/Library/Frameworks/Foundation.framework/Foundation (compatibility version 300.0.0, current version 2503.1.0)
        /usr/lib/libobjc.A.dylib (compatibility version 1.0.0, current version 228.0.0)
        /usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 1345.120.2)
        /System/Library/Frameworks/CryptoKit.framework/CryptoKit (compatibility version 1.0.0, current version 1.0.0)
        ...
```

## Using @MASTG-TOOL-0073

In radare2, use the `il` command on the app binary to list all linked libraries recorded in the binary's load commands.

```bash
r2 MASTestApp
[0x100006e9c]> il
[Linked libraries]
/System/Library/Frameworks/Foundation.framework/Foundation
/usr/lib/libobjc.A.dylib
/usr/lib/libSystem.B.dylib
/System/Library/Frameworks/CryptoKit.framework/CryptoKit
...
```
