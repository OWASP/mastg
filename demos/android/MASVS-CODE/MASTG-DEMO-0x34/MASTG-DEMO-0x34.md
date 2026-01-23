---
platform: android
title: Object Deserialization Using Serializable with semgrep
id: MASTG-DEMO-0x34
code: [kotlin]
test: MASTG-TEST-0x34
---

### Sample

The code snippet shows the utilization of object deserialization using `readObject()` method.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-object-deserialization.yml }}

{{ run.sh }}

### Observation

The output file shows usages of object deserialization in the code.

{{ output.txt }}

### Evaluation

The test fails because the app deserializes untrusted data using `ObjectInputStream.readObject()` without type validation. Specifically:

- On line 107, an `ObjectInputStream` is created from a `ByteArrayInputStream` containing externally-provided serialized data (`serializedPayload` from an Intent extra).
- On line 108, `readObject()` is called on the untrusted input without any class filtering (e.g., `ObjectInputFilter`), allowing arbitrary object instantiation.
