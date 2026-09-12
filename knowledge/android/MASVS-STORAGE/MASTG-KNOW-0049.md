---
masvs_category: MASVS-STORAGE
platform: android
title: Logs
best-practices: [MASTG-BEST-0002]
---

Logging is commonly used during development and troubleshooting to record runtime behavior, errors, and operational events. Depending on what is recorded, logs may include request/response metadata, identifiers, stack traces, and other diagnostic information, and may be visible in development tools, device logs, crash reports, or centralized log collectors. Android developers can write logs through several APIs and mechanisms, including:

- [`android.util.Log`](https://developer.android.com/reference/android/util/Log)
- [`java.util.logging.Logger`](https://developer.android.com/reference/java/util/logging/Logger)
- [`System.out`](https://developer.android.com/reference/java/lang/System#out)
- [`System.err`](https://developer.android.com/reference/java/lang/System#err)
- [`Throwable#printStackTrace`](https://developer.android.com/reference/java/lang/Throwable#printStackTrace)
- [`__android_log_print`](https://developer.android.com/ndk/reference/group/logging) and related `<android/log.h>` APIs for native code

## The Android Logging System

Several of these APIs feed into Android's logging infrastructure. `android.util.Log` writes to the [Android logging system](https://developer.android.com/tools/logcat), where `logd` maintains several circular buffers, including `main`, `system`, `crash`, `radio`, and `events`. Android [redirects `System.out` and `System.err` to the Android log](https://android.googlesource.com/platform/frameworks/base/%2B/master/core/java/com/android/internal/os/RuntimeInit.java), and `Throwable#printStackTrace` writes to the standard error stream by default, so its output follows the same path. Android's [`AndroidHandler`](https://android.googlesource.com/platform/frameworks/base/%2B/master/core/java/com/android/internal/logging/AndroidHandler.java) can route `java.util.logging.Logger` records to the Android log by mapping `java.util.logging.Level` values to Android log priorities. Native C/C++ code can use the NDK `<android/log.h>` APIs. `__android_log_print` writes formatted messages to the `main` log buffer. See @MASTG-DEMO-0006 for an example of tracing common Android logging APIs at runtime.

## Log Levels and Tags

`android.util.Log` assigns each message a priority from `VERBOSE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, and `ASSERT`. The convenience methods `v`, `d`, `i`, `w`, and `e` correspond to these priorities, while `wtf` logs at `ASSERT`. The lower-level `println` method accepts the priority directly. Each log entry can include a tag identifying its source. [`isLoggable`](https://developer.android.com/reference/android/util/Log#isLoggable) reports whether a tag and priority meet the configured threshold. The default threshold for a tag is `INFO`, and it can be changed using the `log.tag.<TAG>` system property. On API level 25 and lower, tags passed to `isLoggable` are limited to 23 characters. This restriction was removed in API level 26.

## Data in Log Output

Depending on what is logged, Android logging APIs and related mechanisms can record:

- Authentication data, such as passwords, access tokens, refresh tokens, and cookies.
- Personally identifiable information, such as usernames, email addresses, account identifiers, and profile data.
- Network metadata, such as internal API routes, staging hosts, request IDs, headers, and backend names.
- Error details, such as exception messages, internal error codes, stack traces, and class or module names.
- Cached or persisted application data loaded from storage.

## Access to Log Output

Since Android 4.1 (API level 16), [access to device-wide `logcat` output has been restricted](https://developer.android.com/privacy-and-security/risks/log-info-disclosure). The [`READ_LOGS`](https://developer.android.com/reference/android/Manifest.permission#READ_LOGS) permission isn't available to ordinary third-party applications and can be granted to privileged system applications. Third-party applications can still access their own log output. With an authorized `adb` connection, [`adb logcat`](https://developer.android.com/tools/logcat) can be used to inspect the device's log buffers. [Android bug reports](https://developer.android.com/studio/debug/bug-report) can also contain `logcat` system messages, including stack traces and messages written by applications using the `Log` API.

## Other Platform Logging APIs

- [`android.util.Slog`](https://android.googlesource.com/platform/frameworks/base/%2B/master/core/java/android/util/Slog.java) is an internal Android framework logging class used by system components. It isn't part of the public SDK.

- [`android.util.EventLog`](https://developer.android.com/reference/android/util/EventLog) provides access to Android's system diagnostic event record for system-level events. Unlike `android.util.Log`, it is intended for system integrators rather than application authors; events use integer tag codes and can carry typed payloads.

## Additional Logging Sources

- Android applications may also use logging libraries such as [Timber](https://github.com/JakeWharton/timber) and [logback-android](https://github.com/tony19/logback-android). These libraries provide abstractions such as automatic tag handling, configurable filtering, and pluggable logging backends. Timber's `DebugTree`, for example, ultimately writes through `android.util.Log`, while other `Tree` implementations can route messages elsewhere. Similarly, logback-android can write to `logcat` through its `LogcatAppender` or to other configured destinations such as files or sockets.

- Application frameworks and SDKs can introduce additional logging paths. OkHttp's [`HttpLoggingInterceptor`](https://github.com/lysine-dev/okhttp/tree/main/okhttp-logging-interceptor), for example, records HTTP request and response information through a configurable logger. WebView JavaScript console messages can be received through [`WebChromeClient.onConsoleMessage()`](https://developer.android.com/reference/android/webkit/WebChromeClient#onConsoleMessage) and forwarded to `logcat`. Crash-reporting SDKs such as [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/android/customize-crash-reports) can maintain custom diagnostic logs and associate them with crash reports sent to their backend.

## Privacy Controls

The Android logging APIs described here don't provide per-value privacy metadata or automatic redaction comparable to Apple's Unified Logging. `android.util.Log` associates messages with a priority and tag, while `EventLog` associates structured event payloads with integer event tags. Neither exposes a subsystem/category model or privacy metadata for individual logged values.

In contrast, Apple's Unified Logging supports subsystem and category organization and [`OSLogPrivacy`](https://developer.apple.com/documentation/os/oslogprivacy) privacy options such as `private`, `public`, `sensitive`, and `auto`. Under its default privacy rules, dynamic strings and complex dynamic objects are redacted, while integer, floating-point, and Boolean values are not.

For Android-specific guidance on handling logging, see @MASTG-BEST-0002.