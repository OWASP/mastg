---
title: Sensitive Action Exposed Through an Unprotected Manifest-Declared Broadcast Receiver
platform: android
id: MASTG-DEMO-0130
code: [kotlin]
test: MASTG-TEST-0366
kind: fail
---

## Sample

The sample implements a small password vault. Tapping **Start** opens `VaultActivity`, which displays the password currently stored in the app (`originalPass123` on first run). The app also declares `PasswordResetReceiver`, a broadcast receiver that changes the stored password from a `newpass` intent extra and logs the old password. `PasswordResetReceiver` is declared as exported in the `AndroidManifest.xml` with no `android:permission`, so external callers can send the broadcast and reset the password. Tapping **Refresh** in `VaultActivity` then shows the new value.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0162 to list the exported broadcast receivers and their associated `android:permission` by running `run.sh`.

The `run.sh` script lists the exported receivers declared in the reverse-engineered manifest.

{{ run.sh }}

## Observation

The output reveals the exported broadcast receivers and their associated permissions:

{{ output.txt }}

## Evaluation

The test case fails because `PasswordResetReceiver` performs a security-relevant action (storing a password and being able to update it) and is exported (`android:exported="true"`) without any permission protection. Because `PasswordResetReceiver` is exported and unprotected, external callers can send a broadcast to it and overwrite the password.

The `onReceive` method changes the stored password from an intent extra and discloses the old password to the log:

```kotlin
override fun onReceive(context: Context, intent: Intent) {
    val newPassword = intent.getStringExtra("newpass") ?: return
    val prefs = context.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
    val oldPassword = prefs.getString(MastgTest.KEY_PASSWORD_STORE, "")
    Log.d("MASTG-DEMO", "Password changed from $oldPassword to $newPassword")
    prefs.edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
}
```

`VaultActivity` does not protect the underlying exported broadcast receiver. Access control must be enforced at the `PasswordResetReceiver` boundary.

The output also lists `androidx.profileinstaller.ProfileInstallReceiver`. This receiver is added by the AndroidX Profile Installer library and, although exported, is protected by `android:permission="android.permission.DUMP"`, a signature/privileged permission that ordinary apps can't hold. It is development tooling and is not reported as vulnerable in this test case.

### Confirm the Exposure

You can use @MASTG-TECH-0162 with @MASTG-TOOL-0004 to deliver the broadcast and trigger the action.

1. Tap **Start** and note the current password shown in `VaultActivity` (`originalPass123`).
2. Send the broadcast, targeting the receiver explicitly so it's delivered on modern Android:

    ```bash
    adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$PasswordResetReceiver' --es newpass hacked123

    Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD flg=0x400000 cmp=org.owasp.mastestapp/.MastgTest$PasswordResetReceiver (has extras) }
    Broadcast completed: result=0
    ```

3. Return to the app and tap **Refresh**. The vault password now shows `hacked123`, confirming that an external caller changed it through the exported receiver.

The disclosed old password is also visible in the log:

```bash
adb logcat -s MASTG-DEMO
```

Output:

```bash
06-01 09:26:01.334 30881 30881 D MASTG-DEMO: Password changed from originalPass123 to hacked123
```

## Fix

There are two ways to fix this, and the right choice depends on whether `PasswordResetReceiver` needs to accept broadcasts from external apps at all.

**Option 1: Set `android:exported="false"` (recommended for most apps)**

If `PasswordResetReceiver` has no legitimate reason to receive broadcasts from another app, prevent external apps from reaching it:

```xml
<receiver
    android:name="org.owasp.mastestapp.MastgTest$PasswordResetReceiver"
    android:exported="false" />
```

Trying to send the broadcast again with `adb` after this change will not necessarily produce an error, but the password will not change and the log will not show the old password, confirming that the receiver is no longer reachable from outside the app.

```bash
adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$PasswordResetReceiver' --es newpass hacked123
Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD flg=0x400000 cmp=org.owasp.mastestapp/.MastgTest$PasswordResetReceiver (has extras) }
Broadcast completed: result=0
```

This is the right choice for the vast majority of receivers that react to internal app events, such as credential-reset or state-change broadcasts, that no external app should be able to influence.

**Option 2: Keep `android:exported="true"` but enforce a `android:permission`**

If the receiver must be reachable by a trusted partner app (for example, a companion lock-screen app from the same developer that can trigger a remote wipe or credential reset), you can keep it exported but gate access with a custom signature-level permission:

```xml
<!-- Declare the permission in the app's manifest -->
<permission
    android:name="org.owasp.mastestapp.SEND_PASSWORD_RESET"
    android:protectionLevel="signature" />

<!-- Require it on the receiver -->
<receiver
    android:name="org.owasp.mastestapp.MastgTest$PasswordResetReceiver"
    android:exported="true"
    android:permission="org.owasp.mastestapp.SEND_PASSWORD_RESET" />
```

With `protectionLevel="signature"`, only apps signed with the same certificate are granted the permission automatically. A real-world example is an enterprise remote-wipe receiver that only responds to broadcasts from the company's own device management app. Both are signed with the enterprise certificate, so only the management app can send broadcasts, while any third-party app is rejected by the OS before `onReceive` is called.

This permission-based fix only resolves the finding if the permission cannot be obtained by untrusted apps. If the receiver were protected by a broadly grantable permission, such as a custom permission with `normal` or `dangerous` protection level, the demo would still fail because untrusted apps could still obtain the permission and send broadcasts to the receiver. See @MASTG-KNOW-0017 for Android permission protection levels.

Trying to send the broadcast again with `adb` after this change will not necessarily produce an error, but it won't have any effect:

```bash
adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$PasswordResetReceiver' --es newpass hacked123
Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD flg=0x400000 cmp=org.owasp.mastestapp/.MastgTest$PasswordResetReceiver (has extras) }
Broadcast completed: result=0
```

**Additional fix - Remove sensitive data from logs:**

Regardless of whether the receiver itself is protected, no credentials must be written to the app logs, which are readable by any app that holds `READ_LOGS` (granted to shell and ADB).
