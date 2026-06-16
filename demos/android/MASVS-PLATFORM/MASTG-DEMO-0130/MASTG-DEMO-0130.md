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
