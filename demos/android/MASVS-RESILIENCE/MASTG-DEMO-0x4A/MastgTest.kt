package org.owasp.mastestapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x4A")
        var anyFail = false

  
        try {
            val foreignDexes = checkForeignDexesInMaps()
            if (foreignDexes.isNotEmpty()) {
                r.add(Status.FAIL, "Foreign DEX/APK mapped into process: ${foreignDexes.joinToString(", ")}")
                anyFail = true
            } else {
                r.add(Status.PASS, "No foreign DEX/APK mapped into process.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "/proc/self/maps inspection failed: $e")
        }

        try {
            val frames = checkInstrumentationFramesInStacks()
            if (frames.isNotEmpty()) {
                r.add(Status.FAIL, "Instrumentation frames on stack: ${frames.joinToString(", ")}")
                anyFail = true
            } else {
                r.add(Status.PASS, "No Xposed/LSPosed/Frida frames found in any thread's stack.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Stack-trace inspection failed: $e")
        }

        if (anyFail) promptUserForLiability(
            "Reverse-engineering or instrumentation tooling (Xposed/LSPosed) was " +
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
            "re.frida"
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

        return hits.toList()
    }

}
