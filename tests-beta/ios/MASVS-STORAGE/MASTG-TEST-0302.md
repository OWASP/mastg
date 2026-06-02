---
platform: ios
title: Sensitive Data Unencrypted in Private Storage Files
id: MASTG-TEST-0302
type: [dynamic, filesystem]
prerequisites:
- identify-sensitive-data
profiles: [L2]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0024]
knowledge: [MASTG-KNOW-0108]
---

## Overview

This test is designed to complement @MASTG-TEST-0301. Instead of monitoring APIs during execution, it performs a differential analysis of the app's private storage by comparing snapshots taken before and after exercising the app. It also enumerates Keychain items created or modified during the session.

The goal is to identify new or modified files and determine whether they contain sensitive data in plaintext or trivially encoded form, and to identify new Keychain entries that may contain sensitive data or keys used for file encryption.

Ensure the device / simulator is in a clean state (no prior test artifacts). When exercising the app, ensure to trigger typical workflows (authentication, profile loading, messaging, caching, offline usage, cryptographic operations).

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0059 to get a baseline list of files of the app's private storage (sandbox) directory tree.
3. Use @MASTG-TECH-0061 to take an initial snapshot of the Keychain items. Optionally record attributes (accessible class, access control flags, etc).
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
5. Use @MASTG-TECH-0059 to retrieve the list of files again.
6. Diff the two private storage snapshots to identify new, deleted, and modified files. For modified files, determine whether content changes involve potential sensitive values.
7. Use @MASTG-TECH-0061 to take a second snapshot of the Keychain items.
8. Use @MASTG-TECH-0061 to diff the two Keychain snapshots and identify new, deleted, and modified items.

## Observation

The output should contain:

- List of new or modified files with: path, size, hash, inferred type, encoding/encryption status (plaintext / encoded / encrypted / unknown).
- List of new or modified Keychain entries.

## Evaluation

The test case fails if sensitive data appears in plaintext or trivially encoded in new or modified files.

Inspect the list of files and Keychain entries for sensitive data. Attempt to identify and decode data that has been encoded using methods such as base64 encoding, hexadecimal representation, URL-encoding, escape sequences, binary plist files, compressed archives like zip, wide characters and common data obfuscation methods such as xoring. Also consider identifying and decompressing compressed files such as tar or zip. These methods obscure but do not protect sensitive data.
