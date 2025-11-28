---
platform: android
title: Using KeyGenParameterSpec with a Broken ECB Block Mode
id: MASTG-DEMO-0058
code: [kotlin]
test: MASTG-TEST-0232
---

### Sample

This code demonstrates the risks of using AES in ECB mode (which is a broken mode of operation) using three scenarios:

1. Importing a raw AES key into AndroidKeyStore with the purpose "decrypt" and mode "ECB"
2. Importing a raw AES key into AndroidKeyStore with the purpose "encrypt" and mode "ECB"
3. Generating an AES key in AndroidKeyStore with the purpose "encrypt" or "decrypt" and mode "ECB"

Current versions of Android prohibit the use of keys with ECB in some cases. For example, it is possible to use such a key for decryption but not to encrypt data by default, unless randomized encryption is explicitly disabled (bad practice).

{{ MastgTest.kt }}

When executing the code, you will see the following results for each of the three scenarios:

1. Decryption succeeds because that's always allowed.
2. Encryption succeeds. The import succeeds in this case because we explicitly disable randomized encryption (bad practice). Otherwise, `KeyStore.setEntry` would fail with an error similar to the one for scenario 3.
3. Encryption cannot even happen because the generation fails (`KeyGenerator.init` specifically) due to randomized encryption not being disabled. The error says `"Randomized encryption (IND-CPA) required but may be violated by block mode: ECB. See android.security.keystore.KeyGenParameterSpec documentation"`.

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

These are the relevant methods we are hooking to detect the use of ECB and whether randomized encryption is disabled:

- Setting block modes:
    - [`KeyGenParameterSpec.Builder#setBlockModes(...)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setBlockModes(java.lang.String[]))
    - [`KeyProtection.Builder#setBlockModes(...)`](https://developer.android.com/reference/android/security/keystore/KeyProtection.Builder#setBlockModes(java.lang.String[])).
- Enabling/disabling randomized encryption:
    - [`KeyGenParameterSpec.Builder#setRandomizedEncryptionRequired`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setRandomizedEncryptionRequired(boolean))
    - [`KeyProtection.Builder#setRandomizedEncryptionRequired`](https://developer.android.com/reference/android/security/keystore/KeyProtection.Builder#setRandomizedEncryptionRequired(boolean))

{{ hooks.js # run.sh }}

### Observation

The output shows all instances of block modes that were found at runtime. A backtrace is also provided to help identify the location in the code. If randomized encryption is disabled, that is also indicated in the output.

{{ output.json }}

### Evaluation

The test fails because the `KeyGenParameterSpec.Builder#setBlockModes(...)` and `KeyProtection.Builder#setBlockModes(...)` methods have been called with ECB.

{{ evaluation.txt # evaluate.sh }}

Regardless of whether the encryption succeeds or not, ECB should never be used in security-sensitive apps. Also, being present in the app may indicate issues in other parts of the app ecosystem (e.g., backend services).
