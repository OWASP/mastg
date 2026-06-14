---
platform: android
title: Uses of Unauthorized Access to Exported Content Providers
id: MASTG-DEMO-0120
code: [kotlin]
kind: fail
test: MASTG-TEST-0355
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0121.

The code below implements an `AppointmentProvider` backed by a SQLite database that stores sensitive patient data. The provider is registered in the `AndroidManifest.xml` with `android:exported="true"` and no `android:readPermission` or `android:writePermission` and `protectionLevel`, allowing any app on the device to query sensitive patient data.

{{ ../MASTG-DEMO-0121/MastgTest.kt # ../MASTG-DEMO-0121/MastgTest_reversed.java # ../MASTG-DEMO-0121/AndroidManifest.xml # ../MASTG-DEMO-0121/AndroidManifest_reversed.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-content-provider-exported.yml }}

{{ run.sh }}

## Observation

The output should contain a finding that a `<provider>` is exported (`android:exported="true"`) without `android:readPermission`, `android:writePermission`, or `android:permission`.

{{ output.txt }}

## Evaluation

The test case fails because the exported `AppointmentProvider` exposes sensitive patient medical records through an exported database-backed content provider without enforcing appropriate access restrictions.

This means any app on the device is allowed to query the content provider to retrieve the data that is offered by the database.
