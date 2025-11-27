---
platform: android
title: Sensitive Data Stored Unencrypted via Android Room DB
id: MASTG-TEST-0306
type: [static, dynamic]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0074]
profiles: [L1, L2]
status: new
---

## Overview

This test verifies whether an app stores sensitive data — such as tokens, credentials, or personally identifiable information (PII) — using the Android Room Persistence Library without encryption.
By default, Room persists data in an unencrypted SQLite database within the app sandbox. **Encryption is not built-in**; developers must manually integrate an encryption mechanism, such as SQLCipher for Android.
The goal of this test is to detect insecure storage of sensitive information in Room database files within the application sandbox.

---

## Steps

### Static Analysis

1. Obtain the application package (e.g., APK file) using @MASTG-TECH-0003.
2. Use a static analysis technique (@MASTG-TECH-0014) to identify references to Room APIs such as:
    - `androidx.room.Room`
    - `@Database`, `@Dao`, `@Entity` annotations
    - `databaseBuilder`, `build` calls, and `SupportSQLiteOpenHelper.Factory` implementations.
3. Inspect the database creation logic to determine whether:
    - sensitive fields (tokens, secrets, PII) are defined in `@Entity` classes and stored in plaintext.
    - a secure factory or wrapper (e.g., passing an implementation of `SupportSQLiteOpenHelper.Factory` for SQLCipher) is explicitly applied to the database builder.

### Dynamic Analysis

1. Install and run the app on a rooted or emulated device (@MASTG-TECH-0005).
2. Trigger app functionality that processes or stores sensitive data.
3. Access the app's private storage and locate the Room database file and related files. This requires accessing the app data directories (@MASTG-TECH-0008). Files are typically found under:
    - `/data/data/<package_name>/databases/<database_name>`
    - `/data/data/<package_name>/databases/<database_name>-wal`
    - `/data/data/<package_name>/databases/<database_name>-shm`
4. **Extract** the database files from the device to the host machine using @MASTG-TECH-0003.
5. Inspect the database content using a SQLite client tool, applying the technique for Dynamic Analysis (@MASTG-TECH-0015) to confirm whether sensitive data is stored in plaintext.

---

## Observation

The output should indicate:

- which Room database files the app creates.
- whether sensitive data (tokens, secrets, PII) is present in tables (mapped from `@Entity` classes).
- whether the stored values appear in plaintext.

---

## Evaluation

The test fails if:

- sensitive data is stored in Room database files without explicit encryption integration (e.g., SQLCipher).
- plaintext tokens, secrets, or PII can be read from the database files through static or dynamic analysis.
