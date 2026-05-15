package org.owasp.mastestapp

import android.content.Context
import java.security.KeyStore
import java.security.MessageDigest
import java.security.Provider
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

import javax.crypto.CipherSpi
import java.security.Key
import java.security.AlgorithmParameters


class CustomProvider : Provider(
    "CustomProvider",
    1.0,
    "OWASP MAS Demo provider"
) {
    init {
        put("Cipher.AES", CustomAesCipherSpi::class.java.name)
    }
}

class CustomAesCipherSpi : CipherSpi() {

    private var mode: Int = 0
    private var key: Key? = null

    override fun engineSetMode(mode: String?) {
        // ignore for demo
    }

    override fun engineSetPadding(padding: String?) {
        // ignore for demo
    }

    override fun engineGetBlockSize(): Int = 16

    override fun engineGetOutputSize(inputLen: Int): Int = inputLen

    override fun engineGetIV(): ByteArray = ByteArray(16)
    override fun engineGetParameters(): AlgorithmParameters? {
        TODO("Not yet implemented")
    }

    override fun engineInit(opmode: Int, key: Key?, params: AlgorithmParameters?, random: SecureRandom?) {
        this.mode = opmode
        this.key = key
    }

    override fun engineInit(opmode: Int, key: Key?, params: java.security.spec.AlgorithmParameterSpec?, random: SecureRandom?) {
        this.mode = opmode
        this.key = key
    }

    override fun engineInit(opmode: Int, key: Key?, random: SecureRandom?) {
        this.mode = opmode
        this.key = key
    }

    override fun engineUpdate(input: ByteArray?, inputOffset: Int, inputLen: Int): ByteArray {
        return ByteArray(inputLen)
    }

    override fun engineUpdate(input: ByteArray?, inputOffset: Int, inputLen: Int, output: ByteArray?, outputOffset: Int): Int {
        if (output == null || input == null) return inputLen
        for (i in 0 until inputLen) {
            output[outputOffset + i] = 0
        }
        return inputLen
    }

    override fun engineDoFinal(input: ByteArray?, inputOffset: Int, inputLen: Int): ByteArray {
        return ByteArray(inputLen)
    }

    override fun engineDoFinal(input: ByteArray?, inputOffset: Int, inputLen: Int, output: ByteArray?, outputOffset: Int): Int {
        if (output == null || input == null) return inputLen
        for (i in 0 until inputLen) {
            output[outputOffset + i] = 0
        }
        return inputLen
    }
}

class MastgTest (private val context: Context){

    private fun ByteArray.toHex(): String {
        val sb = StringBuilder(size * 2)
        for (b in this) {
            val v = b.toInt() and 0xff
            if (v < 16) sb.append('0')
            sb.append(v.toString(16))
        }
        return sb.toString()
    }

    fun mastgTest(): String {
        val results = StringBuilder()

        Security.addProvider(CustomProvider())

        // PASS: AndroidKeyStore is allowed and MUST be there as provider for KeyStore
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        results.append("KeyStore provider: ${keyStore.provider.name}\n")


        // PASS: Using default provider for KeyGenerator
        val keyGen = KeyGenerator.getInstance("AES")
        results.append("Default KeyGenerator provider: ${keyGen.provider.name}\n")

        // PASS: Using default provider for MessageDigest
        val digest = MessageDigest.getInstance("SHA-256")
        results.append("Default MessageDigest provider: ${digest.provider.name}\n")

        // PASS: Using default provider (AndroidOpenSSL/Conscrypt) - no provider specified
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        results.append("Default Cipher provider: ${cipher.provider.name}\n")

        // FAIL: Explicitly specifying BouncyCastle provider
        val cipherBC = Cipher.getInstance("AES/GCM/NoPadding", "BC")
        results.append("Default Cipher provider: ${cipherBC.provider.name}\n")

        val keyBytes = ByteArray(16)
        SecureRandom().nextBytes(keyBytes)
        val key = SecretKeySpec(keyBytes, "AES")

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val gcmSpec = GCMParameterSpec(128, iv)

        cipherBC.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
        val plaintext = "Hello MAS".toByteArray(Charsets.UTF_8)
        val ciphertext = cipherBC.doFinal(plaintext)
        results.append("\n\nCiphertext: ${ciphertext.toHex()}\n")

        // FAIL: Explicitly specifying a custom provider
        val cipherCustom = Cipher.getInstance("AES/CBC/PKCS5Padding", "CustomProvider")

        cipherCustom.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
        val plaintext2 = "Hello MAS".toByteArray(Charsets.UTF_8)
        val ciphertext2 = cipherCustom.doFinal(plaintext2)
        results.append("\n\nCiphertext: ${ciphertext2.toHex()}\n\n---------------\n")

        val builder = java.lang.StringBuilder()
        for (provider in Security.getProviders()) {
            builder.append("provider: ${provider.name} ${provider.version} (${provider.info})\n")
        }
        val providers = builder.toString()

        results.append("\n\nProvider list: ${providers}\n")

        return results.toString()
    }

}