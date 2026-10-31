---
title: Android CLI
platform: android
source: https://developer.android.com/tools/agents/android-cli
hosts: [windows, linux, macOS]
---

[Android CLI](https://developer.android.com/tools/agents/android-cli) is a command-line tool from Google that gives scripts, CI pipelines, and coding agents a single interface to Android projects, devices, and emulators. Among other things, it can capture screenshots and return the UI layout of the app running on a connected device or emulator.

## Installation

Follow the instructions on the [official download page](https://developer.android.com/tools/agents/android-cli/download), which provides installers for Linux, macOS, and Windows. For example, on macOS you can install it with Homebrew:

```bash
brew tap android/tap
brew install android-cli
```

Keep it up to date with:

```bash
android update
```

## Usage

Capture a screenshot of the connected device:

```bash
android screen capture --output=screenshot.png
```

Add `--annotate` to draw labeled bounding boxes around the UI elements detected on the screen.

Return the UI layout of the running app as JSON:

```bash
android layout --pretty --output=layout.json
```

See the [Android CLI documentation](https://developer.android.com/tools/agents/android-cli) for all commands and options.
