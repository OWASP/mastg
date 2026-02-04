---
masvs_category: MASVS-STORAGE
platform: ios
title: File System APIs
---

iOS apps can write data to the file system [using various APIs](https://developer.apple.com/documentation/foundation/using-the-file-system-effectively), depending on the use case.

> Other ways to store data that do not involve direct file system access include: @MASTG-KNOW-0092, @MASTG-KNOW-0093, @MASTG-KNOW-0094, @MASTG-KNOW-0096, @MASTG-KNOW-0097, @MASTG-KNOW-0075

For internal app files, caches, exports, or simple background writes where the app fully controls the path and conflicts are unlikely, apps typically use [`FileManager`](https://developer.apple.com/documentation/foundation/filemanager).

A file can be created and written using `FileManager` and [`createFile(atPath:contents:attributes:)`](https://developer.apple.com/documentation/foundation/filemanager/createfile(atpath:contents:attributes:)).

- First, obtain the path using [`FileManager.default.urls(for:in:)`](https://developer.apple.com/documentation/foundation/filemanager/urls(for:in:)). Use the `for` parameter to specify the directory, such as [`.documentDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/documentdirectory) or [`.libraryDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/librarydirectory).
    - Apps can also write to the temporary directory using the URL property [temporaryDirectory](https://developer.apple.com/documentation/foundation/url/temporarydirectory) and the file manager property [`temporaryDirectory`](https://developer.apple.com/documentation/foundation/filemanager/temporarydirectory). The system may purge this directory when the app isn't running.
    - For files that persist longer than temporary files, but are still purgeable, apps can use the caches directory [`.cachesDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/cachesdirectory).
    - For files that are needed for app operation but don't need to be exposed to the user, apps can use the application support directory [`.applicationSupportDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/applicationsupportdirectory) (typically configuration files, templates, and modified versions of default files from the app bundle).
- Next, call `createFile(atPath:contents:attributes:)`, providing the path, the data to write, and optional attributes such as the file protection level.

Example:

```swift
let url = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    .appendingPathComponent("filename.txt")
FileManager.default.createFile(
    atPath: url.path,
    contents: "secret text".data(using: .utf8),
    attributes: [FileAttributeKey.protectionKey: FileProtectionType.complete]
)
```

Other APIs can write to files as well, including:

- [`Data.write(to:options:)`](https://developer.apple.com/documentation/foundation/nsdata/write(tofile:options:)) and [`String.write(to:)`](https://developer.apple.com/documentation/swift/string/write(to:))
- [`FileHandle.write(contentsOf:)`](https://developer.apple.com/documentation/foundation/filehandle/write(contentsof:)) for incremental writes
- POSIX APIs such as `open`, `write`, `pwrite`, and `close` for low level access

In document based apps, where the system should coordinate access, integrate with iCloud or the Files app, or support autosave and versioning, developers can use [`UIDocument`](https://developer.apple.com/documentation/uikit/uidocument) or [`NSDocument`](https://developer.apple.com/documentation/AppKit/NSDocument). These APIs still read and write regular files under the app sandbox or iCloud containers, so file protection attributes and default protection classes apply in the same way.

## Data Protection

File protection attributes such as [`FileProtectionType.complete`](https://developer.apple.com/documentation/foundation/fileprotectiontype/complete) ensure that data remains encrypted while the device is locked, as described in Apple's [Encrypting Your App's Files](https://developer.apple.com/documentation/uikit/protecting_the_user_s_privacy/encrypting_your_app_s_files).

The default protection level is `NSFileProtectionCompleteUntilFirstUserAuthentication` and can be changed by supplying attributes when creating a file with [`createFile(atPath:contents:attributes:)`](https://developer.apple.com/documentation/foundation/filemanager/createfile(atpath:contents:attributes:)) or later using [`setAttributes(_:ofItemAtPath:)`](https://developer.apple.com/documentation/foundation/filemanager/setattributes(_:ofitematpath:)).

Files created using other APIs inherit the default protection level unless explicitly updated with `setAttributes`. For example:

```swift
FileManager.default.setAttributes(
    [FileAttributeKey.protectionKey: FileProtectionType.complete],
    ofItemAtPath: path
)
```

A default protection level can also be set for the entire app by configuring the `NSFileProtectionKey` in the app's Info.plist file.

## User Exposure

By default, files in the app's private sandbox are not exposed to the user. However, apps can expose files to the user by saving them in specific directories such as the Documents directory and enabling file sharing in the app's Info.plist using the `UIFileSharingEnabled` ("Application supports iTunes file sharing") and `LSSupportsOpeningDocumentsInPlace` ("Supports opening documents in place") keys set to `YES`.

They can also use APIs like [`UIDocumentPickerViewController`](https://developer.apple.com/documentation/uikit/uidocumentpickerviewcontroller) to allow users to export files.
