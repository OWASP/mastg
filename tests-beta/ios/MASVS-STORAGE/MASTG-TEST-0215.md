---
platform: ios
title: Sensitive Data Not Marked For Backup Exclusion
id: MASTG-TEST-0215
type: [static]
weakness: MASWE-0004
best-practices: [MASTG-BEST-0023]
profiles: [L1, L2, P]
---

## Overview

This test verifies whether your app uses the `isExcludedFromBackup` API to instruct the system to exclude sensitive files from backups. This API [does not guarantee the actual exclusion](https://developer.apple.com/documentation/foundation/optimizing_your_app_s_data_for_icloud_backup/#3928527). According to the documentation:

> "The `isExcludedFromBackup` resource value exists only to provide guidance to the system about which files and directories it can exclude; it's not a mechanism to guarantee those items never appear in a backup or on a restored device."

In this test, we identify all locations where the `isExcludedFromBackup` API is used to mark files that might still end up in a backup.

**Note**: Files stored in an app's `/tmp` and `/Library/Caches` directories are **excluded** from iCloud backups. These directories are intended for temporary or cache data, and the system may automatically delete their contents at any time to free up space. Therefore, you don't need to mark these files with `isExcludedFromBackup`. For more details, see the [Apple documentation](https://developer.apple.com/documentation/foundation/optimizing-your-app-s-data-for-icloud-backup#Exclude-Purgeable-Data).

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary, or use a dynamic analysis tool like @MASTG-TOOL-0039, and look for uses of the `isExcludedFromBackup` API.

## Observation

The output should contain the disassembled code of the functions using `isExcludedFromBackup`, and, if possible, the list of affected files.

## Evaluation

The test fails if the `isExcludedFromBackup` API is used and any of the affected files are considered sensitive.

For any sensitive files found, in addition to using `isExcludedFromBackup`, make sure to encrypt them, as `isExcludedFromBackup` does not guarantee exclusion.
