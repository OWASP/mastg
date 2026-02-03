---
platform: ios
title: Uses of Logging APIs with r2
code: [swift]
id: MASTG-DEMO-0065
test: MASTG-TEST-0297
---

## Sample

The code snippet below shows sample code that insecurely logs a sensitive token:

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` to find all occurrences of `NSLog`.
3. Update `loggingApis.r2` to also detect other logging APIs, such as `print`.

{{ loggingApis.r2 }}

{{ run.sh }}

## Observation

The output contains the location of `NSLog` usage in the binary.

{{ output.asm # function.asm }}

Reading `output.asm` doesn't clearly show what arguments are passed to `NSLog`, so you can also make use of `function.asm` for a better overview.

## Evaluation

The test fails because the app calls `NSLog` with a secret token as an argument.
