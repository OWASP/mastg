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

Static analysis of the binary shows cross references to multiple logging related imports, including `NSLog`, `print`, `debugPrint`, `dump`, and unified logging related symbols such as `Logger`, `_os_log_impl`, and `os_log_type_enabled`.

{{ output.txt }}

### Evaluation

The test fails because static analysis shows that the application binary invokes multiple logging APIs capable of producing verbose diagnostic and error output.

The Radare2 output demonstrates cross references to:

- **Traditional logging APIs**: `NSLog`, `print`, and `debugPrint`.
- **Object inspection APIs**: `dump`, which can disclose internal object state and structured error contents.
- **Unified logging APIs**: `Logger` related symbols, `_os_log_impl`, and `os_log_type_enabled`.
- **Multiple log severities**: Unified logging paths associated with debug, error, and fault level logging.

This static evidence is sufficient to show that the binary contains multiple code paths that emit runtime log output. To confirm the specific sensitive content exposed by these log calls during execution, see the related dynamic demo for @MASTG-TEST-03x2.
