---
id: MASTG-DEMO-0137
title: Detecting Unintentionally Exported Activities
platform: android
code: [xml]
kind: fail
---

## Sample

The following reversed `AndroidManifest.xml` snippet shows multiple activities marked as exported. While `MainActivity` must be exported to be launched by the system, `InternalActivity` is intended for internal use and should not be accessible to other apps. Additionally, some activities from external libraries are exported by default.

{{ ../MASTG-DEMO-0136/AndroidManifest.xml # ../MASTG-DEMO-0136/AndroidManifest_reversed.xml }}

## Steps

We can use semgrep to identify all activities that are explicitly exported in the manifest.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The semgrep output lists all activities where `android:exported="true"`.

{{ output.txt }}

## Evaluation

The test case fails because `InternalActivity` is an internal component that has been unintentionally exported. This allows any other application on the device to launch this activity, potentially bypassing intended security controls or intercepting implicit intents.

Review each reported activity and determine whether external access is intentional:

- `MainActivity` is the launcher activity and must be exported.
- `InternalActivity` is an internal component that has no reason to be accessible to other apps.
- `androidx.compose.p000ui.tooling.PreviewActivity` and `androidx.activity.ComponentActivity` are components introduced by external libraries. Verify whether they are necessary in the production build.

**Note on implicit exports (pre-Android 12):** This rule only flags activities with an explicit `android:exported="true"`. On Android versions below API level 31, any activity (or service, or broadcast receiver) that declares an `<intent-filter>` is implicitly exported even without the attribute. From Android 12 (API level 31) onward, `android:exported` must be declared explicitly when an `<intent-filter>` is present. When testing apps targeting older SDK versions or running on older devices, also look for components that have an `<intent-filter>` but no `android:exported` declaration, as these are effectively exported too.
