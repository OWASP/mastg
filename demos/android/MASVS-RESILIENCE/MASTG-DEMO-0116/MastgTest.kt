package org.owasp.mastestapp

import android.content.Context
import android.os.Build
import android.system.OsConstants
import java.io.File

class MastgTest (private val context: Context){

    companion object {
        private var nativeLibraryLoadError: String? = null

        private val nativeLibraryLoaded: Boolean = try {
            System.loadLibrary("tracerpidcheck")
            true
        } catch (t: Throwable) {
            nativeLibraryLoadError = t.message ?: t.toString()
            false
        }
    }

    fun mastgTest(): String {
        // SUMMARY: This sample checks /proc/self/status TracerPid from Kotlin and from a native library.
        val r = DemoResults("0x41")

        try {
            val tracerPidKotlin = getTracerPidFromProcStatus()

            if (tracerPidKotlin > 0) {
                // FAIL: [MASTG-TEST-0352] TracerPid is non-zero, indicating ptrace-based native debugging.
                r.add(
                    Status.FAIL,
                    "Kotlin check: native debugger detected via TracerPid=$tracerPidKotlin in /proc/self/status."
                )
            } else {
                // PASS: [MASTG-TEST-0352] TracerPid is zero, indicating no ptrace-based native debugger is attached.
                r.add(
                    Status.PASS,
                    "Kotlin check: no native debugger detected via TracerPid=$tracerPidKotlin in /proc/self/status."
                )
            }
        } catch (e: IllegalStateException) {
            r.add(Status.ERROR, "Kotlin check failed: ${e.message ?: e.toString()}")
        } catch (e: NumberFormatException) {
            r.add(Status.ERROR, "Kotlin check failed: could not parse TracerPid in /proc/self/status: ${e.message}")
        } catch (e: Exception){
            r.add(Status.ERROR, "Kotlin check failed: unexpected error while checking TracerPid: ${e.message}")
        }

        if (!nativeLibraryLoaded) {
            r.add(Status.ERROR, "Native check failed: could not load tracerpidcheck library: $nativeLibraryLoadError")
            return r.toJson()
        }

        try {
            val tracerPidNative = getTracerPidNative()

            if (tracerPidNative < 0) {
                r.add(Status.ERROR, "Native check failed: could not read or parse TracerPid from /proc/self/status.")
            } else if (tracerPidNative > 0) {
                // FAIL: [MASTG-TEST-0352] Native TracerPid check detected ptrace-based debugging.
                r.add(
                    Status.FAIL,
                    "Native check: native debugger detected via TracerPid=$tracerPidNative in /proc/self/status."
                )
            } else {
                // PASS: [MASTG-TEST-0352] Native TracerPid check did not detect ptrace-based debugging.
                r.add(
                    Status.PASS,
                    "Native check: no native debugger detected via TracerPid=$tracerPidNative in /proc/self/status."
                )
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Native check failed: unexpected error while checking TracerPid: ${e.message}")
        }

        try {
            val tracerPidInline = getTracerPidInlineSyscallNative()
            if (tracerPidInline == -2) {
                r.add(
                    Status.ERROR,
                    "Inline-syscall native check is unsupported on this ABI (${Build.SUPPORTED_ABIS.joinToString()})."
                )
            } else if (tracerPidInline < 0) {
                r.add(Status.ERROR, "Inline-syscall native check failed: could not read or parse TracerPid.")
            } else if (tracerPidInline > 0) {
                // FAIL: [MASTG-TEST-0352] Inline-syscall native TracerPid check detected ptrace-based debugging.
                r.add(
                    Status.FAIL,
                    "Inline-syscall native check: native debugger detected via TracerPid=$tracerPidInline in /proc/self/status."
                )
            } else {
                // PASS: [MASTG-TEST-0352] Inline-syscall native TracerPid check did not detect ptrace-based debugging.
                r.add(
                    Status.PASS,
                    "Inline-syscall native check: no native debugger detected via TracerPid=$tracerPidInline in /proc/self/status."
                )
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Inline-syscall native check failed: unexpected error while checking TracerPid: ${e.message}")
        }

        try {
            val ptraceSelf = ptraceSelfDetectNative()
            if (ptraceSelf > 0) {
                // FAIL: [MASTG-TEST-0352] Native ptrace self-check indicates a ptrace-based debugger is attached.
                r.add(
                    Status.FAIL,
                    "Native ptrace self-check: debugger likely detected (child could not PTRACE_SEIZE parent: EPERM)."
                )
            } else if (ptraceSelf == 0) {
                // PASS: [MASTG-TEST-0352] Native ptrace self-check did not detect a ptrace-based debugger.
                r.add(
                    Status.PASS,
                    "Native ptrace self-check: no ptrace-based debugger detected (child seized parent)."
                )
            } else {
                val errno = -ptraceSelf
                val errnoName = errnoName(errno)
                r.add(
                    Status.ERROR,
                    "Native ptrace self-check failed: ptrace seize flow returned errno=$errno ($errnoName)."
                )
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, "Native ptrace self-check failed: unexpected error: ${e.message}")
        }

        return r.toJson()
    }

    private external fun getTracerPidNative(): Int
    private external fun getTracerPidInlineSyscallNative(): Int
    private external fun ptraceSelfDetectNative(): Int

    private fun getTracerPidFromProcStatus(): Int {
        val statusText = File("/proc/self/status").readText()
        val tracerPidLine = statusText
            .lineSequence()
            .firstOrNull { it.startsWith("TracerPid:") }
            ?: throw IllegalStateException("TracerPid line not found in /proc/self/status")

        val value = tracerPidLine.substringAfter(":").trim()
        if (value.isEmpty()) {
            throw IllegalStateException("TracerPid value is empty in /proc/self/status")
        }

        return value.toInt()
    }

    private fun errnoName(errno: Int): String {
        return when (errno) {
            OsConstants.EPERM -> "EPERM"
            OsConstants.ESRCH -> "ESRCH"
            OsConstants.ECHILD -> "ECHILD"
            OsConstants.EBUSY -> "EBUSY"
            OsConstants.EINVAL -> "EINVAL"
            OsConstants.ENOSYS -> "ENOSYS"
            OsConstants.EACCES -> "EACCES"
            else -> "UNKNOWN_ERRNO"
        }
    }

}
