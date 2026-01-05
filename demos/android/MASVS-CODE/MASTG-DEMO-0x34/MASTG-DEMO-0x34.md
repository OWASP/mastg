---
platform: android
title: Object Deserialization Using Serializable with semgrep
id: MASTG-DEMO-0x34
code: [kotlin]
test: MASTG-TEST-0x34
profiles: [L1, L2]
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

The test fails because `readObject()` method was found in the code.
