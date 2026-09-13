---
platform: android
title: Accessibility Service Reading Sensitive Input
id: MASTG-DEMO-0x02
code: [kotlin, xml]
kind: attack
test: MASTG-TEST-0x01
---

## Sample

The attacker app implements an `AccessibilityService` that is not declared as an accessibility tool. It observes the MASTestApp package and logs text and other accessibility metadata exposed by its nodes.

Note: the service's `AccessibilityServiceInfo` metadata resource is provided through `filepaths.xml`, reusing the resource file that the demo build automation already supports replacing. This is a workaround: the underlying resource being replaced is not related to file paths, and ideally the build automation should be extended with a dedicated `accessibility_service_config.xml` file.

{{ MastgTest.kt # AndroidManifest.xml # filepaths.xml }}

## Steps

1. Install the attacker app on the device (@MASTG-TECH-0005).
2. Open the attacker app and tap **Start** to open Android Accessibility settings.
3. Enable the attacker accessibility service.
4. Clear the previous log output with `./run.sh clear`.
5. Open @MASTG-DEMO-0x01, tap **Start**, and enter `MASTG-SECRET-1234`.
6. Run `./run.sh` to save the accessibility service output.

{{ run.sh }}

## Observation

The accessibility service receives the victim app's accessibility node and reports the secret in its content description.

{{ output.txt }}

## Evaluation

The test case fails because `MASTG-SECRET-1234` is available in plaintext to an accessibility service whose `isAccessibilityTool` property is `false`.
