package org.owasp.mastestapp

// SUMMARY: This sample demonstrates three common Frida detection techniques used by Android
// apps as anti-instrumentation checks: scanning the default Frida TCP port (27042),
// enumerating running processes for `frida-server`/`frida-helper`, and reading
// `/proc/self/maps` for injected libraries such as `frida-agent.so`, `libfrida` or `gum`.
// All three checks are well-known and trivially bypassable (see MASTG-DEMO-0x49).

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.InetSocketAddress
import java.net.Socket

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x48")
        var anyFail = false


        try {
            val portFound = checkFridaDefaultPort()
            if (portFound) {
                // FAIL: [MASTG-TEST-0x48] Frida was detected via the default port 27042.
                r.add(Status.FAIL, "Frida default port (27042) is open — instrumentation detected.")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x48] No process is listening on port 27042.
                r.add(Status.PASS, "Frida default port (27042) is closed.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Port check failed: $e")
        }

    
        try {
            val matches = checkFridaThreads()
            if (matches.isNotEmpty()) {
                // FAIL: [MASTG-TEST-0x48] A suspicious thread name was found in this process.
                r.add(Status.FAIL, "Suspicious threads found in this process: ${matches.joinToString(", ")}")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x48] No suspicious thread names were found.
                r.add(Status.PASS, "No Frida-related thread names found under /proc/self/task.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Thread enumeration failed: $e")
        }

  
        try {
            val libsFound = checkFridaLibraries()
            if (libsFound.isNotEmpty()) {
                // FAIL: [MASTG-TEST-0x48] An injected Frida library was found in /proc/self/maps.
                r.add(Status.FAIL, "Injected libraries detected in /proc/self/maps: ${libsFound.joinToString(", ")}")
                anyFail = true
            } else {
                // PASS: [MASTG-TEST-0x48] /proc/self/maps does not contain any Frida artifacts.
                r.add(Status.PASS, "No Frida libraries mapped into the process.")
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Maps check failed: $e")
        }

        if (anyFail) promptUserForLiability(
            "Reverse-engineering or instrumentation tooling (Frida) was detected on this " +
            "device. Continued use may compromise app security and data integrity. " +
            "Tap \"Accept Liability\" to acknowledge the risk and continue, or \"Exit\" " +
            "to close the app."
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

    private fun checkFridaDefaultPort(): Boolean {
        val socket = Socket()
        return try {
            socket.connect(InetSocketAddress("127.0.0.1", 27042), 200)
            true
        } catch (e: Exception) {
            false
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }


    private fun checkFridaThreads(): List<String> {
        val needles = listOf("gum-js-loop", "gmain", "gdbus", "pool-frida", "frida")
        val matches = mutableListOf<String>()

        val taskDir = File("/proc/self/task")
        val tids = taskDir.listFiles { f -> f.isDirectory && f.name.all { it.isDigit() } } ?: return matches

        for (tid in tids) {
            val commFile = File(tid, "comm")
            if (!commFile.canRead()) continue
            val name = try {
                commFile.readText().trim()
            } catch (_: Exception) { continue }
            for (needle in needles) {
                if (name.contains(needle, ignoreCase = true)) {
                    matches.add("${tid.name}:$name")
                    break
                }
            }
        }
        return matches
    }


    private fun checkFridaLibraries(): List<String> {
        val needles = listOf("frida-agent", "libfrida", "frida-gadget", "gum-js-loop", "linjector", "/gum")
        val hits = mutableSetOf<String>()
        BufferedReader(FileReader("/proc/self/maps")).use { br ->
            br.forEachLine { line ->
                for (needle in needles) {
                    if (line.contains(needle, ignoreCase = true)) {
                        hits.add(needle)
                    }
                }
            }
        }
        return hits.toList()
    }
}