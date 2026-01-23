---
title: Unwanted Object Deserialization Using Serializable
platform: android
id: MASTG-TEST-0x34
type: [static]
weakness: MASWE-0088
profiles: [L1, L2]
---

## Overview

Insecure Deserialization is a vulnerability that occurs when an application deserializes untrusted data without sufficient validation. In Android, data can be passed between components via Intent objects. If an application receives a serialized object within an Intent and deserializes it using an unsafe method like `ObjectInputStream.readObject()`, it becomes vulnerable. A malicious application could send a specially crafted Intent containing a serialized object. When the vulnerable app deserializes this object, it can lead to arbitrary code execution, data tampering, or denial of service. In this test case, it allows for privilege escalation by overwriting the current user's state.

## Steps

1. Run @MASTG-TOOL-0110 on the codebase for usages of `readObject()`.

## Observation

The output should contain a list of locations where `readObject()` or equivalent deserialization APIs are used.

## Evaluation

The test case fails if the app uses `ObjectInputStream.readObject()` (or similar deserialization methods) on data received from untrusted sources (e.g., Intent extras, network input) without proper validation or type filtering.
