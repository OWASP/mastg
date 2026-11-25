package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.ECGenParameterSpec

// SUMMARY: This sample generates a key pair in the Android KeyStore and retrieves its certificate
// chain to send to a server, but sets no attestation challenge. The certificate is therefore
// self-signed and carries no attestation that the server could verify.

class MastgTest(private val context: Context) {

    private val keyAlias = "mastgAttestationKey"

    fun mastgTest(): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (keyStore.containsAlias(keyAlias)) {
            keyStore.deleteEntry(keyAlias)
        }

        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )

        // FAIL: [MASTG-TEST-0x01] No attestation challenge is set, so the certificate is
        // self-signed and carries no attestation.
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
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
}
