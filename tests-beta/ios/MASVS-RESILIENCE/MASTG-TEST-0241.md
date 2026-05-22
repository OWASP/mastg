---
platform: ios
title: Runtime Use of Jailbreak Detection Techniques
id: MASTG-TEST-0241
type: [dynamic, hooks]
weakness: MASWE-0097
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0084]
---

## Overview

The test verifies that a mobile application can identify if the iOS device it is running on is jailbroken. It does so by dynamically analyzing the app binary for common jailbreak detection checks (@MASTG-KNOW-0084) and trying to bypass them. For example, it may detect a check for the presence of certain files or directories that are indicative of a jailbroken device.

The limitations of using jailbreak detection bypass tools should be considered. It is possible that the app uses more sophisticated jailbreak detection techniques that are not detected by the tool used. In such cases, careful manual reverse engineering and deobfuscation are required to identify the jailbreak detection checks. Also, additional dynamic analysis work may be required to bypass the jailbreak detection checks.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0152 to bypass the jailbreak detection.

## Observation

The output should include any instances of common jailbreak detection checks in the app binary and the results of the automated jailbreak detection bypass commands.

## Evaluation

The test case fails if jailbreak detection is not implemented.

!!! note
    This test is not exhaustive and may not detect all jailbreak detection checks as it relies on predefined bypass code that may not cover all possible jailbreak detection checks or may not be up-to-date. The checks may also be more sophisticated than what the tool can detect, so manual reverse engineering and deobfuscation may be required to identify them.
