---
platform: android
title: Object Deserialization Using Serializable with semgrep
id: MASTG-DEMO-0x34
code: [kotlin]
test: MASTG-TEST-0x34
tools: [MASTG-TOOL-0110]
---

## Sample

The sample code demonstrates an insecure deserialization flaw in an Android app. The app reads a Base64 encoded serialized object from the `payload_b64` `Intent` extra, deserializes it with `ObjectInputStream.readObject()`, and uses the result to overwrite the current user state. In this demo, an attacker can exploit the flaw by launching the activity with a crafted serialized `AdminUser` object, for example with:


{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-object-deserialization.yml }}

{{ run.sh }}

## Observation

The output contains the locations of all `ObjectInputStream.readObject()` usages in the code. Each instance was traced backward to verify whether the input is attacker-controlled and whether any filtering or validation is applied before deserialization.

{{ output.txt }}

## Evaluation

The test fails because the output shows that `ObjectInputStream.readObject()` is used on data that originates from an Intent extra without any filtering or type validation before deserialization. Review each of the reported instances:

- Line 103 shows the serialized data originates from an Intent extra (`payload_b64`), which can be controlled by any external application.
The test fails because the app deserializes untrusted data from an external `Intent` extra using `ObjectInputStream.readObject()` without any class filtering before deserialization.

`processIntent()` takes the Base64 value from `payload_b64`, decodes it, deserializes it, and if the result is a `BaseUser`, stores it as the current user.

