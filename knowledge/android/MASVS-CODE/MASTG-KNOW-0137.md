---
masvs_category: MASVS-CODE
platform: android
title: Android Implicit Intents
---

An [Intent](https://developer.android.com/reference/android/content/Intent) is an Android messaging object that requests an action from another app component.

## Implicit Intents

An implicit intent does not name a target component. Instead, it declares an abstract _action_ (and optionally a _data URI_ and _category_) that the system uses to find a matching component across all installed apps.

```kotlin
val intent = Intent("com.example.app.INTERNAL_ACTION")
startActivity(intent)
```

In contrast, an explicit intent identifies the target component directly by its class name (for example, `Intent(context, InternalActivity::class.java)`), so the system delivers it to that exact component without any resolution step.

## Intent Resolution

When the system receives an implicit intent, it queries the [`PackageManager`](https://developer.android.com/reference/android/content/pm/PackageManager) for all activities whose `<intent-filter>` elements match the requested action, data URI scheme, and category. The resolution algorithm is described in the [Android documentation on intent resolution](https://developer.android.com/guide/components/intents-filters#Resolution).

Key matching criteria:

- **Action**: the `<action>` element must declare the same action string as the intent.
- **Category**: all categories in the intent must be listed in the filter; the filter may declare additional ones.
- **Data**: the URI scheme, host, path, and MIME type must satisfy the `<data>` constraints in the filter.

## Intent Filters

An `<intent-filter>` element in `AndroidManifest.xml` declares the types of intents a component can handle:

```xml
<activity android:name=".InternalActivity" android:exported="true">
    <intent-filter>
        <action android:name="com.example.app.INTERNAL_ACTION" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

Any component that declares a matching `<intent-filter>` becomes a candidate for receiving the intent. Components with an `<intent-filter>` are exported by default on Android versions below Android 12 (API level 31); from Android 12 onward, `android:exported` must be declared explicitly.

## System Picker Dialog

If resolution yields more than one matching component, the system presents a chooser dialog (also called the disambiguation dialog or app picker) that lets the user select the target. If only one component matches, the system routes the intent directly without showing the dialog.

Any app that registers an `<intent-filter>` for the same action can become a candidate. The [`android:priority`](https://developer.android.com/guide/topics/manifest/intent-filter-element#priority) attribute in an intent filter influences the order in which the system considers candidates; higher values are considered first.

## `startActivityForResult` and `onActivityResult`

[`startActivityForResult`](https://developer.android.com/reference/android/app/Activity#startActivityForResult(android.content.Intent,int)) launches an activity and expects a result in return. When the started activity finishes, it calls [`setResult`](https://developer.android.com/reference/android/app/Activity#setResult(int,android.content.Intent)) and the calling activity receives the result in [`onActivityResult`](https://developer.android.com/reference/android/app/Activity#onActivityResult(int,int,android.content.Intent)):

```kotlin
// Caller
startActivityForResult(intent, REQUEST_CODE)

// Receiving the result
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        val uri = data?.data
        // process uri
    }
}
```

The result intent can carry a URI (`data`) or extras back to the caller. See @MASTG-KNOW-0138 for details on how URI schemes affect the data returned by the responding app.

## Restricting Resolution with `setPackage`

[`Intent.setPackage`](https://developer.android.com/reference/android/content/Intent#setPackage(java.lang.String)) limits resolution to components within the specified package, turning an otherwise implicit intent into a package-scoped one:

```kotlin
val intent = Intent("com.example.app.INTERNAL_ACTION").apply {
    setPackage("com.example.app")
}
startActivity(intent)
```

This prevents other installed apps from receiving the intent while preserving the flexibility of action-based addressing.
