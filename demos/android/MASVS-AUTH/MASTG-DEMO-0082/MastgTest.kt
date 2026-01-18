package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class MastgTest (private val context: Context){

    val shouldRunInMainThread = true
    private var resultCallback: ((String) -> Unit)? = null
    private var results = StringBuilder()
    private var encryptedToken: ByteArray? = null
    private var encryptionIv: ByteArray? = null

    companion object {
        private const val KEY_NAME = "biometric_key"
        private const val SECRET_TOKEN = "MySecretToken123"
    }

    fun mastgTest(onResult: (String) -> Unit): String {
        this.resultCallback = onResult
        this.results = StringBuilder()

        if (context !is FragmentActivity) {
            return "Error: Context is not a FragmentActivity"
        }

        // Check if biometrics are available
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                    Log.d("MastgTest", "Biometrics available")
            }
            else -> {
                return "Error: Biometric authentication not available on this device"
            }
        }

        // Set initial message
        val initialMessage = "🔐 Biometric Authentication Demo\n\n" +
                           "Testing two implementations:\n" +
                           "1. With DEVICE_CREDENTIAL fallback\n" +
                           "2. With CryptoObject (encrypt/decrypt)\n\n" +
                           "Authenticating...\n\n"

        results.append(initialMessage)

        // Start the first biometric prompt
        showFallbackBiometricPrompt()

        return results.toString()
    }

    private fun showFallbackBiometricPrompt() {
        val executor: Executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val authType = when (result.authenticationType) {
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> "biometric"
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> "device credential"
                        else -> "unknown"
                    }

                    results.append("🔓 AUTH - Success!\n")
                    results.append("✅  Authenticated with: $authType\n")
                    results.append("⚠️ Uses DEVICE_CREDENTIAL fallback\n")
                    results.append("⚠️ Allows PIN/Pattern/Password\n")
                    results.append("✅ Retrieved token: $SECRET_TOKEN\n\n")

                    updateUI()

                    // Wait 2 seconds, then show crypto-based prompt
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        results.append("Starting 2nd authentication with CryptoObject...\n\n")
                        updateUI()
                        showCryptoBasedPrompt()
                    }, 2000)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    results.append("🔓  AUTH - Error: $errString\n\n")
                    updateUI()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    results.append("🔓  AUTH - Failed\n\n")
                    updateUI()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("🔓 1st Biometric Auth")
            .setSubtitle("Allows device credentials")
            .setDescription("This implementation uses DEVICE_CREDENTIAL")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setDeviceCredentialAllowed(true)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showCryptoBasedPrompt() {
        try {
            val secretKey = getSecretKey()

            // Create cipher for encryption
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            // Create BiometricPrompt with CryptoObject
            val executor: Executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        try {
                            // Encrypt the token using the authenticated cipher
                            val authenticatedCipher = result.cryptoObject?.cipher
                            if (authenticatedCipher != null) {
                                encryptedToken = authenticatedCipher.doFinal(SECRET_TOKEN.toByteArray())
                                encryptionIv = authenticatedCipher.iv
                                val encryptedBase64 = Base64.encodeToString(encryptedToken, Base64.NO_WRAP)

                                results.append("🔒 CRYPTO AUTH - Success!\n")
                                results.append("✅  Biometric-only authentication\n")
                                results.append("✅  Uses CryptoObject for encryption\n")
                                results.append("⚠️  Confirmation not required\n")
                                results.append("✅  Token encrypted: ${encryptedBase64.take(20)}...\n\n")

                                updateUI()

                                // Wait 2 seconds, then decrypt
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    results.append("Now decrypting the token...\n\n")
                                    updateUI()
                                    decryptToken()
                                }, 2000)
                            } else {
                                results.append("🔒 CRYPTO AUTH - Error: CryptoObject cipher is null\n")
                                updateUI()
                            }
                        } catch (e: Exception) {
                            results.append("🔒 CRYPTO AUTH - Encryption error: ${e.message}\n")
                            updateUI()
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        results.append("🔒 CRYPTO AUTH - Error: $errString\n")
                        updateUI()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        results.append("🔒 CRYPTO AUTH - Failed\n")
                        updateUI()
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("🔒 Crypto-Based Auth")
                .setSubtitle("Uses CryptoObject")
                .setDescription("Encrypting token: $SECRET_TOKEN")
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(false)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()

            // Authenticate with CryptoObject
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))

        } catch (e: Exception) {
            results.append("🔒 CRYPTO AUTH - Setup error: ${e.message}\n")
            updateUI()
        }
    }

    private fun decryptToken() {
        val iv = encryptionIv
        if (iv == null) {
            results.append("🔓 DECRYPTION - Error: IV is null\n")
            updateUI()
            return
        }

        try {
            val secretKey = getSecretKey()
            val cipher = getCipher()
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

            val executor: Executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        try {
                            val authenticatedCipher = result.cryptoObject?.cipher
                            if (authenticatedCipher != null && encryptedToken != null) {
                                val decryptedBytes = authenticatedCipher.doFinal(encryptedToken)
                                val decryptedToken = String(decryptedBytes)

                                results.append("🔓 DECRYPTION - Success!\n")
                                results.append("⚠️  Confirmation not required\n")
                                results.append("✅  Token decrypted: $decryptedToken\n")
                                results.append("✅  Original token: $SECRET_TOKEN\n")
                                results.append("✅  Match: ${decryptedToken == SECRET_TOKEN}\n")

                                updateUI()
                            } else {
                                results.append("🔓 DECRYPTION - Error: Cipher or encrypted data is null\n")
                                updateUI()
                            }
                        } catch (e: Exception) {
                            results.append("🔓 DECRYPTION - Error: ${e.message}\n")
                            updateUI()
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        results.append("🔓 DECRYPTION - Error: $errString\n")
                        updateUI()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        results.append("🔓 DECRYPTION - Failed\n")
                        updateUI()
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("🔓 Decrypt Token")
                .setSubtitle("Authenticate to decrypt")
                .setDescription("Decrypting the encrypted token")
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(false)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()

            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))

        } catch (e: Exception) {
            results.append("🔓 DECRYPTION - Setup error: ${e.message}\n")
            updateUI()
        }
    }

    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(false)
            .setUserAuthenticationValidityDurationSeconds(86400)
            .setInvalidatedByBiometricEnrollment(false)
            .setUserAuthenticationParameters(86400,KeyProperties.AUTH_BIOMETRIC_STRONG or
                    KeyProperties.AUTH_DEVICE_CREDENTIAL)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        return if (keyStore.containsAlias(KEY_NAME)) {
            keyStore.getKey(KEY_NAME, null) as SecretKey
        } else {
            generateSecretKey()
        }
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_CBC + "/" +
            KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    private fun updateUI() {
        resultCallback?.invoke(results.toString())
    }
}
