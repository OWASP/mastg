---
title: Sensitive Data Exposed Through an Unprotected Activity
platform: android
id: MASTG-DEMO-0128
code: [kotlin]
test: MASTG-TEST-0364
kind: fail
---

## Sample

The sample app performs a login flow by using two activities. Tapping **Start** in the main screen launches `PinEntryActivity`, which prompts for a PIN (4321) before proceeding to `SecretActivity`. `SecretActivity` displays sensitive account data and is meant to be reachable only after the user passes the PIN check.

However, `SecretActivity` is declared as exported in the `AndroidManifest.xml` without an `android:permission`. This allows third-party apps, or `adb`, to start `SecretActivity` directly without interacting with `PinEntryActivity`, bypassing the PIN gate entirely.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app and @MASTG-TECH-0117 to obtain the `AndroidManifest.xml`.
2. Use @MASTG-TECH-0160 to enumerate the activities, their export state, and any associated `android:permission`. `run.sh` does this by running @MASTG-TOOL-0110 in its "Stage 1" with the following rule, which flags activities that are exported without a permission and lists the permission-protected ones separately:

{{ ../../../../rules/mastg-android-exported-activity.yml }}

3. Use @MASTG-TECH-0014 to inspect the decompiled code of each exported activity. `run.sh` runs @MASTG-TOOL-0110 in its "Stage 2" with the following rule to locate the lifecycle entry points reachable when the activity is started (`onCreate`, `onStart`, `onResume`, `onNewIntent`):

{{ ../../../../rules/mastg-android-activity-entrypoints.yml }}

{{ run.sh }}

4. Run `evaluate.sh` to reduce the exported, unprotected activities from "Stage 1" to the ones the app itself declares. Activities in framework or library namespaces (`android.*`, `androidx.*`, `com.google.android.*`) ship with dependencies rather than the app's own code, so they are triaged separately:

{{ evaluate.sh }}

## Observation

"Stage 1" lists the exported activities and their permissions, and "Stage 2" points at the activity code to review:

