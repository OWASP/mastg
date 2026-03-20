---
platform: ios
title: Wildcard in the Associated Domains Entitlement
id: MASTG-DEMO-0070-1
code: [xml]
test: MASTG-TEST-0070-1
tools: [MASTG-TOOL-0126]
---

## Sample

The snippet below shows the `entitlements.plist` file embedded in the application bundle. It declares the domains the application is authorized to open via Universal Links.

{{ entitlements.plist }}

## Steps

1. Unzip the app package and extract the entitlements from the app binary (@MASTG-TECH-0058). For this demo, the file is pre-extracted and provided as `entitlements.plist`.
2. Run `run.sh` to parse the file using `plistutil` (@MASTG-TOOL-0126) and search for the `com.apple.developer.associated-domains` key.

{{ run.sh }}

## Observation

The output contains the associated domains array parsed from the XML structure, listing the Universal Link capabilities configured during the signing process.

{{ output.txt }}

## Evaluation

The test fails because the application configures an overly broad and risky domain in its associated domains array. Specifically:

- `applinks:*.example.com` — Using a wildcard (`*`) expands the attack surface, allowing an attacker who compromises any forgotten or unsecured subdomain to intercept Universal Links intended for this application.
