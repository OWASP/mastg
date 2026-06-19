// SUMMARY: Demonstrates reading a locally stored role value using HMAC for integrity
// protection, with the HMAC key generated inside AndroidKeyStore. The key is hardware-bound
// and cannot be extracted, making the integrity tag unforgeable even by an attacker with
// full filesystem access.

package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.File
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

class MastgTest(private val context: Context) {

    companion object {
        private const val HMAC_ALGORITHM    = "HmacSHA256"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS         = "mastg_demo_integrity_key"

        private const val FILE_ROLE         = "user_role.dat"
        private const val FILE_ROLE_HMAC    = "user_role.hmac"
        private const val FILE_MARKER       = ".setup_done"
        private const val DEFAULT_ROLE      = "user"
        private const val TAG               = "MASTG-DEMO"
    }

    fun mastgTest(): String {
        val dir = context.filesDir
        if (!File(dir, FILE_MARKER).exists()) {
            setup(dir)
            File(dir, FILE_MARKER).writeBytes(ByteArray(0))
            return setupMessage(dir)
        }
        return verify(dir)
    }

    private fun setup(dir: File) {
        val bytes = DEFAULT_ROLE.toByteArray(Charsets.UTF_8)
        File(dir, FILE_ROLE).writeText(DEFAULT_ROLE, Charsets.UTF_8)
        // PASS: [MASTG-TEST-0x01] HMAC key generated inside AndroidKeyStore — never exposed.
        File(dir, FILE_ROLE_HMAC).writeText(computeHmac(bytes), Charsets.UTF_8)
        Log.d(TAG, "Setup complete: role=$DEFAULT_ROLE")
    }

    private fun verify(dir: File): String {
        val roleFile = File(dir, FILE_ROLE)
        val hmacFile = File(dir, FILE_ROLE_HMAC)
        if (!roleFile.exists() || !hmacFile.exists()) return "Error: data files missing."

        // PASS: [MASTG-TEST-0x01] File bytes protected by AndroidKeyStore-backed HMAC.
        // Key is hardware-bound — cannot be extracted and forged by an attacker.
        val payload  = roleFile.readBytes()
        val stored   = hmacFile.readText(Charsets.UTF_8).trim()
        val computed = computeHmac(payload)

        val valid = MessageDigest.isEqual(
            stored.toByteArray(Charsets.UTF_8),
            computed.toByteArray(Charsets.UTF_8)
        )
        val role = if (valid) String(payload, Charsets.UTF_8).trim() else "tampering_detected"
        Log.d(TAG, "role=$role hmacValid=$valid")

        return "=== ON-DISK VALUE ===\n" +
            "user_role.dat : ${roleFile.readText(Charsets.UTF_8).trim()}\n" +
            "user_role.hmac: $stored\n\n" +
            "=== RESULT ===\n" +
            "Role loaded : $role\n" +
            "HMAC check  : ${if (valid) "PASSED — AndroidKeyStore key, unforgeable" else "FAILED — tampering detected"}"
    }

    private fun computeHmac(data: ByteArray): String {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        // PASS: [MASTG-TEST-0x01] Key sourced from AndroidKeyStore via KeyGenParameterSpec.
        mac.init(getOrCreateKey())
        val raw = mac.doFinal(data)
        return buildString { raw.forEach { b -> append("%02x".format(b)) } }
    }

    private fun getOrCreateKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }
        (ks.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        // PASS: [MASTG-TEST-0x01] Key generated with PURPOSE_SIGN | PURPOSE_VERIFY,
        // bound to AndroidKeyStore — key material never leaves the secure element.
        return KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_HMAC_SHA256,
            KEYSTORE_PROVIDER
        ).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                ).setDigests(KeyProperties.DIGEST_SHA256).build()
            )
        }.generateKey()
    }

    private fun setupMessage(dir: File): String =
        "=== SETUP COMPLETE ===\n\n" +
        "Path  : ${dir.absolutePath}/\n" +
        "Files : $FILE_ROLE, $FILE_ROLE_HMAC\n" +
        "Role  : \"$DEFAULT_ROLE\"\n\n" +
        "=== TAMPER RECIPE ===\n\n" +
        "Step 1:  adb shell am force-stop org.owasp.mastestapp\n" +
        "Step 2:  adb shell \"run-as org.owasp.mastestapp sh -c" +
        " 'echo -n admin > files/user_role.dat'\"\n" +
        "Step 3:  adb shell am start -n org.owasp.mastestapp/.MainActivity\n\n" +
        "Tap 'Start': tampering is detected — the Keystore key cannot be extracted to forge the HMAC."
}
