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
