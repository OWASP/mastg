---
masvs_category: MASVS-PLATFORM
platform: android
title: Explicit vs Implicit Intents
---

An [`Intent`](https://developer.android.com/reference/android/content/Intent) is a messaging object used to request an action from another app component. Intents support three fundamental use cases: starting an activity, starting a service, and delivering a broadcast. See @MASTG-KNOW-0020 for the broader Android IPC model.

Android provides two types of intents, as described in the [Android documentation on intents and intent filters](https://developer.android.com/guide/components/intents-filters#Types):

- **Explicit intents** specify which application will satisfy the intent by providing either the target app's package name or a fully qualified component class name. They are commonly used to start a component in the same app because the caller knows the target activity or service class.

  ```java
  Intent downloadIntent = new Intent(this, DownloadActivity.class);
  downloadIntent.setAction("android.intent.action.GET_CONTENT")
  startActivityForResult(downloadIntent);
  ```

- **Implicit intents** do not name a specific component. They declare an action, and optionally data and categories, that another app component can handle. For example, a caller can use an implicit intent to show a location on a map without selecting a specific map app.

  ```java
  Intent downloadIntent = new Intent();
  downloadIntent.setAction("android.intent.action.GET_CONTENT")
  startActivityForResult(downloadIntent);
  ```

## Intent Resolution

When the system receives an implicit intent, it resolves the target by comparing the intent against installed components that declare matching [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) elements. The [intent resolution algorithm](https://developer.android.com/guide/components/intents-filters#Resolution) evaluates the action, category, data URI, and MIME type declared by the intent and the candidate filters.

Key matching criteria include:

- **Action**: the filter must declare the same action string as the intent.
- **Category**: all categories in the intent must be listed in the filter; the filter may declare additional categories.
- **Data**: the URI scheme, host, path, and MIME type must satisfy the `<data>` constraints in the filter.

If resolution yields more than one matching component, Android can present a chooser or disambiguation dialog that lets the user select the target. If only one component matches, the system routes the intent directly.

!!! note

  If the application targets Android 14 (API level 34) or higher, implicit intents will never be sent to internal components. This feature forces developers to implement explicit intents for internal communication, as otherwise the application would not function correctly while testing.

  See [Android 14 behavior changes](https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents) for more info.

## Intent Filters and Component Visibility

An `<intent-filter>` element in `AndroidManifest.xml` declares the types of intents a component can handle:

```xml
<activity android:name=".InternalActivity" android:exported="true">
    <intent-filter>
        <action android:name="com.example.app.INTERNAL_ACTION" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

Any component that declares a matching intent filter becomes a candidate for receiving the intent, subject to component visibility and access controls. Component access is governed by attributes such as `android:exported` and `android:permission`; see @MASTG-KNOW-0132 for activities, @MASTG-KNOW-0133 for services, and @MASTG-KNOW-0134 for broadcast receivers.

## Activity Results

Activities can return results to callers through [`setResult`](https://developer.android.com/reference/android/app/Activity#setResult(int,android.content.Intent)). Legacy code commonly uses [`startActivityForResult`](https://developer.android.com/reference/android/app/Activity#startActivityForResult(android.content.Intent,int)) and [`onActivityResult`](https://developer.android.com/reference/android/app/Activity#onActivityResult(int,int,android.content.Intent)); newer code uses the [Activity Result APIs](https://developer.android.com/training/basics/intents/result). The result intent can carry data in `Intent.getData()` or extras. See @MASTG-KNOW-0138 for details on URI schemes in returned intent data.

## Package-Scoped Resolution

[`Intent.setPackage`](https://developer.android.com/reference/android/content/Intent#setPackage(java.lang.String)) limits resolution to components in the specified package while still using action-based intent matching:

```kotlin
val intent = Intent("com.example.app.INTERNAL_ACTION").apply {
    setPackage("com.example.app")
}
startActivity(intent)
```
