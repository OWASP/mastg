---
title: References to Object Deserialization of Untrusted Data
platform: ios
id: MASTG-TEST-0386
type: [static, code, manual]
weakness: MASWE-0088
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0075]
best-practices: [MASTG-BEST-0064]
---

## Overview

iOS apps can reconstruct objects from serialized data received through files, IPC payloads, network responses, pasteboard data, app extensions, or archived data stored locally. If an attacker can influence this data and the app decodes it without restricting the expected classes, the app may accept unexpected object types. This can lead to object substitution, unintended application behavior, or unsafe state changes.

Developers can introduce this risk when they deserialize potentially untrusted data without enforcing secure coding or class restrictions. Common cases include:

- Using broad decoding APIs such as `decodeObject(forKey:)`, `unarchiveObject(with:)`, or `unarchiveTopLevelObjectWithData(_:)`.
- Creating an `NSKeyedUnarchiver` with APIs such as `init(forReadingFrom:)` or `initForReadingWithData:` and then decoding objects without restricting the expected classes.
- Disabling secure coding enforcement by setting `requiresSecureCoding = false`.
- Relying on `NSCoding` alone instead of enforcing `NSSecureCoding` with class restricted APIs such as `decodeObject(of:forKey:)`, `decodeObject(ofClasses:forKey:)`, `unarchivedObject(ofClass:from:)`, or `unarchivedObject(ofClasses:from:)`.

This test checks whether the app deserializes potentially untrusted data without enforcing secure coding, class restrictions, or equivalent validation. For background on iOS serialization mechanisms, see @MASTG-KNOW-0075.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where object deserialization APIs are used, indicating whether each location enforces secure coding and restricts the decoded classes.

## Evaluation

The test case fails if the app deserializes data from a potentially untrusted or attacker-influenced source using APIs that don't enforce secure coding, class restrictions, or equivalent validation.

**Further Validation Required:**

A reference to one of these APIs doesn't fail the test on its own. Inspect each reported code location using @MASTG-TECH-0076 to determine whether the deserialized data can cross a trust boundary:

- Determine whether the decoded data can originate from an untrusted or attacker-influenced source, such as files, IPC, network responses, app extensions, pasteboard data, or archived data stored locally.
- Determine whether the unarchiver enforces secure coding and whether decoded objects are restricted to the expected classes.

Uses of these APIs on constant, bundled, or otherwise trusted data that an attacker can't control should be reviewed manually rather than treated as a confirmed failure.
