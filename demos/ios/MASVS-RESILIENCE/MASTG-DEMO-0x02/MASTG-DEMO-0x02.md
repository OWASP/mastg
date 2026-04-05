---
platform: ios
title: Verbose Error Logging Runtime Analysis
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
---

## Sample

The sample code below demonstrates insecure verbose logging across multiple iOS logging APIs, including `NSLog`, `print`, `debugPrint`, `dump`, and Apple Unified Logging via `Logger`.

The sample intentionally logs sensitive and internal data during authentication, networking, storage access, and error handling. The emitted logs expose internal API endpoints, usernames and passwords, bearer tokens, refresh tokens, cookies, request and response metadata, cached profile data, error object contents, stack traces, internal module names, and network configuration details.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

## Steps

1. Install and launch the application on an iOS device or simulator.
2. Start monitoring system logs as described in @MASTG-TECH-0060.
3. Interact with the application and trigger the vulnerable logging flow from the UI.
4. Capture the emitted runtime logs.

## Observation

Monitoring system logs during runtime reveals that the application emits verbose debug and error messages containing sensitive data and internal implementation details. The captured logs are stored in `system_log.txt`, while `output.txt` contains the PID used during the run.

{{ system_log.txt # output.txt }}

## Evaluation

The test fails because runtime log monitoring shows that the application emits sensitive information and internal implementation details through multiple logging APIs.

The observed logs reveal:

- **Internal endpoints and backend context**: The logs disclose the authentication endpoint `https://internal-api.example.com/v2/auth/login`, backend identifiers, request IDs, and staging related details.
- **Credentials and authentication data**: The logs expose usernames, passwords, bearer tokens, refresh tokens, session related values, and cookies.
- **Request and response contents**: The application logs request headers, authorization data, request bodies, response headers, response bodies, and other HTTP metadata.
- **Stored application data**: The logs reveal cached profile information and authentication related values loaded from local storage.
- **Detailed error information**: The logs expose `NSError` descriptions, domains, codes, `userInfo` contents, retry related flags, and module level context.
- **Internal implementation details**: The logs reveal authentication flow behavior, cache usage, offline fallback handling, validation logic, and internal module names such as `AuthenticationService.validateCredentials()` and `NetworkManager`.
- **Network and security configuration**: The logs disclose timeout values, retry counts, SSL pinning status, and certificate validation behavior.
- **Execution context**: The logs include stack trace information that exposes internal symbols and code paths.

This dynamic evidence confirms that verbose logging is not only present in the binary, but also actively emits sensitive runtime information that should not be exposed, especially in production builds.
