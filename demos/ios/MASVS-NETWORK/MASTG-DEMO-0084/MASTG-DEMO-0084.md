---
platform: ios
title: Hardcoded HTTP URLs in iOS Binary
code: [swift]
id: MASTG-DEMO-0084
test: MASTG-TEST-0321
kind: fail
---

## Sample

The code snippet below shows sample code that uses hardcoded HTTP URLs:

{{ ../MASTG-DEMO-0083/MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to search for HTTP URLs in the binary.

{{ http_urls.r2 }}

{{ run.sh }}

## Observation

The output contains a list of HTTP URLs found in the binary and locations in the app binary:

{{ output.txt }}

## Evaluation

The test fails because the hardcoded HTTP URL `http://httpbin.org/get` was found in the binary, and the app has an ATS configuration that allows cleartext HTTP traffic to that domain (see @MASTG-DEMO-0083).

We know that the URL is actually used by the app because the string is used at `0x100005130` and passed to an `URL` instance at `0x100005158` which is then used in a `URLSession` at `0x100005238`.
