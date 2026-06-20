---
title: References to Object Deserialization of Untrusted Data
platform: android
id: MASTG-TEST-0337
type: [static, code]
weakness: MASWE-0088
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0021]
---

## Overview

Android apps can reconstruct objects from serialized data received through platform mechanisms such as `Intent` extras, `Bundle` values, IPC payloads, files, or network responses. If the app deserializes data from these sources without restricting the allowed classes or validating the input before use, the deserialization logic can introduce unintended application behavior or unsafe state changes.

This test checks whether the app uses object deserialization on Android and whether the deserialized data originates from potentially untrusted sources without appropriate filtering or validation. For background on Android serialization and deserialization mechanisms, see @MASTG-KNOW-0021.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where object deserialization is used.

## Evaluation

The test case fails if the app deserializes data received from untrusted sources (e.g., Intent extras from any other application) without proper validation or type filtering.
