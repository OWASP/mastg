package org.owasp.mastestapp

// SUMMARY: This sample demonstrates four Xposed/LSPosed/Frida detection techniques. Two
// of them are classic Java-side techniques mirrored from the MASTG-TEST-0048 v1 guidance
// (intentionally vulnerable, easily bypassed, and on a modern LSPosed install often
// silent even without a bypass). The other two are the production-grade techniques we
// found to actually fire against modern LSPosed 1.9+ on Android 12+:
//
//   1. CLASSIC — PackageManager.getPackageInfo for known Xposed/LSPosed/EdXposed Manager
//      package ids declared in <queries> in AndroidManifest.xml. No Play-restricted
//      `QUERY_ALL_PACKAGES` permission needed. Catches lazy installs of the Manager;
//      misses the stealth "Hide LSPosed Manager" mode where the Manager is renamed.
//   2. CLASSIC — Method-descriptor verification via reflection. Pick a small set of
//      guaranteed-native methods (`System.currentTimeMillis` / `nanoTime`,
//      `Object.notify` / `notifyAll`, `Thread.currentThread`); resolve them via
//      reflection; if `Modifier.NATIVE` is no longer set on any one of them, LSPlant or
//      Frida has hooked the method. Acts as a tripwire only — fires when the attacker
//      targets one of the specific methods we audit.
//   3. STRONG — Walk /proc/self/maps for any foreign /data/app/<pkg>/base.apk mapping.
//      When LSPosed injects a module into the host process, the module's base.apk is
//      mmapped right next to the app's own one. Permission-free.
//   4. STRONG — Stack-trace inspection: deliberately call methods an attacker is likely
//      to hook (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) with
//      arguments that force an exception, then walk the exception's stack trace and
//      every live Java thread's stack looking for `de.robv.android.xposed.*`,
//      `org.lsposed.lspd.*`, `LSPHooker_`, or `re.frida.*` frames. Also walks
//      /proc/self/task/<tid>/comm to catch native Frida workers (`gum-js-loop`, etc.)
//      that have no Java Thread representation. Permission-free.
//
// When ANY check fires the app raises a "Security Warning" dialog and asks the user to
// accept liability before continuing, per the MASTG-TEST-0048 guidance.
//
// See MASTG-DEMO-0x4B for the bypass narrative.

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.reflect.Modifier

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x4A")
        var anyFail = false

        // Check 1 (classic): targeted PackageManager lookup against a curated list of known
        // LSPosed / Xposed / EdXposed Manager package ids declared in <queries> in
        // AndroidManifest.xml. This avoids the Play-restricted QUERY_ALL_PACKAGES permission.
        // The trade-off is that Manager packages hidden behind randomized package names
        // won't be caught here; those are covered by Checks 3 and 4.
        try {
            val managers = checkKnownXposedManagerPackages()
            if (managers.isNotEmpty()) {
                // FAIL: [MASTG-TEST-0x49] A known Xposed Manager package is installed.
                r.add(Status.FAIL, "Xposed Manager package(s) installed: ${managers.joinToString(", ")}")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x49] No known Xposed Manager package is installed.
                r.add(Status.PASS, "No known Xposed Manager package found via PackageManager.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Manager lookup failed: $e")
        }

        // Check 2 (classic): method-descriptor verification. Acts as a tripwire — only fires
        // if the attacker has hooked one of the specific guaranteed-native methods we audit.
        try {
            val tampered = checkHookedMethodSignatures()
            if (tampered.isNotEmpty()) {
                // FAIL: [MASTG-TEST-0x49] A guaranteed-native method has had its NATIVE bit cleared.
                r.add(Status.FAIL, "Methods appear to be hooked: ${tampered.joinToString(", ")}")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x49] Sensitive methods retain their original modifiers.
                r.add(Status.PASS, "Audited native methods retain their NATIVE modifier.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Method-descriptor check failed: $e")
        }

        // Check 3 (strong): foreign DEX/APK mapped into our own process.
        try {
            val foreignDexes = checkForeignDexesInMaps()
            if (foreignDexes.isNotEmpty()) {
                // FAIL: [MASTG-TEST-0x49] A foreign APK/DEX is mapped into our process.
                r.add(Status.FAIL, "Foreign DEX/APK mapped into process: ${foreignDexes.joinToString(", ")}")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x49] No foreign DEX/APK is mapped.
                r.add(Status.PASS, "No foreign DEX/APK mapped into process.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "/proc/self/maps inspection failed: $e")
        }

        // Check 4 (strong): stack-trace inspection + native-thread enumeration.
        try {
            val frames = checkInstrumentationFramesInStacks()
            if (frames.isNotEmpty()) {
                // FAIL: [MASTG-TEST-0x49] An instrumentation framework frame was found on a stack.
                r.add(Status.FAIL, "Instrumentation frames on stack: ${frames.joinToString(", ")}")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x49] No instrumentation frames observed in any stack.
                r.add(Status.PASS, "No Xposed/LSPosed/Frida frames found in any thread's stack.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Stack-trace inspection failed: $e")
        }

        if (anyFail) promptUserForLiability(
            "Reverse-engineering or instrumentation tooling (Xposed/LSPosed/Frida) was " +
            "detected on this device. Continued use may compromise app security and data " +
            "integrity. Tap \"Accept Liability\" to acknowledge the risk and continue, or " +
            "\"Exit\" to close the app."
        )

        return r.toJson()
    }

    /**
     * Per the MASTG-TEST-0048 guidance, when reverse-engineering tools are detected the
     * app must alert the user and require them to accept liability before continuing.
     * The dialog is dispatched to the main thread because `mastgTest()` runs on a worker
     * thread (see `MainActivity.kt`).
     */
    private fun promptUserForLiability(message: String) {
        val activity = context as? Activity ?: return
        Handler(Looper.getMainLooper()).post {
            if (activity.isFinishing || activity.isDestroyed) return@post
            AlertDialog.Builder(activity)
                .setTitle("Security Warning")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Accept Liability") { d, _ -> d.dismiss() }
                .setNegativeButton("Exit") { _, _ -> activity.finishAffinity() }
                .show()
        }
    }

    /**
     * Targeted `PackageManager.getPackageInfo` lookup for each known Xposed/LSPosed Manager
     * package id. Those package ids are declared in `<queries>` in `AndroidManifest.xml`,
     * which lets `getPackageInfo` see them on Android 11+ without holding the Play-
     * restricted `QUERY_ALL_PACKAGES` permission. A Manager package id that resolves
     * non-null means that flavour of the framework is installed on the device.
     *
     * Limitation: doesn't detect Managers that have been renamed via LSPosed's
     * "Hide LSPosed Manager" feature. Those installs are caught by Checks 3 and 4.
     */
    private fun checkKnownXposedManagerPackages(): List<String> {
        val knownManagers = listOf(
            "de.robv.android.xposed.installer",  // classic Xposed
            "org.meowcat.edxposed.manager",      // EdXposed
            "io.va.exposed",                     // VirtualXposed
            "com.solohsu.android.edxp.manager",  // EdXposed (alt)
            "org.lsposed.manager"                // modern LSPosed
        )
        val pm = context.packageManager
        return knownManagers.filter { pkg ->
            try { pm.getPackageInfo(pkg, 0); true } catch (_: PackageManager.NameNotFoundException) { false }
        }
    }

    /**
     * Verifies that a small set of methods declared `native` on stock AOSP still report the
     * NATIVE bit via reflection. LSPlant (LSPosed's hooking engine) clears `kAccNative`
     * when it hooks a native method, so this acts as a tripwire for any attacker that
     * targets one of these primitives. Frida's Java bridge also clears the bit when it
     * hooks a native method via implementation-replacement.
     *
     * The set is curated: every entry must be `public final native` (or `public static
     * native`) on every supported Android version. `android.os.Process.myPid` and
     * `java.lang.Object.hashCode` are deliberately omitted — they delegate to libcore in
     * Java and never carry NATIVE on a clean device.
     */
    private fun checkHookedMethodSignatures(): List<String> {
        val tampered = mutableListOf<String>()
        val targets = mapOf(
            "java.lang.System" to listOf("currentTimeMillis", "nanoTime"),
            "java.lang.Object" to listOf("notify", "notifyAll"),
            "java.lang.Thread" to listOf("currentThread")
        )
        for ((cls, methods) in targets) {
            try {
                val c = Class.forName(cls)
                for (mName in methods) {
                    val m = c.getDeclaredMethod(mName)
                    if (!Modifier.isNative(m.modifiers)) {
                        tampered.add("$cls.$mName (native bit cleared, modifiers=0x${Integer.toHexString(m.modifiers)})")
                    }
                }
            } catch (_: Exception) { /* ignore */ }
        }
        return tampered
    }

    /**
     * Reads `/proc/self/maps` and flags any `/data/app/<pkg>/base.apk` mapping that does NOT
     * belong to our own package. When LSPosed injects a module into a host process, the
     * module's `base.apk` is mmapped into that process and shows up in maps alongside the
     * host app's APK.
     */
    private fun checkForeignDexesInMaps(): List<String> {
        val ownPkg = context.packageName
        val hits = LinkedHashSet<String>()
        BufferedReader(FileReader("/proc/self/maps")).use { br ->
            br.forEachLine { line ->
                val idx = line.indexOf("/data/app/")
                if (idx < 0) return@forEachLine
                val path = line.substring(idx).substringBefore(' ')
                if (!path.endsWith(".apk")) return@forEachLine
                if (path.contains("/$ownPkg-") || path.contains("/$ownPkg/")) return@forEachLine
                val pkg = path.substringAfter("/data/app/").substringAfter('/').substringBefore('-')
                hits.add(pkg)
            }
        }
        return hits.toList()
    }

    /**
     * Two-pronged instrumentation probe:
     *
     *  (a) Stack-trace inspection — Xposed/LSPosed hook handlers are Java code, so when a
     *      hooked method throws, the resulting `Throwable.stackTrace` carries the handler
     *      frames. We deliberately call three methods an attacker is likely to hook
     *      (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) with arguments
     *      that force an exception, then scan the captured stack for known framework class
     *      names. Also walks `Thread.getAllStackTraces()` for any Java thread parked
     *      inside a framework class.
     *
     *  (b) Native-thread enumeration — Frida hooks do NOT add Java stack frames (the hook
     *      handler is V8/QuickJS native code), and Frida's worker threads (`gum-js-loop`,
     *      `gmain`, `pool-frida`) are native pthreads with no Java `Thread` object, so they
     *      are invisible to `Thread.getAllStackTraces()`. To catch them we walk
     *      `/proc/self/task/<tid>/comm` directly — the kernel exposes every thread's name
     *      to its own process regardless of whether it's a Java thread.
     */
    private fun checkInstrumentationFramesInStacks(): List<String> {
        val needles = listOf(
            "de.robv.android.xposed",
            "org.lsposed.lspd",
            "org.lsposed.",
            "lsphooker_",
            "lsplant",
            "edxposed",
            "re.frida",
            "gum-js"
        )
        val hits = LinkedHashSet<String>()

        fun scan(label: String, frames: Array<StackTraceElement>?) {
            if (frames == null) return
            for (f in frames) {
                val low = f.className.lowercase()
                for (n in needles) {
                    if (low.contains(n.lowercase())) {
                        hits.add("$label: ${f.className}.${f.methodName}")
                    }
                }
            }
        }

        // Probe 1: PackageManager.getPackageInfo (often hooked by root-bypass modules).
        try {
            context.packageManager.getPackageInfo("___xposed_probe_${System.nanoTime()}", 0)
        } catch (e: Throwable) {
            scan("getPackageInfo", e.stackTrace)
        }

        // Probe 2: Runtime.exec (root-bypass modules typically hook this).
        try {
            Runtime.getRuntime().exec(arrayOf("/__xposed_probe_${System.nanoTime()}"))
        } catch (e: Throwable) {
            scan("Runtime.exec", e.stackTrace)
        }

        // Probe 3: File.exists (root-bypass modules typically hook this too).
        try {
            File("/__xposed_probe_${System.nanoTime()}").exists()
            val t = Throwable("post-File.exists probe")
            scan("File.exists.probe", t.stackTrace)
        } catch (e: Throwable) {
            scan("File.exists", e.stackTrace)
        }

        // Walk every live Java thread's stack.
        try {
            val all = Thread.getAllStackTraces()
            for ((thread, frames) in all) {
                scan("thread=${thread.name}", frames)
            }
        } catch (_: Throwable) { /* SecurityManager could block — ignore */ }

        // Native-thread enumeration via /proc/self/task/<tid>/comm. Catches Frida workers
        // that have no Java Thread representation (gum-js-loop, gmain, pool-frida, …).
        val nativeThreadNeedles = listOf("gum-js", "gmain", "pool-frida", "frida", "linjector", "lspd", "xposed", "lsphooker")
        try {
            val tasks = File("/proc/self/task").listFiles() ?: emptyArray()
            for (task in tasks) {
                val commFile = File(task, "comm")
                if (!commFile.canRead()) continue
                val name = try { commFile.readText().trim() } catch (_: Throwable) { continue }
                val low = name.lowercase()
                for (n in nativeThreadNeedles) {
                    if (low.contains(n)) {
                        hits.add("native-thread: $name")
                        break
                    }
                }
            }
        } catch (_: Throwable) {}

        return hits.toList()
    }
}
