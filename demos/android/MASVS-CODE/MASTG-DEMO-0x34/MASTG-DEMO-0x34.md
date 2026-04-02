---
platform: android
title: Object Deserialization Using Serializable with semgrep
id: MASTG-DEMO-0x34
code: [kotlin]
test: MASTG-TEST-0x34
tools: [MASTG-TOOL-0110]
---

## Sample

The snippet below shows sample code that uses the `ObjectInputStream.readObject()` method to deserialize data received from an untrusted source.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-object-deserialization.yml }}

{{ run.sh }}

## Observation

The output contains the locations of `ObjectInputStream.readObject()` usages in the code. The reported line numbers can be used to inspect whether the deserialized data comes from an untrusted source and whether any filtering or validation is applied before deserialization.

{{ output.txt }}

## Evaluation

The test fails because the output shows that `ObjectInputStream.readObject()` is used on data that originates from an Intent extra without any filtering or type validation before deserialization. Review each of the reported instances:

- Line 103 shows the serialized data originates from an Intent extra (`payload_b64`), which can be controlled by any external application.
- Lines 106-108 show the base64-decoded payload is directly passed to `ObjectInputStream.readObject()` without any class filtering (e.g., no `ObjectInputFilter` is set).
