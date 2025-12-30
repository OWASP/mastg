---
masvs_category: MASVS-STORAGE
platform: ios
title: Logs
---

iOS developers can use various logging mechanisms to output runtime information for debugging and monitoring purposes. Traditional logging methods, such as `print` statements or `NSLog`, can inadvertently expose sensitive information in system logs, which may be accessible to attackers with device access.

A modern and secure approach to logging on iOS is to use [Apple's Unified Logging system](https://developer.apple.com/documentation/os/logging), which provides structured and privacy-aware logging capabilities. This system allows developers to categorize log messages by severity and apply privacy modifiers to protect sensitive data.

The relevant APIs include:

- [`Logger`](https://developer.apple.com/documentation/os/logger) (Swift)
- [`os_log`](https://developer.apple.com/documentation/os/os_log) (Objective-C)

Key concepts:

- **Privacy Modifiers**: These modifiers help control how data appears in logs. For example, `.private` redacts sensitive information in persistent logs, while `.public` allows non-sensitive data to be displayed openly.
- **Log Levels**: Unified logging supports multiple log levels (e.g., `debug`, `info`, `error`, `fault`) to help categorize messages based on their importance and severity.

Apart from using secure logging APIs, developers can also implement build configurations or preprocessor directives to disable or limit logging in production builds. This ensures that sensitive information is not exposed in live environments.

## Additional Logging Sources to Consider

While Apple's Unified Logging system is the recommended approach, developers should be aware of other logging sources that may inadvertently expose sensitive information:

- **Native Libraries (C/C++)**: Lower-level native libraries written in C or C++ may write directly to standard output (`stdout`) or standard error (`stderr`) using functions like `printf` or `fprintf`. These outputs may appear in system logs, especially during development and debugging sessions.

- **Crash Reporting and Error Monitoring Tools**: Crash reporting SDKs and error monitoring services may record logs, stack traces, or breadcrumbs to disk and upload them later. These persistent records may outlive a single app session and may include contextual data that could expose sensitive information if not carefully filtered.

- **Networking Stacks and HTTP Clients**: Networking libraries and HTTP clients sometimes provide verbose or debug logging modes that dump headers, URLs, request/response bodies, or payloads. If these modes are enabled outside of debug builds, they may expose credentials or personal data.

- **WebView and JavaScript Logs**: Apps that embed web content via `WKWebView` may expose JavaScript console logs and errors. These logs may include sensitive data logged from JavaScript code and may be accessible to native code or reflected in system logs.
