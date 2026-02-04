---
title: Bypassing Root Detection
platform: android
---

## Overview

Root detection mechanisms attempt to identify whether an Android device has been rooted, typically by checking for specific files, processes, or system properties. Bypassing these checks is a common step in dynamic analysis and reverse engineering, allowing security researchers to test app behavior on rooted devices.

This technique describes methods to bypass root detection checks implemented in Android applications.

## Prerequisites

- USB debugging enabled on the device
- @MASTG-TOOL-0031 (Frida) or @MASTG-TOOL-0029 (objection) installed
- The target application installed on the device
- Understanding of the app's root detection mechanisms (optional but helpful)

## Steps

### Using objection

objection provides built-in commands to bypass common root detection checks:

1. Start objection session with the target app:

```bash
# For patched APK with Frida gadget
objection -g <package_name> explore

# For rooted device with frida-server running
objection -g <package_name> explore
```

2. Disable root detection:

```bash
android root disable
```

This command hooks common root detection APIs and methods, returning false or safe values to bypass checks.

### Using Custom Frida Scripts

For more sophisticated root detection mechanisms, custom Frida scripts may be required:

1. Identify the root detection methods through static or dynamic analysis
2. Create a Frida script to hook and bypass the identified methods
3. Load and execute the script:

```bash
frida -U -f <package_name> -l bypass_root.js --no-pause
```

Example Frida script to bypass common checks:

```javascript
Java.perform(function() {
    // Hook common root detection methods
    var RootBeer = Java.use("com.scottyab.rootbeer.RootBeer");
    RootBeer.isRooted.implementation = function() {
        console.log("[*] RootBeer.isRooted() bypassed");
        return false;
    };
    
    // Hook file existence checks
    var File = Java.use("java.io.File");
    File.exists.implementation = function() {
        var path = this.getAbsolutePath();
        if (path.indexOf("su") !== -1 || path.indexOf("magisk") !== -1) {
            console.log("[*] File.exists() bypassed for: " + path);
            return false;
        }
        return this.exists.call(this);
    };
});
```

### Manual Bypass Methods

Alternative approaches when automated tools fail:

- **Renaming binaries**: Rename `su` binary to evade basic file-based checks
- **Hiding processes**: Use process hiding techniques to conceal root-related processes
- **Patching the APK**: Directly modify the app's bytecode to remove or neutralize root detection logic
- **Using kernel modules**: Hook system calls at the kernel level to hide root artifacts

## Validation

After applying the bypass:

1. Launch the app and observe its behavior
2. Check logcat output for any root detection warnings or errors
3. Verify that restricted features are accessible
4. Monitor for any remaining detection mechanisms that may need additional bypasses

## Caveats

- Bypass effectiveness depends on the sophistication of the root detection implementation
- Apps may use multiple layers of detection that require bypassing each individually
- Some apps implement tamper detection that may trigger if bypasses are detected
- Native code detection mechanisms may require more complex hooking approaches
- Server-side validation cannot be bypassed using client-side techniques

## References

- [objection - Android Root Detection Bypass](https://github.com/sensepost/objection/wiki/Using-objection#root-detection)
- [Frida - Dynamic Instrumentation Toolkit](https://frida.re/docs/home/)
- [RootBeer - Root Detection Library](https://github.com/scottyab/rootbeer)
- [Android Root Detection Evasion](https://mobile-security.gitbook.io/mobile-security-testing-guide/android-testing-guide/0x05j-testing-resiliency-against-reverse-engineering#bypassing-root-detection)

## Related

- Tools: @MASTG-TOOL-0029, @MASTG-TOOL-0031
- Knowledge: @MASTG-KNOW-0027
