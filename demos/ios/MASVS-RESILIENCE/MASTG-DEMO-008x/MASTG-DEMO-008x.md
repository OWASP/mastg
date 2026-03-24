---
platform: ios
title: Verbose Error Logging Analysis
code: [swift]
id: MASTG-DEMO-008x
test: MASTG-TEST-03x1
---

### Sample

The sample code below demonstrates insecure verbose logging across multiple iOS logging APIs, including `NSLog`, `print`, `debugPrint`, `dump`, and Apple Unified Logging via `Logger`.

The sample intentionally invokes logging APIs during authentication, networking, storage access, and error handling. These code paths are designed to produce verbose debug and error output in the compiled binary.

{{ MastgTest.swift }}

### Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the Radare2 script and identify cross references to logging related imports in the compiled binary.

{{ verbose_logging.r2 }}

{{ run.sh }}

### Observation

The output shows cross references to multiple logging APIs:

{{ output.txt }}

Specifically:

- `NSLog(...)` is used **10 times** in the sample and results in **9 binary xrefs** to `Foundation.NSLog...`.
- `print(...)` is used **23 times** in the sample and results in **22 binary xrefs** to `print.separator.terminator`.
- `debugPrint(...)` is used **2 times** in the sample and results in **2 binary xrefs** to `debugPrint.separator.terminator`.
- `dump(...)` is used **1 time** in the sample and results in **1 binary xref** to `dump.name.indent...`.
- `Logger(...)` is used **2 times** in the sample and results in **2 binary xrefs** to `Logger.subsystem.category...`.
- `logger.debug`, `logger.error`, and `logger.fault` are used **4 times** in the sample and result in:
    - **4 xrefs** to `Logger.logObject...`
    - **4 xrefs** to `_os_log_impl`
    - **4 xrefs** to `os_log_type_enabled`
    - **4 log type xrefs**: **2 debug**, **1 error**, **1 fault**

Note that the source count and xref count do not always match exactly. In this case, `NSLog` and `print` each show one fewer xref than the number of source calls. That can happen because of compiler optimizations, inlining, or code generation details in Swift.

You'll notice that even though we aren't calling the old C style `os_log(...)` API directly, since we are using `Logger`, and `Logger` is part of Apple's Unified Logging system we see references to `os_log`. Under the hood, Swift logging relies on the unified logging machinery, which is why lower level logging symbols appear in the compiled binary.

### Evaluation

The test fails because analysis showed that the application contains implemented logging paths that record verbose diagnostic and error related information, rather than merely linking against or referencing logging APIs.

This was determined by reverse engineering the binary to inspect the data supplied to logging calls, and can be further validated by running the app and capturing runtime logs to confirm the specific content emitted during execution. Static analysis helps identify what may be logged across the binary, but can be difficult when code is optimized, stripped, or indirect (this demo). Dynamic analysis shows what is actually logged in tested scenarios, but may miss code paths that are not triggered (see @MASTG-DEMO-008y). Taken together, these approaches are sufficient to demonstrate that the app produces overly verbose production log output.
