---
platform: ios
title: Extracting the Universal Link Receiver Selector from the Binary
id: MASTG-DEMO-0070-3
code: [swift]
test: MASTG-TEST-0070-3
tools: [MASTG-TOOL-0129]
---

## Sample

The Swift source code below demonstrates an insecure Universal Link receiver method. During a black-box assessment, testers do not have this source code and must extract the Objective-C method signatures embedded within the compiled binary to confirm the application implements this receiver.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main app binary (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` to extract the strings and Objective-C selectors from the compiled binary using `rabin2` (@MASTG-TOOL-0129).
3. Check the output for the `application:continue:restorationHandler:` selector.

{{ run.sh }}

## Observation

The output confirms that the compiled binary contains the exact Objective-C selector required to intercept and process incoming Universal Links from the operating system.

{{ output.txt }}

## Evaluation

The test fails (or rather, confirms the presence of the attack surface) because the `application:continue:restorationHandler:` selector is found inside the binary.

Once this selector is identified, it triggers the next phase of analysis: the tester must decompile the binary (e.g., using Ghidra) or use dynamic instrumentation (Frida) to confirm whether the extracted URL payload is securely validated using `URLComponents` before the application processes it.
