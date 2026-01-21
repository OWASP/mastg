---
platform: ios
title: Hardcoded HTTP URLs
id: MASTG-TEST-0321
type: [static]
weakness: MASWE-0050
profiles: [L1, L2]
---

## Overview

An iOS app may have hardcoded HTTP URLs embedded in the app binary, library binaries, or other resources within the IPA. These URLs may indicate potential locations where the app communicates with servers over an unencrypted connection.

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0071 to search for any `http://` URLs.

## Observation

The output should contain a list of URLs and their locations within the app.

## Evaluation

The test case fails if any HTTP URLs are confirmed to be used for communication.

!!! warning Limitations
    The presence of HTTP URLs alone does not necessarily mean they are actively used for communication. Their usage may depend on runtime conditions, such as how the URLs are invoked and whether cleartext traffic is allowed in the app's ATS configuration. For example, HTTP requests may fail if App Transport Security (ATS) is enabled and no exceptions are configured (see @MASTG-TEST-0322) or may succeed if the app uses low-level APIs that bypass ATS (see @MASTG-TEST-0322).

Additionally, complement this static inspection with dynamic testing methods. For example, capture and analyze network traffic to confirm whether the app connects to the identified HTTP URLs during real-world usage. See @MASTG-TEST-0236.
