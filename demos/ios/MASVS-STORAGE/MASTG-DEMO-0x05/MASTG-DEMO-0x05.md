---
platform: ios
title: Hardcoded API Keys in the App Binary
id: MASTG-DEMO-0x05
code: [swift]
test: MASTG-TEST-0x04
kind: fail
---

## Sample

This sample stores three third-party credentials as Swift string constants: a Google API key, an AWS access key ID, and a client secret.

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0070 to extract the strings from the binary and locate the credentials. Run the r2 script with the `-i` option.

{{ hardcoded_secrets.r2 # run.sh }}

## Observation

The output lists the 27 strings in the `__TEXT.__cstring` section, the two credentials matching well-known provider formats, the cross references to all three credentials, and the disassembly of the function that loads them.

{{ output.txt }}

## Evaluation

The test case fails because credentials that authenticate the app to third-party services are stored in the app binary and can be recovered from the package.

The three credentials appear as plaintext entries in `__TEXT.__cstring`:

- `AIzaSyDFakeMastgDemoKeyNotARealKey12345` at `0x100009460`
- `AKIAIOSFODNN7EXAMPLE` at `0x1000094b0`
- `s3cr3t-not-a-real-value-9f2b` at `0x1000094f0`

The cross reference list shows all three being loaded by `sym.func.100004e2c`, and the disassembly shows the first one being materialized at `0x100004ec8` before being appended to the output string. No decryption or derivation happens in between: the values sit in the binary exactly as they were written in the source.

Note that the compiler preserved every credential as a distinct string literal, including `s3cr3t-not-a-real-value-9f2b`, which is not in any recognizable provider format. Searching only for known key formats such as the `AIza` and `AKIA` prefixes would find two of the three. The full `__TEXT.__cstring` listing is what reveals the third, which is why the test extracts all strings rather than relying on format patterns alone.

See @MASTG-BEST-0x02 for mitigations, including delivering keys over the air after app and device attestation and proxying third-party calls through a backend.
