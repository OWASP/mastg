---
title: SQL Injection in Content Providers
platform: android
id: MASTG-TEST-0339
type: [static]
weakness: MASWE-0086
best-practices: [MASTG-BEST-0039]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0117]
---

## Overview

Android applications can share structured data via `ContentProvider` components. However, if these providers create SQL queries using untrusted input from URIs without adequate validation or parameterization, they risk becoming susceptible to SQL injection attacks.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0013).
2. Run static analysis (@MASTG-TECH-0014) to search for unsafe SQL construction in ContentProviders.

## Observation

The output should contain a list of locations where user-controlled input from URIs or selection arguments is concatenated into SQL queries, for example via `Uri.getPathSegments()` and `SQLiteQueryBuilder.appendWhere()`.

## Evaluation

The test case fails if:

- Untrusted user input (e.g., from `getPathSegments()`) is directly concatenated into SQL statements.
- The app uses `appendWhere()` or builds queries unsafely without sanitization or parameterization.
