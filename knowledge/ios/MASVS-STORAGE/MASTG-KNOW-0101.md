---
masvs_category: MASVS-STORAGE
platform: ios
title: Logs
---

## Overview

Logs are a critical component for debugging and monitoring, used by developers to output runtime information. On iOS, logging has evolved from simple print statements and legacy APIs to the modern Unified Logging System, which provides performance and privacy features by design. Understanding how these mechanisms handle data—specifically what is persisted and what is redacted—is essential for secure application development.

## Logging APIs on iOS

iOS provides several mechanisms for logging, each with different characteristics regarding persistence and privacy.

### Unified Logging System

The standard logging mechanism on modern iOS versions (iOS 10+) is the **Unified Logging System**. It supersedes older APIs and provides a centralized, efficient way to capture log messages.

- **Swift**: The [`Logger`](https://developer.apple.com/documentation/os/logger) structure (introduced in iOS 14) is the recommended API. It offers a type-safe, performant interface.
- **Objective-C (and C)**: The [`os_log`](https://developer.apple.com/documentation/os/os_log) function is the primary interface.

### Legacy APIs

Older applications or libraries may still use deprecated or legacy logging methods:

- **`NSLog`**: Writes to the Apple System Log (ASL) and `stderr`. It is slower than `os_log` and, critically, does not support privacy redaction features.
- **`print` / `debugPrint` (Swift)**: Writes to standard output (`stdout`). These are typically intended for development and do not integrate with the system's structured logging features.
- **`printf` / `fprintf` (C/C++)**: Writes to standard I/O streams.

## Key Concepts

### Privacy Modifiers

A core feature of the Unified Logging System is its ability to handle sensitive data through **privacy modifiers**. When logging dynamic strings or variables, the system determines visibility based on these modifiers:

- **Private (`.private`)**: The data is redacted in the log output (displayed as `<private>`) unless a debugger is attached or the device is configured to collect significantly more diagnostic data. This is the **default** behavior for dynamic strings in Unified Logging, preventing accidental leakage of user data.
- **Public (`.public`)**: The data is visible in all logs. This should only be used for non-sensitive static information or control flow markers.
- **Sensitive (`.sensitive`)**: Treated similarly to private, but explicitly marking the data as highly sensitive (deprecated in favor of `.private` in newer documentation but functionally similar).

Static string literals (e.g., `Logger.info("Application started")`) are implicitly public.

### Log Levels and Persistence

Logs are categorized by levels, which affect whether they are persisted to disk or only kept in memory:

- **Default**: Captured in memory; persisted to disk only if a failure occurs.
- **Info**: Captured in memory; normally not persisted.
- **Debug**: Captured in memory only when debug logging is explicitly enabled via configuration profiles.
- **Error / Fault**: Always captured and usually persisted to disk, as they indicate critical issues.

## Non-Standard Logging Sources

Beyond direct API calls, logs can originate from other components:

- **Standard Output (`stdout` / `stderr`)**: Data written to these streams (via `print`, `printf`, `std::cout`) may be captured by the system's logging daemon (redirected to the unified log) or crash reporting tools. Unlike `os_log`, these streams have no concept of privacy levels.
- **WebViews**: Apps using `WKWebView` may generate logs from JavaScript sources (`console.log`). These are bridged to the native system and can be observable if the application or device is configured for web debugging.
- **Crash Reports**: When an app crashes, the system generates a crash report. Some crash reporting frameworks allow developers to attach "custom keys" or "breadcrumbs" (logs leading up to the crash). If sensitive data is included in these breadcrumbs, it persists in the crash report even if it wouldn't have been persisted by the normal logging system.
- **Third-Party SDKs**: Libraries for analytics, advertising, or networking often include their own logging logic. If a developer unknowingly leaves a library in "verbose" or "debug" mode for a release build, the SDK may write extensive internal state (including PII or authentication tokens) to the system logs, bypassing the application's own logging policies.

## Common Pitfalls

Developers often introduce security risks through improper logging practices:

- **Logging Sensitive Data in Public Mode**: Explicitly marking PII (Personally Identifiable Information), authentication tokens, or session IDs as `.public` overrides the system's default redaction, permanently writing secrets to the device logs.
- **Using Legacy APIs for Sensitive Data**: Using `NSLog` or `print` for sensitive data bypasses the Unified Logging privacy system, meaning the data is never redacted.
- **Leaving Development Loops Enabled**: Third-party networking or analytics SDKs often have "debug" or "verbose" modes. If these are not stripped or disabled in release builds, they may output full HTTP request/response bodies (including headers and payloads) to the system logs.
- **Breadcrumb Leaks**: Attaching sensitive variables to crash report breadcrumbs serves as a persistent record of that data, which may be uploaded to third-party crash analysis services.

## References

- [Apple Developer Documentation: Logging](https://developer.apple.com/documentation/os/logging)
- [Apple Developer Documentation: Generating Log Messages](https://developer.apple.com/documentation/os/generating_log_messages_from_your_code)
- [Apple Developer Documentation: Unified Logging and Activity Tracing](https://developer.apple.com/documentation/os/unified_logging_and_activity_tracing)
