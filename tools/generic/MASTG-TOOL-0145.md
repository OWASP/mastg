---
title: Frooky
platform: generic
source: https://github.com/cpholguera/frooky
host: [ windows, linux, macOS ]
---

[Frooky](https://github.com/cpholguera/frooky "Frooky on GitHub") is a Frida-based dynamic analysis tool for Android and iOS applications. It enables security researchers to instrument mobile apps using JSON-based hook configurations, providing a declarative approach to method interception without writing custom Frida scripts.

## Capabilities

- Hook Java/Kotlin methods and native C/C++ functions
- Support for method overloads with specific argument signatures
- Stack trace capture with configurable depth
- Flexible argument decoding for multiple data types
- Conditional hook triggering based on argument values or stack patterns
- JSON Lines (NDJSON) output format for streamlined data processing
- Support for multiple hook files that get merged together

## Installation

Frooky requires Python 3.10 or later. Install via pip:

```bash
pip install frooky // pip3, or pipx
```

## Usage

Attach to an already running app:

```bash
frooky -U -n "My App" --platform android hooks.json
```

Spawn and instrument an app:

```bash
frooky -U -f com.example.app --platform android storage.json crypto.json
```

Refer to the [official documentation](https://github.com/cpholguera/frooky#usage) for more details on command-line options and configuration.
