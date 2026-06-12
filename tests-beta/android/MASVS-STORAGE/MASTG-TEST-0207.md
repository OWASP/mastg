---
platform: android
title: Runtime Storage of Unencrypted Data in the App Sandbox
id: MASTG-TEST-0207
type: [dynamic, filesystem]
prerequisites:
- identify-sensitive-data
weakness: MASWE-0006
profiles: [L2]
best-practices: [MASTG-BEST-0050]
knowledge: [MASTG-KNOW-0041]
---

## Overview

The goal of this test is to retrieve the files written to the internal storage (@MASTG-KNOW-0041) and inspect them regardless of the APIs used to write them. It uses a simple approach based on file retrieval from the device storage (@MASTG-TECH-0002) before and after the app is exercised to identify the files created during the app's execution and to check if they contain sensitive data.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0008 to take a first copy of the app's private data directory as a reference for offline analysis.
3. Launch and use the app, going through the various workflows while inputting sensitive data wherever you can. Taking note of the data you input can help identify it later using tools to search for it.
4. Use @MASTG-TECH-0008 to take a second copy of the app's private data directory and diff it with the first copy to identify all files created or modified during your testing session.

## Observation

The output should contain a list of files that were created in the app's private storage during execution.

## Evaluation

The test case fails if you find any sensitive data (keys, passwords, or any data inputted into the app) in the extracted files.

When evaluating the data, attempt to identify and decode data that has been encoded using methods such as base64 encoding, hexadecimal representation, URL encoding, escape sequences, wide characters and common data obfuscation methods such as xoring. Also consider identifying and decompressing compressed files such as tar or zip. These methods obscure but do not protect sensitive data.
