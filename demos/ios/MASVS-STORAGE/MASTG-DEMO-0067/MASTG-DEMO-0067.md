---
platform: ios
title: Runtime Tracking of Files Eligible for Backup with Frida
id: MASTG-DEMO-0067
code: [swift]
test: MASTG-TEST-0298
---

## Sample

The code snippet below shows sample code that writes files to storage at different locations.

{{ MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056)
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn your app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C`

{{ run.sh # script.js }}

## Observation

{{ output.txt }}

The output contains all calls to `open` including the paths of the files being accessed and backtraces. In this case the app is writing a file named `secret.json` to multiple locations.

## Evaluation

The test fails because the `secret.json` file exists in multiple directories that are eligible for backup:

```txt
open(.../Documents/secret.json)
open(.../Library/secret.json)
open(.../Library/Application Support/secret.json)
```
