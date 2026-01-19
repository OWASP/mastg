---
masvs_category: MASVS-STORAGE
platform: ios
title: Logs
---

## Overview

Logs are a critical component for debugging and monitoring, but they often become a source of data leakage in mobile applications. While standard system logs are the most common concern, sensitive data can also be exposed through other channels such as standard output (`stdout`), third-party embedded libraries, crash reporting tools, and web views.

On iOS, the primary logging mechanism is **Apple's Unified Logging System**, but developers (and third-party SDKs) may still use legacy APIs or language-specific print functions that behave differently in production environments.

### Logging Sources

1. **Unified Logging System (`os_log`, `Logger`)**: The modern standard. It provides memory-based, performant logging with privacy features.
2. **Standard Output/Error (`print`, `printf`, `std::cout`)**: Common in Swift and C/C++ code. These streams might be captured by the system console or crash reporting tools.
3. **Legacy Logging (`NSLog`)**: An older API that is slower and more verbose, typically writing to ASL (Apple System Log) and `stderr`.
4. **Third-Party SDKs**: Libraries for analytics, networking, or ads often include their own logging mechanisms, which can be verbose if not properly configured for production.
5. **WebView Console Logs**: JavaScript `console.log` calls within `WKWebView` or `UIWebView` (deprecated) may be retrievable by attaching a debugger or using specific tools.
6. **Crash Reports**: Crash handlers (e.g., Crashlytics, KSCrash) capture stack traces and heap data. Custom keys or logged events attached to crashes can leak user state.

## Impact

If an app logs sensitive information (e.g., session tokens, PII, passwords, request/response bodies), this data becomes accessible to:

- Anyone with physical access to the unlocked device (via Console.app).
- Malware running on the device (if logs are persisted to world-readable files, though system logs are generally restricted).
- Developers or attackers with access to the crash reporting dashboard.

## Static Analysis

Review the source code for logging usage. Look for:

- **Keywords**: `print`, `debugPrint`, `NSLog`, `os_log`, `Logger`, `dump`.
- **Third-Party Configs**: Initialization of SDKs with debug flags (e.g., `FirebaseConfiguration.shared.setLoggerLevel(.debug)`, `Alamofire.Session(startRequestsImmediately: true)`).
- **Preprocessor Macros**: Ensure debug code is wrapped in `#if DEBUG`.

### Example: Insecure Logging in Swift

```swift
func authenticate(user: User, pass: String) {
    // BAD: Leaks credentials to stdout
    print("Authenticating user: \(user.id) with pass: \(pass)")
    
    // BAD: Specific SDK logging enabled in production
    SomeAnalytics.setLogLevel(.verbose)
}
```

## Dynamic Analysis

To capture and analyze logs from an iOS device, you can use **Console.app** on macOS or the `log` command-line tool.

### 1. Using Console.app

1. Connect the iOS device to a Mac via USB.
2. Open **Console.app** (Applications > Utilities > Console).
3. Select the device in the sidebar.
4. In the search bar, verify that "Include Info Messages" and "Include Debug Messages" are enabled (Action menu).
5. Filter by the application's **Process Name** or **Bundle Identifier** (e.g., `com.example.myapp`).
6. Interact with the application, focusing on sensitive inputs (login, profile updates, payments).
7. **Observation**: Watch for plain-text credentials, tokens, or PII appearing in the main log window.

### 2. Using the `log` CLI

For more advanced filtering or scripting, use the `log` command via `xcrun simctl` (for Simulator) or directly on connected devices.

```bash
# Stream logs from a connected device specifically for a process
# --predicate filters for the specific process name
# --debug includes debug-level logs often missed by default
xcrun simctl spawn booted log stream --predicate 'process == "MyApp"' --debug
```

### 3. Checking WebView Logs

If the application uses WebViews:

1. Enable **Web Inspector** on the iOS device: `Settings` > `Safari` > `Advanced` > `Web Inspector`.
2. Connect the device to your Mac.
3. Launch **Safari** on your Mac.
4. Open the app on the device and navigate to a page with a WebView.
5. In Mac Safari, go to `Develop` > `[Device Name]` > `[App Name/URL]`.
6. Open the **Console** tab in the Web Inspector.
7. **Observation**: Check if JS logs (`console.log`) expose session cookies, tokens, or user data.

### 4. Third-Party and Network Logs

Some libraries log network traffic or internal state. Even if the app itself is quiet, an outdated or debug-configured network library (like Alamofire or AFNetworking) might dump headers and bodies.

- **Trigger**: Perform network requests.
- **Observation**: Look for JSON objects or HTTP headers in the Console output.

## Remediation

### 1. Use Unified Logging with Privacy

Prefer `os_log` or `Logger` (iOS 14+). Mark sensitive variables as `.private`.

```swift
import os

let logger = Logger(subsystem: "com.example.app", category: "Network")

// Sensitive data is redacted in release logs
logger.info("User login: \(username, privacy: .public), Token: \(token, privacy: .private)")
```

### 2. Strip Debug Print Statements

Remove standard print statements in release builds using compiler flags.

```swift
func logDebug(_ message: String) {
    #if DEBUG
    print("[DEBUG] \(message)")
    #endif
}
```

### 3. Configure Third-Party Libraries

Ensure all third-party SDKs have their logging disabled or set to `ERROR` level in the release configuration.

```swift
#if !DEBUG
    Analytics.setLogLevel(.none)
    NetworkLib.consoleLogging = false
#endif
```

## References

- [Apple Documentation - Logging](https://developer.apple.com/documentation/os/logging)
- [Apple Documentation - Generating Log Messages](https://developer.apple.com/documentation/os/generating_log_messages_from_your_code)
