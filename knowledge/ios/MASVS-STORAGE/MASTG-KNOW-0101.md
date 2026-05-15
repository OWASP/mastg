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
