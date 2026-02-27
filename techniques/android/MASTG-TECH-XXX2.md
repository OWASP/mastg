---
title: ContentProvider URI Injection and Path-Segment Abuse
platform: android
---

Injection vulnerabilities in `ContentProvider`s arise when untrusted input (such as a path segment from `Uri.getPathSegments()` or a selection string supplied by the caller) is directly concatenated into SQL queries rather than being parameterized. A frequent point of concern is `SQLiteQueryBuilder.appendWhere(...)`. The risk is particularly significant for exported providers or those that offer broad read permissions. See @MASTG-TECH-XXX1 for how to interact with ContentProviders using the ADB shell.

## Inputs To Validate

- URI path segments: values from `Uri.getPathSegments()` / `lastPathSegment` concatenated into SQL (for example, `appendWhere("id=" + id)`) are a common injection vector. See @MASTG-BEST-XXXX for safe alternatives.

## Injection Flaw Testing

As a tester, you can investigate the behavior using the Android shell; a positive indication of an issue is when a query retrieves more rows than expected or circumvents filtering.

```bash
adb shell content query --uri content://org.owasp.mastestapp.provider/students
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```

**Injection probe** (only if applicable, to detect unsafe string concatenation in selection logic)

```bash
content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob' OR '1'='1'"
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```

If results exceed the intended filter, the content provider may be concatenating untrusted input instead of using parameterized selections.

## Observe Logs

Use @MASTG-TOOL-0004 to look for `SQLiteException`, syntax errors, or provider log statements that indicate raw string concatenation or leaking SQL statements.

```bash
adb logcat | grep -i -E "sqlite|contentresolver|provider"
```
