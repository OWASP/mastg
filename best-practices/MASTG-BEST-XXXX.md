---
title: Restrict and Validate Access to Sensitive Exported Content Providers
alias: restrict-and-validate-access-to-sensitive-exported-content-providers
id: MASTG-BEST-XXXX
platform: android
---

Do not expose sensitive app data through an exported Android content provider unless cross-app access is strictly required. If the provider handles credential records or files from private storage, prefer `android:exported="false"`; if it must remain exported, protect it with explicit manifest permissions and enforce authorization again in provider code.

Manifest restrictions and runtime checks solve different parts of the problem. Android's [`<provider>`](https://developer.android.com/guide/topics/manifest/provider-element) and [`<permission>`](https://developer.android.com/guide/topics/manifest/permission-element) controls limit which apps can reach the component, while provider-side checks using [`Binder.getCallingUid`](https://developer.android.com/reference/android/os/Binder#getCallingUid()) and [`PackageManager.checkSignatures`](https://developer.android.com/reference/android/content/pm/PackageManager#checkSignatures(java.lang.String,%20java.lang.String)) protect the data path itself. For file-backed providers, validate canonical paths before opening files so attacker-controlled path segments cannot escape the intended directory.

## Keep Unnecessary Providers Non-Exported

If a provider is only used inside the app, keep it private instead of hardening an avoidable IPC surface.

```xml
<provider
    android:name="org.owasp.mastestapp.CredentialProvider"
    android:authorities="org.owasp.mastestapp.credentials"
    android:exported="false" />
```

This is the simplest and strongest option because external apps cannot query the provider at all.

## Protect Required IPC with Signature-Level Permissions

If the provider must be exported for trusted companion apps, declare a custom permission with `signature` protection level and apply it to the provider's read and write operations.

```xml
<permission
    android:name="org.owasp.mastestapp.permission.IPC_SIGNATURE"
    android:protectionLevel="signature" />

<provider
    android:name="org.owasp.mastestapp.CredentialProvider"
    android:authorities="org.owasp.mastestapp.credentials"
    android:exported="true"
    android:readPermission="org.owasp.mastestapp.permission.IPC_SIGNATURE"
    android:writePermission="org.owasp.mastestapp.permission.IPC_SIGNATURE" />
```

This aligns access with Android's documented permission model and blocks arbitrary third-party apps before provider logic is reached.

## Verify the Caller in Provider Code

If sensitive data still crosses an exported provider boundary, verify that the caller is allowed to access it before returning any rows or files. Same-signature validation is a practical pattern for apps that intentionally share data only with apps signed by the same developer certificate.

```kotlin
private fun ContentProvider.enforceSameSignatureCaller() {
    val ctx = requireNotNull(context)
    val pm = ctx.packageManager
    val myPkg = ctx.packageName
    val callerUid = Binder.getCallingUid()

    if (callerUid == android.os.Process.myUid()) return

    val callerPkgs = pm.getPackagesForUid(callerUid) ?: emptyArray()
    val match = callerPkgs.any { callerPkg ->
        pm.checkSignatures(myPkg, callerPkg) == PackageManager.SIGNATURE_MATCH
    }

    if (!match) {
        throw SecurityException("Caller not signed with same certificate")
    }
}
```

This adds defense in depth if a manifest setting is misapplied or if the provider exposes especially sensitive operations.

## Validate File Paths Before Opening Files

For file-backed providers, combine caller validation with canonical path checks using Java's [`File.getCanonicalFile()`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html#getCanonicalFile--) and Android's [`ContentProvider.openFile`](https://developer.android.com/reference/android/content/ContentProvider#openFile(android.net.Uri,%20java.lang.String)) contract. Reject path separators in the requested name and ensure the resolved file stays within the intended base directory.

```kotlin
override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
    enforceSameSignatureCaller()

    val base = requireNotNull(context).filesDir.canonicalFile
    val filename = uri.lastPathSegment ?: return null

    if (filename.contains('/') || filename.contains('\\')) {
        throw SecurityException("Invalid filename")
    }

    val target = File(base, filename).canonicalFile
    if (!target.path.startsWith(base.path + File.separator)) {
        throw SecurityException("Path traversal blocked")
    }

    return ParcelFileDescriptor.open(target, ParcelFileDescriptor.MODE_READ_ONLY)
}
```

This prevents requests such as `../databases/creds.db` from exposing files outside the provider's intended scope.

## Minimize the Data Returned

Even after access control is in place, expose only the data a caller actually needs. Android's [ContentProvider guidance](https://developer.android.com/guide/topics/providers/content-providers) supports returning narrow projections instead of mirroring internal storage structures or serving arbitrary files from private storage.

For database-backed providers, avoid returning passwords, tokens, or other secrets when the integration needs only identifiers or display data. For file-backed providers, restrict access to a controlled set of files instead of mapping broad `content://` paths to app-private directories.

!!! warning "Exported providers should remain exceptional"
    Signature permissions and runtime checks reduce risk, but they do not make broad data exposure safe by default. If the feature does not need external IPC, keep the provider non-exported instead of hardening an unnecessary attack surface.
