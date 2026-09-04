package org.owasp.mastestapp

import android.content.Context
import android.util.Log
import java.security.MessageDigest

class MastgTest(private val context: Context) {

    // SUMMARY: This sample demonstrates various common ways of hashing data with broken algorithms (MD5, SHA-1) via MessageDigest, plus a SHA-256 pass case.

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    fun mastgTest(): String {
        val password = "superSecretPassword!"
        val tokenMaterial = "session-token-user-42"
        val fileContents = "readme.txt"

        // FAIL: [MASTG-TEST-0x01] The app hashes a password with MD5.
        val md5 = MessageDigest.getInstance("MD5")
        val md5Password = md5.digest(password.toByteArray(Charsets.UTF_8)).toHex()
        Log.d("MASTG-TEST", "MD5 password hash: $md5Password")

        // FAIL: [MASTG-TEST-0x01] The app hashes authentication token material with SHA-1.
        val sha1 = MessageDigest.getInstance("SHA-1")
        val sha1Token = sha1.digest(tokenMaterial.toByteArray(Charsets.UTF_8)).toHex()
        Log.d("MASTG-TEST", "SHA-1 token hash: $sha1Token")

        // FAIL: [MASTG-TEST-0x01] The app hashes authentication token material with the SHA1 JCA alias of SHA-1.
        val sha1Alias = MessageDigest.getInstance("SHA1")
        val sha1AliasToken = sha1Alias.digest(tokenMaterial.toByteArray(Charsets.UTF_8)).toHex()
        Log.d("MASTG-TEST", "SHA1 alias token hash: $sha1AliasToken")

        // FAIL: [MASTG-TEST-0x01] The app uses MD5. Further validation is required to confirm this checksum is not security-relevant.
        val md5ChecksumDigest = MessageDigest.getInstance("MD5")
        val md5Checksum = md5ChecksumDigest.digest(fileContents.toByteArray(Charsets.UTF_8)).toHex()
        Log.d("MASTG-TEST", "MD5 checksum: $md5Checksum")

        // PASS: [MASTG-TEST-0x01] The app hashes a password with SHA-256.
        val sha256 = MessageDigest.getInstance("SHA-256")
        val sha256Password = sha256.digest(password.toByteArray(Charsets.UTF_8)).toHex()
        Log.d("MASTG-TEST", "SHA-256 password hash: $sha256Password")

        return buildString {
            appendLine("MD5 password hash: $md5Password")
            appendLine("SHA-1 token hash: $sha1Token")
            appendLine("SHA1 alias token hash: $sha1AliasToken")
            appendLine("MD5 checksum: $md5Checksum")
            appendLine("SHA-256 password hash: $sha256Password")
        }
    }
}
