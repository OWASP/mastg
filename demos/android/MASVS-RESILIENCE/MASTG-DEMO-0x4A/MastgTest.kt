package org.owasp.mastestapp
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

        try {
            context.packageManager.getPackageInfo("___xposed_probe_${System.nanoTime()}", 0)
        } catch (e: Throwable) {
            scan("getPackageInfo", e.stackTrace)
        }

        try {
            Runtime.getRuntime().exec(arrayOf("/__xposed_probe_${System.nanoTime()}"))
        } catch (e: Throwable) {
            scan("Runtime.exec", e.stackTrace)
        }

        try {
            File("/__xposed_probe_${System.nanoTime()}").exists()
            val t = Throwable("post-File.exists probe")
            scan("File.exists.probe", t.stackTrace)
        } catch (e: Throwable) {
            scan("File.exists", e.stackTrace)
        }

        try {
            val all = Thread.getAllStackTraces()
            for ((thread, frames) in all) {
                scan("thread=${thread.name}", frames)
            }
        } catch (_: Throwable) { /* SecurityManager could block — ignore */ }

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
