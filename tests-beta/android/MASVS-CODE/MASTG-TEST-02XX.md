---
title: SQL Injection in ContentProvider
platform: android
id: MASTG-TEST-02XX
type: [static]
weakness: MASWE-0086
best-practices: [MASTG-BEST-XXXX]
profiles: [L1, L2]
---

## Overview

Android applications can share structured data via `ContentProvider` components. However, if these providers create SQL queries using untrusted input from URIs without adequate validation or parameterization, they risk becoming susceptible to SQL injection attacks.

## Steps

1. Run @MASTG-TOOL-0110 rule against the code file to detect SQL injection caused by unvalidated use of `Uri.getPathSegments()` inside `appendWhere()`.

## Observation

This code uses untrusted input from the URI path directly in a SQL query, enabling potential SQL injection.

```java
String id = uri.getPathSegments().get(1);
qb.appendWhere("id=" + id);
```

## Evaluation

The test fails if:

- Untrusted user input (e.g., from `getPathSegments()`) is directly concatenated into SQL statements.
- The app uses `appendWhere()` or builds queries unsafely without sanitization or parameterization.
