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

1. Use @MASTG-TECH-0013 to reverse engineer the app and @MASTG-TECH-0117 to obtain the `AndroidManifest.xml`.
2. Use @MASTG-TECH-0161 to enumerate the services, their export state, and any associated `android:permission`. `run.sh` does this by running @MASTG-TOOL-0110 with the following rule, which flags services that are exported without a permission and lists the permission-protected ones separately:

{{ ../../../../rules/mastg-android-exported-service.yml }}

3. Use @MASTG-TECH-0014 to inspect the decompiled code of each exported service. `run.sh` runs @MASTG-TOOL-0110 with the following rule to locate the entry points reachable when the service is started or bound (`onStartCommand`, `onBind`, `onRebind`, `onHandleIntent`) and any runtime caller-permission checks:

{{ ../../../../rules/mastg-android-service-entrypoints.yml }}

{{ run.sh }}

4. Run `evaluate.sh` to reduce the exported, unprotected services from "Stage 1" to the ones the app itself declares. Services in framework or library namespaces (`android.*`, `androidx.*`, `com.google.android.*`) ship with dependencies rather than the app's own code, so they are triaged separately:

{{ evaluate.sh }}

## Observation

"Stage 1" lists the exported services and their permissions, and "Stage 2" points at the service code to review:

{{ manifest_scan.txt # code_scan.txt }}

`evaluate.sh` narrows the exported, unprotected services down to the app's own components:

{{ evaluation.txt }}

## Evaluation

The test case fails because `org.owasp.mastestapp.MastgTest$AuthService` exposes a security-relevant operation (changing the vault password) and is exported (`android:exported="true"`) without any permission protection. Because this service is exported and unprotected, external callers that can address the component can start it directly and overwrite the password.

The "Stage 2" code scan flags the `onStartCommand` entry point and finds no runtime caller-permission check (no `checkCallingPermission`/`enforceCallingPermission`), so nothing restricts the caller. The service changes the stored password from an intent extra in `onStartCommand`:

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
