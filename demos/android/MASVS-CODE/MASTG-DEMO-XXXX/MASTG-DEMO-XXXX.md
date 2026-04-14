---
platform: android
title: Local Storage for Input Validation with semgrep
id: MASTG-DEMO-XXXX
code: [kotlin]
test: MASTG-TEST-XXXX
---

## Sample

The sample implements a small role based demo using `SharedPreferences`. On the first run, it initializes two entries, `user_role_insecure` and `user_role_secure`, both with the value `user`. The secure entry is stored together with an HMAC. On later runs, the app reads both values back through `loadData(...)` and uses them in a security relevant decision.

The important detail is that `loadData(...)` has two modes. When called with `useHmac = false`, it returns the value loaded from `SharedPreferences` directly. When called with `useHmac = true`, it loads the companion HMAC, recomputes the HMAC for the stored value, and only returns the value if both match. The app then compares the loaded role with `admin` to demonstrate whether tampering succeeded.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-local-storage-input-validation.yml }}

{{ run.sh }}

## Observation

The rule reports two `SharedPreferences.getString(...)` reads inside `loadData(...)`:

```java
String value = prefs.getString(key, null);
String storedHmac = prefs.getString(key + "_hmac", null);
```

These findings are indicators only. They show that security relevant data is loaded from local storage, but they do not by themselves prove that the app fails the test. To determine that, we must reverse engineer the surrounding control flow and check whether the loaded value is validated before it is trusted.

The scan may also return additional matches related to equality checks or other nearby operations, depending on how broad the Semgrep patterns are. Those extra matches are useful as context, but the `SharedPreferences` reads are the primary starting point for the analysis.

{{ output.txt }}

## Evaluation

The test case fails because at least one security relevant local storage path loads and trusts data without validating its integrity and authenticity.

The output shows all places where the app reads role related data from `SharedPreferences`. That is the starting point for the analysis, not the conclusion. To determine whether the test fails, we must inspect how the loaded values are handled after they are read.

### Failing case: data loaded and trusted without integrity validation

Reverse engineering `mastgTest()` shows that one role is loaded through:

```java
String insecureRole = loadData("user_role_insecure", "error", false);
```

The third argument disables integrity checking. Looking at `loadData(...)`, when `useHmac` is `false`, the method returns the value loaded from `SharedPreferences` directly:

```java
if (useHmac) {
    ...
}
Log.d("MASTG-TEST", "Loaded data without HMAC check. Value is: " + value);
return value;
```

This means that the role stored under `user_role_insecure` is accepted without any integrity or authenticity validation. Because the returned role is then used in a security-relevant decision, namely whether the app accepts a tampered `admin` role, this path fails the test.

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
String secureRole = loadData("user_role_secure", "tampering_detected", true);
```

Here, `useHmac` is enabled. In that branch, the app loads the stored HMAC, recomputes the HMAC over the stored value, and compares both before returning the value:

```java
String storedHmac = prefs.getString(key + "_hmac", null);
if (storedHmac == null) {
    return defaultValue;
}
String calculatedHmac = calculateHmac(value);
if (Intrinsics.areEqual(storedHmac, calculatedHmac)) {
    return value;
}
return defaultValue;
```

So for this path, the value is not trusted just because it was read from local storage. It is first checked for integrity, and if the check fails the method returns the default value instead. Under the scope of this test, this path passes because the app does perform an integrity check before using the loaded value.

### Final Note

Even though this demo includes a second issue, namely that the **HMAC key is hardcoded in the app**, that is covered by a different test. Still, it is useful to understand how the protected path could be abused in practice. An attacker who reverse engineers the app can recover the hardcoded key, compute a valid HMAC for a forged value such as `admin`, and update both the stored role and the stored HMAC so that the integrity check succeeds.

For example, the forged HMAC for `admin` can be computed with:

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
