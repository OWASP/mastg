---
platform: android
title: Detecting Emulator Detection Checks with Frida
id: MASTG-DEMO-0114
code: [kotlin]
test: MASTG-TEST-0351
kind: pass
---

## Sample

The snippet below shows sample code that performs common emulator indicator checks and logs the queried values and matches against common emulator values (see @MASTG-KNOW-0031 for more information about common emulator checks and emulator values).

The checks cover several categories (build properties, telephony identifiers, package visibility, and OpenGL renderer information).

Notes about the checks performed:

- The sample avoids `PackageManager.getInstalledPackages()` because Android 11+ requires the `QUERY_ALL_PACKAGES` permission to access the full installed app inventory. Google Play treats that inventory as sensitive and allows it only for apps with a strong, declared need. Instead, this demo uses launcher package queries and explicit checks for known emulator packages.
- The sample avoids Play Integrity checks (@MASTG-KNOW-0035) because they require Play Console configuration and server-side verification, which breaks the self-contained requirement for MASTG demos.
- The manifest declares `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` so the runtime permission prompts can be shown before querying telephony values, and it includes `<queries>` entries for package visibility checks.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0005 to install the app. It does not need to be an emulated device.
2. Use @MASTG-TECH-0043 to trace emulator detection API calls and run `run.sh` to spawn the app.
3. Open the app and grant the `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` permissions when prompted, then tap **Start**.
4. Stop the Frida session by pressing `Ctrl+C`.

{{ run.sh }}

{{ script.js }}

## Observation

The output shows all emulator detection method invocations captured during app execution.

{{ output.txt }}

## Evaluation

The test passes because the output confirms the app implements emulator detection checks that were triggered at runtime:

- **`Build.*` field accesses for build property checks:**
    - The app reads 13 build properties (`Build.BOARD`, `Build.BRAND`, `Build.DEVICE`, `Build.FINGERPRINT`, `Build.MODEL`, `Build.MANUFACTURER`, `Build.PRODUCT`, `Build.HARDWARE`, `Build.ID`, `Build.RADIO`, `Build.SERIAL`, `Build.TAGS`, `Build.USER`) and compares them against known emulator values.
    - Several values are characteristic of an emulated device (e.g., `Build.BOARD=goldfish_arm64`, `Build.DEVICE=emu64a`, `Build.HARDWARE=ranchu`, `Build.TAGS=test-keys`).

- **`PackageManager.hasSystemFeature` calls for feature checks:**
    - The app checks for `android.hardware.type.watch`, `android.hardware.telephony`, and `android.hardware.telephony.calling` to determine device type and telephony capabilities.

- **`TelephonyManager` calls for telephony identifier checks:**
    - The app queries `getLine1Number`, `getNetworkCountryIso`, `getNetworkType`, `getNetworkOperator`, `getNetworkOperatorName`, `getPhoneType`, `getSimCountryIso`, and `getVoiceMailNumber`.
    - The returned values (e.g., `+15551234567` for `getLine1Number`, `T-Mobile` for `getNetworkOperatorName`) are typical emulator defaults.

- **`PackageManager.queryIntentActivities` and `PackageManager.getPackageInfo` calls for emulator package checks:**
    - The app queries launcher packages and checks for known emulator-specific packages such as `com.google.android.launcher.layouts.genymotion`, `com.nox.mopen.app`, `com.bignox.app`, and `com.microvirt`. All return "not found" on this device.

- **`ActivityManager.getRunningServices` calls for emulator service checks:**
    - The app enumerates running services (count=0 on this device) to check for emulator-specific service prefixes such as `com.bluestacks.`.

- **`GLES20.glGetString` calls for OpenGL renderer checks:**
    - The app queries `GL_RENDERER`, `GL_VENDOR`, and `GL_VERSION`.
    - The `GL_RENDERER` value (`Android Emulator OpenGL ES Translator`) is a well-known emulator indicator, confirming the device is running in an emulated environment.
