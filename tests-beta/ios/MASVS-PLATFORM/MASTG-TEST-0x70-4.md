---
platform: ios
title: References to Missing URL Validation in the Data Handler
id: MASTG-TEST-0070-4
type: [static]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

After the receiver method extracts the incoming URL, the application's data handler processes it. Because the URL can be spoofed or maliciously crafted by an attacker using a bypassed domain, if the handler does not strictly deconstruct and validate the URL components (scheme, host, path, and query parameters) using [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents) before execution, an attacker can manipulate the application's routing logic or trigger unintended actions.

## Steps

1. Once the receiver method is identified, decompile the application binary (@MASTG-TECH-0048) to trace the data flow from the `application:continue:restorationHandler:` method.
2. Locate the specific data handling functions or routing classes where the URL is parsed.
3. Review the decompiled code to verify if `URLComponents` (or an equivalent strict URL parser) is used to validate the payload prior to execution.

## Observation

The output should contain either strict validation checks (e.g., explicitly verifying host strings and path integrity) or direct, unsafe usage of the raw URL properties.

## Evaluation

The test case fails if:

- The URL is processed without strict allow-listing of the host, path, and parameters.
- The validation logic relies on weak string matching which can be bypassed.
- The application does not gracefully drop the request and clear state if unexpected or malformed parameters are encountered.
