package org.owasp.mastestapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

// SUMMARY: This sample demonstrates common root detection techniques used in Android applications.

class MastgTest(private val context: Context) {
    fun mastgTest(): String {
        val checks = mutableListOf<String>()
        
        // FAIL: [MASTG-TEST-0289] The app checks for su binary in common locations
        if (checkForSuBinary()) {
            checks.add("✓ Found su binary")
        } else {
            checks.add("✗ No su binary found")
        }
        
        // FAIL: [MASTG-TEST-0289] The app checks for root management packages
        if (checkForRootPackages()) {
            checks.add("✓ Found root management apps")
        } else {
            checks.add("✗ No root management apps found")
        }
        
        // FAIL: [MASTG-TEST-0289] The app checks for test-keys build
        if (checkForTestKeys()) {
            checks.add("✓ Device has test-keys build")
        } else {
            checks.add("✗ Device has release-keys build")
        }
        
        // FAIL: [MASTG-TEST-0289] The app checks for dangerous system properties
        if (checkForDangerousProps()) {
            checks.add("✓ Found dangerous system properties")
        } else {
            checks.add("✗ No dangerous system properties")
        }
        
        val isRooted = checkForSuBinary() || checkForRootPackages() || 
                       checkForTestKeys() || checkForDangerousProps()
        
        return "Root Detection Results:\n\n" + 
               checks.joinToString("\n") + 
               "\n\nDevice appears to be rooted: $isRooted"
    }
    
    /**
     * Check for su binary in common locations
     */
    private fun checkForSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }
        return false
    }
    
    /**
     * Check for root management packages
     */
    private fun checkForRootPackages(): Boolean {
        val packages = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
        )
        
        for (packageName in packages) {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // Package not found, continue
            }
        }
        return false
    }
    
    /**
     * Check for test-keys build
     */
    private fun checkForTestKeys(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }
    
    /**
     * Check for dangerous system properties
     */
    private fun checkForDangerousProps(): Boolean {
        val dangerousProps = mapOf(
            "ro.debuggable" to "1",
            "ro.secure" to "0"
        )
        
        for ((prop, value) in dangerousProps) {
            val propValue = getSystemProperty(prop)
            if (propValue == value) {
                return true
            }
        }
        return false
    }
    
    private fun getSystemProperty(key: String): String? {
        return try {
            val process = Runtime.getRuntime().exec("getprop $key")
            process.inputStream.bufferedReader().use { it.readText().trim() }
        } catch (e: Exception) {
            null
        }
    }
}
