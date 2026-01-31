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

## Additional Logging Sources

Beyond the standard iOS logging APIs, developers may introduce logs through various other mechanisms:

### Native Libraries

Lower-level native libraries written in C or C++ may write directly to standard output (`stdout`) or standard error (`stderr`) using functions such as `printf`, `fprintf`, or similar I/O functions. These outputs can appear in device logs, particularly during development and debugging sessions. When these libraries are integrated into iOS applications, their log output becomes part of the application's logging footprint.

### Crash Reporting and Error Monitoring

Crash reporting and error monitoring tools (such as Crashlytics, Sentry, or similar services) may record logs, breadcrumbs, or contextual data to disk before uploading them to remote servers. These persistent records can outlive a single app session and may include environmental data, user actions, or system state information captured at the time of an error or crash. The data collected by these tools is often retained locally until network connectivity allows for transmission.

### Networking and HTTP Clients

Networking stacks and HTTP client libraries sometimes provide verbose or debug logging modes that can output detailed information about network requests and responses. This may include HTTP headers, request URLs, response bodies, or authentication tokens. When these debug modes are inadvertently enabled in production builds, the detailed network logs may expose credentials, API keys, session tokens, or personal data.

### WebViews and JavaScript Console

Applications that embed web content using `WKWebView` or `UIWebView` (deprecated) can receive logging output from JavaScript code running within the web context. JavaScript's `console` methods (such as `console.log`, `console.error`, `console.warn`) produce messages that can be bridged into the native logging system. These messages can be captured by implementing the appropriate delegate methods, such as [`WKScriptMessageHandler`](https://developer.apple.com/documentation/webkit/wkscriptmessagehandler) for `WKWebView`, which allows the native app to receive and process JavaScript console output.
