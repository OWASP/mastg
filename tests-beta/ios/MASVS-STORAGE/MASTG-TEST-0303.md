---
platform: ios
title: References to APIs for Storing Unencrypted Data in Shared Storage
id: MASTG-TEST-0303
type: [static, code]
profiles: [L1, L2]
best-practices: [MASTG-BEST-0024]
weakness: MASWE-0007
knowledge: [MASTG-KNOW-0122, MASTG-KNOW-0091, MASTG-KNOW-0057, MASTG-KNOW-0108]
---

## Overview

This test checks whether the app stores sensitive data without encryption in iOS sandbox locations that may become user accessible when file sharing is enabled.

On iOS, the app sandbox is private by default. However, when the app sets [`UIFileSharingEnabled`](https://developer.apple.com/documentation/bundleresources/information-property-list/uifilesharingenabled) or [`LSSupportsOpeningDocumentsInPlace`](https://developer.apple.com/documentation/bundleresources/information-property-list/lssupportsopeningdocumentsinplace) to `YES` in its `Info.plist`, files in certain sandbox locations, especially the [`documentDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/documentdirectory), may become accessible through Finder, iTunes File Sharing, or the Files app.

Review the app for APIs that create, modify, or persist files in shared or potentially shared storage locations, such as [`documentDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/documentdirectory), [`URL.documentsDirectory`](https://developer.apple.com/documentation/foundation/url/documentsdirectory), or equivalent paths resolved at runtime. Relevant APIs include [`FileManager`](https://developer.apple.com/documentation/foundation/filemanager), [`Data.write(to:)`](https://developer.apple.com/documentation/foundation/data/write%28to:options:%29), [`String.write(to:atomically:encoding:)`](https://developer.apple.com/documentation/swift/string/write%28to:atomically:encoding:%29), [`FileHandle`](https://developer.apple.com/documentation/foundation/filehandle), and [`OutputStream`](https://developer.apple.com/documentation/foundation/outputstream), as well as lower level POSIX or BSD file I,O functions such as `open`, `write`, `fwrite`, and `fputs`.

Also review whether the app protects sensitive data before writing it to these locations. For example, the app may encrypt data using keys stored in the Keychain. Keychain API usage, such as [`SecItemAdd`](https://developer.apple.com/documentation/security/secitemadd%28_:_:%29), [`SecItemUpdate`](https://developer.apple.com/documentation/security/secitemupdate%28_:_:%29), and [`SecItemCopyMatching`](https://developer.apple.com/documentation/security/secitemcopymatching%28_:_:%29), can help determine whether encryption keys are created, retrieved, and protected with appropriate access control and accessibility attributes.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.
3. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
4. Use @MASTG-TECH-0154 to check for the `UIFileSharingEnabled` and `LSSupportsOpeningDocumentsInPlace` flags.

## Observation

The output should contain:

- A list of code locations that write (or could write) to shared storage.
- The state of `UIFileSharingEnabled` and `LSSupportsOpeningDocumentsInPlace`.

## Evaluation

The test case fails if:

- The app writes unencrypted sensitive data to `documentDirectory` (or equivalent shared storage path), and
- `Info.plist` enables user access to the Documents directory (`UIFileSharingEnabled` and/or `LSSupportsOpeningDocumentsInPlace`).

Note: `documentDirectory` by itself is not inherently insecure; the risk arises when sensitive data is stored there and exposed via file sharing or Files app access. In contrast, data stored in other locations within the app sandbox (e.g., `Library/Application Support`) with encryption, or in the Keychain cannot be accessed even if file sharing is enabled.
