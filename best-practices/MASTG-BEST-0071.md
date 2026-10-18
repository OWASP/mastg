---
title: Validate Input Parameters in Deep Link and Custom URL Scheme Handlers
alias: validate-input-parameters-in-deep-link-and-custom-url-scheme-handlers
id: MASTG-BEST-0071
platform: android
knowledge: [MASTG-KNOW-0019]
---

Validate and sanitize every value read from an incoming deep link before using it. Any app on the device can send an Intent that targets your handler, and Android provides no reliable way to identify the caller, so treat all parameters obtained from `Intent.getData()`, `Uri.getQueryParameter()`, `Uri.getPathSegments()`, or `Uri.getLastPathSegment()` as untrusted input (see @MASTG-KNOW-0019).

## Convert to Expected Types

When a parameter represents a numeric value, convert it explicitly with [`toLongOrNull()`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/to-long-or-null.html) or [`toIntOrNull()`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/to-int-or-null.html) and handle the failure case. Never use the raw string returned by `getQueryParameter()` directly in an operation that expects a specific type.

```kotlin
val amount = uri.getQueryParameter("amount")?.toLongOrNull()
    ?: return  // reject missing or non-numeric values
```

## Check Bounds and Ranges

After type conversion, verify that the value falls within acceptable limits before acting on it. For financial or resource-sensitive operations, enforce both a minimum and a maximum.

```kotlin
if (amount <= 0 || amount > 10_000) return
```

## Sanitize String Parameters

Without sanitization, a crafted URI can target different parts of the app:

- **Path traversal**: a value like `path=../../databases/secrets.db` can escape an intended directory if used in file operations. Resolve and verify the canonical path stays within the expected base directory.
- **Script injection**: a value like `q=<script>alert(1)</script>` can execute JavaScript if rendered in a [`WebView`](https://developer.android.com/reference/android/webkit/WebView). See @MASTG-TEST-0031.
- **Query or command injection**: values interpolated into SQL queries or shell commands can alter their logic. Use parameterized queries and avoid string concatenation.

Prefer allowlists for parameters that select a resource or an action when the set of valid inputs is known. Reject any value that does not match rather than attempting to strip individual characters.

## Restrict the Handler Surface

If a deep link handler does not need to be reachable by other apps, set [`android:exported="false"`](https://developer.android.com/guide/topics/manifest/activity-element#exported) on its activity. For links that trigger sensitive actions, prefer verified App Links (see @MASTG-BEST-0070) over custom URL schemes.