{{ manifest_scan.txt # code_scan.txt }}

`evaluate.sh` narrows the exported, unprotected activities down to the app's own components:

{{ evaluation.txt }}

## Evaluation

The test case fails because `SecretActivity` exposes sensitive functionality and is exported (`android:exported="true"`) without any permission protection, so external callers can start it directly by using an explicit intent.

The "Stage 1" scan reports four exported, unprotected activities, and we discard three of them:

- `androidx.compose.ui.tooling.PreviewActivity` is a Compose tooling activity used by Android Studio to run composable previews, and `androidx.activity.ComponentActivity` is a generic host activity added by the Compose libraries. Both live in the `androidx.*` namespace, so `evaluate.sh` drops them as library code; they are not part of the app's authentication flow and should only be reviewed if the tested build is meant for production.
- `MainActivity` is app-owned, so it survives the namespace filter and remains in `evaluation.txt`, but the "Stage 1" scan shows it carries the `LAUNCHER` intent filter. Launcher activities must be exported so Android and the launcher can start the app (see [Android's guidance](https://developer.android.com/about/versions/12/behavior-changes-12#exported)), and its `onCreate` only shows the app's entry screen, so it is discarded after manual review.

`org.owasp.mastestapp.MastgTest$SecretActivity` is the remaining app-owned activity. Its `onCreate` displays sensitive account data without checking whether the user completed the PIN challenge.

```kotlin
class SecretActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... displays account number, balance, and recovery PIN ...
    }
}
```

### Confirm the Exposure

You can use @MASTG-TECH-0160 to start `SecretActivity` directly and confirm that the sensitive screen is reachable without entering the PIN:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$SecretActivity'
```

The secret screen appears without any PIN prompt, confirming the authentication bypass.

An external app can start `SecretActivity` directly, but that does not automatically let the external app read the activity's UI contents or obtain the displayed data programmatically. Android does not normally return another activity's screen text to the caller.

The security issue is that the protected screen becomes reachable without completing the PIN challenge. This can still expose sensitive data to anyone using the device, to screen capture or accessibility based threats, or to any flow where the attacker can trick the user into opening the activity. If the activity also returns data through results, sends broadcasts, writes files, accepts attacker controlled extras, or performs account actions on launch, the impact could be higher.

In this sample, the finding is an authentication bypass because `SecretActivity` displays sensitive account data without verifying that the user completed the PIN challenge. The direct launch proves unauthorized access to the protected screen, even though the calling app does not automatically read the displayed data.

## Fix

There are two ways to fix this, and the right choice depends on whether `SecretActivity` needs to be reachable by external apps at all.

**Option 1: Set `android:exported="false"` (recommended for most apps)**

If `SecretActivity` has no legitimate reason to be started by another app, simply prevent external apps from reaching it:

```xml
<activity
    android:name="org.owasp.mastestapp.MastgTest$SecretActivity"
    android:exported="false" />
```

Trying to start `SecretActivity` again with `adb` after this change will fail with an error, confirming that the activity is no longer reachable from outside the app:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$SecretActivity'
Starting: Intent { cmp=org.owasp.mastestapp/.MastgTest$SecretActivity }

Exception occurred while executing 'start':
java.lang.SecurityException: Permission Denial: starting Intent { flg=0x10000000 xflg=0x4 cmp=org.owasp.mastestapp/.MastgTest$SecretActivity } from null (pid=29738, uid=2000) not exported from uid 10225
```

This is the right choice for the vast majority of activities that display sensitive data or are part of an internal authentication flow. Android 12 and later require you to explicitly set `android:exported` on any activity with an `<intent-filter>`; setting it to `false` on activities that don't need it is the minimal, correct fix.

**Option 2: Keep `android:exported="true"` but enforce a `android:permission`**

If the activity must be reachable by a trusted partner app (for example, a companion widget or a deep-link handler used by a first-party browser), you can keep it exported but gate access with a custom signature-level permission:

```xml
<!-- Declare the permission in the app's manifest -->
<permission
    android:name="org.owasp.mastestapp.ACCESS_SECRET"
    android:protectionLevel="signature" />

<!-- Require it on the activity -->
<activity
    android:name="org.owasp.mastestapp.MastgTest$SecretActivity"
    android:exported="true"
    android:permission="org.owasp.mastestapp.ACCESS_SECRET" />
```

With `protectionLevel="signature"`, only apps signed with the same certificate are granted the permission automatically. A real-world example is a banking app that exposes a payment-confirmation activity to its own companion wearable app. Both are signed with the bank's certificate, so only the wearable can start the activity, while any third-party app is rejected by the OS before `onCreate` is even called.

This permission-based fix only resolves the finding if the permission cannot be obtained by untrusted apps. If the activity were protected by a broadly grantable permission, such as a custom permission with `normal` or `dangerous` protection level, the demo would still fail because untrusted apps could still obtain the permission and start the activity. See @MASTG-KNOW-0017 for Android permission protection levels.

Trying to start `SecretActivity` again with `adb` after this change will fail with a different error, confirming that the activity is still exported but now requires a permission that the calling app does not have:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$SecretActivity'
Starting: Intent { cmp=org.owasp.mastestapp/.MastgTest$SecretActivity }

Exception occurred while executing 'start':
java.lang.SecurityException: Permission Denial: starting Intent { flg=0x10000000 xflg=0x4 cmp=org.owasp.mastestapp/.MastgTest$SecretActivity } from null (pid=29880, uid=2000) requires org.owasp.mastestapp.ACCESS_SECRET
```

**Why not rely solely on the PIN check in the calling activity?:**

Enforcing authentication only in `PinEntryActivity` and trusting that `SecretActivity` is always reached through it is a broken client-side control. Android's activity model makes no such guarantee: any exported activity can be started directly. Authentication state must be checked inside the activity that performs the sensitive operation, or the activity must not be exported.
