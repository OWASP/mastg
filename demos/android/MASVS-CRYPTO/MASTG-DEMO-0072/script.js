if (Java.available) {
    Java.perform(function() {
        
        // --- Configuration Constants ---
        const CRYPTO_OP_ENCRYPT_DECRYPT = "encryption/decryption";
        const CRYPTO_OP_SIGN_VERIFY = "sign/verify";
        
        // Storage for tracking key usage: { key_toString: usage_type }
        const usedKeys = {};
      
        // --- Core Reflection Setup Function ---
        /**
         * Initializes and returns the reflected Method object for Object.toString().
         */
        function getToStringMethodRef() {
            try {
                const Object = Java.use('java.lang.Object');
                const toStringMethod = Object.class.getDeclaredMethod("toString", []);
                toStringMethod.setAccessible(true);
                return toStringMethod;
            } catch (e) {
                console.log("❌ CRITICAL SETUP ERROR: Failed to prepare toString reflection: " + e);
                return null;
            }
        }
        
        // Execute the reflection setup once at the start
        const toStringMethodRef = getToStringMethodRef();

        if (!toStringMethodRef) {
            console.log("❌ Script halted: Reflection setup failed.");
            return;
        }

        try {
            // --- Java Class References ---
            const Cipher = Java.use("javax.crypto.Cipher");
            const Signature = Java.use("java.security.Signature");
            
            // --- Helper Functions ---

            /**
             * Safely invokes the Java toString() method on the key/certificate and logs usage.
             */
            function logKeyDetails(key, certificate, cryptoOp) {
                
                // If a certificate is provided, use its PublicKey for tracking
                let trackingKey = key;
                if (certificate) {
                    try {
                        trackingKey = certificate.getPublicKey();
                        console.log("  Tracking Key: Certificate's Public Key");
                    } catch (e) {
                        console.log(`  ERROR: Failed to get Public Key from Certificate: ${e}`);
                        return;
                    }
                }

                if (trackingKey === null) {
                    console.log("  ERROR: Key object is null, skipping tracking.");
                    return;
                }
                
                let javaToStringResult = "UNKNOWN_KEY_ID";

                try {
                    // Invoke toString() using the pre-calculated reference.
                    // Pass the instance (trackingKey) and an empty array of arguments ([]).
                    javaToStringResult = toStringMethodRef.invoke(trackingKey, []);
                } catch (e) {
                    // Fallback to native pointer if reflection fails
                    try {
                        javaToStringResult = trackingKey.toPointer().toString();
                    } catch (pe) {
                        javaToStringResult = `ERROR_ID_${Date.now()}`;
                    }
                    console.log(`  WARNING: Reflection failed. Tracking ID: ${javaToStringResult}`);
                }

                console.log(`  ${cryptoOp} with key: "${javaToStringResult}"`);
                
                // --- Usage Tracking Logic ---
                const storedOp = usedKeys[javaToStringResult];

                if (storedOp !== undefined && storedOp !== cryptoOp) {
                    console.log("!!! WARNING: This key is used for multiple, conflicting purposes: " + storedOp + " and " + cryptoOp);
                } else if (storedOp === undefined) {
                    // Store the key's first detected usage
                    usedKeys[javaToStringResult] = cryptoOp;
                }
            }
            
            /**
             * Logs the current Java stack trace.
             */
            function logStackTrace() {
                console.log("  Stack Trace:");
                const exception = Java.use("java.lang.Exception").$new();
                const stackTraceElements = exception.getStackTrace();
                for (let i = 0; i < stackTraceElements.length; i++) {
                    const element = stackTraceElements[i];
                    if (i < 10) { // Limit stack depth for cleaner output
                        console.log("    " + element.toString());
                    }
                }
                console.log("  --- End Stack Trace ---");
            }


            // --- HOOKS: Cipher (Encryption/Decryption) ---

            Cipher.init.overload('int', 'java.security.Key').implementation = function(opmode, key) {
                console.log("\n🔒 *** Cipher.init(Key) HOOKED ***");
                logKeyDetails(key, null, CRYPTO_OP_ENCRYPT_DECRYPT);
                logStackTrace();
                this.init(opmode, key);
            };

            Cipher.init.overload('int', 'java.security.cert.Certificate').implementation = function(opmode, certificate) {
                console.log("\n📜 *** Cipher.init(Certificate) HOOKED ***");
                logKeyDetails(null, certificate, CRYPTO_OP_ENCRYPT_DECRYPT);
                logStackTrace();
                this.init(opmode, certificate);
            };
            
            // --- HOOKS: Signature (Sign/Verify) ---
            
            Signature.initSign.overload('java.security.PrivateKey').implementation = function(key) {
                console.log("\n✍️ *** Signature.initSign(PrivateKey) HOOKED ***");
                logKeyDetails(key, null, CRYPTO_OP_SIGN_VERIFY);
                logStackTrace();
                this.initSign(key);
            };
            
            Signature.initVerify.overload('java.security.PublicKey').implementation = function(key) {
                console.log("\n✅ *** Signature.initVerify(PublicKey) HOOKED ***");
                logKeyDetails(key, null, CRYPTO_OP_SIGN_VERIFY);
                logStackTrace();
                this.initVerify(key);
            };
            
            Signature.initVerify.overload('java.security.cert.Certificate').implementation = function(certificate) {
                console.log("\n📜 *** Signature.initVerify(Certificate) HOOKED ***");
                logKeyDetails(null, certificate, CRYPTO_OP_SIGN_VERIFY);
                logStackTrace();
                this.initVerify(certificate);
            };
            
            console.log("✅ Frida script loaded and cryptographic APIs hooked successfully!");

        } catch (e) {
            console.log("❌ Failed to load hooks (make sure target process is running): " + e);
        }
    });
} else {
    console.log("❌ Java is not available. Ensure Frida is attached to a JVM process.");
}
