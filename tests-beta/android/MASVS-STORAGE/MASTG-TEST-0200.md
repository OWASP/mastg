---
platform: android
title: Files Written to External Storage
id: MASTG-TEST-0200
type: [dynamic, filesystem]
weakness: MASWE-0007
profiles: [L1, L2]
---

## Overview

The goal of this test is to retrieve the files written to the external storage (@MASTG-KNOW-0042) and inspect them regardless of the APIs used to write them. It uses a simple approach based on file retrieval from the device storage (@MASTG-TECH-0002) before and after the app is exercised to identify the files created during the app's execution and to check if they contain sensitive data.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0002 to get the current list of files in the external storage.
3. Exercise the app.
4. Use @MASTG-TECH-0002 to retrieve the list of files in the external storage again.
5. Calculate the difference between the two lists.

## Observation

The output should contain a list of files that were created on the external storage during the app's execution.

## Evaluation

The test case fails if the files found above are not encrypted and leak sensitive data.

To confirm this, you can reverse engineer the app (@MASTG-TECH-0017) and inspect the code (@MASTG-TECH-0023).
