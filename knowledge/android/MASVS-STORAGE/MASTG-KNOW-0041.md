---
masvs_category: MASVS-STORAGE
platform: android
title: Internal Storage
---

You can save files to the device's [internal storage](https://developer.android.com/training/data-storage#filesInternal "Using Internal Storage"). Files saved to internal storage are containerized by default and cannot be accessed by other apps on the device. When the user uninstalls your app, these files are removed.

Apps write files to internal storage using the Java and Kotlin File APIs described in @MASTG-KNOW-0x01. The most common locations are:

- [`context.filesDir`](https://developer.android.com/reference/android/content/Context#getFilesDir()): persistent private files.
- [`context.cacheDir`](https://developer.android.com/reference/android/content/Context#getCacheDir()): temporary cache files that the system may delete when storage is low.
- [`context.noBackupFilesDir`](https://developer.android.com/reference/android/content/Context#getNoBackupFilesDir()): persistent private files excluded from auto-backup.

For example, the following Kotlin snippet stores sensitive information in clear text to a file `sensitive_info.txt` residing on internal storage.

```kotlin
val fileName = "sensitive_info.txt"
val fileContents = "This is some top-secret information!"
File(filesDir, fileName).bufferedWriter().use { writer ->
    writer.write(fileContents)
}
```

You can also use [`Context.openFileOutput(name, mode)`](https://developer.android.com/reference/android/content/Context#openFileOutput(java.lang.String,int)) to write directly to `filesDir`. The `mode` parameter controls access: `MODE_PRIVATE` (the default) restricts the file to the calling app, while `MODE_WORLD_READABLE` and `MODE_WORLD_WRITEABLE` are deprecated and raise a `SecurityException` on API level 24 and above.

**Android Security Guidelines**: Android highlights that the data in the internal storage is private to the app and other apps cannot access it. It also recommends avoiding the use of `MODE_WORLD_READABLE` and `MODE_WORLD_WRITEABLE` modes for IPC files and using a [content provider](https://developer.android.com/privacy-and-security/security-tips#content-providers) instead. See the [Android Security Guidelines](https://developer.android.com/privacy-and-security/security-tips#internal-storage "Android Security Guidelines"). Android also provides a [guide](https://developer.android.com/privacy-and-security/security-best-practices#internal-storage "Store data in internal storage based on use case") on how to use internal storage securely.
