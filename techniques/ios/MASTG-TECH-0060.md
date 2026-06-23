---
title: Monitoring System Logs
platform: ios
---

Many apps emit runtime messages through more than one output path. On iOS, it is important to distinguish between:

- **System logs / Unified Logging**, such as messages emitted through `NSLog`, `os_log`, or `Logger`.
- **Process console output**, such as messages written to standard output or standard error, for example through `print`, `debugPrint`, or `dump`.

These outputs may overlap in development tools, but they are not equivalent. In practice, Xcode can show both app console output and system log entries, while system log collection tools generally only show messages that reach the Unified Logging pipeline. Apple documents Unified Logging and its log viewing tools separately, and also documents that Simulator launch can attach an app's standard input, output, and error to a terminal with `simctl launch --console-pty`.

## Using @MASTG-TOOL-0070

Xcode is a convenient way to observe both app activity and log output during testing.

1. Launch Xcode.
2. Connect your device to your host computer, or use a simulator.
3. Choose **Window** -> **Devices and Simulators**.
4. Select the connected iOS device or simulator.
5. Reproduce the behavior you want to inspect.
6. Open the device or simulator console to review emitted log entries.

Apple documents the Devices and Simulators workflow in Xcode, and also notes that for Simulator you can open the simulated device's system log from Xcode.

<img src="Images/Chapters/0x06b/open_device_console.png" width="100%" />

To save the console output to a text file, use the **Save** button in the Console window.

<img src="Images/Chapters/0x06b/device_console.png" width="100%" />

## Using @MASTG-TOOL-0126

For a connected physical device, `idevicesyslog` can be used to collect device logs from the command line.

1. Connect your device to your host computer.
2. Run `idevicesyslog | grep YOUR_APP_NAME` in your terminal.
3. Reproduce the behavior in the app and review the captured output.

This method is useful for collecting device side logs outside Xcode, but it should be treated as a system log collection technique rather than a full replacement for the Xcode console.

<img src="Images/Chapters/0x06b/idevicesyslog-screen.png" width="100%" />

!!! note
    `idevicesyslog` may not show all log levels or all output sources. In particular, debug and info level Unified Logging messages may be filtered, transient, or unavailable depending on configuration and collection method. Apple's logging documentation explains that Unified Logging uses different stores and retention behavior, and Apple Developer guidance notes that viewing debug and info messages may require enabling them explicitly in log viewers.

## Using system log tooling on the Simulator

For simulator based testing, Apple provides command line access to log collection through `simctl spawn booted log`. This is useful when you want to monitor the simulator's **system log** rather than rely on the Xcode UI. See Apple's documentation on [Getting the Most Out of Simulator](https://developer.apple.com/videos/play/wwdc2019/418/) for more details.

Example:

```sh
xcrun simctl spawn booted log stream --style compact --level debug --predicate 'process CONTAINS[c] "YOUR_APP_NAME"'
````

To retrieve historical log entries instead of streaming live output, use `log show`:

```sh
xcrun simctl spawn booted log show --style compact --last 5m --predicate 'process CONTAINS[c] "YOUR_APP_NAME"'
```

Apple documents the Unified Logging tools for viewing log messages, and Xcode documentation also points to opening the simulator system log. See [Generating Log Messages from Your Code](https://developer.apple.com/documentation/os/generating-log-messages-from-your-code).

## Capturing app console output on the Simulator

When you specifically need the app's standard output and standard error, for example messages produced by `print`, `debugPrint`, or `dump`, monitor the app console rather than only the system log.

For simulator runs, Apple documents `simctl launch --console-pty`, which attaches the app's standard input, output, and error to the current terminal.

Example:

```sh
xcrun simctl launch --console-pty booted YOUR_BUNDLE_ID
```

This distinction matters during testing. Some messages visible in Xcode may come from the app's console output and not from the system log stream. As a result, `log stream` and Xcode Console output may differ even when observing the same app behavior.

## Practical guidance

When testing for verbose logging or sensitive log exposure on iOS:

- Use **system log monitoring** when you want to verify what reaches Unified Logging, for example `NSLog`, `os_log`, and `Logger`.
- Use **app console capture** when you want to observe standard output and error, for example `print`, `debugPrint`, and `dump`.
- Use **Xcode** when you want a convenient interactive view that may surface both during development.

In other words, do not assume that one collection method captures every log source. If a message appears in Xcode but not in a `log stream` capture, it may be coming from the app's console output rather than the system log.

This update makes the main missing point explicit, system logs and app console output are related but not identical, so different tools can legitimately show different subsets of messages.
