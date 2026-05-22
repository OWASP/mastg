---
title: Detecting Xposed/LSPosed Instrumentation
alias: detecting-xposed-lsposed-instrumentation
id: MASTG-BEST-0x49
platform: android
knowledge: [MASTG-KNOW-0030]
---

LSPosed is built to defeat classic Java-only Xposed detection checks. Its Manager app can hide behind a random package name, `XposedBridge` lives in a classloader your app can't reach, the "Xposed API call protection" toggle blocks `Class.forName` lookups, and its hooking engine (LSPlant) leaves no fields on `java.lang.reflect.Method` to find. Any single Java-side check is easy to silence. Use multiple layers, and let the layer the attacker can't reach be the one that decides access to sensitive flows.

## Play Integrity

A rooted device can lie to the app about its own state. The only check the attacker can't fake is one signed by the device's secure hardware (TEE / StrongBox) and verified by your server.

- Require a **[Play Integrity API](https://developer.android.com/google/play/integrity) verdict** from your server before unlocking sensitive flows. A compromised kernel cannot forge the device-key-signed attestation — the signing happens inside the TEE / StrongBox.
- Store secrets in the [Android Keystore](https://developer.android.com/privacy-and-security/keystore) with `setIsStrongBoxBacked(true)`. The key cannot be pulled off a rooted device even if user-space is fully compromised.
- Treat the client-side checks below as inputs to the server's decision, not as a gate on their own.

## Native Probes

Java-side checks run in memory the attacker controls. Put the important detection in JNI, so a bypass has to hook the linker itself, not just a Java method.

- Use [`dl_iterate_phdr`](https://man7.org/linux/man-pages/man3/dl_iterate_phdr.3.html) from a small `.so` to walk every shared object loaded into the process. Flag anything outside `/data/app/<your-pkg>/`, `/system/`, `/apex/`, or `/vendor/`. This catches `liblspd.so` and any LSPosed module SO loaded via memfd — neither shows up as a `/data/app/.../base.apk` in `/proc/self/maps`.
- Send the native verdict to your server with the attestation token. Don't let JNI return a plain boolean that Java code reads and acts on — that boolean is itself something Frida can hook.

## Memory and Process Inspection

These checks are trivial and catch lazy attacker setups. They are not enough on their own — any single one can be defeated — but together they raise the cost of a generic bypass.

- **Look up known Manager packages** with `PackageManager.getPackageInfo` against a fixed list (`de.robv.android.xposed.installer`, `org.lsposed.manager`, `org.meowcat.edxposed.manager`, `io.va.exposed`, `com.solohsu.android.edxp.manager`) declared in `<queries>`. Misses installs with "Hide LSPosed Manager" enabled — the checks below cover that case.
- **Scan `/proc/self/maps`** for any `/data/app/<pkg>/base.apk` that is not yours. LSPosed `mmap`s every active module's APK into the host process, so a foreign `base.apk` means an active module.
- **Probe stack traces and thread names**: force exceptions through methods an attacker is likely to hook (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) and scan `Throwable.getStackTrace()` for `de.robv.android.xposed.*`, `org.lsposed.lspd.*`, `LSPHooker_<id>`, `re.frida.*` frames. Walk `Thread.getAllStackTraces()` and `/proc/self/task/<tid>/comm` for native worker threads (`gum-js-loop`, `gmain`, `pool-frida`).

## Code Integrity

LSPlant clears `kAccNative` when it hooks a native method. Auditing that bit on methods that should always be native catches the in-process hook.

- Audit a small set of guaranteed-native methods (`System.currentTimeMillis` / `nanoTime`, `Object.notify` / `notifyAll`, `Thread.currentThread`) with `java.lang.reflect.Method.getModifiers()` and require `Modifier.NATIVE` to still be set. LSPlant (and Frida's Java bridge) clear that bit when they hook a native method.
