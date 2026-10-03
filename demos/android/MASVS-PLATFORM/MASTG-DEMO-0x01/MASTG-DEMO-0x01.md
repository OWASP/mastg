---
title: Sensitive Action Exposed Through an Unprotected Context-Registered Broadcast Receiver
platform: android
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0366
kind: fail
---

## Sample

The sample implements a small password vault. Tapping **Start** opens `VaultActivity`, which displays the password currently stored in the app (`originalPass123` on first run). Instead of declaring its broadcast receivers in the `AndroidManifest.xml`, the app registers them at runtime (context-registered):

- `VaultActivity` registers `PasswordResetReceiver` with `RECEIVER_EXPORTED` and no `broadcastPermission`, so any app on the device can send the `org.owasp.mastestapp.RESET_PASSWORD` broadcast and reset the password. It also logs the old password.
- `VaultActivity` registers `VaultRefreshReceiver` with `RECEIVER_NOT_EXPORTED`, so only the app itself can trigger it. It only refreshes the UI and exposes no sensitive functionality.
- The in-app-only `AdminActivity` registers `AdminCommandReceiver` with `RECEIVER_EXPORTED` but restricted with a signature-level `broadcastPermission`. It can wipe the vault, a sensitive action, but only apps signed with the same certificate can deliver its broadcast.

Because they are context-registered, none of these receivers appears in the manifest, so they can only be found by analyzing the code.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app and @MASTG-TECH-0117 to obtain the `AndroidManifest.xml`.
2. Use @MASTG-TECH-0162 to enumerate the manifest-declared receivers. `run.sh` does this by running @MASTG-TOOL-0110 with the following rule. Because the app's receivers are context-registered, this stage shows only the library `ProfileInstallReceiver`, confirming the app declares none of its own in the manifest:

{{ ../../../../rules/mastg-android-exported-receiver.yml }}

3. List the custom permissions the manifest declares and their protection levels, so an exported component's `broadcastPermission` can later be judged as strong or weak. `run.sh` runs @MASTG-TOOL-0110 with the following rule, which records each permission's level and flags weak ones (`normal`/`dangerous`, or no level, which defaults to `normal`):

{{ ../../../../rules/mastg-android-declared-permission-protection-level.yml }}

4. Use @MASTG-TECH-0014 to find the context-registered receivers in the decompiled code. `run.sh` runs @MASTG-TOOL-0110 with the following rule, which locates `Context.registerReceiver` / `ContextCompat.registerReceiver` calls and classifies each registration as exported with no permission (`RECEIVER_EXPORTED`), exported but restricted with a `broadcastPermission`, not exported (`RECEIVER_NOT_EXPORTED`), or registered without an explicit flag. Decompilers usually inline these flag constants to their integer values (`RECEIVER_EXPORTED == 2`, `RECEIVER_NOT_EXPORTED == 4`), so the rule matches both forms:

{{ ../../../../rules/mastg-android-context-registered-receiver.yml }}

5. Use @MASTG-TECH-0014 to inspect the `onReceive` implementations. `run.sh` runs @MASTG-TOOL-0110 with the following rule to locate the `onReceive` entry points and the attacker-controllable intent extras they read:

{{ ../../../../rules/mastg-android-receiver-entrypoints.yml }}

{{ run.sh }}

6. Run `evaluate.sh` to split the context-registered receivers into the exported, unprotected ones (reported as vulnerable) and the exported but permission-restricted ones. For the latter, `evaluate.sh` resolves the `broadcastPermission` to the protection level declared in the manifest and prints a verdict, so a weak (`normal`/`dangerous`) permission is reported as vulnerable instead of being assumed safe:

{{ evaluate.sh }}

## Observation

The manifest scan finds no app-owned receiver (only the permission-protected library `ProfileInstallReceiver`). The permission scan records the protection level of each declared custom permission. The code scan finds the three context-registered receivers and classifies them, and the `onReceive` scan points at their code:

