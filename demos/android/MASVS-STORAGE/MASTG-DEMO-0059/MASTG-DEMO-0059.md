---
platform: android
title: Using SharedPreferences to Write Sensitive Data Unencrypted to the App Sandbox
id: MASTG-DEMO-0059
code: [kotlin]
test: MASTG-TEST-0207
---

### Sample

The code below stores sensitive data using the `SharedPreferences` API, both with and without encryption:

- An AWS key is stored encrypted
- A GitHub token is stored unencrypted
- A set of binary pre-shared keys is stored unencrypted

When encryption is performed, it uses a securely generated key stored in the Android KeyStore.

{{ MastgTest.kt }}

When executing the code, you will be able to inspect the shared preferences file created in the app sandbox. For example, run the following command:

```sh
adb shell cat /data/data/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml
```

Which returns:

{{ MasSharedPref_Sensitive_Data.xml }}

All unencrypted entries can be leveraged by an attacker.

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

These are the relevant methods we are hooking to detect the use of `SharedPreferences` to write strings:
    - [`SharedPreferences.Editor.putString(...)`](https://developer.android.com/reference/android/content/SharedPreferences.Editor#putString(java.lang.String,%20java.lang.String))
    - [`SharedPreferences.Editor.putStringSet(...)`](https://developer.android.com/reference/android/content/SharedPreferences.Editor#putStringSet(java.lang.String,%20java.util.Set))

Our hooks also trace calls to cryptographic methods to help determine whether the written values are encrypted or not; whether the Android KeyStore is used; and whether Base64 encoding is used to convert binary data to strings:
    - [`javax.crypto.Cipher.*(...)`](https://developer.android.com/reference/javax/crypto/Cipher)
    - [`java.security.KeyStore.*(...)`](https://developer.android.com/reference/java/security/KeyStore)
    - [`javax.crypto.KeyGenerator.*(...)`](https://developer.android.com/reference/javax/crypto/KeyGenerator)
    - [`android.util.Base64.*(...)`](https://developer.android.com/reference/android/util/Base64)

{{ hooks.js # run.sh }}

### Observation

The output shows all instances of strings written via `SharedPreferences` that were found at runtime. A backtrace is also provided to help identify the corresponding locations in the code.

{{ output.json }}

### Evaluation

The test fails because secrets are written to SharedPreferences without encryption.

In `output.json` we can identify several entries that use the `SharedPreferences` API to write strings to the app's local sandbox—in this case, to `/data/data/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml`.

Determining if a string is encrypted or not, especially with crypto keys can be challenging.

#### Option 1: High level trace inspection

After slightly processing the output using `jq`, we can get a high level view of the relevant calls, which can help us identify unencrypted secrets.

{{ evaluation.txt # evaluate.sh }}

Here we can see that:

- the value `ghp_1234567890a...` is not preceded by any Cipher calls when written via `putString`.
- the value `V1QyXhGV88RQLmMjoTLLl...` has several calls to Cipher and then a `putString`.
- the set of values `MIIEvAIBADAN...` and `gJXS9EwpuzK8...` are also not preceded by any Cipher calls when written via `putStringSet`.

#### Option 2: Pattern matching

At this point you could use a secrets detection tool such as @MASTG-TOOL-0144 to try to detect any secrets present in cleartext or encoded.

```sh
cat ./output.json | gitleaks -v stdin

    ○
    │╲
    │ ○
    ○ ░
    ░    gitleaks

Finding:     "value": "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALfX7kbfFv3pc3JjOHQ=\n-...,-----BEGIN PRIVATE ...
Secret:      -----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALfX7kbfFv3pc3JjOHQ=\n-...
RuleID:      private-key
Entropy:     4.884846

Finding:     ...END PRIVATE KEY-----,-----BEGIN PRIVATE KEY-----\ngJXS9EwpuzK8U1TOgfplwfKEVngCE2D5FNBQWvNmuHHbigmTCabsA=\n-----END PRIVAT...
Secret:      -----BEGIN PRIVATE KEY-----\ngJXS9EwpuzK8U1TOgfplwfKEVngCE2D5FNBQWvNmuHHbigmTCabsA=\n-----END PRIVAT...
RuleID:      private-key
Entropy:     4.945110

Finding:     "value": "AKIAABCDEFGHIJKLMNOP
Secret:      AKIAABCDEFGHIJKLMNOP
RuleID:      aws-access-token
Entropy:     3.884184

Finding:     "value": "ghp_1234567890abcdefghijklmnopqrstuvABCD
Secret:      ghp_1234567890abcdefghijklmnopqrstuvABCD
RuleID:      github-pat
Entropy:     5.171928
```

#### Option 3: Detailed trace inspection

The provided `output.json` in this case allows you to trace the written values back to cryptographic method calls and this way find out whether they are encrypted. For example, let's analyze the `EncryptedAwsKey` with value `V1QyXhGV88RQLmMjoTLLl...`:

- `V1QyXhGV88RQLmMjoTLLl...` is the return value of `Base64.encodeToString` for the input `0x5754325e1195f3c45...`.
- `0xa132cb95022985be` is the return value of `Cipher.doFinal` for the input `AKIAABCDEFGHIJKLMNOP`.

However, we cannot find any calls to `Base64.encodeToString` or `Cipher.*` for the `preSharedKeys` values written by `putStringSet` (`MIIEvAIBADAN...` and `gJXS9EwpuzK8...`).

#### Option 4: Manual reverse engineering

You can confirm this by reverse engineering the app and inspecting the code. Review the `stackTrace` of the `putString` and `putStringSet` entries, then navigate to the corresponding locations in the code. For example, open the `org.owasp.mastestapp.MastgTest.mastgTest` method and try to trace back the input parameters to determine whether they are encrypted.
