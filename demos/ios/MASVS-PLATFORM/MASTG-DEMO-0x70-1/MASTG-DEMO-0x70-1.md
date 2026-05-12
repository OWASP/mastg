---
platform: ios
title: Wildcard in the Associated Domains Entitlement
id: MASTG-DEMO-0x70-1
code: [xml]
test: MASTG-TEST-0x70-1
tools: [MASTG-TOOL-0126]
---

## Sample

The code snippet below shows the `entitlements.plist` file embedded in the application bundle. It declares the domains the application is authorized to open via Universal Links.

{{ entitlements.plist }}

## Steps

1. Extract the app package content using @MASTG-TECH-0058 and locate the `entitlements.plist` file, which is located at `./Payload/MASTestApp.app/entitlements.plist`.
2. Run the `run.sh` script to parse the `entitlements.plist` and search for the `com.apple.developer.associated-domains` key.

{{ run.sh }}

## Observation

The output contains the associated domains array parsed from the XML structure, listing the Universal Link capabilities configured during the signing process.

{{ output.txt }}

## Evaluation

The test fails because the application configures an overly broad and risky domain in its associated domains array. Specifically:

- `applinks:*.example.com` — Using a wildcard (`*`) expands the attack surface, as any compromised or misconfigured subdomain could be exploited to intercept Universal Links intended for the application.
