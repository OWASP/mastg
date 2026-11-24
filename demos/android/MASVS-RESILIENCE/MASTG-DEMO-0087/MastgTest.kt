package org.owasp.mastestapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// SUMMARY: This sample demonstrates common root detection techniques used in Android applications, and performs a sensitive operation when none of them detects root.

class MastgTest(private val context: Context) {

    private val tag = "MASTG"

    private val sensitiveApiKey = "sk-OWASP-MAS-SuperSecretKey-1234567890"
    private val keyAlias = "mastgCipherKey"

    fun mastgTest(): String {
        val checks = mutableListOf<String>()
        Log.i(tag, "Starting root detection checks")

        val su = checkForSuBinary()
        checks.add(if (su) "✓ Found su binary" else "✗ No su binary found")

        val whichSu = checkForWhichSu()
        checks.add(if (whichSu) "✓ Found su via which command" else "✗ su not found via which command")

        val pkgs = checkForRootPackages()
        checks.add(if (pkgs) "✓ Found root management apps" else "✗ No root management apps found")

        val testKeys = checkForTestKeys()
        checks.add(if (testKeys) "✓ Device has test-keys build" else "✗ Device has release-keys build")

        val props = checkForDangerousProps()
        checks.add(if (props) "✓ Found dangerous system properties" else "✗ No dangerous system properties")

        val isRooted = su || whichSu || pkgs || testKeys || props
        Log.i(tag, "Completed checks: rooted=$isRooted")

        val operation = if (isRooted) {
            Log.i(tag, "Sensitive operation skipped: root detected")
            "The sensitive operation was not performed because the device seems to be rooted."
        } else {
            performSensitiveOperation()
        }

        return "Root Detection Results:\n\n" +
                checks.joinToString("\n") +
                "\n\nDevice appears to be rooted: $isRooted" +
                "\n\n" + operation
    }

    private fun performSensitiveOperation(): String {
        return try {
            val key = getOrCreateSecretKey()

            // Encrypt the sensitive API key
            val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            encryptCipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = encryptCipher.iv
            val encryptedBytes = encryptCipher.doFinal(sensitiveApiKey.toByteArray(Charsets.UTF_8))
            val encryptedData = Base64.encodeToString(iv + encryptedBytes, Base64.DEFAULT)

            // Decrypt to verify
            val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
            val ivFromData = decodedData.copyOfRange(0, 12)
            val ciphertext = decodedData.copyOfRange(12, decodedData.size)

            val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            decryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, ivFromData))
            val decryptedBytes = decryptCipher.doFinal(ciphertext)
            val decryptedString = String(decryptedBytes, Charsets.UTF_8)
            val decryptionSuccessful = decryptedString == sensitiveApiKey
            Log.i(tag, "Sensitive operation performed: decryption successful=$decryptionSuccessful")

            "Sensitive operation performed.\n" +
                    "Encrypted: $encryptedData\n" +
                    "Decryption successful: $decryptionSuccessful"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return if (keyStore.containsAlias(keyAlias)) {
            (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            ).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
        }
    }

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

        Log.d(tag, "checkForSuBinary: testing ${paths.size} paths")

        var found = false
        for (path in paths) {
            try {
                val exists = File(path).exists()
                Log.d(tag, "su path check: path=$path, exists=$exists")
                if (exists) found = true
            } catch (se: SecurityException) {
                Log.w(tag, "su path check blocked: path=$path, msg=${se.message}")
            } catch (t: Throwable) {
                Log.w(
                    tag,
                    "su path check error: path=$path, err=${t::class.java.simpleName}, msg=${t.message}"
                )
            }
        }

        Log.i(tag, "checkForSuBinary result: found=$found")
        return found
    }

    private fun checkForWhichSu(): Boolean {
        return try {
            Log.d(tag, "checkForWhichSu: executing which su")
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))

            val stdout = process.inputStream.bufferedReader().use { it.readText().trim() }
            val stderr = process.errorStream.bufferedReader().use { it.readText().trim() }
            val exit = try { process.waitFor() } catch (_: Throwable) { -1 }

            val found = stdout.isNotEmpty() && exit == 0
            if (found) {
                Log.i(tag, "su found via which: path=$stdout")
            } else {
                Log.d(
                    tag,
                    "which su not found: exit=$exit, stderr=$stderr"
                )
            }
            found
        } catch (se: SecurityException) {
            Log.w(
                tag,
                "checkForWhichSu blocked: msg=${se.message}"
            )
            false
        } catch (t: Throwable) {
            Log.w(
                tag,
                "checkForWhichSu error: err=${t::class.java.simpleName}, msg=${t.message}"
            )
            false
        }
    }

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

        Log.d(tag, "checkForRootPackages: testing ${packages.size} package names")

        var foundAny = false
        for (packageName in packages) {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                Log.i(tag, "root package detected: package=$packageName")
                foundAny = true
            } catch (_: PackageManager.NameNotFoundException) {
                Log.d(tag, "root package not present: package=$packageName")
            } catch (se: SecurityException) {
                Log.w(
                    tag,
                    "package check blocked: package=$packageName, msg=${se.message}"
                )
            } catch (t: Throwable) {
                Log.w(
                    tag,
                    "package check error: package=$packageName, err=${t::class.java.simpleName}, msg=${t.message}"
                )
            }
        }

        Log.i(tag, "checkForRootPackages result: found=$foundAny")
        return foundAny
    }

    private fun checkForTestKeys(): Boolean {
        val buildTags = Build.TAGS
        val isTestKeys = buildTags != null && buildTags.contains("test-keys")
        Log.i(
            tag,
            "checkForTestKeys: buildTags=$buildTags, isTestKeys=$isTestKeys"
        )
        return isTestKeys
    }

    private fun checkForDangerousProps(): Boolean {
        val dangerousProps = mapOf(
            "ro.debuggable" to "1",
            "ro.secure" to "0"
        )

        Log.d(
            tag,
            "checkForDangerousProps: testing ${dangerousProps.size} properties"
        )

        var matchedAny = false
        for ((prop, expected) in dangerousProps) {
            val actual = getSystemProperty(prop)
            val matched = actual == expected
            Log.i(
                tag,
                "system property check: prop=$prop, actual=$actual, expected=$expected, matched=$matched"
            )
            if (matched) matchedAny = true
        }

        Log.i(tag, "checkForDangerousProps result: matched=$matchedAny")
        return matchedAny
    }

    private fun getSystemProperty(key: String): String? {
        return try {
            Log.d(tag, "getSystemProperty exec: key=$key")
            val process = Runtime.getRuntime().exec(arrayOf("getprop", key))

            val stdout = process.inputStream.bufferedReader().use { it.readText().trim() }
            val stderr = process.errorStream.bufferedReader().use { it.readText().trim() }
            val exit = try { process.waitFor() } catch (_: Throwable) { -1 }

            if (stderr.isNotEmpty()) {
                Log.w(
                    tag,
                    "getprop stderr: key=$key, exit=$exit, stderr=$stderr"
                )
            } else {
                Log.d(
                    tag,
                    "getprop ok: key=$key, exit=$exit"
                )
            }

            stdout.ifEmpty { null }
        } catch (se: SecurityException) {
            Log.w(
                tag,
                "getSystemProperty blocked: key=$key, msg=${se.message}"
            )
            null
        } catch (t: Throwable) {
            Log.w(
                tag,
                "getSystemProperty error: key=$key, err=${t::class.java.simpleName}, msg=${t.message}"
            )
            null
        }
    }
}
