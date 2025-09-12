---
platform: android
title: Injection flaws in android Content providers
id: MASTG-DEMO-0061
code: [kotlin]
test: MASTG-TEST-0288
status: new
profiles: [L1, L2]
---

### Sample

The following code implements a vulnerable `ContentProvider` that appends user-controlled input from the URI path directly into a SQL.

{{ MastgTest.kt # MastgTest_reversed.kt }}

### Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-sql-injection-contentprovider.yml }}

{{ run.sh }}

### Observation

The rule has identified the use of untrusted input from `Uri.getPathSegments().get(...)` being concatenated and passed into `SQLiteQueryBuilder.appendWhere(...)`, which is a known vector for SQL injection in exported `ContentProviders`.

{{ output.txt }}

### Evaluation

This test case fails because the application constructs a SQL `WHERE` clause by directly appending untrusted user input from the URI without any validation or sanitization. This approach allows attackers to perform SQL injection by crafting a malicious `content://` URI to manipulate the query logic. For example, the following content query command can be used to list all names:

`content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob' OR '1'='1'"`
