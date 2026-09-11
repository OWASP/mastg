---
platform: ios
title: Implementation Details Exposed in App Logs
code: [swift]
id: MASTG-DEMO-0125
test: MASTG-TEST-0359
---

## Sample

This demo uses the sample from @MASTG-DEMO-0124.

{{ ../MASTG-DEMO-0124/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056)
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device
3. Make sure you have @MASTG-TOOL-0126 installed on your machine and the `idevicesyslog` tool available in your PATH
4. Run `run.sh` to spawn your app with Frida and to start monitoring system logs with `idevicesyslog` as described in @MASTG-TECH-0060
5. Click the **Start** button
6. Stop the script by pressing `Ctrl+C`

{{ run.sh }}

The script collects two complementary log sources:

1. `frida.log` contains process output captured with Frida, including Swift `print`, `debugPrint`, `NSLog`, and intercepted unified logging calls
2. `unified.log` contains rendered iOS unified logging output collected with `idevicesyslog`, including `NSLog` and `os.Logger` messages

## Observation

The following output includes both `idevicesyslog` and Frida results to show how the two collection methods differ. `idevicesyslog` shows rendered iOS unified logging records, such as `NSLog` and `os.Logger`, but may miss stdout based Swift `print` and `debugPrint` messages. Therefore, absence from `idevicesyslog` alone does not prove that the app did not log sensitive data.

{{ unified.log }}

Frida captures stdout and stderr based logging by hooking functions such as `fwrite` and `write`. In this run, Frida captured all relevant messages, including Swift `print`, `debugPrint`, `NSLog`, and observed unified logging calls. Using both sources helps correlate results and reduces the chance of missing logs that are only visible through one method.

{{ frida.log }}

## Evaluation

The test case fails because runtime log monitoring shows that the application emits internal implementation details through multiple logging mechanisms.

The observed logs reveal the following information.

1. Internal endpoint details. The app logs the internal authentication endpoint.

   ```log
   [NSLogv.format] [DEBUG] Attempting to connect to API endpoint: https://internal-api.example.com/v2/auth/login
    ```

2. Authentication flow behavior. The app logs that `performLogin()` was called and includes the supplied username.

   ```log
   [fwrite] [DEBUG] performLogin() called with username: testuser
   ```

3. Local credential validation behavior. The app logs that credentials are validated against a local database.

   ```log
   [_os_log_impl.format] Validating credentials against local database
   ```

4. Password handling details. The app logs the password hashing algorithm used during validation.

   ```log
   [_os_log_impl.format] Checking password hash: SHA256 algorithm
   ```

5. Cache and network behavior. The app logs that the user profile is loaded from cache and that a network call is bypassed.

   ```log
   [fwrite] "[DEBUG] User profile loaded from cache, bypassing network call"
   ```

6. Session handling details. The app logs that authentication succeeded and exposes a generated session token.

   ```log
   [fwrite] "✅ [DEBUG] Authentication successful - Session token generated: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
   ```

The logs also expose the username `testuser` and a session token. These values are sensitive data and should be assessed under the appropriate sensitive data logging test, such as @MASTG-TEST-0296 or @MASTG-TEST-0297.
