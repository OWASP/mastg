---
platform: ios
title: Verbose Error Logging and Debugging Messages
id: MASTG-TEST-03x1
type: [static]
weakness: MASWE-0094
knowledge: [MASTG-KNOW-0064, MASTG-KNOW-0101]
profiles: [R]
---

## Overview

This test checks for verbose error logging and debugging messages in iOS applications. While logging is useful during development, verbose logging in production builds can expose implementation details such as function names, code paths, internal state information, and error conditions that could be exploited by attackers performing reverse engineering.

Common logging APIs on iOS include `NSLog`, `print`, `println`, `dump`, `debugPrint`, and `os_log`. Applications should ensure that debug-level logging is disabled in production builds and that any error messages logged are minimal and don't reveal sensitive implementation details.

This test focuses on verbose logging that exposes implementation details. For tests specifically targeting sensitive data in logs, see @MASTG-TEST-0296 and @MASTG-TEST-0297.

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0072 to look for uses of logging APIs such as `NSLog`, `print`, `os_log`, `dump`, and `debugPrint`.
3. Use @MASTG-TECH-0071 to look for logging strings.
4. Use @MASTG-TECH-0076 to analyze the relevant code paths and correlate strings and logging API calls where needed.

## Observation

The output should contain a list of logging function calls found in the binary.

## Evaluation

The test case fails if the app logs verbose debug messages in production builds that expose implementation details such as:

- Internal function names or code paths
- Detailed error messages with stack information
- API endpoints or internal URLs
- Internal state or configuration details
- Library or framework version information
- Debugging information intended only for developers
