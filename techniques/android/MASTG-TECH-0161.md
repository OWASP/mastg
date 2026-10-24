---
title: Enumerating Services
platform: android
---

You can enumerate the services an app declares, determine which of them are exported, and identify any associated `android:permission` values by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0133 for background on services and the `android:exported` attribute.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<service>`](https://developer.android.com/guide/topics/manifest/service-element) elements, and record their `android:name`, `android:exported`, `android:permission`, and intent filters.

Declared permissions are part of the service's access control. An exported service protected with [`android:permission`](https://developer.android.com/guide/topics/manifest/service-element#prmsn) can only be started or bound to by callers that hold the required permission. Review the referenced permission and its protection level to determine whether it effectively restricts access to the intended callers. See @MASTG-KNOW-0017 for permission protection levels, component permission enforcement, and custom permissions.

Interpret `android:exported` together with the component's intent filters, target SDK, Android version, and associated permission. On apps targeting Android 11 or below, any service that declares an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) and does not set `android:exported="false"` can become reachable by other apps. Apps targeting Android 12 (API level 31) or higher must explicitly declare `android:exported` on services with intent filters, or the app fails to install.

For example, with the manifest extracted to standard XML, you can list each `<service>` element with its `android:name` (the name of the service), `android:exported` (whether it is exported), `android:permission` (the permission required to start or bind to it), and number of intent filters:

```bash
xmlstarlet sel -t -m "//service" -v "@android:name" -o " exported=" -v "@android:exported" -o " permission=" -v "@android:permission" -o " intent_filters=" -v "count(intent-filter)" -n AndroidManifest.xml
```

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including services, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A20 "E: service"
```

Inspect each service block for `android:name`, `android:exported`, `android:permission`, and nested `intent-filter` entries. In the raw output, `android:exported="true"` appears as `0xffffffff`. A nested `intent-filter` is a signal to interpret with the target SDK and Android version rules above, not proof of exportability by itself.

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can inspect the package manager state and start a service with the activity manager:

```bash
# List services known to the package manager for a package
adb shell dumpsys package <package_name> | awk '/^Service Resolver Table:/{show=1} /^Domain verification status:/{show=0} show'

# Start a service by component name
adb shell am startservice -n <package_name>/<service_name>
```

To interact with a bound service, you first need to identify the inputs it expects (for example, the `Message` codes of a `Messenger` interface or the methods of an AIDL interface) through static analysis, as described in @MASTG-TECH-0023.

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate exported services and send messages to them. Use it when you need to craft and deliver structured `Message` objects to a bound service:

```bash
run app.service.info -a <package_name>
run app.service.send <package_name> <service_name> --msg <what> <arg1> <arg2> --extra <type> <key> <value> --bundle-as-obj
```
