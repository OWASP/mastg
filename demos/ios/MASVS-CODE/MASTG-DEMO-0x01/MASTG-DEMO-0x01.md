---
platform: ios
title: References to Insecure Object Deserialization with r2
code: [swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
status: draft
---

## Sample

The code snippet below defines `UserSession` conforming to `NSCoding` instead of `NSSecureCoding`. It uses `decodeObject(forKey:)` without type restriction and sets `requiresSecureCoding = false` during unarchiving, making the app accept any class substituted in the archive.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ nscoding.r2 }}

{{ run.sh }}

## Observation

The output shows `NSCoding` conformance methods (`encode(with:)` and `init(coder:)`), calls to `NSKeyedArchiver.archivedData(withRootObject:requiringSecureCoding:)` and `NSKeyedUnarchiver.init(forReadingFrom:)`, and a reference to `requiresSecureCoding` being set in `mastgTest`.

{{ output.txt }}

## Evaluation

The test case fails because:

1. `UserSession` conforms to `NSCoding` instead of `NSSecureCoding`. The `init(coder:)` implementation uses `decodeObject(forKey:)` without class restriction, so any class can be substituted for `UserSession` in a crafted archive without triggering a type mismatch.

2. `NSKeyedUnarchiver` is initialized with `requiresSecureCoding = false`, which explicitly disables the type enforcement that `NSSecureCoding` would otherwise provide. This means the unarchiver will not reject archives that contain unexpected class types.

To fix these issues, `UserSession` should conform to `NSSecureCoding`, set `supportsSecureCoding = true`, use `decodeObject(of:forKey:)` to restrict the allowed type, and the unarchiver should use `requiresSecureCoding = true`.
