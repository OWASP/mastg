---
platform: android
title: Injection Flaws in Android Content Providers
id: MASTG-DEMO-00XX
code: [kotlin]
test: MASTG-TEST-02XX
status: new
---

## Sample

The following code implements a vulnerable `ContentProvider` that appends user-controlled input from the URI path directly into a SQL query.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-sql-injection-contentprovider.yml }}

{{ run.sh }}

## Observation

The rule has identified the use of untrusted input from `Uri.getPathSegments().get(...)` being concatenated and passed into `SQLiteQueryBuilder.appendWhere(...)`, which is a known vector for SQL injection in exported `ContentProviders`.

{{ output.txt }}

## Evaluation

This test case fails because the application constructs a SQL `WHERE` clause by directly appending untrusted user input from the URI without any validation or sanitization. This approach allows attackers to perform SQL injection by crafting a malicious `content://` URI to manipulate the query logic. For example, the following content query command can be used to list all names:

```bash
content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob' OR '1'='1'"
Row: 0 id=1, name=Alice
Row: 1 id=2, name=Bob
Row: 2 id=3, name=Charlie
```
