---
platform: android
title: Unauthorized Access to Database Records through Exported Content Provider
id: MASTG-DEMO-0121
code: [kotlin]
kind: fail
test: MASTG-TEST-0356
---

## Sample

This sample demonstrates the insecure export of Content Providers, allowing unauthorized access.

The `android:authorities` value in the Android Manifest identifies the provider in `content://` URIs and is the entry point for dynamic testing.

The reverse engineered AndroidManifest only reveals the authority via `android:authorities="org.owasp.mastestapp.appointments"`, but the path segment must be discovered by reverse-engineering the provider class. The path can be identified in the reversed class `MastgTest_reversed.java` in the method `addURI`, which is either `appointments` to retrieve all rows from the database or `appointments/#` to query for one specific row.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml}}

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Tap the **Start** button.
3. Run `run.sh`.
4. Use @MASTG-TECH-0148 to query the exported content provider.

{{ run.sh }}

## Observation

The output contains patient appointment records returned through the exported content provider, confirming no access control is in place.

{{ output.txt }}

## Evaluation

The test case fails because the exported `AppointmentProvider` allows any external caller to query patient data without holding any permission.

A malicious app could implement the following:

```java
// Grab all patient records
contentResolver.query(

Uri.parse("content://org.owasp.mastestapp.appointments/appointments"),
    null, null, null, null
)

// Or target a specific patient by ID
contentResolver.query(

Uri.parse("content://org.owasp.mastestapp.appointments/appointments/1"),
    null, null, null, null
)
```

The returned records contain sensitive health information: patient names, dates of birth, diagnoses, and physician notes.

A secure implementation would either keep the provider non-exported or guard it with a signature-level permission, in which case `adb shell content` would receive a `SecurityException` instead of data.
