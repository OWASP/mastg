---
masvs_category: MASVS-PLATFORM
platform: ios
title: Document Picker, Document Interaction, and Open in Place
---

iOS provides several mechanisms for exchanging files between apps. These mechanisms are user-mediated: the user chooses which files to share, open, import, or export, and which apps or locations are involved.

Storage apps can expose documents to system document UIs, such as the document picker, document browser, and Files app, through [File Provider extensions](https://developer.apple.com/documentation/fileprovider). This is especially relevant for document picker and open in place flows, where the selected file may live outside the receiving app's sandbox.

## Document Picker

[`UIDocumentPickerViewController`](https://developer.apple.com/documentation/uikit/uidocumentpickerviewcontroller) presents a system UI that lets users browse and select files from iCloud Drive, third-party storage providers, and on-device locations. It lets apps import, export, open, or move documents outside their own sandbox.

Key behaviors:

- Open and move operations provide security-scoped URLs for external documents. Apps should call `startAccessingSecurityScopedResource()` before accessing them, unless access is managed by `UIDocument`.
- Import and export operations copy files into or out of the app's sandbox.
- Access to files outside the app's sandbox is mediated by the system. Access to external documents should be coordinated using @MASTG-KNOW-0127, especially if the file may be modified by multiple apps or processes.

## Document Interaction

[`UIDocumentInteractionController`](https://developer.apple.com/documentation/uikit/uidocumentinteractioncontroller) lets an app present a system UI for previewing, opening, copying, or printing a file. The sending app initiates the interaction, but the user chooses the action and destination app.

Key behaviors:

- The system filters available actions and apps based on the file type, using declared document types and Uniform Type Identifiers.
- When a file is opened by another app as a copy, the receiving app receives its own copy, commonly in its `Documents/Inbox` directory.
- The sending app retains its original file.
- The receiving app should treat imported files as untrusted input.

## Open in Place

Apps can declare support for opening documents in place by setting [`LSSupportsOpeningDocumentsInPlace`](https://developer.apple.com/documentation/bundleresources/information-property-list/lssupportsopeningdocumentsinplace) in their `Info.plist`. [Document browser based apps](https://developer.apple.com/documentation/uikit/view_controllers/adding_a_document_browser_to_your_app) may also use [`UISupportsDocumentBrowser`](https://developer.apple.com/documentation/bundleresources/information-property-list/uisupportsdocumentbrowser). When an open in place flow is used, the receiving app accesses the original document at its current location rather than receiving a separate copy.

Key behaviors:

- Access is still user initiated and mediated through system document UI, such as the document picker, document browser, or Files app.
- The receiving app may access the original file through a security scoped URL, depending on the operation and file location.
- Access to external documents should be coordinated using @MASTG-KNOW-0127, especially if the file may be modified by multiple apps or processes.
- The receiving app must declare support for the relevant document types, using Uniform Type Identifiers.
