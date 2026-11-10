package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.IOException
import java.net.URL
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.ECGenParameterSpec
import javax.net.ssl.HttpsURLConnection

// SUMMARY: This sample generates a key pair in the Android KeyStore with an attestation challenge
// fetched from the server for this request, and retrieves the resulting certificate chain to send
// to the server for verification.

class MastgTest(private val context: Context) {

    private val keyAlias = "mastgAttestationKey"

    // The endpoint that issues a new, single-use challenge. No challenge value is compiled into
    // the app.
    private val challengeEndpoint = "https://mastg.example.com/attest/challenge"

    fun mastgTest(): String {
        // PASS: [MASTG-TEST-0x01] The challenge is requested from the server for this
        // attestation only.
        val attestationChallenge = fetchChallenge()
            ?: return "Could not obtain a challenge from the server."

        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (keyStore.containsAlias(keyAlias)) {
            keyStore.deleteEntry(keyAlias)
        }

        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )

        // PASS: [MASTG-TEST-0x01] The server-issued challenge is embedded into the attestation.
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setAttestationChallenge(attestationChallenge)
            .build()

        kpg.initialize(spec)
        kpg.generateKeyPair()

        val certChain = keyStore.getCertificateChain(keyAlias)

        val sb = StringBuilder()
        sb.appendLine("Sending certificate chain to https://example.com/attestation-verify ...")
        sb.appendLine("Chain length: ${certChain.size}")
        certChain.forEachIndexed { i, cert ->
            val pem = Base64.encodeToString(cert.encoded, Base64.DEFAULT)
            sb.appendLine("Certificate[$i] (first 60 chars): ${pem.take(60)}...")
        }
        return sb.toString()
    }

    // Requests a new challenge from the backend. The server generates it with a CSPRNG, stores it
    // against the session, and rejects any attestation carrying a challenge it did not issue or
    // has already accepted.
    private fun fetchChallenge(): ByteArray? {
        return try {
            val connection = URL(challengeEndpoint).openConnection() as HttpsURLConnection
            try {
                connection.inputStream.use { it.readBytes() }
            } finally {
                connection.disconnect()
            }
        } catch (e: IOException) {
            null
        }
    }
}
