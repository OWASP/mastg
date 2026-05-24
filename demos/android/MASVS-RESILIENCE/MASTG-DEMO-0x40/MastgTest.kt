package org.owasp.mastestapp

// SUMMARY: This demo detects active JDWP debugger attachment at runtime.

import android.content.Context
import android.os.Debug

class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val r = DemoResults("0x40")

        try {
            val debuggerConnected = Debug.isDebuggerConnected()

            if (debuggerConnected) {
                // FAIL: [MASTG-TEST-0x01] Debug.isDebuggerConnected reports an attached JDWP debugger.
                r.add(Status.FAIL, "Debug.isDebuggerConnected reports an attached JDWP debugger.")
            } else {
                // PASS: [MASTG-TEST-0x01] Debug.isDebuggerConnected reports no attached JDWP debugger.
                r.add(Status.PASS, "Debug.isDebuggerConnected reports no attached JDWP debugger.")
            }

        } catch (e: Exception) {
            r.add(Status.ERROR, "Unexpected error while running anti-debugging checks: ${e.javaClass.simpleName}: ${e.message}")
        }

        return r.toJson()
    }

}
