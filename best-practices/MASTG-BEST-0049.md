---
title: Restrict and Validate Access to Exported Content Providers
alias: restrict-and-validate-access-to-exported-content-providers
id: MASTG-BEST-0049
platform: android
---

Content Providers are not inherently unsafe, but database-backed and file-backed providers can expose sensitive data if they are exported, have weak permissions, or grant access through overly broad URI scopes.

To reduce data exposure and follow the least-privilege security principle, apply the below techniques:

## Keep Content Providers Non-Exported

If a provider is only used within the app, set [`android:exported="false"`](https://developer.android.com/guide/topics/manifest/provider-element#exported) explicitly. Although providers are not exported by default since API level 17, explicit configuration is easier to audit and safer for mixed/legacy codebases.

```xml
<provider
    android:name="org.owasp.mastestapp.MastgTest$AppointmentProvider"
    android:authorities="org.owasp.mastestapp.appointments"
    android:exported="false" />
```

As a result, apps on the device are not able to query the provider at all.

## Permissions on Exported Content Providers

If the Content Provider must be exported, access should be restricted using one or more of the following `<provider>` manifest attributes:

- **[`android:readPermission`](https://developer.android.com/guide/topics/manifest/provider-element#rprmsn)**: Required permission to query or read data from the provider.
- **[`android:writePermission`](https://developer.android.com/guide/topics/manifest/provider-element#wprmsn)**: Required permission to insert, update, or delete data in the provider.
- **[`android:permission`](https://developer.android.com/guide/topics/manifest/provider-element#prmsn)**: A single permission that covers both read and write access. It is overridden by `readPermission` or `writePermission` when those are set.

```xml
<provider
    android:name="org.owasp.mastestapp.MastgTest$AppointmentProvider"
    android:authorities="org.owasp.mastestapp.appointments"
    android:exported="true"
    android:readPermission="org.owasp.mastestapp.permission.READ_APPOINTMENTS" />
```

Setting one or more of these manifest attributes makes Android check whether the caller has actually been granted the required permission. Declaring `<uses-permission>` in the client manifest only requests the permission; whether the request is granted depends on the permission's `protectionLevel` (covered in the next section).

```xml
<uses-permission android:name="org.owasp.mastestapp.permission.READ_APPOINTMENTS" />
```

## Protection Levels on Exported Content Providers

A `protectionLevel` defines the conditions under which Android will grant a declared `<permission>` to a requesting app. For example, automatically at install time (`normal`), only after explicit user approval (`dangerous`), or exclusively to apps signed with the same developer certificate (`signature`).

The following example, declared by the provider app, enforces that only client apps signed with the same developer certificate are allowed to use the content provider.

```xml
<permission
      android:name="org.owasp.mastestapp.permission.READ_APPOINTMENTS"
      android:protectionLevel="signature" />
```

A custom permission without an explicit `protectionLevel` defaults to `normal`. This means any app can request and typically receive it automatically, so it is a weak security boundary.

The `readPermission`/`writePermission` attributes on the provider are the enforcement point, but the declared permission's `protectionLevel` determines how strong that enforcement is.

## Share Files Securely with FileProvider

For sharing files with other apps, Android recommends using [`FileProvider`](https://developer.android.com/reference/androidx/core/content/FileProvider) with a non-exported provider, narrow path mappings, and temporary URI grants.

`FileProvider` enforces path scoping based on a declarative configuration and refuses to serve files outside the declared subtrees, including paths that attempt to escape via `..`.

### Declare a Narrow Scope in `file_paths.xml`

Declare only the specific subdirectories that should be shareable. Each path element maps to a directory on the device, as documented in the [`FileProvider` reference](https://developer.android.com/reference/androidx/core/content/FileProvider#specifying-available-files):

- `<files-path>`: a subpath of `Context.getFilesDir()`
- `<cache-path>`: a subpath of `Context.getCacheDir()`
- `<external-files-path>`: a subpath of the app-specific external files directory
- `<external-cache-path>`: a subpath of the app-specific external cache directory
- `<external-path>`: a subpath of `Environment.getExternalStorageDirectory()`

Pick the narrowest path that satisfies the use case. For example, if only PDF reports under `filesDir/reports/` should be shareable:

```xml
<!-- res/xml/file_paths.xml -->
<paths>
    <files-path name="reports" path="reports/" />
</paths>
```

Files outside the declared subtrees (such as `filesDir/session_token.txt`) cannot be addressed through the provider.

### Configure the FileProvider in the Manifest

Keep the provider non-exported and enable per-URI grants. The `android:grantUriPermissions="true"` attribute allows other apps to be granted temporary access through `FLAG_GRANT_READ_URI_PERMISSION` or `FLAG_GRANT_WRITE_URI_PERMISSION`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="org.owasp.mastestapp.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

### Share Files via URIs with Temporary Grants

Use [`FileProvider.getUriForFile()`](https://developer.android.com/reference/androidx/core/content/FileProvider#getUriForFile(android.content.Context,%20java.lang.String,%20java.io.File)) to obtain a `content://` URI for a file inside one of the declared subtrees, then attach [`FLAG_GRANT_READ_URI_PERMISSION`](https://developer.android.com/reference/android/content/Intent#FLAG_GRANT_READ_URI_PERMISSION) to the `Intent` that hands the URI to the receiving app. Requests for files outside the declared paths throw `IllegalArgumentException`. The grant is scoped to that single URI, the receiving app does not need to declare any manifest permission, and the grant expires when the receiving task ends.

```kotlin
val report = File(context.filesDir, "reports/lab_result_3829.pdf")
val uri = FileProvider.getUriForFile(
    context,
    "org.owasp.mastestapp.fileprovider",
    report
)

val intent = Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(uri, "application/pdf")
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
startActivity(intent)
```

For sensitive workflows, minimize each grant to the required access mode (read vs. write), and revoke explicit grants with [`Context.revokeUriPermission()`](https://developer.android.com/reference/android/content/Context#revokeUriPermission(android.net.Uri,%20int)) once they are no longer needed.

!!! warning "Avoid `<root-path>` and `path=\".\"` declarations"
    `<root-path>` maps the provider root to `/` and can make any app-accessible file reachable through the provider if the app grants such a URI. It does not bypass Android/Linux file permissions, but it is still too broad for secure sharing. Setting `path="."` on any path element exposes the entire corresponding directory, including files added later such as authentication tokens, databases, or logs. Always declare a specific subdirectory.
