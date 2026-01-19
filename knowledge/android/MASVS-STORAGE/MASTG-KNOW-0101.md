---
masvs_category: MASVS-STORAGE
platform: android
title: Logs
---

## Overview

Logging is a fundamental part of Android application development, used for debugging, monitoring, and error reporting. However, production builds often inadvertently retain verbose logging code, leading to the exposure of sensitive data such as authentication tokens, user credentials, PII (Personally Identifiable Information), and internal application logic.

While the standard `android.util.Log` class is the most common mechanism, logs can originate from various other sources that testers must verify:

1.  **System Logs (`Logcat`)**: The primary Android logging buffer.
2.  **Standard Output/Error (`System.out`, `stdout`, `stderr`)**: Java `System.out` and native C/C++ `printf` calls.
3.  **Third-Party SDKs**: Analytics, ads, and crash reporting libraries often have their own debug modes.
4.  **Network Libraries**: HTTP clients (e.g., OkHttp) configured to log request/response headers and bodies.
5.  **WebViews**: JavaScript `console.log` messages from embedded web pages.

## Static Analysis

Review the source code and configuration files for indicators of excessive logging.

### Java/Kotlin Sources
Search for usage of the `Log` class, `System.out`, and `System.err`.

```bash
grep -r "Log\." .
grep -r "System\.out\." .
grep -r "System\.err\." .
```

### Native Code (C/C++)
Native libraries used via JNI/NDK might use `<android/log.h>` or standard I/O.

```bash
grep -r "__android_log_print" .
grep -r "printf" .
grep -r "std::cout" .
```

### Third-Party & Network Configuration
Look for debug flags or logging interceptors initialization.

```java
// Vulnerable Examples
// OkHttp Logging Interceptor
logging.setLevel(HttpLoggingInterceptor.Level.BODY);

// Firebase / Analytics
FirebaseConfiguration.getInstance().setLogLevel(Logger.Level.DEBUG);

// WebView Debugging
WebView.setWebContentsDebuggingEnabled(true);
```

## Dynamic Analysis

Dynamic analysis involves interacting with the application while monitoring various log outputs to identify sensitive data leaks.

### 1. Monitoring Logcat

The primary tool for Android log analysis is `adb logcat`. Filters should be applied to focus on the application's process.

**Test Steps:**

1.  Connect the device and identify the app's process ID (PID) or package name.
2.  Clear the existing logs: `adb logcat -c`.
3.  Start monitoring logs for the target package.
4.  Exercise application functionality (Login, Search, Payment, etc.).

```bash
# Filter by package name (modern adb)
adb logcat --pid=$(adb shell pidof -s com.example.vulnerableapp)

# Or grep for the package name (older adb)
adb logcat | grep "com.example.vulnerableapp"
```

**Observation:**
Look for sensitive values like passwords, "Bearer" tokens, or user details appearing in the output.

### 2. Native (C/C++) Logging

Native code often uses `__android_log_print`, which appears in Logcat with a custom tag. Cross-platform code might use `printf` or `stdout`, which may be redirected to Logcat under the tag `stdout`, `stderr`, or a generic system tag depending on the Android version and device configuration.

**Example Logcat Output (Native):**
```text
D/MyNDKApp(12345): [Native] Decrypting user key...
I/stdout(12345): User Secret: 0xDEADBEEF
```

### 3. WebView Console Logs

If an app uses WebViews, JavaScript logs (`console.log`) may be retained. These can sometimes be seen in Logcat (bridget internally) or via Remote Debugging.

**Test Steps:**
1.  Enable "USB Debugging" on the device.
2.  Open the app and navigate to the WebView.
3.  On your desktop Chrome browser, go to `chrome://inspect`.
4.  Click "Inspect" under the target Webview.
5.  Check the "Console" tab for leaked data.

**Example Console Output:**
```text
> console.log("Auth Token:", sessionStorage.getItem("auth_token"));
Auth Token: eyJhbGciOiJIUzI1Ni...
```

### 4. Network & Third-Party SDK Logs

Third-party libraries (e.g., Facebook SDK, Flurry, Crashlytics) and HTTP clients (OkHttp, Retrofit) can generate verbose logs if debug modes are enabled.

**Test Steps:**
1.  Trigger network-heavy operations (feed refresh, uploading data).
2.  Watch Logcat for tags related to common libraries (e.g., `OkHttp`, `Facebook`, `Firebase`).

**Example Logcat Output (Network):**
```text
D/OkHttp  (12345): --> POST https://api.example.com/login http/1.1 (52-byte body)
D/OkHttp  (12345): {"username":"admin","password":"SuperSecretPassword"}
D/OkHttp  (12345): <-- END HTTP (52-byte body)
```

## Remediation

### 1. Strip Logs in Production (R8/ProGuard)
Configure R8 (ProGuard) rules to remove logging calls from the release bytecode.

**proguard-rules.pro:**
```proguard
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
```

### 2. Use Build Config Flags
Wrap logging code in conditional blocks that check the build type.

```java
if (BuildConfig.DEBUG) {
    Log.d("TAG", "Sensitive data: " + data);
}
```

### 3. Disable Third-Party Logging
Explicitly disable debug logging for dependencies in your Application initialization logic.

```java
if (!BuildConfig.DEBUG) {
    // OkHttp
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
    
    // WebView
    WebView.setWebContentsDebuggingEnabled(false);
    
    // Analytics
    SomeAnalyticsSDK.setLogLevel(SomeAnalyticsSDK.LogLevel.NONE);
}
```
