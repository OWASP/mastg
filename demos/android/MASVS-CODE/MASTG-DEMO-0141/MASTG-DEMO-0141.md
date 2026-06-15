---
platform: android
title: Attacker App Returning Malicious ContentProvider Filename
id: MASTG-DEMO-0141
code: [kotlin, xml]
test: MASTG-TEST-0375
kind: info
---

## Sample

The following attacker app responds to the custom implicit intent `org.owasp.mastestapp.REQUEST_FILE` used in @MASTG-DEMO-0139. When selected by the user, its `AttackerActivity` returns a `content://` URI pointing to its own `AttackerContentProvider`, which supplies a path-traversal string (`../private/secret.txt`) as the `DISPLAY_NAME`. This causes the victim app to construct a `File` path that escapes its intended `public/` directory.

{{ MastgTest.kt # AndroidManifest.xml }}

You can use this app to demonstrate the vulnerability shown in @MASTG-DEMO-0139: install both apps on the same device, launch the victim app, and tap **Start**. The system will present a chooser dialog listing this app as a candidate for handling `REQUEST_FILE`. Selecting it delivers the malicious filename to the victim app, triggering the path traversal.

Note that this app is not inherently malicious in isolation. The vulnerability lies in the victim app trusting and using the filename returned by an external `ContentProvider` without sanitization.

## Steps

Let's run our @MASTG-TOOL-0110 rule against the manifest to detect the exported `ContentProvider`.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The rule detected the exported `ContentProvider` in the manifest:

{{ output.txt }}

## Evaluation

The finding confirms that this app declares an exported `ContentProvider`. This isn't a vulnerability in this app itself; it shows that the app can respond to `ContentProvider` queries from external apps and return attacker-controlled data. The actual vulnerability lies in the victim app (@MASTG-DEMO-0139) that uses the filename returned by the `ContentProvider` without sanitization, enabling path traversal.

When assessing an app for path traversal vulnerabilities, the threat model should include apps that expose a `ContentProvider` returning attacker-controlled filenames.
