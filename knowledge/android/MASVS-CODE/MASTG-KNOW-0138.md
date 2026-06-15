---
masvs_category: MASVS-CODE
platform: android
title: URI Schemes in Android Intent Results
---

When an activity uses [`startActivityForResult`](https://developer.android.com/reference/android/app/Activity#startActivityForResult(android.content.Intent,int)) to request content from another app, the responding app returns a result via [`setResult`](https://developer.android.com/reference/android/app/Activity#setResult(int,android.content.Intent)). The result can carry a URI in [`Intent.getData()`](https://developer.android.com/reference/android/content/Intent#getData()) that the caller uses to access the content. The URI scheme determines how the system routes that access.

## URI Schemes

Android supports several URI schemes. The two most relevant in intent result handling are:

| Scheme | Route | Access control |
| --- | --- | --- |
| `content://` | Routes through a `ContentProvider` | Governed by provider permissions and `android:exported` |
| `file://` | Accesses the filesystem path directly | Governed only by Unix file permissions |

A `content://` URI identifies content managed by a specific `ContentProvider` on the device. The caller uses [`ContentResolver.openInputStream`](https://developer.android.com/reference/android/content/ContentResolver#openInputStream(android.net.Uri)) to open a stream, and the system routes the request through the provider's `openFile` method. The provider can enforce access controls and return only data it explicitly exposes.

A `file://` URI references a filesystem path directly. When the calling app opens a stream for a `file://` URI, the system reads from that path using the calling app's own process identity, bypassing any `ContentProvider` access controls entirely.

## How Callers Process a Returned URI

A typical pattern in the caller's `onActivityResult`:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == RESULT_OK) {
        val uri = data?.data ?: return
        val inputStream = contentResolver.openInputStream(uri)
        // copy or process the stream
    }
}
```

### Potential Exploitation via `file://` URIs

A responding app can exploit this behavior by returning a `file://` URI that points to the calling app's own private storage (for example, `file:///data/data/com.example.app/shared_prefs/session.xml`). The read succeeds because `openInputStream` uses the caller's process identity, not the responder's.

If the calling app then writes the content to a world-readable location (such as [`externalCacheDir`](https://developer.android.com/reference/android/content/Context#getExternalCacheDir())), the responding app or any other app with `READ_EXTERNAL_STORAGE` permission can access the copied data.

### Potential Exploitation via ContentProvider Metadata

When the responding app returns a `content://` URI, the calling app typically queries [`OpenableColumns.DISPLAY_NAME`](https://developer.android.com/reference/android/provider/OpenableColumns#DISPLAY_NAME) to get a human-readable filename:

```kotlin
fun getDisplayName(uri: Uri): String? {
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            return cursor.getString(0)
        }
    }
    return null
}
```

The provider controls the value returned for that column and the Android system does not sanitize it. A malicious provider can return a path-traversal string such as `../lib-main/lib.so`. If the calling app then constructs a path using [`File(dir, name)`](https://developer.android.com/reference/java/io/File#File(java.io.File,%20java.lang.String)), the resolved path can escape the intended directory:

```kotlin
val name = getDisplayName(uri) ?: "download"
val target = File(context.filesDir, name) // resolves to ../lib-main/lib.so
```

If the overwritten file is a native library later loaded via [`System.loadLibrary`](https://developer.android.com/reference/java/lang/System#loadLibrary(java.lang.String)) or [`System.load`](https://developer.android.com/reference/java/lang/System#load(java.lang.String)), the attacker's code runs with the app's full process identity. The same applies to `.dex` or `.apk` files loaded dynamically via [`DexClassLoader`](https://developer.android.com/reference/dalvik/system/DexClassLoader). Additionally, when the caller calls `openInputStream`, the system invokes [`ContentProvider.openFile`](https://developer.android.com/reference/android/content/ContentProvider#openFile(android.net.Uri,%20java.lang.String)) and returns a [`ParcelFileDescriptor`](https://developer.android.com/reference/android/os/ParcelFileDescriptor) — the provider controls which file descriptor it returns and can point it at any file it can read, regardless of what the URI path suggests.
