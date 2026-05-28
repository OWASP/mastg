---
title: Detecting Xposed/LSPosed Instrumentation
alias: detecting-xposed-lsposed-instrumentation
id: MASTG-BEST-0x49
platform: android
knowledge: [MASTG-KNOW-0030]
---

Employ various techniques to detect the presence of the Xposed Framework or its modern derivatives like LSPosed and EdXposed. These frameworks modify the Android Runtime (ART) to allow hooking of Java methods, which can be used to bypass security controls or steal sensitive data.

## Xposed Detection Techniques

### Stack Trace Analysis
Xposed leaves artifacts in the call stack when a hooked method is executed. Throwing a `Throwable` and inspecting the stack trace can reveal framework-related classes:
- `de.robv.android.xposed.XposedBridge`
- `org.lsposed.lspd`
- `lsphooker_`
- `lsplant`

### Memory Mapping Scan
Scan `/proc/self/maps` for foreign APK or DEX files mapped into the process's address space. Xposed and LSPosed modules often inject their own code, which can be identified by looking for entries containing:
- Paths to the Xposed/LSPosed manager app.
- Package names of known modules (e.g., checking for `/data/app/` paths of module APKs).

### Checking for Known Files and Packages
Check for the presence of the Xposed installer app or framework files:
- Package names: `de.robv.android.xposed.installer`, `org.lsposed.manager`.
- System files: `/system/bin/app_process` (if it has been modified to support Xposed).

## Countermeasures and Limitations

Modern instrumentation frameworks like LSPosed and EdXposed are highly effective at bypassing detection checks by default. Because they operate within the Android Runtime (ART), they can intercept and modify any Java API the application uses for its own defense.

- **Selective Hooking (Scoping)**: Modern frameworks allow users to enable hooks only for specific applications. This prevents "global" artifacts (like modified system files or globally visible processes) from being easily detected by apps not currently being targeted.
- **API Spoofing**: The framework can hook the very APIs used to detect it. For example, it can intercept `PackageManager.getPackageInfo` to hide its own manager app, or `BufferedReader.readLine` to filter out its own entries from `/proc/self/maps`.
- **Stack Trace Cleaning**: Frameworks often automatically strip their own class names (`de.robv.android.xposed.*`) from `Throwable.getStackTrace` and `Thread.getStackTrace` results, making the stack trace appear legitimate even when running within a hooked environment.

To enhance detection:
- **Native Probes**: Implement detection logic in native code (C/C++) using the NDK. Native code is harder (though not impossible) to hook than Java methods and can use direct system calls to bypass Java-level spoofing.
- **Method Integrity Checks**: Use the NDK to inspect the `ArtMethod` structure of critical Java methods. Xposed often modifies these structures (e.g., changing the entry point to a native trampoline) to facilitate hooking.
- **Anti-Hooking**: Implement checks to detect if critical methods have been hooked (e.g., by checking for known trampolines in the native implementation of Java methods or verifying the method's access flags).
- **Silent Detection**: Instead of crashing immediately upon detection, change the app's behavior subtly or report the detection to a backend server to avoid tipping off the attacker.
