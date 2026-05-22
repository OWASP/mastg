---
title: Resist Xposed/LSPosed Instrumentation with Layered Detection and Server Attestation
alias: resist-xposed-lsposed-instrumentation-with-layered-detection-and-server-attestation
id: MASTG-BEST-0x49
platform: android
knowledge: [MASTG-KNOW-0030]
---

Modern LSPosed (1.9+) is built to defeat the classic Java-only checks from the v1 MASTG-TEST-0048 guidance. Its Manager app can hide behind a random package name, `XposedBridge` lives in a classloader your app can't reach, the "Xposed API call protection" toggle blocks `Class.forName` lookups, and its hooking engine (LSPlant) leaves no fields on `java.lang.reflect.Method` to find. Any single Java-side check is easy to silence. Use multiple layers, and let the layer the attacker can't reach be the one that decides access to sensitive flows.

## 1. Server-side attestation

A rooted device can lie to the app about its own state. The only check the attacker can't fake is one signed by the device's secure hardware (TEE / StrongBox) and verified by your server.

- Require a **[Play Integrity API](https://developer.android.com/google/play/integrity) verdict** from your server before unlocking sensitive flows. A compromised kernel cannot forge the device-key-signed attestation — the signing happens inside the TEE / StrongBox.
- Store secrets in the [Android Keystore](https://developer.android.com/privacy-and-security/keystore) with `setIsStrongBoxBacked(true)`. The key cannot be pulled off a rooted device even if user-space is fully compromised.
- Treat the client-side checks below as inputs to the server's decision, not as a gate on their own.

## 2. Native-side detection

Java-side checks run in memory the attacker controls. Put the important detection in JNI, so a bypass has to hook the linker itself, not just a Java method.

- Use [`dl_iterate_phdr`](https://man7.org/linux/man-pages/man3/dl_iterate_phdr.3.html) from a small `.so` to walk every shared object loaded into the process. Flag anything outside `/data/app/<your-pkg>/`, `/system/`, `/apex/`, or `/vendor/`. This catches `liblspd.so` and any LSPosed module SO loaded via memfd — neither shows up as a `/data/app/.../base.apk` in `/proc/self/maps`.
- Hash your own DEX and `.so` segments at build time and check them at runtime. Pin the signing certificate via `PackageInfo.signingInfo`. This catches the app-patching bypass — an attacker who NOPs out the detection and re-signs the APK.
- Send the native verdict to your server with the attestation token. Don't let JNI return a plain boolean that Java code reads and acts on — that boolean is itself something Frida can hook.

## 3. Java-side tripwires

Java-side checks are trivial and catch lazy attacker setups. They are not enough on their own — any single one can be defeated — but together they raise the cost of a generic bypass.

- **Look up known Manager packages** with `PackageManager.getPackageInfo` against a fixed list (`de.robv.android.xposed.installer`, `org.lsposed.manager`, `org.meowcat.edxposed.manager`, `io.va.exposed`, `com.solohsu.android.edxp.manager`) declared in `<queries>` (see the permission note below). Misses installs with "Hide LSPosed Manager" enabled — the next checks cover that case.
- **`Modifier.isNative()` tripwire** on methods that should always be native (`System.currentTimeMillis`/`nanoTime`, `Object.notify`/`notifyAll`, `Thread.currentThread`). LSPlant clears the `kAccNative` flag when it hooks a native method; so does Frida's Java bridge. If the bit is clear on any of these, something has hooked them.
- **`/proc/self/maps` scan** for any `/data/app/<pkg>/base.apk` that is not yours. LSPosed `mmap`s every active module's APK into the host process, so a foreign `base.apk` means an active module.
- **Stack-trace + thread probe**: force exceptions through methods an attacker is likely to hook (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) and scan `Throwable.getStackTrace()` for `de.robv.android.xposed.*`, `org.lsposed.lspd.*`, `LSPHooker_<id>`, `re.frida.*` frames. Walk `Thread.getAllStackTraces()` and `/proc/self/task/<tid>/comm` for native worker threads (`gum-js-loop`, `gmain`, `pool-frida`).

When any check fires, the app **must** show the user a warning and require them to accept the risk before continuing (see the dialog in @MASTG-DEMO-0x4A).

For runtime checks, see the passing demo @MASTG-DEMO-0x4A and the bypass demo @MASTG-DEMO-0x4B.
