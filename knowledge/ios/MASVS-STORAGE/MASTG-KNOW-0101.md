---
masvs_category: MASVS-STORAGE
platform: ios
title: Logs
---

Logging is commonly used during development and troubleshooting to record runtime behavior, errors, and operational events. Depending on what is recorded, logs may include request/response metadata, identifiers, stack traces, and other diagnostic information, and may be visible in development tools, device logs, crash reports, or centralized log collectors.
iOS developers can write logs through several APIs and mechanisms, including:

- [`print`](https://developer.apple.com/documentation/swift/print(_:separator:terminator:))
- [`debugPrint`](https://developer.apple.com/documentation/swift/debugprint(_:separator:terminator:))
- [`NSLog`](https://developer.apple.com/documentation/foundation/nslog)
- [`Logger`](https://developer.apple.com/documentation/os/logger)
- [`os_log`](https://developer.apple.com/documentation/os/os_log)

Depending on what you log, these APIs can record:

- Authentication data, such as passwords, access tokens, refresh tokens, and cookies.
- Personally identifiable information, such as usernames, email addresses, account identifiers, and profile data.
- Network metadata, such as internal API routes, staging hosts, request IDs, headers, and backend names.
- Error details, such as `NSError.userInfo`, internal error codes, stack traces, and module names.
- Cached or persisted application data loaded from storage.

Apple's [Unified Logging system](https://developer.apple.com/documentation/os/logging) is the preferred logging mechanism on modern Apple platforms because it supports structured logging, log levels, and privacy controls. However, using Unified Logging does not automatically make logging safe. Sensitive values can still be exposed if developers log them directly or mark them as public.

Key concepts:

- **Log Levels**: Unified logging supports levels such as `debug`, `info`, `notice`, `error`, and `fault` to classify messages by severity.
- **Privacy Controls**: Interpolated values can be marked with privacy annotations such as `privacy: .private` to reduce exposure of sensitive data in logs.
- **Structured Logging**: Developers can organize logs by subsystem and category to improve observability without dumping raw internal state.

Common patterns that produce verbose logs include:

- Logging full request headers or bodies.
- Logging authentication responses containing tokens or cookies.
- Logging objects with `debugPrint` or `dump` when they contain sensitive fields.
- Logging complete `NSError` objects, including `domain`, `code`, and `userInfo`.
- Logging stack traces or internal class and method names in production builds.

Logs can be retained in different stores and collected by development tools or third-party services, so their contents can be visible outside the app during development and troubleshooting.

## Additional Logging Sources

Logging exposure on iOS is not limited to the standard Apple logging APIs. Sensitive information may also be emitted or persisted through other components integrated into the application.

- **Native libraries**: Bundled C, C++, or mixed language components may write directly to standard output or standard error using functions such as `printf`, `fprintf`, or related I/O APIs. These messages can become visible during development, debugging, or runtime monitoring.
- **Crash reporting and error monitoring tools**: Third party SDKs may collect logs, breadcrumbs, exception context, request metadata, or user actions and persist them locally before transmission. This can create an additional exposure surface beyond the app's immediate console output.
- **Networking and HTTP client libraries**: Debug or verbose modes in networking stacks may log URLs, headers, request bodies, response bodies, cookies, API keys, and authentication tokens.
- **WebViews and JavaScript logging**: Apps that embed web content may capture JavaScript console output or bridge messages from web content into native logging and diagnostic handlers, which can expose sensitive data originating in the web layer.
