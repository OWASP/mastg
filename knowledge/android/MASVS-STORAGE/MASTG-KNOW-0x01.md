---
masvs_category: MASVS-STORAGE
platform: android
title: Android DataStore
available_since: 21
---

[Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) is an Android data storage library designed as the modern replacement for [`SharedPreferences`](https://developer.android.com/training/data-storage/shared-preferences). It stores key-value pairs or typed objects asynchronously using Kotlin coroutines and Flow, providing a non-blocking, consistent API.

DataStore comes in two flavors:

- **Preferences DataStore**: stores and accesses untyped key-value pairs, similar to `SharedPreferences` but without an XML schema.
- **Proto DataStore**: stores typed objects defined with [Protocol Buffers](https://protobuf.dev/) (protobuf), providing type safety at compile time.

## Storage Location

Both DataStore variants write their data to the app's internal storage, under the app-specific directory:

- Preferences DataStore: `/data/data/<package-name>/files/datastore/<filename>.preferences_pb`
- Proto DataStore: `/data/data/<package-name>/files/datastore/<filename>.pb`

The data is stored in protobuf binary format, not in plain-text XML like `SharedPreferences`. However, the files aren't encrypted by default, so their contents can still be read on a rooted device.

## API Overview

### Preferences DataStore

A `DataStore<Preferences>` instance is typically created at the top level using a file-delegate:

```kotlin
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
```

Data is read via a `Flow`:

```kotlin
val MY_KEY = stringPreferencesKey("my_key")
val value: Flow<String?> = context.dataStore.data.map { preferences ->
    preferences[MY_KEY]
}
```

Data is written with a suspending `edit` call:

```kotlin
context.dataStore.edit { preferences ->
    preferences[MY_KEY] = "myValue"
}
```

### Proto DataStore

A `DataStore<T>` instance for a protobuf-defined type `T` requires a custom `Serializer<T>` and is created with `createDataStore` or the `dataStore` delegate:

```kotlin
val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)
```

Reads and writes follow the same coroutine-based `data` Flow and `updateData` API as Preferences DataStore.

## Encryption

Neither Preferences DataStore nor Proto DataStore encrypts data at rest by default. To protect sensitive data, you can wrap the `Serializer` with encryption logic using the [Android Keystore](https://developer.android.com/training/articles/keystore) or a library such as [Tink](https://developers.google.com/tink).

## Backup Behavior

DataStore files stored under the app's internal `files/datastore/` directory are included in [Android Auto Backup](https://developer.android.com/identity/data/autobackup) by default (available since Android 6.0, API level 23). Apps can opt specific files out of backup using the `android:fullBackupContent` rules or `android:dataExtractionRules` (Android 12 (API level 31) and higher).
