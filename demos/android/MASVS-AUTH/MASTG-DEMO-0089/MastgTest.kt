package org.owasp.mastestapp

import android.content.Context
import android.hardware.biometrics.BiometricManager.Authenticators.*
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.concurrent.CountDownLatch

// SUMMARY: This sample demonstrates insecure biometric authentication that allows fallback to device credentials (PIN, pattern, password).

class MastgTest(private val context: Context) {

    // Run in background thread - we'll post UI operations to main thread
    val shouldRunInMainThread = false

    private val mainHandler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.R)
    fun mastgTest(): String {
        val results = DemoResults("0083")

        // Test 1: DEVICE_CREDENTIAL - This FAILS the security test
        // Allows PIN/pattern/password which is less secure than biometrics
        val latch1 = CountDownLatch(1)
        var authResult1: String? = null

        // FAIL: [MASTG-TEST-0326] Using DEVICE_CREDENTIAL allows fallback to PIN/pattern/password
        val prompt1 = BiometricPrompt.Builder(context)
            .setTitle("Test 1: Device Credential")
            .setSubtitle("Using DEVICE_CREDENTIAL (Security: FAIL)")
            .setDescription("This allows also PIN/pattern/password authentication")
            .setAllowedAuthenticators(
                BIOMETRIC_STRONG or
                DEVICE_CREDENTIAL
            )
            .setDeviceCredentialAllowed(true)
            .build()

        // Post authenticate to main thread
        mainHandler.post {
            prompt1.authenticate(
                CancellationSignal(),
                context.mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        Log.d("MASTG-TEST", "DEVICE_CREDENTIAL authentication succeeded")
                        authResult1 = "User can authenticate with DEVICE_CREDENTIAL (PIN/pattern/password)"
                        results.add(
                            Status.FAIL,
                            "$authResult1. " +
                                    "\n🔓 AUTH - Success!\n" +
                                    "⚠️ Allows also PIN/Pattern/Password\n" +
                                    "⚠️ Uses DEVICE_CREDENTIAL fallback\n"
                        )
                        latch1.countDown()
                    }

                    override fun onAuthenticationFailed() {
                        Log.d("MASTG-TEST", "DEVICE_CREDENTIAL authentication failed")
                        authResult1 = "Authentication attempt failed"
                        results.add(
                            Status.FAIL,
                            "$authResult1. " +
                                    "\n⚠️ AUTH - Failed\n" +
                                    "⚠️ Allows PIN/Pattern/Password\n" +
                                    "⚠️ Uses DEVICE_CREDENTIAL fallback\n"
                        )
                        latch1.countDown()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.d("MASTG-TEST", "DEVICE_CREDENTIAL authentication error: $errString")
                        authResult1 = "Authentication error: $errString (code: $errorCode)"
                        results.add(
                            Status.ERROR,
                            "$authResult1. " +
                                    "\n⚠️ AUTH - Error\n" +
                                    "⚠️ Allows PIN/Pattern/Password\n" +
                                    "⚠️ Uses DEVICE_CREDENTIAL fallback\n"
                        )
                        latch1.countDown()
                    }
                }
            )
        }

        // Wait for first authentication to complete (background thread waits, main thread is free)
        latch1.await()


        // Test 2: BIOMETRIC_STRONG - This PASSES the security test
        // Only allows Class 3 biometrics (fingerprint, face with depth)
        val latch2 = CountDownLatch(1)
        var authResult2: String? = null

        // PASS: [MASTG-TEST-0326] Using BIOMETRIC_STRONG only requires biometric authentication
        val prompt2 = BiometricPrompt.Builder(context)
            .setTitle("Test 2: Biometric Strong")
            .setSubtitle("Using BIOMETRIC_STRONG (Security: PASS)")
            .setDescription("This only allows Class 3 biometrics")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButton("Cancel", context.mainExecutor) { _, _ ->
                Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication cancelled")
                authResult2 = "User cancelled authentication"
                latch2.countDown()
            }
            .build()

        // Post authenticate to main thread
        mainHandler.post {
            prompt2.authenticate(
                CancellationSignal(),
                context.mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication succeeded")
                        authResult2 = "User authenticated with BIOMETRIC_STRONG"
                        results.add(
                            Status.PASS,
                            "$authResult2. " +
                                    "\n🔓 AUTH - Success!\n" +
                                    "✅ Allows only Strong Biometric\n" +
                                    "\nThis configuration is secure because it only allows Class 3 biometrics."
                        )
                        latch2.countDown()
                    }

                    override fun onAuthenticationFailed() {
                        Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication failed")
                        authResult2 = "Authentication attempt failed (biometric not recognized)"
                        results.add(
                            Status.FAIL,
                            "$authResult2. " +
                                    "\n⚠️ AUTH - Failed\n"
                        )
                        latch2.countDown()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication error: $errString")
                        authResult2 = "Authentication error: $errString (code: $errorCode)"
                        results.add(
                            Status.ERROR,
                            "$authResult2. " +
                                    "\n⚠️ AUTH - Error\n"
                        )
                        latch2.countDown()
                    }
                }
            )
        }

        // Wait for second authentication to complete
        latch2.await()

        return results.toJson()
    }
}
