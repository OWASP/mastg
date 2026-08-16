---
platform: android
title: Attacker App Returning Malicious ContentProvider Filename
id: MASTG-DEMO-0141
code: [kotlin, xml]
test: MASTG-TEST-0375
kind: attack
---

## Sample

The following attacker app responds to the custom implicit intent `org.owasp.mastestapp.REQUEST_FILE` used in the @MASTG-DEMO-0139 demo. When Android resolves it as a handler, its `AttackerActivity` returns a `content://` URI pointing to its own `AttackerContentProvider`, which supplies a path-traversal string (`../private/secret.txt`) as the `DISPLAY_NAME`.

{{ MastgTest.kt # AndroidManifest.xml }}

Note that this app is not inherently malicious in isolation. The vulnerability lies in the victim app trusting and using the filename returned by an external `ContentProvider` without sanitization.

## Steps

1. Use @MASTG-TECH-0005 to install the victim app from @MASTG-DEMO-0139.
2. Use @MASTG-TECH-0005 to install this attacker app on the same device.
3. Launch the victim app and tap **Start** to trigger the file request.
4. If Android presents a resolver for `REQUEST_FILE`, select this attacker app.
5. Run `run.sh` to capture the returned URI and provider metadata from logcat.

{{ run.sh }}

## Observation

The attacker app logs the returned URI, the provider-controlled display name, and the payload served through the returned `content://` URI:

{{ output.txt }}

## Evaluation

The test case fails because @MASTG-DEMO-0139 accepts provider-controlled data returned by this attacker app and uses it in a file write without validation.

The output confirms that the attacker app returned a `content://` URI and supplied `../private/secret.txt` as `OpenableColumns.DISPLAY_NAME`.

When the victim app receives this result, it queries the returned provider, trusts the provider-controlled filename, and uses it to build a destination `File` under its `filesDir/public/` directory. Because the filename contains `../`, the resulting path escapes the intended `public/` directory and points to `files/private/secret.txt`.

The attacker app therefore controls both the file content served through the returned URI and the filename metadata that drives the victim app's write location.
