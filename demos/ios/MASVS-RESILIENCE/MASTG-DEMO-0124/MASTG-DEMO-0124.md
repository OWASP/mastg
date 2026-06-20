---
platform: ios
title: Logging APIs Exposing Implementation Details with r2
code: [swift]
id: MASTG-DEMO-0124
test: MASTG-TEST-0358
---

## Sample

The sample code below demonstrates verbose logging across multiple iOS logging APIs, including `NSLog`, `print`, `debugPrint`, `dump`, and Apple Unified Logging via `Logger`, during authentication, networking, storage access, and error-handling. These code paths are designed to produce verbose debug and error output in the compiled binary.

The sample includes logs exposing an internal API endpoint, a username, a mock session token, cached profile usage, error object contents, stack traces, internal module names, authentication flow details, validation logic, and network-related configuration details.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main application binary (@MASTG-TECH-0058).
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the Radare2 script. The script first identifies cross references to the logging API imports, and then disassembles a selection of those call sites to recover the actual log messages. At each call site the message string is loaded into a register by an `adrp`/`add` pair right before the `bl` to the logging API, and Radare2 resolves that pointer and annotates it with the literal string, so you can read exactly what is logged.

{{ verbose_logging.r2 }}

{{ run.sh }}

## Observation

The output has two parts. The first lists cross references to multiple logging APIs, and the second shows the disassembly of selected call sites with the recovered log message strings.

{{ output.txt }}

The cross references show how often each logging API is reached:

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

The second part of the output goes beyond confirming that the APIs are referenced and shows the literal message strings recovered from the call sites. Each block is the full disassembly window from the string load down to the logging call (with `asm.bytes=true`, the raw opcode bytes are shown alongside each instruction), so the data flow is explicit end to end: an `adrp`/`add` pair computes a pointer into the `__cstring` (or `__oslogstring`) section, Radare2 resolves it and prints the string as a comment (for example `; 0x10000ba80 ; "[DEBUG] Attempting to connect to API endpoint: https://internal-api.example.com/v2/auth/login"`), the pointer is moved through a few argument-setup instructions, and the same register is then handed to the `bl` into the logging API that closes the window. Reading each snippet top to bottom shows the string flowing into the log call. The recovered content includes an internal API endpoint, the username, a mock session token (`eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`), the disabled SSL pinning state, the password hashing algorithm, and an internal error code and module name, all of which expose implementation details.

## Evaluation

The test fails because the app contains implemented logging paths that record verbose diagnostic and error-related information, rather than merely linking against or referencing logging APIs.

This was determined by reverse engineering the binary in two steps. First, cross references to the logging APIs show that authentication, networking, and error-handling code paths reach `NSLog`, `print`, `debugPrint`, `dump`, and unified logging (`Logger`/`os_log`). Second, disassembling those call sites recovers the literal message strings that are passed to the logging functions, so the conclusion is not based on the mere presence of logging APIs but on the actual content that is logged. The recovered strings confirm that the compiled app emits sensitive implementation details, including an internal API endpoint, a username, a mock session token, the SSL pinning state, the password hashing algorithm, and an internal error code and module name.

The disassembly recovers the static string operands of each logging call. Values that are only known at runtime, such as the interpolated results of `generateMockToken()` or `Thread.callStackSymbols`, are not resolved by static analysis; to capture those concrete values you can use dynamic analysis and runtime log capture, see @MASTG-DEMO-0125.