{{ manifest_scan.txt # permissions_scan.txt # code_scan.txt # onreceive_scan.txt }}

`evaluate.sh` splits the context-registered receivers into the exported, unprotected one (reported as vulnerable) and the exported but permission-restricted one. For the protected one it resolves the `broadcastPermission` to its declared protection level and prints a verdict, so the reader does not have to look the level up by hand:

{{ evaluation.txt }}

## Evaluation

The test case fails because `PasswordResetReceiver` performs a security-relevant action (resetting the stored password) and is registered as exported with no `broadcastPermission`.

Manifest enumeration from "Stage 1" was not enough here: the only receiver in the manifest is `androidx.profileinstaller.ProfileInstallReceiver`, which is added by an AndroidX library and is protected by `android:permission="android.permission.DUMP"`, so it is **not** reported as vulnerable. The app's own receivers are registered at runtime and only appear in the code.

The "Stage 2" code scan finds three context-registered receivers, and we discard two of them:

- `vaultRefreshReceiver` is registered with `RECEIVER_NOT_EXPORTED`, so other apps cannot deliver broadcasts to it. It is not part of the external attack surface, so `evaluate.sh` drops it.
- `adminCommandReceiver` is registered with `RECEIVER_EXPORTED` but passes a `broadcastPermission`. `evaluate.sh` resolves that permission (`org.owasp.mastestapp.ADMIN_COMMAND_PERMISSION`) against the permission scan and reports `protectionLevel=signature -> OK`, so only apps signed with the same certificate can deliver its broadcast. It performs a sensitive action (wiping the vault), but the strong sender protection means it is **not** reported as vulnerable. Had the permission resolved to `normal` or `dangerous` (or not been declared at all), the verdict would instead read `WEAK -> treat as vulnerable`, because untrusted apps could obtain it. The `AdminActivity` that registers it is not exported, so it is not part of the attack surface either.

`passwordResetReceiver` is registered with `RECEIVER_EXPORTED` and no `broadcastPermission`, so any app on the device can deliver its broadcast. It is the candidate to inspect.

Using the output from "Stage 3" scan we can confirm the sensitivity of `passwordResetReceiver`: its `onReceive` changes the stored password from an unvalidated intent extra and discloses the old password to the log:

```kotlin
override fun onReceive(context: Context, intent: Intent) {
    val newPassword = intent.getStringExtra("newpass") ?: return
    val prefs = context.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
    val oldPassword = prefs.getString(MastgTest.KEY_PASSWORD_STORE, "")
    Log.d("MASTG-DEMO", "Password changed from $oldPassword to $newPassword")
    prefs.edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
}
```

Unlike a manifest-declared receiver, a context-registered receiver only exists while the component that registered it is alive. `VaultActivity` registers `PasswordResetReceiver` in `onCreate` and unregisters it in `onDestroy`, so the receiver is reachable only while `VaultActivity` is active. The exposure is real but bounded to that window.

### Confirm the Exposure

You can use @MASTG-TECH-0162 with @MASTG-TOOL-0004 to deliver broadcasts and observe which receivers act on them. In a separate terminal, watch the app's own log tag so you can see each delivery:

```bash
adb logcat -s MASTG-DEMO
```

**The unprotected receiver accepts the broadcast.**

1. Tap **Start** to open `VaultActivity` (which registers `PasswordResetReceiver`) and note the current password (`originalPass123`).
2. While `VaultActivity` is in the foreground, send the broadcast, targeting the package explicitly so it is delivered on modern Android:

    ```bash
    adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -p org.owasp.mastestapp --es newpass hacked123

    Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD pkg=org.owasp.mastestapp (has extras) }
    Broadcast completed: result=0
    ```

3. Tap **Refresh**. The vault password now shows `hacked123`, and the disclosed old password appears in logcat, confirming the external broadcast reached the receiver:

    ```bash
    06-13 11:12:55.445  4942  4942 D MASTG-DEMO: Password changed from originalPass123 to hacked123
    ```

**The permission-protected receiver rejects the broadcast.**

`AdminCommandReceiver` listens for the `org.owasp.mastestapp.ADMIN_COMMAND` action (this is the action, *not* the permission name) and is registered with the signature-level `broadcastPermission` `org.owasp.mastestapp.ADMIN_COMMAND_PERMISSION`. Open `AdminActivity` with the **Admin** button (which registers it), then try the same kind of attack:

```bash
adb shell am broadcast -a org.owasp.mastestapp.ADMIN_COMMAND -p org.owasp.mastestapp --es command wipe

Broadcasting: Intent { act=org.owasp.mastestapp.ADMIN_COMMAND pkg=org.owasp.mastestapp (has extras) }
Broadcast completed: result=0
```

`Broadcast completed: result=0` only means the system accepted the broadcast for dispatch; it does **not** mean any receiver was invoked. `AdminCommandReceiver` logs every delivery on entry, so if it had been reached you would see:

```bash
06-13 11:13:10.000  4942  4942 D MASTG-DEMO: AdminCommandReceiver received broadcast: org.owasp.mastestapp.ADMIN_COMMAND
```

Because `adb`/shell is not signed with the app's certificate, it does not hold `ADMIN_COMMAND_PERMISSION`, so the OS drops the broadcast before `onReceive` runs. No `AdminCommandReceiver received broadcast` line appears in logcat, and the vault recovery key shown in `AdminActivity` is unchanged, confirming the receiver is protected. This is the difference that matters: the same `am broadcast` that works against `PasswordResetReceiver` is silently rejected here, with no error returned to the sender.

> Note: targeting the permission string as the action (`-a org.owasp.mastestapp.ADMIN_COMMAND_PERMISSION`) also produces no effect, but for a different reason: no receiver listens for that action at all. Always send the action the receiver's `IntentFilter` declares.

## Fix

There are two ways to fix this, and the right choice depends on whether `PasswordResetReceiver` needs to accept broadcasts from external apps at all.

**Option 1: Register with `RECEIVER_NOT_EXPORTED` (recommended for most apps)**

If the receiver only reacts to broadcasts the app sends to itself, register it as not exported, exactly as `VaultRefreshReceiver` already does:

```kotlin
ContextCompat.registerReceiver(
    this,
    passwordResetReceiver,
    IntentFilter(ACTION_RESET_PASSWORD),
    ContextCompat.RECEIVER_NOT_EXPORTED
)
```

Other apps can no longer deliver the broadcast, so the password can't be reset from outside the app. For purely in-process events, an even stronger option is to avoid a global broadcast entirely and use a `LocalBroadcastManager`-style in-app event bus or a `ViewModel`/`StateFlow`.

**Option 2: Keep it exported but require a signature `broadcastPermission`**

If the receiver must be reachable by a trusted partner app from the same developer, keep it exported but pass a custom signature-level permission as the `broadcastPermission` argument so only apps signed with the same certificate can send the broadcast. This is exactly what `AdminActivity` already does for `AdminCommandReceiver` in this sample:

```kotlin
// <permission android:name="org.owasp.mastestapp.SEND_PASSWORD_RESET"
//             android:protectionLevel="signature" /> declared in the manifest

ContextCompat.registerReceiver(
    this,
    passwordResetReceiver,
    IntentFilter(ACTION_RESET_PASSWORD),
    "org.owasp.mastestapp.SEND_PASSWORD_RESET",
    null,
    ContextCompat.RECEIVER_EXPORTED
)
```

This only resolves the finding if the permission cannot be obtained by untrusted apps. A custom permission with `normal` or `dangerous` protection level would still let untrusted apps send the broadcast. See @MASTG-KNOW-0017 for Android permission protection levels.

**Additional fix - Remove sensitive data from logs:**

Regardless of whether the receiver itself is protected, no credentials must be written to the app logs, which are readable by any app that holds `READ_LOGS` (granted to shell and ADB).
