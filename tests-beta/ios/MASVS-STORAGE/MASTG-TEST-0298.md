---
platform: ios
title: Runtime Monitoring of Files Eligible for Backup
id: MASTG-TEST-0298
type: [dynamic, hooks]
weakness: MASWE-0004
best-practices: [MASTG-BEST-0023]
profiles: [L1, L2, P]
knowledge: [MASTG-KNOW-0102]
---

## Overview

This test logs every file system API use, such as `open`, `fopen`, `NSFileManager`, or `FileHandle` that creates or writes files to the app's data container at `/var/mobile/Containers/Data/Application/$APP_ID` to identify which files are eligible for backup.

Files stored in the `tmp` or `Library/Caches` subdirectories should not be logged, as they are not backed up.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should list every file the app opens that is eligible for backup.

## Evaluation

The test case fails if any sensitive files are found in the output.
