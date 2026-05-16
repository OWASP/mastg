package org.owasp.mastestapp

import android.content.Context
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.cert.X509Certificate
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

/**
 * A utility class to demonstrate and test RSA key management, encryption, and signing
 * using the Android Keystore system.
 */
class MastgTest(private val context: Context) {

    // --- Keystore and Algorithm Constants ---
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val KEY_ALIAS = "MultiPurposeKey"

    private val ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
    private val BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
    private val PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1

    // Combined transformation string for Cipher
    private val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    /**
     * Executes the main test flow: key generation, encryption/decryption, and signing/verification.
     * @return A log string detailing the test results.
     */
    fun mastgTest(): String {
        // Ensure key is generated and stored in Keystore
        generateKey()

        val data = "secret".toByteArray()
        var logs = "Original Data: '${data.toString(Charsets.UTF_8)}'\n\n"

        // 1. Encryption and Decryption Test
        val encryptedData = encrypt(data)
        val encryptedDataPreview = encryptedData?.toString(Charsets.UTF_8)?.take(7)

        if (encryptedData == null || encryptedDataPreview == null) {
            return "FAILURE - Encryption failed."
        }
        logs += "Encrypted data preview: $encryptedDataPreview...\n"

        val decryptedData = decrypt(encryptedData)
        logs += "Decrypted data: ${decryptedData?.toString(Charsets.UTF_8)}\n\n"


        // 2. Signing and Verification Test (using original data)
        val signature = sign(data)
        val signaturePreview = signature.toString(Charsets.UTF_8).take(10)

        logs += "Signing data...\n"
        logs += "Signature preview: $signaturePreview...\n"
        logs += "Verifying signature...\n"

        logs += if (verify(data, signature)) {
            "Verification result: Signature is correct\n\n"
        } else {
            "Verification result: Signature is invalid\n\n"
        }

        logs += "SUCCESS!!"
        return logs
    }

    /**
     * Generates an RSA KeyPair in the Android Keystore with multiple purposes.
     * Deletes the existing key if an alias conflict exists.
     */
    fun generateKey(): KeyPair {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        // Clean up previous entry
        if (ks.containsAlias(KEY_ALIAS)) {
            ks.deleteEntry(KEY_ALIAS)
        }

        // Define key validity period
        val startDate = GregorianCalendar()
        val endDate = GregorianCalendar().apply { add(Calendar.YEAR, 1) }

        // Initialize key generator for RSA
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)

        // Build key generation specification
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY or
                    KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
        ).run {
            setCertificateSerialNumber(BigInteger.valueOf(777))
            setCertificateSubject(X500Principal("CN=$KEY_ALIAS"))
            setDigests(KeyProperties.DIGEST_SHA256)
            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            setCertificateNotBefore(startDate.time)
            setCertificateNotAfter(endDate.time)
            build()
        }

        // Generate the key pair
        keyPairGenerator.initialize(parameterSpec)
        return keyPairGenerator.genKeyPair()
    }

    /**
     * Encrypts the provided data using the public key from the Keystore.
     * @param data The plaintext data to encrypt.
     * @return The encrypted byte array, or null on failure.
     */
    fun encrypt(data: ByteArray): ByteArray? {
        return try {
            val cert = getCertificate()!!
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, cert.publicKey)
            cipher.doFinal(data)
        } catch (e: Exception) {
            // Log exceptions during encryption
            e.printStackTrace()
            null
        }
    }

    /**
     * Decrypts the provided data using the private key from the Keystore.
     * @param data The encrypted byte array.
     * @return The decrypted byte array, or null on failure.
     */
    fun decrypt(data: ByteArray?): ByteArray? {
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val privateKey = ks.getKey(KEY_ALIAS, null) as PrivateKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(data)
        } catch (e: Exception) {
            // Log exceptions during decryption
            e.printStackTrace()
            null
        }
    }

    /**
     * Signs the provided data using the private key from the Keystore.
     * @param data The data to be signed.
     * @return The signature byte array.
     */
    fun sign(data: ByteArray): ByteArray {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val privateKey = ks.getKey(KEY_ALIAS, null) as PrivateKey
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initSign(privateKey)
        sig.update(data)
        return sig.sign()
    }

    /**
     * Verifies a signature against the provided data using the public key (Certificate).
     * @param data The original data.
     * @param signature The signature to verify.
     * @return True if the signature is valid, false otherwise.
     */
    fun verify(data: ByteArray, signature: ByteArray): Boolean {
        val sig = Signature.getInstance("SHA256withRSA")
        // Initialize with public key for verification
        sig.initVerify(getCertificate()?.publicKey)
        sig.update(data)
        return sig.verify(signature)
    }

    /**
     * Retrieves the X.509 Certificate (containing the public key) from the Keystore.
     * @return The certificate, or null if not found.
     */
    private fun getCertificate(): X509Certificate? {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        return ks.getCertificate(KEY_ALIAS) as? X509Certificate
    }
}
