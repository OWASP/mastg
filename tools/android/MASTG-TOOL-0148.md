---
title: apkeep
platform: android
source: https://github.com/EFForg/apkeep
hosts: [windows, linux, macOS]
---

[apkeep](https://github.com/EFForg/apkeep) is a command-line tool for downloading APK files from various sources including Google Play Store. It's designed to make downloading and archiving Android APKs simple and reliable.

apkeep offers several key features:

- Download APKs directly from Google Play Store
- Supports downloading specific versions of apps
- Can download split APKs and bundle them
- Provides CSV output for automation
- No authentication required for free apps

## Installation

apkeep is written in Rust and can be installed via cargo:

```bash
cargo install apkeep
```

Alternatively, you can download pre-built binaries from the [releases page](https://github.com/EFForg/apkeep/releases).

## Usage

To download an APK, use the package name:

```bash
apkeep -a com.example.app .
```

This downloads the latest version of the app to the current directory.

To download a specific version:

```bash
apkeep -a com.example.app@1.2.3 .
```

To download and list all split APKs:

```bash
apkeep -a com.example.app -d .
```

For more information about available options, run:

```bash
apkeep --help
```
