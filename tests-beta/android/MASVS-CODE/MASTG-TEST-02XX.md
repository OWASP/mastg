---
title: SQL Injection in Content Providers
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

1. Run @MASTG-TECH-XXX2 on the app to identify unsafe SQL construction in ContentProviders.

## Observation

The output should contain the location in the code where untrusted input from `Uri.getPathSegments()` is concatenated into a SQL query via `SQLiteQueryBuilder.appendWhere()`.

## Evaluation

The test case fails if:

- Untrusted user input (e.g., from `getPathSegments()`) is directly concatenated into SQL statements.
- The app uses `appendWhere()` or builds queries unsafely without sanitization or parameterization.
