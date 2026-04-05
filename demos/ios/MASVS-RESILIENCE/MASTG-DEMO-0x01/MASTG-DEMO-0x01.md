---
platform: ios
title: Logging APIs Exposing Implementation Details with r2
code: [swift]
id: MASTG-DEMO-0x1
test: MASTG-TEST-0x01
---

## Sample

The sample code below demonstrates verbose logging across multiple iOS logging APIs, including `NSLog`, `print`, `debugPrint`, `dump`, and Apple Unified Logging via `Logger`, during authentication, networking, storage access, and error-handling. These code paths are designed to produce verbose debug and error output in the compiled binary.

The sample includes logs exposing an internal API endpoint, a username, a mock session token, cached profile usage, error object contents, stack traces, internal module names, authentication flow details, validation logic, and network-related configuration details.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main application binary (@MASTG-TECH-0058).
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the Radare2 script and identify cross references to logging API imports in the compiled binary.

{{ verbose_logging.r2 }}

{{ run.sh }}

## Observation

The output shows cross references to multiple logging APIs:

{{ output.txt }}

Specifically:

- **9 binary xrefs** to `Foundation.NSLog...` (the sample uses `NSLog(...)` 10 times).
- **22 binary xrefs** to `print.separator.terminator` (the sample uses `print(...)` 23 times).
- **2 binary xrefs** to `debugPrint.separator.terminator` (the sample uses `debugPrint(...)` 2 times).
- **1 binary xref** to `dump.name.indent...` (the sample uses `dump(...)` 1 time).
- **2 binary xrefs** to `Logger.subsystem.category...` (the sample uses `Logger(...)` 2 times).
- `logger.debug`, `logger.error`, and `logger.fault` are used **4 times** in the sample and result in:
  - **4 xrefs** to `Logger.logObject...`
  - **4 xrefs** to `_os_log_impl`
  - **4 xrefs** to `os_log_type_enabled`
  - **4 log type xrefs**: **2 debug**, **1 error**, **1 fault**

Note that the number of logging calls in the source code and the number of binary xrefs do not always match exactly. In this case, `NSLog` and `print` each show one fewer xref than the number of source calls. That can happen because of compiler optimizations, inlining, or code generation details in Swift.

You'll notice that even though we aren't calling the old C-style `os_log(...)` API directly, since we are using `Logger`, and `Logger` is part of Apple's Unified Logging system, we see references to `os_log`. Under the hood, Swift logging relies on the unified logging machinery, which is why lower-level logging symbols appear in the compiled binary.

## Evaluation

The test fails because the app contains implemented logging paths that record verbose diagnostic and error-related information, rather than merely linking against or referencing logging APIs.

This was determined by reverse engineering the binary to identify cross references to logging APIs and correlating them with the security-relevant code paths observed in the sample. The static analysis output shows that authentication, networking, storage, and error-handling code paths all lead to verbose logging calls, indicating that the compiled app will emit detailed diagnostic information in these contexts. You can further validate the specific log contents with dynamic analysis and runtime log capture, see @MASTG-DEMO-0x02, but that is outside the scope of this static demo.