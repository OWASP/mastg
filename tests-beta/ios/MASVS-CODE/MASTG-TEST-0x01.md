---
title: References to Insecure Object Deserialization
platform: ios
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0088
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0075]
best-practices: [MASTG-BEST-0x01]
---

## Overview

iOS apps can reconstruct objects from serialized data received through various channels such as IPC payloads, files, network responses, or `UserDefaults`. The `NSCoding` protocol allows any class to be decoded without type validation, making it possible to inject unexpected object types during deserialization.

The `NSSecureCoding` protocol addresses this by requiring explicit class-type checks during decoding. However, if an app uses `NSCoding` instead of `NSSecureCoding`, or disables secure coding by setting `requiresSecureCoding = false` on `NSKeyedUnarchiver`, the deserialization logic may accept attacker-controlled objects and lead to unintended application behavior or unsafe state changes.

This test checks whether the app uses insecure object deserialization APIs that do not enforce type safety. For background on iOS serialization mechanisms, see @MASTG-KNOW-0075.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0058).
2. Run static analysis (@MASTG-TECH-0076) to search for references to insecure object deserialization APIs, such as `NSCoding` conformances, `NSKeyedUnarchiver` usage without `requiresSecureCoding`, or calls to `decodeObject(forKey:)` without type restriction.

## Observation

The output should contain a list of locations where insecure object deserialization APIs are used.

## Evaluation

The test case fails if the app deserializes data from potentially untrusted sources (such as files, IPC, or network) using `NSCoding` instead of `NSSecureCoding`, or uses `NSKeyedUnarchiver` with `requiresSecureCoding = false`, without proper type validation or class filtering.
