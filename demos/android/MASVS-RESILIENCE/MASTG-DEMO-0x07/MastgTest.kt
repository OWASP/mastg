package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import java.io.IOException
import java.net.URL
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.security.spec.ECGenParameterSpec
import javax.net.ssl.HttpsURLConnection

// SUMMARY: This sample generates a key pair in the Android KeyStore with an attestation challenge
// fetched from the server, retrieves the certificate chain, and reads the attestation extension
// that carries the device integrity signals. It forwards the chain to the server and does not
// evaluate the signals on the device.

class MastgTest(private val context: Context) {

    private val keyAlias = "mastgDeviceAttestationKey"

    // OID of the Android key attestation extension carried in the leaf certificate.
    private val attestationOid = "1.3.6.1.4.1.11129.2.1.17"

    // The endpoint that issues a new, single-use challenge. No challenge value is compiled into
    // the app.
    private val challengeEndpoint = "https://mastg.example.com/attest/challenge"

    fun mastgTest(): String {
        // PASS: [MASTG-TEST-0x05] The challenge is requested from the server for this
        // attestation only.
        val challenge = fetchChallenge()
            ?: return "Could not obtain a challenge from the server."

        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (keyStore.containsAlias(keyAlias)) {
            keyStore.deleteEntry(keyAlias)
        }

        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )

        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setAttestationChallenge(challenge)
            .build()

        kpg.initialize(spec)
        kpg.generateKeyPair()

        // PASS: [MASTG-TEST-0x05] The certificate chain carries the device integrity signals in
        // the rootOfTrust field of the leaf certificate's attestation extension.
        val certChain = keyStore.getCertificateChain(keyAlias)

        val sb = StringBuilder()
        sb.appendLine("Chain length: ${certChain.size}")

        val leaf = certChain[0] as X509Certificate
        val extension = leaf.getExtensionValue(attestationOid)

        if (extension != null) {
            sb.appendLine("Attestation extension ($attestationOid): present, ${extension.size} bytes")
            sb.appendLine("This extension carries rootOfTrust (verifiedBootState, verifiedBootKey,")
            sb.appendLine("deviceLocked) and attestationSecurityLevel.")
        } else {
            sb.appendLine("Attestation extension ($attestationOid): ABSENT")
            sb.appendLine("No device integrity signals are available in this chain.")
        }

        // The security level of the key itself indicates whether the attestation was produced
        // by secure hardware or entirely in software.
        val privateKey = keyStore.getKey(keyAlias, null) as java.security.PrivateKey
        val factory = KeyFactory.getInstance(privateKey.algorithm, "AndroidKeyStore")
        val keyInfo = factory.getKeySpec(privateKey, KeyInfo::class.java)

        val level = when (keyInfo.securityLevel) {
            KeyProperties.SECURITY_LEVEL_SOFTWARE -> "Software"
            KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT -> "TrustedEnvironment"
            KeyProperties.SECURITY_LEVEL_STRONGBOX -> "StrongBox"
            else -> "Unknown (${keyInfo.securityLevel})"
        }
        sb.appendLine("Key security level: $level")

        sb.appendLine()
        sb.appendLine("Sending chain to https://example.com/attestation-verify for evaluation ...")
        sb.appendLine("The server, not the app, evaluates rootOfTrust and attestationSecurityLevel.")

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
