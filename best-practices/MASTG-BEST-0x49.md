---
title: Resist Xposed/LSPosed Instrumentation with Layered Detection and Server Attestation
alias: resist-xposed-lsposed-instrumentation-with-layered-detection-and-server-attestation
id: MASTG-BEST-0x49
platform: android
knowledge: [MASTG-KNOW-0030]
---

Modern LSPosed (1.9+) is a Zygisk-based reimplementation of Xposed that is designed to defeat the classic Java-side detection techniques the v1 MASTG-TEST-0048 guidance lists. LSPosed Manager can be hidden behind a randomized package name ("Hide LSPosed Manager"), `XposedBridge` is loaded into a separate module classloader that the host app cannot reach, the "Xposed API call protection" toggle additionally intercepts `Class.forName` lookups, and LSPlant (LSPosed's hooking engine) uses ART trampolines instead of injecting fields into `java.lang.reflect.Method`. A detection that relies on any one of those classic surfaces will be silent on a production-attacker device. The defense must be layered.

## Permission considerations — do not ship with `QUERY_ALL_PACKAGES`

Avoid `android.permission.QUERY_ALL_PACKAGES` for production apps. It is a Google Play sensitive permission restricted to a narrow list of approved use cases (security/antivirus, accessibility, file managers, etc.) and apps that cannot justify it under one of those categories are rejected at review.

For Xposed Manager probing, use the manifest `<queries>` block instead:

```xml
<queries>
    <package android:name="de.robv.android.xposed.installer" />
    <package android:name="org.lsposed.manager" />
    <package android:name="org.meowcat.edxposed.manager" />
    <package android:name="io.va.exposed" />
    <package android:name="com.solohsu.android.edxp.manager" />
</queries>
```

`PackageManager.getPackageInfo(pkg, 0)` will then resolve the listed packages on Android 11+ without any sensitive permission. This is the technique demonstrated in @MASTG-DEMO-0x4A.

## Java-side detection (layered, no single check is sufficient)

Run *all* of the following — any single one is trivially defeated, but together they raise the cost of a generic bypass:

- **Targeted Manager-package lookup** via `PackageManager.getPackageInfo` against the list above. Catches lazy installs that use a stock LSPosed Manager. Misses installs with "Hide LSPosed Manager" enabled — that case is covered by the next two checks.
- **`Modifier.isNative()` tripwire** on guaranteed-native methods (`System.currentTimeMillis`/`nanoTime`, `Object.notify`/`notifyAll`, `Thread.currentThread`). LSPlant clears the `kAccNative` access-flag bit when it hooks a native method; so does Frida's Java bridge. Acts as a high-confidence tripwire when an attacker hooks one of the audited methods.
- **`/proc/self/maps` scan** for any `/data/app/<pkg>/base.apk` that does not belong to your own package. LSPosed `mmap`s every active module's APK into the host process; this catches active modern modules.
- **Stack-trace probe**: force exceptions through likely-hooked methods (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) and scan `Throwable.getStackTrace()` for `de.robv.android.xposed.*`, `org.lsposed.lspd.*`, `LSPHooker_<id>` frames. Combine with `/proc/self/task/<tid>/comm` enumeration for native worker threads.

When *any* check fires, the app **must** alert the user and require explicit acceptance of liability before proceeding (see the dialog pattern in @MASTG-DEMO-0x4A). Silently continuing — or worse, blocking only on a network call later — wastes the detection.

## What modern LSPosed has already neutralized — do not rely on these

- **`Class.forName("de.robv.android.xposed.XposedBridge")`** — silenced by classloader isolation and by the "Xposed API call protection" toggle that LSPosed ships with by default.
- **`Method.getDeclaredFields()` for `xposedHookedMethodCallbacks` / `additionalInfo`** — a rovo89-era artefact. LSPlant uses ART trampolines and adds no fields to `java.lang.reflect.Method`.
- **`getInstalledApplications` enumeration** for "any app whose package id starts with `de.robv.android.xposed`" — useful only if the attacker hasn't enabled "Hide LSPosed Manager", which is a one-toggle action.

Including these in the demo (as in @MASTG-DEMO-0x4A) is fine *as long as* they are not the only line of defense.

## Native-side detection (the bypass-resistant layer)

Java-side checks live in the same memory the attacker controls. Move the critical detection into JNI:

- Use [`dl_iterate_phdr`](https://man7.org/linux/man-pages/man3/dl_iterate_phdr.3.html) from a small `.so` to walk every shared object loaded into the process. Flag anything outside `/data/app/<your-pkg>/`, `/system/`, `/apex/`, or `/vendor/`. This catches `liblspd.so` and any LSPosed module SO that is loaded via memfd and would not appear as a `/data/app/.../base.apk` in `/proc/self/maps`.
- Verify your own DEX and `.so` segments against a build-time hash and pin the signing certificate via `PackageInfo.signingInfo` — this catches bytecode/native patching attacks where the attacker NOPs out the detection and re-signs the APK.

## Server-side attestation (the kernel-level layer)

Kernel-mode interception is the third bypass category in @MASTG-TEST-0x49 and cannot be reliably detected from user-space. Mitigate it by:

- Requiring a **[Play Integrity API](https://developer.android.com/google/play/integrity) verdict** from your server before unlocking sensitive flows. A compromised kernel still cannot forge the device-key-signed attestation, because the signing happens inside the TEE / StrongBox.
- Storing sensitive cryptographic material in the [Android Keystore](https://developer.android.com/privacy-and-security/keystore) with `setIsStrongBoxBacked(true)`. The key is unrecoverable from a rooted device even if the userspace process is fully compromised.

## What not to do

- **Do not hardcode "Hide LSPosed Manager" packages.** The whole point of the feature is that the package id is randomized per-install. Use the multi-signal approach instead.
- **Do not return a single boolean from `isInstrumented()`.** An attacker hooks the function and you lose everything. Spread the checks across multiple call sites, mix Java and native, and let the server reject a session when the cumulative risk score is above a threshold.
- **Do not ship the alert dialog without a server check.** A Java-side dialog can be dismissed by the same bypass that silenced the detection (see @MASTG-DEMO-0x4B). The dialog is the *user-facing* control; the *authoritative* control must be on a server you operate.

For runtime checks, see the dynamic demo @MASTG-DEMO-0x4A (passing case) and bypass @MASTG-DEMO-0x4B (failing case).
