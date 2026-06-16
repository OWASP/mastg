---
title: Sensitive Data Exposed Through an Unprotected Activity
platform: android
id: MASTG-DEMO-0128
code: [kotlin]
test: MASTG-TEST-0364
kind: fail
---

## Sample

The sample app performs a login flow by using two activities. Tapping **Start** in the main screen launches `PinEntryActivity`, which prompts for a PIN (4321) before proceeding to `SecretActivity`. `SecretActivity` displays sensitive account data and is meant to be reachable only after the user passes the PIN check.

However, `SecretActivity` is declared as exported in the `AndroidManifest.xml` without an `android:permission`. This allows third-party apps, or `adb`, to start `SecretActivity` directly without interacting with `PinEntryActivity`, bypassing the PIN gate entirely.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0160 to list the exported activities and their associated `android:permission` by running `run.sh`.

{{ run.sh }}

## Observation

The output reveals the exported activities and their associated permissions:

{{ output.txt }}

## Evaluation

The test case fails because `SecretActivity` exposes sensitive functionality and is exported (`android:exported="true"`) without any permission protection, so external callers can start it directly by using an explicit intent.

`PinEntryActivity` does not protect the underlying exported activity; access control must be enforced at the `SecretActivity` boundary.

The activity displays account data in `onCreate` without checking whether the user completed the PIN challenge:

```kotlin
class SecretActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... displays account number, balance, and recovery PIN ...
    }
}
```

The output also lists other exported activities. These are triaged but not reported as vulnerable in this test case.

`MainActivity` is the launcher activity. Launcher activities normally need to be exported so Android and the launcher can start the app. [Android's guidance](https://developer.android.com/about/versions/12/behavior-changes-12#exported) says activities with the `LAUNCHER` category should use `android:exported="true"`, while most other components should use `false`.

`androidx.activity.ComponentActivity` is commonly added by the Compose UI test manifest as a generic host activity for Compose tests. This is expected in debug or test builds, but should be reviewed if it appears in a production build.

`androidx.compose.ui.tooling.PreviewActivity` is a Compose tooling activity used by Android Studio to run composable previews. It is not part of the app's authentication flow and should normally be treated as development tooling unless the tested build is intended for production.
