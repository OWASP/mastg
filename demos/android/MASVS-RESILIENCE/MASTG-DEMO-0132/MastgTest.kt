package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

// SUMMARY: This sample demonstrates a Java/Kotlin root detection routine based on root manager package names.
class MastgTest(private val context: Context) {

    // FAIL: [MASTG-TEST-0368] Due to weak code obfuscation, the root detection logic remains identifiable in the decompiled Java/Kotlin code despite minification.
    val shouldRunInMainThread = true

    private val rootManagerPackages = listOf(
        "com.topjohnwu.magisk",
        "eu.chainfire.supersu",
        "me.weishu.kernelsu"
    )

    fun mastgTest(): String {
        val results = DemoResults("0x51")

        return try {
            val detectedPackage = rootManagerPackages.firstOrNull(::isPackageInstalled)

            if (detectedPackage != null) {
                val message = "Detected root manager package '$detectedPackage' and triggered the root-detection branch. The test fails because this security-relevant logic remains identifiable in the decompiled Java/Kotlin code despite minification."
                Log.w("MASTG-DEMO-0132", message)
                results.add(Status.FAIL, message)
                closeApp()
            } else {
                results.add(Status.PASS, "The root-detection branch was not triggered in this execution.")
            }

            results.toJson()
        } catch (e: Exception) {
            results.add(Status.ERROR, e.toString())
            results.toJson()
        }
    }

    @Suppress("DEPRECATION")
    private fun isPackageInstalled(packageName: String): Boolean {
        val packageManager = context.packageManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun closeApp() {
        val activity = context as? Activity ?: return
        activity.finishAffinity()
    }
}
