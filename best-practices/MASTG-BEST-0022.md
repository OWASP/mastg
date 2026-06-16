---
title: Disable Verbose and Debug Logging in Production Builds
alias: remove-logging-in-production
id: MASTG-BEST-0022
platform: ios
knowledge: [MASTG-KNOW-0101]
---

When logging information, it's crucial to protect sensitive values and avoid exposing unnecessary implementation details.

## Keep Production Logs Minimal

Use logging only for operational events that are necessary for support and monitoring. Production logs should be limited to high-level, non-sensitive events that are useful for monitoring and support. Good examples include a generic authentication failure, a network timeout, or an unexpected state transition.

In particular, avoid logging:

- full request or response headers and bodies.
- authentication tokens, cookies, session identifiers, or API keys.
- usernames, email addresses, or other personal data unless strictly necessary and appropriately protected.
- full error objects, diagnostic context, attached metadata, nested causes, or stack traces.
- backend hostnames, staging endpoints, feature flags, or internal module and class names.
- certificate validation behavior, SSL pinning status, retry logic, or other network security details.

## Use Logging APIs with Privacy Controls

When logging is required, prefer the APIs that use [Apple's Unified Logging system](https://developer.apple.com/documentation/os/logging): [`Logger`](https://developer.apple.com/documentation/os/logger) in Swift or [`os_log`](https://developer.apple.com/documentation/os/os_log) in Objective-C. Avoid ad hoc logging through `print`, `NSLog`, or third-party SDKs that do not support structured logging and privacy controls.

### Privacy Modifiers

Apple's Unified Logging system provides [privacy modifiers](https://developer.apple.com/documentation/os/oslogprivacy) that let you control how data appears in logs.

- **`.private`** redacts the value in persistent logs while still allowing debugging workflows.
- **`.private(mask:)`** can preserve limited correlation, for example by hashing a value without exposing the original.
- **`.sensitive`** behaves like `.private`, but remains redacted even when private data logging is enabled.
- **`.public` (not recommended)** explicitly marks a value as safe to display in logs. Use this only for non-sensitive operational information.

Privacy modifiers help protect individual values, but they do not make verbose logging safe by themselves. The principle of minimal logging still applies.

### Log Levels

Apple's Unified Logging system supports multiple [log levels](https://developer.apple.com/documentation/os/oslogtype) so you can categorize messages by importance and severity.

- **`debug`** for detailed debugging information.
- **`info`** for general operational messages.
- **`error`** for failures the app can recover from.
- **`fault`** for serious failures that require immediate attention.

Use these levels carefully. Higher quality logging is not about emitting more detail, it is about emitting only the detail that is appropriate for the environment. In production, avoid using log levels as a reason to include sensitive values or internal implementation details.

## Use Macros or Build Flags to Disable Verbose Logging in Production

To reduce risk, verbose diagnostics should be compiled out of release builds whenever possible. This is especially important for `print`, `NSLog`, and ad hoc debugging statements.

### 1. Swift

```swift
#if DEBUG
print("Hello world")
#endif
```

### 2. Objective-C

```objectivec
#ifdef DEBUG
# define NSLog(...) NSLog(__VA_ARGS__)
#else
# define NSLog(...)
#endif
```

Then set the `DEBUG` flag in **Apple Clang - Preprocessing > Preprocessor Macros** for development builds only.
