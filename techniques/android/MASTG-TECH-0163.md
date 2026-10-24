---
title: Enumerating Content Providers
platform: android
---

You can enumerate the content providers an app declares, and determine which of them are exported, by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0117 for background on content providers, URI structure, and access control. Once you've identified a provider and its authority, use @MASTG-TECH-0148 to query, insert, update, or delete data through it.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<provider>`](https://developer.android.com/guide/topics/manifest/provider-element) elements and record their [`android:authorities`](https://developer.android.com/guide/topics/manifest/provider-element#auth) values, which form the `content://<authority>` part of the provider's URIs.

A content provider is exported, and therefore reachable by other apps, when either of the following is true:

- It sets [`android:exported="true"`](https://developer.android.com/guide/topics/manifest/provider-element#exported).
- On apps targeting API level 16 or lower, `android:exported` is not set (the historical default was `true`).

Note the access-control attributes: [`android:permission`](https://developer.android.com/guide/topics/manifest/provider-element#prmsn), [`android:readPermission`](https://developer.android.com/guide/topics/manifest/provider-element#rprmsn), [`android:writePermission`](https://developer.android.com/guide/topics/manifest/provider-element#wprmsn), and [`android:grantUriPermissions`](https://developer.android.com/guide/topics/manifest/provider-element#gprmsn).

For example, with the manifest extracted to standard XML, you can list provider declarations with:

```bash
xmlstarlet sel -t -m "//provider" -v "@android:name" -o " authorities=" -v "@android:authorities" -o " exported=" -v "@android:exported" -n AndroidManifest.xml
```

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including providers, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A4 "E: provider"
```

In the raw output, an exported provider has `android:exported` set to `0xffffffff` (true).

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can list the providers a package registers with the package manager:

```bash
adb shell dumpsys package <package_name> | grep -i "Provider{"
```

To discover the URI paths a provider exposes, reverse engineer the provider class as described in @MASTG-TECH-0014 and look for the `content://` URIs and `UriMatcher` patterns it handles (see @MASTG-KNOW-0117). Then interact with the provider as described in @MASTG-TECH-0148.

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate providers, surface their URIs, and probe them:

```bash
run app.provider.info -a <package_name>
run app.provider.finduri <package_name>
```
