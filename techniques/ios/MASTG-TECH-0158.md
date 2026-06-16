---
title: Extracting Loaded Libraries
platform: ios
---

This technique describes how to enumerate the dynamic libraries loaded into memory by a running iOS app. Unlike @MASTG-TECH-0082, which identifies bundled libraries statically from the IPA, this approach requires the app to be running on a device.

Due to iOS code signing requirements (see @MASTG-KNOW-0058), third-party native code loaded by an App Store app must be signed and installed as part of the app bundle. In practice, the app-bundled libraries visible at runtime are normally a **subset** of the native libraries available in the installed bundle. What changes dynamically is **which bundled libraries have been loaded** at any given moment.

Not all bundled libraries are necessarily linked in the binary's Mach-O load commands (`LC_LOAD_DYLIB`). Some are loaded lazily via `dlopen()` only when a specific code path is triggered. These do **not** appear in the output of `otool -L` or `radare2 il` (see @MASTG-TECH-0082) but will appear in `Process.enumerateModules()` once they have been loaded.

This means the dynamic technique is complementary to the static one:

- @MASTG-TECH-0082 gives the list of app-bundled native libraries that are visible statically in the IPA.
- This technique gives the **active list** at a specific runtime moment, which is useful to confirm which libraries are actually in use during a given app state.

## Enumerating Loaded Libraries at Runtime

### Using @MASTG-TOOL-0039

`Process.enumerateModules()` returns all modules currently loaded into the process memory. Filter out system libraries to focus on the app's own libraries:

```javascript
// Attach with: frida -U -n <AppName>
Process.enumerateModules()
  .filter(m => (m.path.endsWith('.dylib') || m.path.endsWith('/<AppName>'))
            && !m.path.startsWith('/System/')
            && !m.path.startsWith('/usr/'));
```

Example output:

```json
[
  {
    "name": "MyApp",
    "base": "0x10008c000",
    "size": 49152,
    "path": "/private/var/containers/Bundle/Application/F390A491-.../MyApp.app/MyApp"
  },
  {
    "name": "AFNetworking",
    "base": "0x1a2345000",
    "size": 425984,
    "path": "/private/var/containers/Bundle/Application/F390A491-.../MyApp.app/Frameworks/AFNetworking.framework/AFNetworking"
  }
]
```

To observe libraries as they get loaded, which is useful to catch conditionally loaded ones, use `Process.attachModuleObserver()`:

```javascript
Process.attachModuleObserver({
  onAdded: function(module) {
    if (!module.path.startsWith('/System/') && !module.path.startsWith('/usr/')) {
      console.log('[+] Loaded: ' + module.name + ' from ' + module.path);
    }
  }
});
```

### Using @MASTG-TOOL-0074

Objection's `ios bundles list_frameworks` lists all framework bundles loaded by the running app:

```bash
...itudehacks.DVIAswiftv2.develop on (iPhone: 13.2.3) [usb] # ios bundles list_frameworks
Executable      Bundle                                     Version    Path
--------------  -----------------------------------------  ---------  -------------------------------------------
Bolts           org.cocoapods.Bolts                        1.9.0      ...8/DVIA-v2.app/Frameworks/Bolts.framework
RealmSwift      org.cocoapods.RealmSwift                   4.1.1      ...A-v2.app/Frameworks/RealmSwift.framework
...
```

The `ios bundles list_bundles` command lists other loaded bundles not related to frameworks:

```bash
...itudehacks.DVIAswiftv2.develop on (iPhone: 13.2.3) [usb] # ios bundles list_bundles
Executable    Bundle                                       Version  Path
------------  -----------------------------------------  ---------  -------------------------------------------
DVIA-v2       com.highaltitudehacks.DVIAswiftv2.develop          2  ...-1F0C-4DB1-8C39-04ACBFFEE7C8/DVIA-v2.app
CoreGlyphs    com.apple.CoreGlyphs                               1  ...m/Library/CoreServices/CoreGlyphs.bundle
```

## Extracting Libraries

When analyzing loaded libraries, distinguish iOS system libraries from app-bundled libraries. System libraries are provided by the operating system and are commonly mapped from the dyld shared cache, see ["A Deep Dive into Dyld Cache"](https://www.nowsecure.com/blog/2024/09/11/reversing-ios-system-libraries-using-radare2-a-deep-dive-into-dyld-cache-part-1/) for more details. This section focuses on app-bundled libraries, for example embedded frameworks under `Frameworks/`, app extensions under `PlugIns/`, and bundled `.dylib` files.

App Store FairPlay encryption should be checked per bundled Mach-O executable. Do not assume that embedded frameworks are always unencrypted. For each bundled executable, inspect the `LC_ENCRYPTION_INFO` or `LC_ENCRYPTION_INFO_64` load command with `otool -l`. A `cryptid` value of `1` indicates that the file is encrypted on disk, while `0` indicates that it is not encrypted or has already been decrypted.

If a bundled library has `cryptid 0`, it can be extracted directly from the app bundle and does not need to be dumped from memory. If it has `cryptid 1`, extract it from memory using the same FairPlay decryption workflow used for encrypted app binaries.
