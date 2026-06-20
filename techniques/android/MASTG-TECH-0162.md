---
title: Enumerating Broadcast Receivers
platform: android
---

You can enumerate the broadcast receivers an app declares, determine which of them are exported, and identify any associated `android:permission` values by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0134 for background on broadcast receivers and the `android:exported` attribute.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior. Note that context-registered receivers (registered at runtime with `Context.registerReceiver`) don't appear in the manifest and require code or runtime analysis.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<receiver>`](https://developer.android.com/guide/topics/manifest/receiver-element) elements, and record their `android:name`, `android:exported`, `android:permission`, and intent filters.

Declared permissions are part of the receiver's access control. An exported receiver protected with [`android:permission`](https://developer.android.com/guide/topics/manifest/receiver-element#prmsn) can only receive broadcasts from senders that hold the required permission. Review the referenced permission and its protection level to determine whether it effectively restricts access to the intended senders. See @MASTG-KNOW-0017 for permission protection levels, component permission enforcement, and custom permissions.

Interpret `android:exported` together with the component's intent filters, target SDK, Android version, and associated permission. On apps targeting Android 11 or below, any receiver that declares an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) and does not set `android:exported="false"` can become reachable by other apps. Apps targeting Android 12 (API level 31) or higher must explicitly declare `android:exported` on receivers with intent filters, or the app fails to install.

For example, with the manifest extracted to standard XML, you can list each `<receiver>` element with its `android:name` (the name of the receiver), `android:exported` (whether it is exported), `android:permission` (the permission required to send broadcasts to it), and number of intent filters:

```bash
xmlstarlet sel -t -m "//receiver" -v "@android:name" -o " exported=" -v "@android:exported" -o " permission=" -v "@android:permission" -o " intent_filters=" -v "count(intent-filter)" -n AndroidManifest.xml
```

Interpret missing `android:exported` values together with the component's intent filters, target SDK, and Android version.

## Identifying Context-Registered Receivers

Receivers created at runtime are registered by calling [`Context.registerReceiver`](https://developer.android.com/reference/android/content/Context#registerReceiver(android.content.BroadcastReceiver,%20android.content.IntentFilter)). To find them, search the reverse-engineered code for calls to `registerReceiver` and for subclasses of [`BroadcastReceiver`](https://developer.android.com/reference/android/content/BroadcastReceiver), as described in @MASTG-TECH-0014. Check whether the call specifies `RECEIVER_NOT_EXPORTED`, which since Android 13 (API level 33) prevents other apps from delivering broadcasts to the receiver.

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including receivers, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A20 "E: receiver"
```

Inspect each receiver block for `android:name`, `android:exported`, `android:permission`, and nested `intent-filter` entries. In the raw output, `android:exported="true"` appears as `0xffffffff`. A nested `intent-filter` is a signal to interpret with the target SDK and Android version rules above, not proof of exportability by itself.

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can inspect the package manager state, send a broadcast to a receiver, and inspect recent broadcasts with `adb`:

```bash
# List receivers known to the package manager for a package
adb shell dumpsys package <package_name> | awk '/^Receiver Resolver Table:/{show=1} /^Service Resolver Table:/{show=0} show'

# Send a broadcast with a specific action and string extras
adb shell am broadcast -a <action> --es <key> <value>

# Inspect recent broadcasts (extras are not shown)
adb shell dumpsys activity broadcasts | grep <action>
```

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate exported receivers and send broadcasts to them:

```bash
run app.broadcast.info -a <package_name>
run app.broadcast.send --action <action> --extra string <key> <value>
```
