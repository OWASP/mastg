package org.owasp.mastestapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NativeFlowTest {
    @Test fun encryptAndRecoverTwice() {
        val demo = MastgTest(InstrumentationRegistry.getInstrumentation().targetContext)
        val first = demo.mastgTest()
        val second = demo.mastgTest()
        val value = "Native decrypted: sk-OWASP-MAS-SuperSecretNativeKey-1234567890"
        assertTrue(first, first.contains(value))
        assertTrue(second, second.contains(value))
        assertNotEquals(first.substringAfter("Native encrypted: ").substringBefore('\n'),
            second.substringAfter("Native encrypted: ").substringBefore('\n'))
    }
}
