---
title: Extracting Loaded Libraries
platform: ios
---

This technique describes how to enumerate the dynamic libraries loaded into memory by a running iOS app. Unlike @MASTG-TECH-0082, which identifies bundled libraries statically from the IPA, this approach requires the app to be running on a device.

## Code Signing and Its Implications for Library Loading

iOS enforces [mandatory code signing](https://support.apple.com/guide/security/app-code-signing-process-sec7c917bf14/web) on every binary that gets mapped into a process. Before loading any dynamic library (whether at launch or via a `dlopen()` call at runtime) `dyld` and the kernel perform two distinct checks:

1. **Signature validity and Team ID**: the library must have a valid code signature and its Team ID must match the main executable's Team ID or be an Apple-signed system library.

2. **Trust authorization**: the binary's code directory hash must be present in the device's [trust cache](https://support.apple.com/guide/security/trust-caches-sec7d38fbf97/web), a system-level record of binaries that are authorized to run. The trust cache is populated only through Apple-controlled installation mechanisms (App Store, TestFlight, or a provisioning profile). Binaries that were never installed through these mechanisms are absent from the trust cache and will be rejected regardless of their signature.

> "At runtime, code signature checks of all executable memory pages are checked as they're loaded to help ensure that an app hasn't been modified since it was installed or last updated." — [Apple Platform Security](https://support.apple.com/guide/security/intro-app-security-ios-ipados-visionos-secf49cad4db/web)

The trust cache check is what prevents loading a library that is not in the app's bundle, even if it is validly signed with the developer's own Team ID. Such a library passes the Team ID check but its code directory hash is not in the trust cache because it was never installed through an Apple-controlled mechanism. Attempting to load it fails unconditionally:

```
dlopen failed: ... no suitable image found. Did find:
    ...: code signing blocked mmap() of '<path>'
```

This means that for App Store apps, **every native library that can ever load at runtime must have been present in the app bundle at installation time**; it is the only path by which the library's hash enters the device's trust cache. There is no mechanism for an App Store app to introduce new native code onto a device at runtime.

## Bundled vs. Runtime library list

Because all libraries must be in the bundle, the set of libraries visible at runtime is always a **subset** of what is in the bundle, and never a superset. What changes dynamically is **which bundled libraries have been loaded** at any given moment.

Not all bundled libraries are necessarily linked in the binary's Mach-O load commands (`LC_LOAD_DYLIB`). Some are loaded lazily via `dlopen()` only when a specific code path is triggered. These do **not** appear in the output of `otool -L` or `radare2 il` (see @MASTG-TECH-0082) but will appear in `Process.enumerateModules()` once they have been activated.

A real example is WhatsApp's `PsiphonTunnel.framework` library: it is present in the app's `Frameworks/` directory and signed with Meta's Team ID, but it is loaded via `dlopen()` only when the app detects network restrictions in certain regions. At the start of a normal session it is absent from the list of loaded modules; once the circumvention feature activates, it appears.

This means the dynamic technique is complementary to the static one:

- @MASTG-TECH-0082 gives the **complete list** of libraries that could ever be loaded (everything in the bundle).
- This technique gives the **active list** at a specific runtime moment, which is useful to confirm which libraries are actually in use during a given app state.

## Enumerating Loaded Libraries at Runtime

### Using @MASTG-TOOL-0039

`Process.enumerateModules()` returns all modules currently loaded into the process memory. Filter out system libraries to focus on the app's own libraries:

```javascript
// Attach with: frida -U -n <AppName>
Process.enumerateModules()
  .filter(m => m.path.endsWith('.dylib') || m.path.endsWith('/<AppName>')
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

To observe libraries as they get loaded (useful to catch conditionally-loaded ones), use `Process.attachModuleObserver()`:

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

Since all loaded libraries must come from the app's bundle and are not FairPlay-encrypted.

App Store DRM only encrypts the `__TEXT` segment of the main app binary, not the embedded frameworks in `Frameworks/`, which can be verified by checking that their `cryptid` field is `0` with `otool -l`; the binary you find in the bundle on disk is identical to what is mapped in memory. There is no need for a memory dump to obtain the library binary; therefore, to extract any of these libraries, use the static techniques described in @MASTG-TECH-0082.
