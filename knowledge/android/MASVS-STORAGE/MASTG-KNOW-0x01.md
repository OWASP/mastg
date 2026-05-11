---
masvs_category: MASVS-STORAGE
platform: android
title: File System APIs
---

Android apps can write data to the file system using [various Java and Kotlin APIs](https://developer.android.com/training/data-storage/app-specific). The choice of API determines how data is written, but the storage location is determined separately. See @MASTG-KNOW-0041 for internal (app-specific) storage and @MASTG-KNOW-0042 for external storage.

> Other ways to store data that do not involve direct file system access include: @MASTG-KNOW-0036, @MASTG-KNOW-0037, @MASTG-KNOW-0039, @MASTG-KNOW-0040, @MASTG-KNOW-0043

## Context File APIs

[`Context.openFileOutput(String name, int mode)`](https://developer.android.com/reference/android/content/Context#openFileOutput(java.lang.String,%20int)) opens a named file in the app's internal files directory (`filesDir`) for writing and returns a [`FileOutputStream`](https://developer.android.com/reference/java/io/FileOutputStream). The `mode` parameter controls access:

- `MODE_PRIVATE` (value `0`) — the file is accessible only to the calling app (default).
- `MODE_APPEND` — opens the file for appending if it already exists.
- `MODE_WORLD_READABLE` and `MODE_WORLD_WRITEABLE` were deprecated in API level 17 and raise a `SecurityException` on API level 24 and above.

[`Context.openFileInput(String name)`](https://developer.android.com/reference/android/content/Context#openFileInput(java.lang.String)) is the corresponding read API, returning a `FileInputStream`.

```kotlin
val fos = context.openFileOutput("data.txt", Context.MODE_PRIVATE)
fos.write("content".toByteArray())
fos.close()
```

## java.io APIs

The `java.io` package provides the classic byte-stream and character-writer APIs:

- [`FileOutputStream`](https://developer.android.com/reference/java/io/FileOutputStream): byte-oriented output stream that writes directly to a file. Can be constructed with a `File` object or a file path string.
- [`FileInputStream`](https://developer.android.com/reference/java/io/FileInputStream): byte-oriented input stream that reads from a file.
- [`FileWriter`](https://developer.android.com/reference/java/io/FileWriter): character-oriented writer that writes to a file. Often wrapped in a `BufferedWriter` for efficiency.
- [`FileReader`](https://developer.android.com/reference/java/io/FileReader): character-oriented reader that reads from a file.
- [`BufferedWriter`](https://developer.android.com/reference/java/io/BufferedWriter): buffers character output, commonly wrapping a `FileWriter`.
- [`BufferedReader`](https://developer.android.com/reference/java/io/BufferedReader): buffers character input, commonly wrapping a `FileReader`.
- [`PrintWriter`](https://developer.android.com/reference/java/io/PrintWriter): prints formatted text to a file or another `Writer`.
- [`RandomAccessFile`](https://developer.android.com/reference/java/io/RandomAccessFile): supports both reading and writing at arbitrary byte positions within a file; opened with a mode string such as `"r"` (read-only) or `"rw"` (read-write).

Example using `FileOutputStream` directly:

```kotlin
val file = File(context.filesDir, "data.bin")
FileOutputStream(file).use { fos ->
    fos.write(data)
}
```

Example using `FileWriter` with `BufferedWriter`:

```kotlin
val file = File(context.filesDir, "data.txt")
BufferedWriter(FileWriter(file)).use { writer ->
    writer.write("content")
}
```

## java.nio.file APIs (API level 26+)

Since Android 8.0 (API level 26), the `java.nio.file` package is available:

- [`Files.write(Path, byte[], OpenOption...)`](https://developer.android.com/reference/java/nio/file/Files#write(java.nio.file.Path,%20byte[],%20java.nio.file.OpenOption[])): atomically writes a byte array to a file, creating or truncating it.
- [`Files.newOutputStream(Path, OpenOption...)`](https://developer.android.com/reference/java/nio/file/Files#newOutputStream(java.nio.file.Path,%20java.nio.file.OpenOption[])): opens a file for writing and returns an [`OutputStream`](https://developer.android.com/reference/java/io/OutputStream).
- [`Files.newBufferedWriter(Path, OpenOption...)`](https://developer.android.com/reference/java/nio/file/Files#newBufferedWriter(java.nio.file.Path,%20java.nio.file.OpenOption[])): opens a file for writing text and returns a [`BufferedWriter`](https://developer.android.com/reference/java/io/BufferedWriter).
- [`Files.newByteChannel(Path, OpenOption...)`](https://developer.android.com/reference/java/nio/file/Files#newByteChannel(java.nio.file.Path,%20java.nio.file.OpenOption[])): opens or creates a file and returns a seekable [`FileChannel`](https://developer.android.com/reference/java/nio/channels/FileChannel) for both reading and writing.

`FileChannel` can also be obtained from a `FileOutputStream` via `FileOutputStream.getChannel()` and supports memory-mapped I/O via `FileChannel.map()`.

Example:

```kotlin
val path = File(context.filesDir, "data.bin").toPath()
Files.write(path, data)
```

## Kotlin Extension APIs

Kotlin provides extension functions on `java.io.File` for concise file I/O:

- [`File.writeText(text, charset)`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/write-text.html): writes text to the file, replacing any existing content.
- [`File.appendText(text, charset)`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/append-text.html): appends text to the end of the file.
- [`File.writeBytes(array)`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/write-bytes.html): writes bytes to the file, replacing any existing content.
- [`File.appendBytes(array)`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/append-bytes.html): appends bytes to the end of the file.
- [`File.bufferedWriter()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/buffered-writer.html): returns a `BufferedWriter` for writing to the file.
- [`File.printWriter()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/print-writer.html): returns a `PrintWriter` for writing to the file.
- [`File.outputStream()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/output-stream.html): returns a `FileOutputStream` for writing to the file.
- [`File.readText(charset)`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/read-text.html): reads the entire file as a string.
- [`File.readBytes()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/read-bytes.html): reads the entire file as a byte array.
- [`File.bufferedReader()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/buffered-reader.html): returns a `BufferedReader` for reading from the file.
- [`File.inputStream()`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/input-stream.html): returns a `FileInputStream` for reading from the file.

Example:

```kotlin
File(context.filesDir, "data.txt").writeText("content")
File(context.filesDir, "more.txt").appendText("additional content")
```

## Native (NDK/JNI) APIs

Apps that include native code via the [Android NDK](https://developer.android.com/ndk/guides) can write files from C or C++ using standard POSIX and C library calls. The file path is typically obtained from the Java layer (for example, from `Context.getFilesDir()`) and passed down via JNI.

Commonly used write APIs:

- [`fopen(path, mode)` / `fwrite()` / `fclose()`](https://en.cppreference.com/w/c/io): standard C I/O; `mode` can be `"w"` (write), `"a"` (append), or `"wb"` / `"ab"` for binary variants.
- [`open(path, flags, mode)` / `write()` / `close()`](https://man7.org/linux/man-pages/man2/open.2.html): POSIX system calls; `flags` such as `O_WRONLY | O_CREAT | O_TRUNC` control creation and truncation behavior.
- [`pwrite(fd, buf, count, offset)`](https://man7.org/linux/man-pages/man2/pwrite.2.html): writes at a specific byte offset without changing the file position.
- [`mmap()` with `MAP_SHARED`](https://man7.org/linux/man-pages/man2/mmap.2.html): maps a file into memory for direct read/write access; changes are written back to disk when the mapping is flushed with `msync()` or unmapped.

Commonly used read APIs:

- `fopen(path, "r")` / `fread()` / `fgets()` / `fclose()`: standard C I/O for reading.
- `open(path, O_RDONLY)` / `read()` / `close()`: POSIX system calls for reading.
- [`pread(fd, buf, count, offset)`](https://man7.org/linux/man-pages/man2/pread.2.html): reads from a specific byte offset.
- `mmap()` with `MAP_PRIVATE` / `PROT_READ`: maps a file into read-only memory.

The Android NDK also exposes [`AAssetManager`](https://developer.android.com/ndk/reference/group/asset) for reading bundled assets from the APK, though this is read-only.

Example (writing via POSIX from C):

```c
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

void write_data(const char *path, const char *data) {
    int fd = open(path, O_WRONLY | O_CREAT | O_TRUNC, 0600);
    if (fd >= 0) {
        write(fd, data, strlen(data));
        close(fd);
    }
}
```
