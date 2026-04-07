---
title: Validate URI Origin, Scheme, and Path Before Processing
alias: validate-uri-before-processing
id: MASTG-BEST-0x15
platform: android
knowledge: [MASTG-KNOW-0025]
---

Always validate a URI returned from an external source — such as an `onActivityResult` callback or a `ContentProvider` — before reading from it, copying its content, or passing it to system APIs. An attacker-controlled app can supply a `file://` URI pointing to the victim app's internal storage, or a `content://` URI whose `ContentProvider` returns a path-traversal filename (e.g., `../lib-main/lib.so`), enabling arbitrary file read or overwrite.

## Validate the URI scheme

Reject URI schemes that are unsafe in your context. For most result-handling scenarios, only `content://` URIs from known, trusted authorities should be accepted:

```kotlin
fun isSafeUri(uri: Uri): Boolean {
    // Reject file:// — allows reading arbitrary internal storage paths
    if (uri.scheme == "file") return false
    // Reject unknown content authorities
    val trustedAuthorities = setOf("com.example.app.provider")
    if (uri.scheme == "content" && uri.authority !in trustedAuthorities) return false
    return true
}
```

## Validate the filename from a ContentProvider

When querying a `ContentProvider` for a display name or file path, sanitize the result before using it as a file name. Path-traversal sequences (`../`) in the filename can redirect writes outside the intended directory:

```kotlin
fun sanitizeFileName(name: String): String {
    // Remove all path separators and traversal sequences
    return File(name).name  // strips any directory components
}

val rawName = getFileNameFromUri(uri) ?: "default.bin"
val safeName = sanitizeFileName(rawName)
val target = File(context.filesDir, safeName)
```

`File(name).name` returns only the final path component, discarding any `../` prefix an attacker might inject.

## Avoid world-readable output locations

Don't copy URI content to `externalCacheDir`, `getExternalFilesDir`, or any path on shared storage unless the data is intentionally public. Use internal storage (`filesDir`, `cacheDir`) for any content received from an untrusted source:

```kotlin
// Avoid: world-readable on older Android versions
val output = File(activity.externalCacheDir, fileName)

// Prefer: private to the app
val output = File(activity.filesDir, fileName)
```

!!! note

     Validating the URI and filename reduces the attack surface but doesn't eliminate it entirely if the content itself is attacker-controlled. Never execute or dynamically load files (via `System.load()`, `DexClassLoader`, etc.) whose content originates from an untrusted source, regardless of where they are stored.
