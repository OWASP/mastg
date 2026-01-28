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

The test fails if the app uses `ObjectInputStream.readObject()` on data received from untrusted sources without proper validation or type filtering. Review each of the reported instances:

- Line 103 shows the serialized data originates from an Intent extra (`payload_b64`), which can be controlled by any external application.
- Lines 106-108 show the base64-decoded payload is directly passed to `ObjectInputStream.readObject()` without any class filtering (e.g., no `ObjectInputFilter` is set).
