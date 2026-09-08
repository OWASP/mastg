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
```

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
