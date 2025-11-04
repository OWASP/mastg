---
title: Prevent SQL Injection in ContentProviders
alias: prevent-sqli-contentprovider
id: MASTG-BEST-XXXX
platform: android
---

The `ContentProvider` enables Android applications to share data with other applications and system components. If a `ContentProvider` constructs SQL queries using untrusted input from URIs, IPC calls, or Intents without validation or parameterization, it becomes vulnerable to SQL injection. Attackers can take advantage of this vulnerability to bypass access controls and extract sensitive data. Improper handling of URI path segments, query parameters, or `selection` arguments in `ContentProvider` queries can lead to arbitrary SQL execution.

- Use Parameterized Queries : Instead of building SQL using string concatenation, use `selection` and `selectionArgs` parameters.

For example:

```kotlin

  val idSegment = uri.getPathSegments()[1]
  val selection = "id = ?"
  val selectionArgs = arrayOf(idSegment)
  val cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)

```

Refer to ["Protect against malicious input"](https://developer.android.com/guide/topics/providers/content-provider-basics#Injection) for more information.
