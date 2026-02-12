package org.owasp.mastestapp

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricManager.Authenticators.*
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.util.concurrent.CountDownLatch
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// SUMMARY: This sample demonstrates event-bound biometric authentication where authenticate() is called without CryptoObject, making it vulnerable to bypass.

class MastgTest(private val context: Context) {

    // Run in background thread - we'll post UI operations to main thread
    val shouldRunInMainThread = false

    private val mainHandler = Handler(Looper.getMainLooper())

    // Secret token (simulating an API key or sensitive data)
    private val secretToken = "7xK9mP2qR5tY8wZ3aB6cD0eF"

    // Keystore constants
    private val KEY_NAME = "biometric_secret_key"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"

    // Encrypted token storage
    private var encryptedToken: ByteArray? = null
    private var encryptionIv: ByteArray? = null

    private fun generateSecretKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .setInvalidatedByBiometricEnrollment(false)
                .setUserAuthenticationParameters(86400, 0)
                .setUserAuthenticationValidityDurationSeconds(86400)
                .build()
        )
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey
    }

    private fun getCipherForEncryption(): Cipher {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return cipher
    }

    private fun getCipherForDecryption(): Cipher {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, encryptionIv))
        return cipher
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun mastgTest(): String {
        val results = DemoResults("0084")

        // Test 1: INSECURE - No CryptoObject
        // This demonstrates the insecure pattern: authentication without cryptographic binding.
        // The secret token is returned directly after auth success, without using CryptoObject.
        // Also allows DEVICE_CREDENTIAL fallback (PIN/pattern/password).
        val latch1 = CountDownLatch(1)
        var authResult1: String? = null

        val prompt1 = BiometricPrompt.Builder(context)
            .setTitle("Test 1: No CryptoObject")
            .setSubtitle("Insecure: No cryptographic binding")
            .setDescription("Secret returned directly without CryptoObject protection")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(false)
            .build()

        // FAIL: [MASTG-TEST-0327] BiometricPrompt.authenticate() called without CryptoObject for sensitive operations
        mainHandler.post {
            prompt1.authenticate(
                CancellationSignal(),
                context.mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        Log.d("MASTG-TEST", "Test 1: Auth succeeded (no CryptoObject)")
                        authResult1 = "Authenticated without CryptoObject"
                        // Simply return the secret token without crypto protection
                        results.add(
                            Status.FAIL,
                            "Secret Token: $secretToken\n" +
                                    "\n🔓 AUTH - Success!\n" +
                                    "⚠️ No CryptoObject used - secret token returned directly\n" +
                                    "⚠️ Allows DEVICE_CREDENTIAL fallback\n"
                        )
                        latch1.countDown()
                    }

                    override fun onAuthenticationFailed() {
                        Log.d("MASTG-TEST", "Test 1: Auth failed (biometric not recognized)")
                        authResult1 = "Authentication attempt failed"
                        results.add(
                            Status.FAIL,
                            "$authResult1\n" +
                                    "\n⚠️ AUTH - Failed\n"
                        )
                        latch1.countDown()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.d("MASTG-TEST", "Test 1: Auth error - $errString")
                        authResult1 = "Authentication error: $errString (code: $errorCode)"
                        results.add(
                            Status.ERROR,
                            "$authResult1\n" +
                                    "\n⚠️ AUTH - Error\n"
                        )
                        latch1.countDown()
                    }
                }
            )
        }

        // Wait for Test 1 to complete
        latch1.await()


        // Test 2: SECURE - BIOMETRIC_STRONG with CryptoObject
        // This demonstrates the secure pattern: cryptographic operations bound to biometric auth.
        // The secret token is encrypted/decrypted using a key that requires BIOMETRIC_STRONG.
        // Each crypto operation (encrypt and decrypt) requires separate biometric authentication.

        // Generate AES key in Android Keystore with setUserAuthenticationRequired(true)
        generateSecretKey()

        // Step 2a: Authenticate with CryptoObject to ENCRYPT the token
        val latchEncrypt = CountDownLatch(1)
        var encryptionSucceeded = false

        val encryptCipher = getCipherForEncryption()
        val encryptCryptoObject = BiometricPrompt.CryptoObject(encryptCipher)

        val promptEncrypt = BiometricPrompt.Builder(context)
            .setTitle("Test 2a: Encrypt with CryptoObject")
            .setSubtitle("Secure: BIOMETRIC_STRONG required")
            .setDescription("Biometric required to encrypt the secret token")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButton("Cancel", context.mainExecutor) { _, _ ->
                Log.d("MASTG-TEST", "Test 2a: Encryption cancelled by user")
                latchEncrypt.countDown()
            }
            .build()

        // PASS: [MASTG-TEST-0327] BiometricPrompt.authenticate() called with CryptoObject for crypto-bound authentication
        mainHandler.post {
            promptEncrypt.authenticate(
                encryptCryptoObject,
                CancellationSignal(),
                context.mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        Log.d("MASTG-TEST", "Test 2a: Biometric auth succeeded, encrypting token")
                        try {
                            val authenticatedCipher = result.cryptoObject?.cipher
                            encryptionIv = authenticatedCipher?.iv
                            encryptedToken = authenticatedCipher?.doFinal(secretToken.toByteArray(Charsets.UTF_8))
                            encryptionSucceeded = true
                            Log.d("MASTG-TEST", "Test 2a: Token encrypted successfully with CryptoObject")
                        } catch (e: Exception) {
                            Log.e("MASTG-TEST", "Test 2a: Encryption failed - ${e.message}", e)
                            results.add(
                                Status.ERROR,
                                "Test 2a: Encryption failed: ${e.message}\n"
                            )
                        }
                        latchEncrypt.countDown()
                    }

                    override fun onAuthenticationFailed() {
                        Log.d("MASTG-TEST", "Test 2a: Biometric not recognized")
                        latchEncrypt.countDown()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.d("MASTG-TEST", "Test 2a: Auth error - $errString")
                        results.add(
                            Status.ERROR,
                            "Test 2a: Auth error: $errString (code: $errorCode)\n"
                        )
                        latchEncrypt.countDown()
                    }
                }
            )
        }

        latchEncrypt.await()

        // Step 2b: Authenticate with CryptoObject to DECRYPT the token
        if (encryptionSucceeded && encryptedToken != null && encryptionIv != null) {
            val latchDecrypt = CountDownLatch(1)

            val decryptCipher = getCipherForDecryption()
            val decryptCryptoObject = BiometricPrompt.CryptoObject(decryptCipher)

            val promptDecrypt = BiometricPrompt.Builder(context)
                .setTitle("Test 2b: Decrypt with CryptoObject")
                .setSubtitle("Secure: BIOMETRIC_STRONG required")
                .setDescription("Biometric required to decrypt the secret token")
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .setNegativeButton("Cancel", context.mainExecutor) { _, _ ->
                    Log.d("MASTG-TEST", "Test 2b: Decryption cancelled by user")
                    latchDecrypt.countDown()
                }
                .build()

            mainHandler.post {
                promptDecrypt.authenticate(
                    decryptCryptoObject,
                    CancellationSignal(),
                    context.mainExecutor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            Log.d("MASTG-TEST", "Test 2b: Biometric auth succeeded, decrypting token")
                            try {
                                val authenticatedCipher = result.cryptoObject?.cipher
                                val decryptedBytes = authenticatedCipher?.doFinal(encryptedToken)
                                val decryptedToken = decryptedBytes?.toString(Charsets.UTF_8)

                                Log.d("MASTG-TEST", "Test 2b: Token decrypted successfully with CryptoObject")
                                results.add(
                                    Status.FAIL,
                                    "Decrypted Secret Token: $decryptedToken\n" +
                                            "\n🔓 AUTH - Success!\n" +
                                            "✅ Uses CryptoObject for encryption and decryption operations\n" +
                                            "✅ BIOMETRIC_STRONG required for all crypto operations\n" +
                                            "⚠️ Key created with setUserAuthenticationRequired(false)\n" +
                                            "⚠️ Key created with setInvalidatedByBiometricEnrollment(false)\n" +

                                            "\nCrypto operations are bound to biometric authentication, but it is not implemented according to security best practices."
                                )
                            } catch (e: Exception) {
                                Log.e("MASTG-TEST", "Test 2b: Decryption failed - ${e.message}", e)
                                results.add(
                                    Status.ERROR,
                                    "Test 2b: Decryption failed: ${e.message}\n" +
                                            "\n⚠️ AUTH succeeded but decryption failed\n"
                                )
                            }
                            latchDecrypt.countDown()
                        }

                        override fun onAuthenticationFailed() {
                            Log.d("MASTG-TEST", "Test 2b: Biometric not recognized")
                            results.add(
                                Status.FAIL,
                                "Test 2b: Biometric not recognized\n" +
                                        "\n⚠️ AUTH - Failed\n"
                            )
                            latchDecrypt.countDown()
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            Log.d("MASTG-TEST", "Test 2b: Auth error - $errString")
                            results.add(
                                Status.ERROR,
                                "Test 2b: Auth error: $errString (code: $errorCode)\n" +
                                        "\n⚠️ AUTH - Error\n"
                            )
                            latchDecrypt.countDown()
                        }
                    }
                )
            }

            latchDecrypt.await()
        }

        return results.toJson()
    }
}
