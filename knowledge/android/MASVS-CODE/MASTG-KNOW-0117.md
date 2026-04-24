---
masvs_category: MASVS-CODE
platform: android
title: Android ContentProvider
best-practices: [MASTG-BEST-0039]
---

A [`ContentProvider`](https://developer.android.com/reference/android/content/ContentProvider) is an Android component that exposes structured data to other apps and system services through a standardized URI-based interface. Providers support CRUD operations (`query`, `insert`, `update`, `delete`) and are typically backed by an SQLite database, though any data source may be used. Clients interact with a provider through [`ContentResolver`](https://developer.android.com/reference/android/content/ContentResolver) or, on a device shell, via the `content` command.

## URI Structure

Content URIs follow the scheme `content://<authority>/<path>` or `content://<authority>/<path>/<id>`:

- **Authority**: a unique string identifying the provider, for example `com.example.app.provider`, declared in the `<provider android:authorities="…">` element of the manifest.
- **Path**: identifies the resource type or table, for example `students`.
- **ID segment**: an optional integer row identifier appended to the path, for example `students/3`.

## URI Parsing APIs

Android provides several APIs to extract components from a content URI inside a provider implementation:

- `Uri.getPathSegments()` returns a decoded list of path segments after the authority. Index 0 is typically the resource path and index 1, when present, is an ID.
- `Uri.getLastPathSegment()` returns the final path segment.
- `ContentUris.parseId(Uri)` parses and returns a `long` ID from the end of the URI path. It throws `NumberFormatException` if the segment is not a valid integer.

These values are often user-controlled when the provider is exported.

## UriMatcher

[`UriMatcher`](https://developer.android.com/reference/android/content/UriMatcher) maps incoming content URIs to integer constants, allowing a provider to dispatch logic per URI pattern:

```kotlin
val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(AUTHORITY, "students",   STUDENTS)
    addURI(AUTHORITY, "students/#", STUDENT_ID)
}
```

The `#` wildcard matches a single numeric segment. The `*` wildcard matches any string segment.

## SQLiteQueryBuilder

[`SQLiteQueryBuilder`](https://developer.android.com/reference/android/database/sqlite/SQLiteQueryBuilder) is a helper class for constructing SELECT statements in `ContentProvider.query()` implementations.

Key methods:

- `setTables(String)` sets the FROM clause.
- `appendWhere(CharSequence)` appends a condition to the WHERE clause. The provided string is inserted verbatim into the SQL query and is not parameterized.
- `appendWhereEscapeString(String)` appends a condition with escaping applied via `DatabaseUtils.sqlEscapeString()`.
- `query(SQLiteDatabase, String[], String, String[], String, String, String)` builds and executes the query. The `selection` argument is ANDed with any clause added via `appendWhere`.

## selection and selectionArgs

The `query()` method accepts a `selection` string and a `selectionArgs` array. Each `?` placeholder in `selection` is replaced with the corresponding value from `selectionArgs`, and those values are treated strictly as data:

```kotlin
val cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
```

This prevents SQL injection because values are bound, not interpreted as SQL.

In contrast:

- Concatenating values into `selection`
- Passing user-controlled input to `appendWhere`

causes those values to become part of the SQL statement itself, where they are parsed as SQL. This is a common source of SQL injection. See @MASTG-DEMO-XXXX for a concrete example.

## Access Control

A `ContentProvider`'s availability to other apps is governed by attributes in the Android manifest.

- `android:exported`: when `true`, other apps can access the provider, subject to permissions. When `false`, access is limited to the same app or apps sharing the same UID. Since Android 4.2, the default is `false` if no `<intent-filter>` is defined.
- `android:readPermission` and `android:writePermission`: restrict read or write operations to callers holding specific permissions.
- `android:permission`: applies a single permission requirement to both read and write operations.
- `android:grantUriPermissions`: allows temporary, URI-scoped access grants.
- Signature-level permissions restrict access to apps signed with the same certificate.

Exported providers that process user-controlled input without validation are a common attack surface.
