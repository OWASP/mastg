---
masvs_category: MASVS-RESILIENCE
platform: android
title: Emulator Detection
---

In the context of anti-reversing, the goal of emulator detection is to increase the difficulty of running the app on an emulated device. This increased difficulty forces the reverse engineer to defeat the emulator checks or use a physical device, thereby limiting the access required for large-scale device analysis.

!!! note
    Emulator detection is inherently a cat-and-mouse game. Detection methods and bypass techniques evolve continuously, and determined attackers with sufficient time and resources can circumvent these protections, for example, by developing custom AOSP builds. These techniques should be part of a defense-in-depth strategy, not a standalone solution.

There are several indicators that the device in question is being emulated.

## Build Characteristics

Apps can read build properties through [`android.os.Build`](https://developer.android.com/reference/android/os/Build). Emulators often ship with default or vendor-specific values.

| Field | Example emulator values or patterns |
| --- | --- |
| `Build.FINGERPRINT` | starts with `generic`, contains `test-keys`, contains `generic/sdk/generic`, `generic x86`, `vbox86p`, `ttvm` |
| `Build.MODEL` | `sdk`, `google_sdk`, `emulator`, `android sdk built for x86`, `android sdk built for x86_64`, `droid4x`, `tiantianvm`, `genymotion`, `andy`, `nox` |
| `Build.MANUFACTURER` | `unknown`, `genymotion`, `droid4x`, `tiantianvm`, `andy` |
| `Build.HARDWARE` | `goldfish`, `ranchu`, `vbox86`, `nox`, `ttvm` |
| `Build.PRODUCT` | `sdk`, `google_sdk`, `sdk_x86`, `sdk_google`, `vbox86p`, `droid4x`, `andy`, `ttvm`, `nox`, starts with `itoolsavm` |
| `Build.BRAND` / `Build.DEVICE` | start with `generic`, include `generic x86`, `vbox86p`, `ttvm`, `andy`, `nox` |
| `Build.BOARD` | `unknown`, contains `nox` |
| `Build.SERIAL` | `unknown`; deprecated, check for `Build.getSerial()` instead |
| `Build.ID` | `frf91` |
| `Build.RADIO` | blank or `unknown`; deprecated, check for `Build.getRadioVersion()` instead |
| `Build.TAGS` | `test-keys` |
| `Build.USER` | `android-build` |

Notes: It is recommended to normalize to lowercase when checking these values.

## Telephony Characteristics

Check telephony only when the device reports the relevant telephony feature, such as [`FEATURE_TELEPHONY`](https://developer.android.com/reference/android/content/pm/PackageManager#FEATURE_TELEPHONY) or [`FEATURE_TELEPHONY_CALLING`](https://developer.android.com/reference/android/content/pm/PackageManager#FEATURE_TELEPHONY_CALLING), depending on the API being used. Emulators often return fixed identifiers through [`TelephonyManager`](https://developer.android.com/reference/android/telephony/TelephonyManager).

| Method | Example emulator values |
| --- | --- |
| `getLine1Number()` | `15555215554` ... `15555215584` (even suffixes) |
| `getNetworkOperatorName()` | `android` |
| `getVoiceMailNumber()` | `15552175049` |

Permission notes:

- [`getLine1Number()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getLine1Number()) requires phone-number access, such as [`READ_PHONE_NUMBERS`](https://developer.android.com/reference/android/Manifest.permission#READ_PHONE_NUMBERS), [`READ_SMS`](https://developer.android.com/reference/android/Manifest.permission#READ_SMS), default SMS app status, or carrier privileges. On older target SDK versions, [`READ_PHONE_STATE`](https://developer.android.com/reference/android/Manifest.permission#READ_PHONE_STATE) may also apply.
- [`getLine1Number()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getLine1Number()) is deprecated as of Android 13 (API level 33). Use [`SubscriptionManager.getPhoneNumber(int)`](https://developer.android.com/reference/android/telephony/SubscriptionManager#getPhoneNumber(int)) instead.
- [`getVoiceMailNumber()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getVoiceMailNumber()) requires [`READ_PHONE_STATE`](https://developer.android.com/reference/android/Manifest.permission#READ_PHONE_STATE).

## Package Name Indicators

Use [`PackageManager`](https://developer.android.com/reference/android/content/pm/PackageManager) to inspect installed packages or launcher activities for known emulator prefixes:

`com.vphone.`, `com.bignox.`, `com.nox.mopen.app`, `me.haima.`, `com.bluestacks`, `cn.itools.`, `com.kop.`, `com.kaopu.`, `com.microvirt.`, `com.bignox.app`, and the exact package `com.google.android.launcher.layouts.genymotion`. Some checks also flag `Build.PRODUCT` starting with `iToolsAVM`.

On Android 11 (API level 30) and later, [package visibility restrictions](https://developer.android.com/training/package-visibility) affect package-based emulator detection. If a package is installed but not visible to the app, [`getPackageInfo`](https://developer.android.com/reference/android/content/pm/PackageManager#getPackageInfo(java.lang.String,%20int)) behaves the same as if the package were not installed, typically by throwing [`PackageManager.NameNotFoundException`](https://developer.android.com/reference/android/content/pm/PackageManager.NameNotFoundException). This can create false negatives for package-based emulator detection.

Developers can query specific packages on Android 11 (API level 30) and later by declaring them in the app manifest using the `<queries>` element:

```xml
<queries>
    <package android:name="com.bignox.app" />
</queries>
```

Otherwise, they can use the `QUERY_ALL_PACKAGES` permission, which grants visibility to all installed apps but is [subject to Google Play restrictions](https://support.google.com/googleplay/android-developer/answer/10158779) and may not be justifiable for many use cases.

## Available Activities and Services

Apps can query launcher activities with [`Intent.ACTION_MAIN`](https://developer.android.com/reference/android/content/Intent#ACTION_MAIN) and [`Intent.CATEGORY_LAUNCHER`](https://developer.android.com/reference/android/content/Intent#CATEGORY_LAUNCHER) and look for emulator package prefixes, often `com.bluestacks.`.

Launcher activity discovery through [`PackageManager.queryIntentActivities()`](https://developer.android.com/reference/android/content/pm/PackageManager#queryIntentActivities(android.content.Intent,%20int)) is also affected by Android 11 (API level 30) and later package visibility restrictions. Apps should declare the matching intent signature in the `<queries>` element so it can return relevant launcher activities:

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

[`ActivityManager.getRunningServices`](https://developer.android.com/reference/android/app/ActivityManager#getRunningServices(int)) is restricted on Android 8.0 (API level 26) and later and only returns the app's own services.

## File System Artifacts

Apps can check for emulator-specific files, sockets, and device nodes using standard file APIs such as [`java.io.File.exists()`](https://developer.android.com/reference/java/io/File#exists()). Common examples include:

| Path | Description |
| --- | --- |
| `/dev/socket/qemud` | QEMU daemon socket |
| `/dev/qemu_pipe` | QEMU communication pipe |
| `/dev/goldfish_pipe` | Goldfish emulator pipe |
| `/sys/qemu_trace` | QEMU trace marker |
| `/dev/socket/genyd` | Genymotion daemon socket |
| `/dev/socket/baseband_genyd` | Genymotion baseband socket |

## OpenGL Renderer

Create an EGL context and read [`GLES20.glGetString(GL_RENDERER)`](https://developer.android.com/reference/android/opengl/GLES20#glGetString(int)). Renderer strings that contain `Bluestacks` or `Translator` can indicate emulators.

## Deprecated or Advanced Techniques

Starting in Android 10 (API level 29), non-resettable identifiers, such as IMEI, serial number, SIM serial number, and subscriber ID, are restricted. Apps must have the privileged [`READ_PRIVILEGED_PHONE_STATE`](https://developer.android.com/reference/android/Manifest.permission#READ_PRIVILEGED_PHONE_STATE) permission in order to access the device's non-resettable identifiers. Affected methods include [`Build.getSerial()`](https://developer.android.com/reference/android/os/Build#getSerial()), [`TelephonyManager.getImei()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getImei()), [`getDeviceId()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getDeviceId()), [`getMeid()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getMeid()), [`getSimSerialNumber()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getSimSerialNumber()), and [`getSubscriberId()`](https://developer.android.com/reference/android/telephony/TelephonyManager#getSubscriberId()). If an app targets Android 10 (API level 29) or higher and tries asking for information about non-resettable identifiers without the permission, a [`SecurityException`](https://developer.android.com/reference/java/lang/SecurityException) occurs. If the app is the device or profile owner app, it needs only the [`READ_PHONE_STATE`](https://developer.android.com/reference/android/Manifest.permission#READ_PHONE_STATE) permission, even if it targets Android 10 (API level 29) or higher. If the app has special carrier permissions, it does not need any permissions to access the identifiers. See [Android 10 privacy changes](https://developer.android.com/about/versions/10/privacy/changes#non-resettable-device-ids).

`netcfg`-based IP detection no longer works on modern Android and has been deprecated since Android 6.0 (API level 23).

## Google Play Integrity API

The app can also use the Google Play Integrity API to request an integrity verdict for the app and device environment. This API performs checks that can help detect requests from emulated environments. See @MASTG-KNOW-0035 for more details on the Google Play Integrity API.
