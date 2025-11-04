---
title: Testing Content URI Injection and Path-Segment Abuse
platform: android
---

Android `ContentProvider`s make structured data available to other applications through `content://` URIs. They specify an authority (a unique identifier), one or more paths (tables/resources), and carry out CRUD operations (`query`, `insert`, `update`, `delete`). Clients interact with them via `ContentResolver` or directly from the device shell. The accessibility of a provider is contingent upon its `exported` setting and any permissions declared in the application's manifest (refer to @MASTG-TECH-0117).

## What They Are

- Interface for cross-app data access and IPC on Android.
- Identified by a URI: `content://<authority>/<path>` or `content://<authority>/<path>/<id>`.
- Backed by storage such as SQLite; many apps use `SQLiteQueryBuilder` in `query`.
- Access control via `android:exported` and read/write permissions; signature-level permissions can restrict access to trusted apps only.

## Using Content query

Use @MASTG-TOOL-0004 to interact with providers on a device or emulator via the `content` command:

- Query rows

```bash
$ adb shell content query --uri content://org.owasp.mastestapp.provider/students
$ adb shell content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob'"
```

- Insert a row

```bash
$ adb shell content insert \
    --uri content://org.owasp.mastestapp.provider/students \
    --bind name:s:"Eve"
```

- Update rows

```bash
$ adb shell content update \
    --uri content://org.owasp.mastestapp.provider/students \
    --where "id=1" \
    --bind name:s:"Alice Jr"
```

- Delete rows

```bash
$ adb shell content delete --uri content://org.owasp.mastestapp.provider/students --where "id=3"
```

## Inputs To Validate

- URI path segments
  - Risk: values from `Uri.getPathSegments()` / `lastPathSegment` concatenated into SQL (for example, `appendWhere("id=" + id)`).
  - Safer: parse numeric IDs with `ContentUris.parseId(uri)`; strictly validate/whitelist path segments; never concatenate untrusted data into SQL.

## Injection Flaw Testing

Injection vulnerabilities in `ContentProvider`s usually arise when untrusted input (such as a path segment from `Uri.getPathSegments()` or a selection string provided by the caller) is directly concatenated into SQL queries rather than being parameterized. A frequent point of concern is `SQLiteQueryBuilder.appendWhere(...)`. The potential risk is particularly significant for exported providers or those that offer extensive read permissions. As a tester, you can investigate the behavior using the Android shell; a positive indication of an issue is when a query retrieves more rows than expected or circumvents filtering.

```bash
$ adb shell content query --uri content://org.owasp.mastestapp.provider/students
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```

**Injection probe**(only if applicable, to detect unsafe string concatenation in selection logic)

```bash
$ content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob' OR '1'='1'"
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```

If results exceed the intended filter, the content provider may be concatenating untrusted input instead of using parameterized selections.

## Observe logs

Use @MASTG-TOOL-0004 to look for `SQLiteException`, syntax errors, or provider log statements that indicate raw string concatenation or leaking SQL statements.

```bash
$ adb logcat | grep -i -E "sqlite|contentresolver|provider"
```
