---
platform: ios
title: Source Code Integrity Check using dladdr and CC_SHA256 with r2
code: [swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: pass
status: draft
---

## Sample

The sample code demonstrates source code integrity checking by using `dladdr` to resolve the binary base address, parsing the Mach-O header to locate the `__TEXT/__text` section, and applying `CC_SHA256` to compute a hash of it.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ source_integrity.r2 }}

{{ run.sh }}

## Observation

The output shows references to `dladdr` and `CC_SHA256` in the binary's import table along with their cross-references, indicating calls from the app's source code integrity check routine.

{{ output.asm }}

## Evaluation

The test passes because the app contains references to source code integrity check APIs (`dladdr`, `CC_SHA256`).
