---
title: Sanitize Data Coming from External Components
alias: sanitize-external-data
id: MASTG-BEST-0057
platform: android
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-0138]
---

All data received from external sources (such as `Intent` extras, `onActivityResult` callbacks, or `ContentProvider` results) must be treated as untrusted and thoroughly sanitized before use. Failure to validate this data can lead to serious vulnerabilities, including arbitrary file access and path traversal. Specifically, applications must always validate URIs and associated metadata before reading from them, copying their content, or passing them to sensitive system APIs.

## Validate the URI scheme

Prefer `content://` URIs over `file://` URIs. A `content://` URI routes through a `ContentProvider`, which controls exactly what data it exposes. A `file://` URI is resolved directly using the calling app's own process identity and permissions. This means a malicious responding app can return a `file://` URI pointing at any path the calling app can access, which, depending on the permissions the app holds, may go well beyond its own private storage. See @MASTG-KNOW-0138 for a detailed explanation of how this is exploited.

## Sanitize Filenames Provided by External Components

When querying a `ContentProvider` for a display name, sanitize the result before using it as a filename. A malicious provider can return a path-traversal sequence (such as `../lib/native.so`) that redirects writes outside the intended directory. Use `File(name).name` to strip any directory components:

```kotlin
fun sanitizeFileName(name: String): String {
    return File(name).name  // returns only the final path component
}

val rawName = getFileNameFromUri(uri) ?: "default.bin"
val safeName = sanitizeFileName(rawName)
```

## Sanitize Externally Provided Paths Before File Operations

Avoid using paths or filenames received from an intent to construct an output location directly. A malicious responder could supply an absolute path (such as `/sdcard/evil.apk`) that writes to an unintended location. Always anchor the output to a directory you control, such as `filesDir` or `cacheDir`, and append only a sanitized filename:

```kotlin
// Avoid: output path comes from intent data
val output = File(fileNameFromIntent)

// Prefer: fixed base dir with a sanitized filename
val output = File(context.filesDir, sanitizeFileName(fileNameFromIntent))
```
