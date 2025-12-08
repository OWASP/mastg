---
platform: ios
title: References to APIs for Storing Unencrypted Data in Shared Storage
id: MASTG-TEST-0303
type: [static]
profiles: [L1, L2]
best-practices: [MASTG-BEST-0024]
weakness: MASWE-0007
---

## Overview

This test checks whether the app writes sensitive data unencrypted to storage locations in the app sandbox (@MASTG-KNOW-0108) that are configured for sharing by enabling file sharing in the app's Info.plist using the `UIFileSharingEnabled` ("Application supports iTunes file sharing") and `LSSupportsOpeningDocumentsInPlace` ("Supports opening documents in place") keys set to `YES` (see @MASTG-KNOW-0091).

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary.

2. Search for APIs that indicate use of shared storage, for example:

      - [`documentDirectory`](https://developer.apple.com/documentation/foundation/filemanager/searchpathdirectory/documentdirectory) (commonly exposed via iTunes File Sharing / Files app)
      - `FileManager.default.urls(for:in:)` with `documentDirectory`
      - Direct path manipulation under `.../Documents` for write operations (`Data.write(to:)`, `String.write(to:)`, `NSFileHandle`, `NSOutputStream`)

3. Check the app's `Info.plist` (@MASTG-TECH-0058) for the `UIFileSharingEnabled` and `LSSupportsOpeningDocumentsInPlace` flags.

## Observation

The output should contain:

- A list of code locations that write (or could write) to shared storage.
- The state of `UIFileSharingEnabled` and `LSSupportsOpeningDocumentsInPlace`.

## Evaluation

The test fails if:

- The app writes unencrypted sensitive data to `documentDirectory` (or equivalent shared storage path), and
- `Info.plist` enables user access to the Documents directory (`UIFileSharingEnabled` and/or `LSSupportsOpeningDocumentsInPlace`).

Note: `documentDirectory` by itself is not inherently insecure; the risk arises when sensitive data is stored there and exposed via file sharing or Files app access. In contrast, data stored in other locations within the app sandbox (e.g., `Library/Application Support`) with encryption, or in the Keychain cannot be accessed even if file sharing is enabled.
