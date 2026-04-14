---
platform: android
title: Local Storage for Input Validation with semgrep
id: MASTG-DEMO-0101
code: [kotlin]
test: MASTG-TEST-0338
---

## Sample

The sample implements a small role based demo using `SharedPreferences`. On the first run, it initializes two entries, `user_role_insecure` and `user_role_secure`, both with the value `user`. The secure entry is stored together with an HMAC. On later runs, the app reads both values back through `loadData(...)` and uses them in a security-relevant decision.

The important detail is that `loadData(...)` has two modes. When called with `useHmac = false`, it returns the value loaded from `SharedPreferences` directly. When called with `useHmac = true`, it loads the companion HMAC, recomputes the HMAC for the stored value, and only returns the value if both match. The app then compares the loaded role with `admin` to demonstrate whether tampering succeeded.

App behavior:

- A first click on **Start** triggers the setup and display the stored values being both `user`.
- A second click triggers the check, which without any tampering shows:

    ```txt
    ✅ Insecure value unchanged.
    ✅ Secure value unchanged.
    ```

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-local-storage-input-validation.yml }}

{{ run.sh }}

## Observation

The rule reports two `SharedPreferences.getString(...)` reads inside `loadData(...)` and other matches related to nearby operations, which are useful as context.

{{ output.txt }}

## Evaluation

The test case fails because at least one security relevant local storage path loads and trusts data without validating its integrity and authenticity.

The output is a good starting point for analysis, not the conclusion. To determine whether the test fails, we must inspect how the loaded values are handled after they are read.

### Failing case: data loaded and trusted without integrity validation

Reverse engineering `mastgTest()` shows that one role is loaded through:

```java
String insecureRole = loadPlain(KEY_ROLE_INSECURE, "error");
```

The implementation of `loadPlain(...)` reads from `SharedPreferences` and returns the value directly:

```java
private final String loadPlain(String key, String defaultValue) {
    String string = prefs().getString(key, defaultValue);
    return string == null ? defaultValue : string;
}
```

This means that the role stored under `user_role_insecure` is accepted without any integrity or authenticity validation. Because the returned role is then used in the demo's security relevant result logic, this path fails the test.

You can demo this by editing the shared preferences manually and re-launching the app:

```sh
adb shell "am force-stop org.owasp.mastestapp"
adb shell "sed -i 's#>user</#>admin</#g' /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml"
adb shell "cat /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml"
adb shell monkey -p org.owasp.mastestapp -c android.intent.category.LAUNCHER 1
```

Click **Start** and you'll see:

```txt
❌ Insecure check bypassed.
```

### Passing case: data validated with HMAC before use

The second role is loaded through:

```java
String secureRole = loadProtected(KEY_ROLE_SECURE, TAMPERING_DETECTED);
```

Here, the app loads the stored HMAC, recomputes the HMAC over the stored value, and compares both before returning the value:

```java
private final String loadProtected(String key, String defaultValue) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
    String storedHmac;
    String value = prefs().getString(key, null);
    if (value == null || (storedHmac = prefs().getString(key + "_hmac", null)) == null) {
        return defaultValue;
    }
    String calculatedHmac = calculateHmac(value);
    return Intrinsics.areEqual(storedHmac, calculatedHmac) ? value : defaultValue;
}
```

So for this path, the value is **not trusted** just because it was read from local storage. It is first checked for integrity, and if the check fails the method returns the default value instead. Under the scope of this test, this path passes because the app does perform an integrity check before using the loaded value.

You can demo this by editing the shared preferences manually and re-launching the app:

```sh
adb shell "am force-stop org.owasp.mastestapp"
adb shell "sed -i 's#>user</#>admin</#g' /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml"
adb shell "cat /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml"
adb shell monkey -p org.owasp.mastestapp -c android.intent.category.LAUNCHER 1
```

Click **Start** and you'll see:

```java
"✅ Secure check detected tampering."
```

### Final Note

Even though this demo includes a second issue about the HMAC key being hardcoded in the app, that issue is covered by a different test. Still, it is useful to understand how the protected path could be exploited in practice. An attacker who reverse engineers the app can recover the hardcoded key, compute a valid HMAC for a forged value (such as `admin`), and update the stored role and HMAC to make the integrity check succeed.

The hardcoded key is visible here:

```java
private static final String SECRET_KEY = "this-is-a-very-secret-key-for-the-demo";
```

The forged HMAC for `admin` can be computed with:

```bash
python3 -c 'import hmac,hashlib; print(hmac.new(b"this-is-a-very-secret-key-for-the-demo", b"admin", hashlib.sha256).hexdigest())'
```

And then run:

```sh
adb shell "am force-stop org.owasp.mastestapp"
adb shell "sed -i \"s#<string name=\\\"user_role_secure\\\">user</string>#<string name=\\\"user_role_secure\\\">admin</string>#g\" /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml"
adb shell "sed -i \"s#<string name=\\\"user_role_secure_hmac\\\">[0-9a-f]*</string>#<string name=\\\"user_role_secure_hmac\\\">3e578c851ac37cb66033471a49585bedbb4dd5a3b2b2240f7ff6c8c2da993635</string>#g\" /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml"
adb shell monkey -p org.owasp.mastestapp -c android.intent.category.LAUNCHER 1
```

Click **Start** and you'll see:

```txt
⚠️ Secure check bypassed with forged HMAC.
```
