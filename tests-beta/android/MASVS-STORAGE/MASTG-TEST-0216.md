---
platform: android
title: Sensitive Data Not Excluded From Backup
id: MASTG-TEST-0216
type: [dynamic, filesystem]
weakness: MASWE-0004
best-practices: [MASTG-BEST-0004]
profiles: [L1, L2, P]
knowledge: [MASTG-KNOW-0050]
---

## Overview

This test verifies whether apps correctly instruct the system to exclude sensitive files from backups by performing a backup and restore of the app data and checking which files are restored.

See @MASTG-TEST-0262 for a static analysis counterpart.

Android provides a way to start the backup daemon to back up and restore app files, which you can use to verify which files are actually restored from the backup.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Launch and use the app going through the various workflows while inputting sensitive data wherever you can.
3. Use @MASTG-TECH-0128 to perform a backup and restore of the app data.
4. Uninstall and reinstall the app but don't open it anymore.
5. Restore the data from the backup and get the list of restored files.

## Observation

The output should contain a list of files that are restored from the backup.

## Evaluation

The test case fails if any of the files are considered sensitive.
