---
title: Sensitive Action Exposed Through an Unprotected Service
platform: android
id: MASTG-DEMO-0129
code: [kotlin]
test: MASTG-TEST-0365
kind: fail
---

## Sample

The sample implements a small password vault. Tapping **Start** opens `VaultActivity`, which displays the password currently stored in the app (`originalPass123` on first run). The app also declares `AuthService`, a started service that reads a new password from the intent extras passed to `onStartCommand` and writes it to shared preferences. `AuthService` is declared as exported in the `AndroidManifest.xml` with no `android:permission`, so external callers can start it directly with an explicit intent and reset the password. Tapping **Refresh** in `VaultActivity` then shows the new value.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0161 to list the exported services and their associated `android:permission` by running `run.sh`.

The `run.sh` script lists the exported services declared in the reverse-engineered manifest.

{{ run.sh }}

## Observation

The output reveals the exported services and their associated permissions:

{{ output.txt }}

## Evaluation

The test case fails because `AuthService` exposes a security-relevant operation (changing the vault password) and is exported (`android:exported="true"`) without any permission protection. Because `AuthService` is exported and unprotected, external callers that can address the component can start it directly and overwrite the password.

The service changes the stored password from an intent extra in `onStartCommand` without enforcing any caller permission:

```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val newPassword = intent?.getStringExtra(KEY_PASSWORD)
    if (newPassword != null) {
        applicationContext.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
            .edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
    }
    return START_NOT_STICKY
}
```

`VaultActivity` does not protect the underlying exported service. Access control must be enforced at the `AuthService` boundary.

### Confirm the Exposure

Use @MASTG-TECH-0161 with `adb` to start `AuthService` directly and pass a new password as an intent extra:

1. Tap **Start** and note the current password shown in `VaultActivity` (`originalPass123`).
2. Start the exported service with a new password:

    ```bash
    adb shell am startservice -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$AuthService' --es org.owasp.mastestapp.PASSWORD hacked123

    Starting service: Intent { cmp=org.owasp.mastestapp/.MastgTest$AuthService (has extras) }
    ```

3. Return to the app and tap **Refresh**. The vault password now shows `hacked123`, confirming that an external caller changed it through the exported service.

## Fix

There are two ways to fix this, and the right choice depends on whether `AuthService` needs to accept commands from external apps at all.

**Option 1: Set `android:exported="false"` (recommended for most apps)**

If `AuthService` has no legitimate reason to be started by another app, prevent external apps from reaching it:

```xml
<service
    android:name="org.owasp.mastestapp.MastgTest$AuthService"
    android:exported="false" />
```

Trying to start `AuthService` again with `adb` after this change will fail with an error, confirming that the service is no longer reachable from outside the app:

```bash
adb shell am startservice -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$AuthService' --es org.owasp.mastestapp.PASSWORD hacked123
Starting service: Intent { cmp=org.owasp.mastestapp/.MastgTest$AuthService (has extras) }
Error: Requires permission not exported from uid 10225
```

This is the right choice for the vast majority of services that manage internal state, such as credentials, session tokens, or sync state, that no external app should be able to influence. The OS will reject external `startService` and `bindService` calls before they reach the service entry points.

**Option 2: Keep `android:exported="true"` but enforce a `android:permission`**

If the service must be reachable by a trusted partner app (for example, a separate authenticator app from the same developer), you can keep it exported but gate access with a custom signature-level permission:

```xml
<permission
    android:name="org.owasp.mastestapp.USE_AUTH_SERVICE"
    android:protectionLevel="signature" />

<service
    android:name="org.owasp.mastestapp.MastgTest$AuthService"
    android:exported="true"
    android:permission="org.owasp.mastestapp.USE_AUTH_SERVICE" />
```

With `protectionLevel="signature"`, only apps signed with the same certificate are granted the permission automatically. A real-world example is an enterprise MDM agent that exposes a configuration service to a companion management app. Both are signed with the enterprise certificate, so only the management app can send commands, while any third-party app is rejected by the OS before `onStartCommand` is called.

This permission-based fix only resolves the finding if the permission cannot be obtained by untrusted apps. If the service were protected by a broadly grantable permission, such as a custom permission with `normal` or `dangerous` protection level, the demo would still fail because untrusted apps could still obtain the permission and start or bind to the service. See @MASTG-KNOW-0017 for Android permission protection levels.

Trying to start `AuthService` again with `adb` after this change will fail with a different error, confirming that the service is still exported but now requires a permission that the calling app does not have:

```bash
adb shell am startservice -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$AuthService' --es org.owasp.mastestapp.PASSWORD hacked123
Starting service: Intent { cmp=org.owasp.mastestapp/.MastgTest$AuthService (has extras) }
Error: Requires permission org.owasp.mastestapp.USE_AUTH_SERVICE
```
