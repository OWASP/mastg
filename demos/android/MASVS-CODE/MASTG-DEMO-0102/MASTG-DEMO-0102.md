---
platform: android
title: SQL Injection via URI Path and Selection in Android Content Providers
id: MASTG-DEMO-0102
code: [kotlin]
test: MASTG-TEST-0339
---

## Sample

The following code implements a vulnerable `ContentProvider` that demonstrates two SQL injection cases.

The first case passes caller-controlled `selection` input into a SQL query.

The second case appends user-controlled input from the URI path directly into a SQL query.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-sql-injection-contentprovider.yml }}

{{ run.sh }}

## Observation

The rule identified two uses of data from `Uri.getPathSegments()` being passed into `SQLiteQueryBuilder.appendWhere(...)`.

{{ output.txt }}

The first finding uses the `students/#` route, which is constrained by `UriMatcher` to numeric path segments. The second finding uses the `students/filter/*` route, which accepts arbitrary path input and is exploitable as path based SQL injection.

## Evaluation

This test case fails because the application constructs SQL `WHERE` clauses using untrusted, user-controlled input from two different sources.

The vulnerable data flow is visible in the reversed code. The provider registers both a numeric route and an arbitrary path segment route:

```java
$this$uriMatcher_u24lambda_u240.addURI(AUTHORITY, "students/#", 2);
$this$uriMatcher_u24lambda_u240.addURI(AUTHORITY, "students/filter/*", 3);
````

For the path-based case, the provider reads a user-controlled URI segment and appends it directly into the SQL query:

```java
String filter = uri.getPathSegments().get(2);
qb.appendWhere(filter);
```

For the selection-based case, the provider passes the user-controlled `selection` argument directly into the query without validation:

```java
Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
```

These two code paths create separate SQL injection vectors.

### Exploitation

You can use @MASTG-TECH-0148 to interact with the `ContentProvider` and confirm the injection vulnerabilities.

**Selection-based SQL Injection:**

An attacker can inject SQL through the `--where` argument of the `content` command:

```bash
adb shell 'content query --uri content://org.owasp.mastestapp.provider/students --where "name='\''Bob'\'' OR '\''1'\''='\''1'\''"'
```

Output:

```text
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```

**Path-based SQL Injection:**

An attacker can inject SQL through the URI path using the `students/filter/*` route:

```bash
adb shell 'content query --uri "content://org.owasp.mastestapp.provider/students/filter/id%3D2%20OR%201%3D1"'
```

Output:

```text
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```

The `students/#` route is limited to numeric input by `UriMatcher` and isn't practically exploitable, but it's still flagged because it demonstrates unsafe concatenation of user-controlled data into a SQL query.

Both vulnerabilities arise from directly incorporating untrusted input into SQL statements instead of using parameterized queries or proper input validation.
