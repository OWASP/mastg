---
masvs_category: MASVS-STORAGE
platform: android
title: Android DataStore
available_since: 21
---

[Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) is an Android data storage library designed as the modern replacement for [`SharedPreferences`](https://developer.android.com/training/data-storage/shared-preferences). It is therefore suitable for data like user preferences or app settings but not for complex or large datasets such as media data or relation data.

It stores key-value pairs or typed objects asynchronously using Kotlin coroutines and Flow, providing a non-blocking, consistent API.

DataStore comes in two flavors:

- **Preferences DataStore**: stores and accesses untyped key-value pairs, similar to `SharedPreferences`.
- **`Serializer<T>` DataStore**: stores any object which implements `Serializer` for the type `T` providing type safety at compile time.

> Well suited objects for serialization are [Protocol Buffers](https://protobuf.dev/) and [JSON](https://developer.android.com/topic/libraries/architecture/datastore#json-serialization)

## Storage Location

Both DataStore variants write their data to the app's internal storage, under the directory `/data/data/<package-name>/files/datastore/`.

Preferences are stored as serialized protocol buffer in a file called `<name>.preferences_pb`, while custom serialized objects are stored in the file name you provide (for example, `settings.pb`) when initializing DataStore.

The data is stored in protobuf binary format, not in plain-text XML like `SharedPreferences`. The files are not encrypted by default.

## API Overview

### Preferences DataStore

A `DataStore<Preferences>` instance is typically created at the top level using a file-delegate:

```kotlin
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
```

Data is read via a `Flow`:

```kotlin
private val LANGUAGE_KEY = stringPreferencesKey("language")
val value: Flow<String?> = context.dataStore.data.map { preferences ->
    preferences[LANGUAGE_KEY]
}
```

Data is written with a suspending `edit` call:

```kotlin
context.dataStore.edit { preferences ->
    preferences[LANGUAGE_KEY] = "kotlin"
}
```

### `Serializer<T>` DataStore

A `DataStore<T>` instance for any serializable type `T` requires a custom `Serializer<T>` and is created with the `dataStore` delegate:

```kotlin
val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)
```

Reads and writes follow the same coroutine-based `data` Flow and `updateData` API as Preferences DataStore.

## Encryption

Neither Preferences DataStore nor Proto DataStore encrypts data at rest by default. The `Serializer` can be wrapped with custom encryption logic using the [Android Keystore](https://developer.android.com/training/articles/keystore) or a library such as [Tink](https://developers.google.com/tink) to encrypt data at rest.

## Backup and Device-Transfer Behavior

DataStore files stored under the app's internal `files/datastore/` directory are included in [Android Auto Backup](https://developer.android.com/identity/data/autobackup) and device-to-device transfers by default.

Auto Backup is available for apps that target Android 6.0 (API level 23), or higher. They can exclude specific DataStore files or directories using `android:fullBackupContent`. On Android 12 (API level 31 ) and higher, apps can use `android:dataExtractionRules` for backup configuration.
