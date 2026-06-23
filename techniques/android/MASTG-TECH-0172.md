---
title: Listing Deep Links
platform: android
---

## Inspecting the Android Manifest

Extract the `AndroidManifest.xml` (@MASTG-TECH-0117) and analyze it (@MASTG-TECH-0150), looking for [`<intent-filter>` elements](https://developer.android.com/guide/components/intents-filters.html#DataTest "intent-filters - DataTest") that determine whether deep links (with or without custom URL schemes) are defined.

- **Custom Url Schemes**: The following example specifies a deep link with a custom URL scheme called `myapp://`.

  ```xml
  <activity android:name=".MyUriActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="myapp" android:host="path" />
    </intent-filter>
  </activity>
  ```

- **Deep Links**: The following example specifies a deep Link using both the `http://` and `https://` schemes, along with the host and path that will activate it (in this case, the full URL would be `https://www.myapp.com/my/app/path`):

  ```xml
  <intent-filter>
    ...
    <data android:scheme="http" android:host="www.myapp.com" android:path="/my/app/path" />
    <data android:scheme="https" android:host="www.myapp.com" android:path="/my/app/path" />
  </intent-filter>
  ```

- **App Links**: If the `<intent-filter>` includes the flag `android:autoVerify="true"`, this causes the Android system to reach out to the declared `android:host` in an attempt to access the [Digital Asset Links file](https://developers.google.com/digital-asset-links/v1/getting-started "Digital Asset Link") in order to [verify the App Links](https://developer.android.com/training/app-links/verify-android-applinks "Verify Android App Links"). **A deep link can be considered an App Link only if the verification is successful.**

  ```xml
  <intent-filter android:autoVerify="true">
  ```

When listing deep links remember that `<data>` elements within the same `<intent-filter>` are actually merged together to account for all variations of their combined attributes.

```xml
<intent-filter>
  ...
  <data android:scheme="https" android:host="www.example.com" />
  <data android:scheme="app" android:host="open.my.app" />
</intent-filter>
```

It might seem as though this supports only `https://www.example.com` and `app://open.my.app`. However, it actually supports:

- `https://www.example.com`
- `app://open.my.app`
- `app://www.example.com`
- `https://open.my.app`

## Using Dumpsys

Use @MASTG-TOOL-0004 to run the following command that will show all schemes:

```bash
adb shell dumpsys package com.example.package
```

## Using Android "App Link Verification" Tester

Use the [Android "App Link Verification" Tester](https://github.com/inesmartins/Android-App-Link-Verification-Tester) script to list all deep links (`list-all`) or only app links (`list-applinks`):

```bash
python3 deeplink_analyser.py -op list-all -apk ~/Downloads/example.apk

.MainActivity

app://open.my.app
app://www.example.com
https://open.my.app
https://www.example.com
```
