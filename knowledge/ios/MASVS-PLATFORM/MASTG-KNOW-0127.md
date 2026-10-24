---
masvs_category: MASVS-PLATFORM
platform: ios
title: File Coordination APIs
---

The File Coordination APIs provide a mechanism for coordinating safe, concurrent access to files and directories. They are particularly important when multiple processes or objects, such as an app and its extensions, read or write shared files in an App Group container (see @MASTG-KNOW-0125).

File coordination is implemented through two main classes:

- **[`NSFileCoordinator`](https://developer.apple.com/documentation/foundation/nsfilecoordinator)**: Coordinates reads and writes to a file or directory among participating file presenters. Callers use it to declare their intent before accessing a file, allowing the system to serialize conflicting coordinated access.
- **[`NSFilePresenter`](https://developer.apple.com/documentation/foundation/nsfilepresenter)**: A protocol adopted by objects that want to be notified when a file or directory they are interested in changes. Presenters are registered with the file coordination system and receive callbacks for changes made through coordinated access.

Before reading or writing a shared file, a process wraps the access in a coordination block:

```swift
let coordinator = NSFileCoordinator(filePresenter: nil)
coordinator.coordinate(readingItemAt: fileURL, options: [], error: nil) { url in
    // Read the file at `url`
}
```

For writing:

```swift
coordinator.coordinate(writingItemAt: fileURL, options: .forReplacing, error: nil) { url in
    // Write to the file at `url`
}
```

`NSFileCoordinator` methods execute synchronously on the calling thread. The system serializes conflicting coordinated access and notifies registered `NSFilePresenter` observers of relevant changes.

## Scope and Constraints

- File coordination is only meaningful between cooperating processes or objects that use the file coordination system. Uncoordinated file access bypasses the coordination mechanism.
- Coordination is commonly used for external documents, document-based apps, and App Group shared containers where the main app and its extensions may access the same files.
- When using [`UIDocument`](https://developer.apple.com/documentation/uikit/uidocument) or `NSDocument`, file coordination is managed automatically by the document class.
- For app extensions, Apple states that [`NSFileCoordinator` can be used directly for shared data access on iOS 9 and later](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionScenarios.html), but `NSFilePresenter` objects must be removed when the extension transitions into the background.
