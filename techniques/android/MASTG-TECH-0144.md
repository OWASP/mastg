---
title: Bypassing Root Detection
platform: android
---

Root detection mechanisms attempt to identify whether an Android device has been rooted, typically by checking for specific files, processes, or system properties. Bypassing these checks is a common step in dynamic analysis and reverse engineering, allowing security researchers to test app behavior on rooted devices.

## Using @MASTG-TOOL-0029

objection provides built-in commands to bypass common root detection checks:

```bash
objection -n "MASTestApp" start

     _   _         _   _
 ___| |_|_|___ ___| |_|_|___ ___
| . | . | | -_|  _|  _| | . |   |
|___|___| |___|___|_| |_|___|_|_|
      |___|(object)inject(ion) v1.12.3

     Runtime Mobile Exploration
        by: @leonjza from @sensepost

[tab] for command suggestions
MASTestApp (run) on (Android: 14) [usb] # android root disable
```

This command hooks common root detection APIs and methods, returning false or safe values to bypass checks.

## Using Custom @MASTG-TOOL-0001 Scripts

For more sophisticated root detection mechanisms, custom Frida scripts may be required:

1. Identify the root detection methods through static or dynamic analysis (see @MASTG-TECH-0014, @MASTG-TECH-0033, @MASTG-TECH-0032, etc.)
2. Create a Frida script to hook and bypass the identified methods
3. Load and execute the script:

```bash
frida -U -f <package_name> -l bypass_root.js
```

Example Frida script to bypass common checks:

```javascript
'use strict';

Java.perform(function () {
  var File = Java.use("java.io.File");
  var exists = File.exists.overload();

  exists.implementation = function () {
    var path = this.getAbsolutePath();
    if (path.indexOf("su") !== -1 || path.indexOf("magisk") !== -1) {
      console.log("[*] File.exists() bypassed for: " + path);
      return false;
    }
    return exists.call(this);
  };
});
```

This will intercept calls to `File.exists()` and return false for paths commonly associated with root access.

```sh
[*] File.exists() bypassed for: /sbin/su
[*] File.exists() bypassed for: /system/bin/su
[*] File.exists() bypassed for: /system/xbin/su
[*] File.exists() bypassed for: /data/local/xbin/su
[*] File.exists() bypassed for: /data/local/bin/su
[*] File.exists() bypassed for: /system/sd/xbin/su
[*] File.exists() bypassed for: /system/bin/failsafe/su
[*] File.exists() bypassed for: /data/local/su
[*] File.exists() bypassed for: /su/bin/su
```

## Manual Bypass Methods

Alternative approaches when automated tools fail:

- **Renaming binaries:** Rename or relocate the `su` binary. This only defeats basic file existence checks and is ineffective against modern detection.
- **Hiding processes:** @MASTG-TOOL-0021 with Zygisk and DenyList provides per app isolation using mount namespaces and Zygote injection so that root related artifacts and indicators are not visible from the app's process. This is not true kernel level process hiding. Frameworks like [LSPosed](https://github.com/LSPosed/LSPosed) combined with detection bypass modules hook Java and native APIs such as process enumeration calls and selectively filter results returned to the app.
- **Patching the APK:** Static patching is commonly done using tools like apktool to decompile and modify Smali code, removing or neutralizing root detection logic. See @MASTG-TECH-0038.
- **Using kernel modules:** True kernel level hiding is implemented by custom kernels or loadable kernel modules rather than mainstream rooting tools. Projects such as [KernelSU](https://github.com/tiann/KernelSU) and research LKM based rootkits hook syscalls or task iteration logic to hide processes, files, and mounts globally. This approach directly corresponds to syscall hooking techniques described in kernel focused research and is distinct from user space hiding.

## Validation

After applying the bypass:

1. Launch the app and observe its behavior.
2. Check logcat output for any root detection warnings or errors.
3. Verify that restricted features are accessible.
4. Monitor for any remaining detection mechanisms that may need additional bypasses.

## Caveats

- Bypass effectiveness depends on the sophistication of the root detection implementation.
- Apps may use multiple layers of detection that require bypassing each individually.
- Some apps implement tamper detection that may trigger if bypasses are detected.
- Native code detection mechanisms may require more complex hooking approaches.
- Server-side validation cannot be bypassed using client-side techniques.
