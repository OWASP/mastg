---
platform: android
title: App Writing Sensitive Data to Sandbox using SharedPreferences
id: MASTG-DEMO-0059
code: [kotlin]
test: MASTG-TEST-0207
---

### Sample

The code snippet below shows sample code which stores sensitive data using `SharedPreferences`. It stores sensitive data using `String` and `StringSet`.

{{ MastgTest.kt }}

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ hooks.js # run.sh }}

### Observation

The output shows all instances of strings written via `SharedPreferences` that were found at runtime. A backtrace is also provided to help identify the location in the code.

{{ output.json }}

### Evaluation

In output.json we can identify several entries that use the `SharedPreferences` API write strings to the app's local sandbox. In this case to `/data/data/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml`:

- `putString` is used to write an unencrypted `UnencryptedGitHubToken` of value `ghp_1234567890a...`
- `putString` is used to write an encrypted `EncryptedAwsKey` of value `V1QyXhGV88RQLmMjoTLLl...`
- `putStringSet` is used to write an unencrypted `UnencryptedPreSharedKeys` set with values `MIIEvAIBADAN...` and `gJXS9EwpuzK8...`

We can use the values and try to trace them back to crypto method calls and check if they are encrypted. For example, let's analyze the `EncryptedAwsKey` of value `V1QyXhGV88RQLmMjoTLLl...`:

- `V1QyXhGV88RQLmMjoTLLl...` is the return value of `Base64.encodeToString` for the input `0x5754325e1195f3c45...`
- `0xa132cb95022985be` is the return value of `Cipher.doFinal` for the input `AKIAIOSFODNN7EXAMPLE`

However, we cannot find any calls to `Base64.encodeToString` or `Cipher.***` for the `preSharedKeys` values written by `putStringSet` (`MIIEvAIBADAN...` and `gJXS9EwpuzK8...`).

You can confirm this by reverse engineering the app and inspecting the code. Inspect the `stackTrace` of the `putString` and `putStringSet` entries, then go to the corresponding locations in the code. For example, go to the `org.owasp.mastestapp.MastgTest.mastgTest` method and try to trace back the input parameters to determine whether they are encrypted.

The test **fails** due because we found some entries that aren't encrypted.

Any data in the app sandbox can be extracted using backups or root access on a compromised phone. For example, run the following command:

```sh
adb shell cat /data/data/org.owasp.mastestapp/shared_prefs/MasSharedPref_Sensitive_Data.xml
```

Which returns:

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="EncryptedAwsKey">V1QyXhGV88RQLmMjoTLLlQIphb6SKf4CBqx+PqhH/TTPFtCh9RPTAYezWW5RPhPP&#10;    </string>
    <set name="UnencryptedPreSharedKeys">
        <string>gJXS9EwpuzK8U1TOgfplwfKEVngCE2D5FNBQWvNmuHHbigmTCabsA=</string>
        <string>MIIEvAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALfX7kbfFv3pc3JjOHQ=</string>
    </set>
    <string name="UnencryptedGitHubToken">ghp_1234567890abcdefghijklmnOPQRSTUV</string>
</map>
```

All entries that aren't encrypted can be leveraged by an attacker.
