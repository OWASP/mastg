---
title: Disable Verbose and Debug Logging in Production Builds
alias: remove-logging-in-production
id: MASTG-BEST-0022
platform: ios
---

You should avoid using insecure logging mechanisms like `print` or [`NSLog`](https://developer.apple.com/documentation/foundation/nslog). These APIs can expose sensitive runtime data to system logs, which an attacker with device access may retrieve. Instead, you should adopt [Apple's Unified Logging system](https://developer.apple.com/documentation/os/logging) (`Logger` in Swift, `os_log` in Objective-C), available from iOS 10.0 and later.

If you rely on `print` or `NSLog`:

- Your logs may end up in system diagnostics and remain accessible to attackers.
- Debuggers or jailbroken devices can capture verbose log messages.
- There is a risk of exposing tokens, passwords, or PII.

## Use Unified Logging Features

Switching to Unified Logging gives you structured, privacy-aware logging that is safer for production environments. Here are the main features you can use when adopting [`Logger`](https://developer.apple.com/documentation/os/logger) (Swift) or [`os_log`](https://developer.apple.com/documentation/os/os_log) (Objective-C):

### Privacy Modifiers

When logging information, it's crucial to protect sensitive data such as personal identifiers, authentication tokens, or secrets. Apple's unified logging system provides [privacy modifiers](https://developer.apple.com/documentation/os/oslogprivacy) that let you control how data appears in logs.

- **`.public`**: Explicitly marks the value as safe to display in all logs. Use this only for **non-sensitive debug information**.
- **`.private`**: Redacts the value in persistent logs but still shows it in memory while debugging (e.g., PII, secrets, tokens, and sensitive data).
- **`.private(mask:)`**: Allows you to preserve data correlation. For example, applying a hash mask enables identifying repeated values across logs without exposing the raw data.
- **`.sensitive`**: Behaves identically to `.private`, but remains redacted even if private data logging is globally enabled.

### Log Levels

Unified logging supports multiple [log levels](https://developer.apple.com/documentation/os/oslogtype) to help you categorize and prioritize messages based on their importance and severity. By assigning the appropriate log level, you can control which messages appear in production, aid in debugging, and quickly identify critical issues that require attention.

- **`debug`**: Used for detailed debugging information.
- **`info`**: Used for general operational messages.
- **`error`**: Used when something goes wrong, but the app can continue.
- **`fault`**: Used for serious issues that require immediate attention (e.g., crashes, corruption).

## Use Macros to Disable Logging in Production

To ensure maximum security, the safest approach is to completely remove these logging calls from the app. Below is sample code that demonstrates how to eliminate logging APIs from your application during compilation.

### 1. Swift

```swift
#if DEBUG
print("Hello world")
#endif
```

### 2. Objective-C

```objectivec
#ifdef DEBUG 
# define NSLog (...) NSLog(__VA_ARGS__) 
#else 
# define NSLog (...) 
#endif
```

Then you need to set `DEBUG` flag in `Apple Clang - Preprocessing > Preprocessor Macros` for the development builds.
