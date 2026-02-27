---
masvs_category: MASVS-CODE
platform: android
title: Android ContentProvider
best-practices: [MASTG-BEST-XXXX]
---

A [`ContentProvider`](https://developer.android.com/reference/android/content/ContentProvider) is an Android component that exposes structured data to other apps and system services through a standardized URI-based interface. Providers support CRUD operations (`query`, `insert`, `update`, `delete`) and are typically backed by an SQLite database, though any data source may be used. Clients interact with a provider through [`ContentResolver`](https://developer.android.com/reference/android/content/ContentResolver) or, on a device shell, via the `content` command.

## URI Structure

Content URIs follow the scheme `content://<authority>/<path>` or `content://<authority>/<path>/<id>`:

- **Authority**: a unique string identifying the provider (for example, `com.example.app.provider`), declared in the `<provider android:authorities="…">` element of the manifest.
- **Path**: identifies the resource type or table (for example, `students`).
- **ID segment** (optional): an integer row identifier appended to the path (for example, `students/3`).

## URI Parsing APIs

Android provides several APIs to extract components from a content URI inside a provider implementation:

- `Uri.getPathSegments()` returns a decoded list of path segments after the authority. Index 0 is typically the resource path and index 1, when present, is an ID.
- `Uri.getLastPathSegment()` is a convenience method returning the final path segment.
- `ContentUris.parseId(Uri)` parses and returns a `long` ID from the end of the URI path. It throws `NumberFormatException` if the segment is not a valid long integer.

## UriMatcher

[`UriMatcher`](https://developer.android.com/reference/android/content/UriMatcher) maps incoming content URIs to integer constants, allowing a provider to dispatch logic per URI pattern:

```kotlin
val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(AUTHORITY, "students",   STUDENTS)    // matches content://…/students
    addURI(AUTHORITY, "students/#", STUDENT_ID)  // # matches a single numeric segment
}
```

The `#` wildcard matches a single numeric segment; `*` matches any string segment.

## SQLiteQueryBuilder

[`SQLiteQueryBuilder`](https://developer.android.com/reference/android/database/sqlite/SQLiteQueryBuilder) is a helper class for constructing SELECT statements in `ContentProvider.query()` implementations. Its primary methods are:

- `setTables(String)`: sets the FROM clause table name(s).
- `appendWhere(CharSequence)`: appends a condition to the WHERE clause; successive calls are ANDed together. The appended string is incorporated verbatim into the final SQL statement.
- `appendWhereEscapeString(String)`: appends a condition after escaping the value with `DatabaseUtils.sqlEscapeString()`.
- `query(SQLiteDatabase, String[], String, String[], String, String, String)`: builds and executes the query. The `selection` argument is ANDed with any clause previously set via `appendWhere`.

## selection and selectionArgs

The `query()` method on both `SQLiteQueryBuilder` and `ContentResolver` accepts two related parameters: a `selection` string and a `selectionArgs` array. The SQLite layer substitutes each `?` placeholder in `selection` with the corresponding value from `selectionArgs`, treating each value as a literal data element rather than SQL syntax:

```kotlin
// Values in selectionArgs are bound as parameters, not interpreted as SQL
val cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
```

In contrast, values concatenated directly into `selection` or passed to `appendWhere` become part of the SQL string itself and are parsed as SQL by the SQLite engine. See @MASTG-DEMO-XXXX for a concrete example.

## Access Control

A `ContentProvider`'s availability to other apps is governed by attributes in the Android manifest.

- `android:exported`: when `true`, any app can reach the provider, subject to declared permissions. When `false`, access is limited to the same app or apps sharing a `sharedUserId`. Since Android 4.2 (API level 17), the default is `false` for providers that declare no `<intent-filter>`.
- `android:readPermission` and `android:writePermission`: require a named permission for query or modification operations, respectively.
- `android:permission`: a single attribute covering both read and write access.
- `android:grantUriPermissions`: enables temporary, URI-scoped access grants independent of the provider-wide permission.
- Signature-level permissions (`android:protectionLevel="signature"`): restrict access to apps signed with the same certificate, typically used to limit exposure to trusted first-party components.
