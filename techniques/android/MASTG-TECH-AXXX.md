---
title: Verifying Signature-Protected Content Providers and Canonical Path Validation
platform: android
---

This technique supports @MASTG-TEST-0007 by showing how to verify that exported content providers do not expose sensitive stored data to unauthorized callers. The workflow below is based on the secure `org.owasp.mastestapp` demo (`0x07IP_SECURE`), where exported providers are protected with a signature-level permission, enforce runtime same-signature validation, and block file traversal with canonical path checks.

## Using the AndroidManifest

Inspect `AndroidManifest.xml` and confirm that the app declares a signature-level permission and applies it to the sensitive providers.

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

<provider
    android:name="org.owasp.mastestapp.FileLeakProvider"
    android:authorities="org.owasp.mastestapp.files"
    android:exported="true"
    android:readPermission="org.owasp.mastestapp.permission.IPC_SIGNATURE"
    android:writePermission="org.owasp.mastestapp.permission.IPC_SIGNATURE" />
```

Manifest protection alone is not enough for sensitive IPC. Review the provider code for runtime authorization and path validation.

## Using Runtime Checks

Confirm that the provider verifies the calling app's signing certificate before returning data.

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

For file-backed providers, confirm that the implementation restricts file selection and validates canonical paths:

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

These checks prevent both unauthorized IPC access and `../` traversal into other private files.

## Using @MASTG-TOOL-0015

Use drozer to confirm that the providers are still visible in the attack surface but no longer leak data to an external caller.

```bash
$ dz> run app.package.attacksurface org.owasp.mastestapp
Attack Surface:
  2 providers exported
```

```bash
$ dz> run app.provider.info -a org.owasp.mastestapp
Package: org.owasp.mastestapp
  Authority: org.owasp.mastestapp.credentials
    Read Permission: org.owasp.mastestapp.permission.IPC_SIGNATURE
    Write Permission: org.owasp.mastestapp.permission.IPC_SIGNATURE
  Authority: org.owasp.mastestapp.files
    Read Permission: org.owasp.mastestapp.permission.IPC_SIGNATURE
    Write Permission: org.owasp.mastestapp.permission.IPC_SIGNATURE
```

Attempt to query the credential provider:

```bash
$ dz> run app.provider.query content://org.owasp.mastestapp.credentials/credentials
Exception occurred: Permission Denial
```

Attempt to read the internal file:

```bash
$ dz> run app.provider.read content://org.owasp.mastestapp.files/files/secret.txt
Exception occurred: Permission Denial
```

Attempt traversal through the file-backed provider:

```bash
$ dz> run app.provider.read content://org.owasp.mastestapp.files/files/../databases/creds.db
SecurityException
```

Even if a tool tries to specify the permission name manually, access should still fail when the caller is not signed with the same certificate:

```bash
$ dz> run app.provider.query content://org.owasp.mastestapp.credentials/credentials --permission org.owasp.mastestapp.permission.IPC_SIGNATURE
```

The expected result is still denial.

## Using @MASTG-TOOL-0004

`adb shell content` can be used to confirm the same behavior from another external execution context.

```bash
$ adb shell content query --uri content://org.owasp.mastestapp.credentials/credentials
```

```bash
$ adb shell content read --uri content://org.owasp.mastestapp.files/files/secret.txt
```

These commands should not return credential rows or file contents. A secure implementation keeps the providers exported only where necessary and prevents external callers from retrieving sensitive stored data over IPC.
