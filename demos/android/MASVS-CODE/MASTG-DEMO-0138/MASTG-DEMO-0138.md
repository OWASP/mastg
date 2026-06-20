---
id: MASTG-DEMO-0138
title: Leaking Sensitive Arguments via Implicit Intents
platform: android
code: [kotlin]
kind: fail
---

## Sample

The following code implements an application that demonstrates how sensitive information can be leaked when sent as extras in an implicit intent. Because the intent is implicit, any application that registers for the action can receive it and extract its arguments. The snippets show both the original source code and the reversed Java version.

{{ ../MASTG-DEMO-0136/MastgTest.kt # ../MASTG-DEMO-0136/MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code to detect the use of implicit intents that have sensitive arguments attached.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The @MASTG-TOOL-0110 scan identifies the vulnerable code block where the implicit intent is created, populated with extras, and used to start an activity.

{{ output.txt }}

## Evaluation

The test case fails because the application transmits sensitive arguments using an implicit intent.

When reviewing the findings, closely examine the arguments being set on the intent. In this case, `user_id` and `session_token` are passed as extras. Because the intent is implicit, these sensitive values are exposed to potential interception by any other application on the device that registers for the same action.
