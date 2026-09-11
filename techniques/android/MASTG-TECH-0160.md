---
title: Enumerating Activities
platform: android
---

You can enumerate the activities an app declares, determine which of them are exported, and identify any associated `android:permission` values by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0132 for background on activities and the `android:exported` attribute.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<activity>`](https://developer.android.com/guide/topics/manifest/activity-element) and [`<activity-alias>`](https://developer.android.com/guide/topics/manifest/activity-alias-element) elements, and record their `android:name`, `android:exported`, `android:permission`, and intent filters.

Declared permissions are part of the activity's access control. An exported activity protected with [`android:permission`](https://developer.android.com/guide/topics/manifest/activity-element#prmsn) can only be started by callers that hold the required permission. Review the referenced permission and its protection level to determine whether it effectively restricts access to the intended callers. See @MASTG-KNOW-0017 for permission protection levels, component permission enforcement, and custom permissions.

Interpret `android:exported` together with the component's intent filters, target SDK, Android version, and associated permission. On apps targeting Android 11 or below, any activity that declares an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) and does not set `android:exported="false"` can become reachable by other apps. Apps targeting Android 12 (API level 31) or higher must explicitly declare `android:exported` on activities with intent filters, or the app fails to install.

For example, with the manifest extracted to standard XML, you can list each `<activity>` and `<activity-alias>` element with its element type (`activity` or `activity-alias`), `android:name` (the name of the activity or alias), `android:exported` (whether it is exported), `android:permission` (the permission required to start it), and number of intent filters:

```bash
xmlstarlet sel -t -m "//activity | //activity-alias" -v "name()" -o " name=" -v "@android:name" -o " exported=" -v "@android:exported" -o " permission=" -v "@android:permission" -o " intent_filters=" -v "count(intent-filter)" -n AndroidManifest.xml
```

Note that activity aliases have their own `android:exported`, `android:permission`, and intent filters, so review them separately from the target activity.

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including activities and activity aliases, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A20 -E "E: activity|E: activity-alias"
```

Inspect each activity block for `android:name`, `android:exported`, `android:permission`, and nested `intent-filter` entries. In the raw output, `android:exported="true"` appears as `0xffffffff`. A nested `intent-filter` is a signal to interpret with the target SDK and Android version rules above, not proof of exportability by itself.

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can inspect the package manager state:

```bash
adb shell dumpsys package <package_name> | awk '/^Activity Resolver Table:/{show=1} /^Receiver Resolver Table:/{show=0} show'
adb shell cmd package query-activities --components -p <package_name> -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
```

Use `dumpsys package` to review the activity resolver table and any associated `permission` values shown by the package manager. `cmd package query-activities` is useful for triage, but it is an intent-resolution view for the supplied package, action, and category. These commands do not replace manifest inspection and do not enumerate every activity declared by the package or every associated permission.

To launch an exported activity and observe its behavior, use the activity manager:

```bash
# Start an activity by component name.
adb shell am start -n <package_name>/<activity_name>

# Start an activity specifying an action and a category
adb shell am start -n <package_name>/<activity_name> -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
```

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate exported activities and their permissions. Use its start command when you need intent-crafting helpers to launch a candidate activity:

```bash
run app.activity.info -a <package_name>
run app.activity.start --component <package_name> <activity_name>
```
