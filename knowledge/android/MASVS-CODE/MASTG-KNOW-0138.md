---
masvs_category: MASVS-CODE
platform: android
title: URI Schemes in Android Intent Results
---

When an activity requests content from another app and receives a result, the responding activity returns data via [`setResult`](https://developer.android.com/reference/android/app/Activity#setResult(int,android.content.Intent)). Legacy code commonly receives that result through [`onActivityResult`](https://developer.android.com/reference/android/app/Activity#onActivityResult(int,int,android.content.Intent)); newer code uses the [Activity Result APIs](https://developer.android.com/training/basics/intents/result). The result can carry a URI in [`Intent.getData()`](https://developer.android.com/reference/android/content/Intent#getData()) that the caller uses to access the content. The URI scheme determines how Android routes that access.

## URI Schemes

Android supports several URI schemes. The two most relevant in intent result handling are:

| Scheme | Route | Access control |
| --- | --- | --- |
| `content://` | Routes through a `ContentProvider` | Governed by provider permissions, URI grants, and provider export state |
| `file://` | Accesses the filesystem path directly | Governed by filesystem permissions for the calling process |

A `content://` URI identifies content managed by a specific `ContentProvider` on the device. The caller uses [`ContentResolver.openInputStream`](https://developer.android.com/reference/android/content/ContentResolver#openInputStream(android.net.Uri)) to open a stream, and the system routes the request through provider methods such as [`ContentProvider.openFile`](https://developer.android.com/reference/android/content/ContentProvider#openFile(android.net.Uri,%20java.lang.String)). Provider access depends on manifest permissions, URI permissions, and the provider implementation. See @MASTG-KNOW-0117 for general ContentProvider behavior.

A `file://` URI references a filesystem path directly. When the calling app opens a stream for a `file://` URI, the system reads from that path using the calling app's process identity and filesystem permissions rather than routing access through a `ContentProvider`.

## How Callers Process Returned URIs

A common legacy pattern in the caller's `onActivityResult` is to read `data.data` and open it with `ContentResolver`:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == RESULT_OK) {
        val uri = data?.data ?: return
        val inputStream = contentResolver.openInputStream(uri)
        // copy or process the stream
    }
}
```

### `file://` URI Handling

A responding app controls the URI value it returns in the result intent. If that value uses the `file://` scheme, the path is interpreted from the caller's process context. For example, `file:///data/data/com.example.app/shared_prefs/session.xml` denotes a filesystem path under the app-private data directory of `com.example.app`.

If the caller copies content from a returned URI to another location, the destination's storage area and permissions determine who can read the copied data. For example, [`Context.getExternalCacheDir`](https://developer.android.com/reference/android/content/Context#getExternalCacheDir()) returns an app-specific external cache directory.

### ContentProvider Metadata

When the responding app returns a `content://` URI, the calling app can query [`OpenableColumns.DISPLAY_NAME`](https://developer.android.com/reference/android/provider/OpenableColumns#DISPLAY_NAME) to get a human-readable filename:

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

The provider controls the value returned for that column. If the value contains path separators, constructing a path with [`File(dir, name)`](https://developer.android.com/reference/java/io/File#File(java.io.File,%20java.lang.String)) resolves it according to normal filesystem path rules:

```kotlin
val name = getDisplayName(uri) ?: "download"
val target = File(context.filesDir, name) // resolves to ../lib-main/lib.so
```

When the caller opens a `content://` URI, the provider implementation returns a [`ParcelFileDescriptor`](https://developer.android.com/reference/android/os/ParcelFileDescriptor) or stream for the requested content. The file descriptor returned by the provider, the metadata returned by `query`, and the URI path are related by provider logic rather than by a universal Android guarantee.
