---
title: References to Object Deserialization of Untrusted Data
platform: android
id: MASTG-TEST-0x34
type: [static]
weakness: MASWE-0088
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0021]
---

## Overview

If the app deserializes untrusted data without sufficient validation, it becomes vulnerable to malicious object injection. In Android, data can be passed between components via Intent objects. When an application receives a serialized object within an Intent and deserializes it using `ObjectInputStream.readObject()` without type filtering, a malicious application can send a specially crafted Intent containing a malicious serialized object. This can lead to arbitrary code execution, data tampering, privilege escalation, or denial of service depending on the available gadget chains in the application's classpath.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0013).
2. Run static analysis (@MASTG-TECH-0014) to search for references to object deserialization APIs.

## Observation

The output should contain a list of locations where object deserialization is used.

## Evaluation

The test case fails if the app deserializes data received from untrusted sources (e.g., Intent extras from any other application) without proper validation or type filtering.
