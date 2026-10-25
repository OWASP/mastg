---
title: Maestro
platform: generic
source: https://github.com/mobile-dev-inc/maestro
hosts: [windows, linux, macOS]
---

[Maestro](https://github.com/mobile-dev-inc/maestro) is an open-source UI testing framework for Android, iOS, and web apps. You describe interactions, such as tapping elements, entering text, and taking screenshots, in YAML files called flows, and Maestro runs them against the app on a connected device or emulator.

For security testing, Maestro is useful to inspect the UI hierarchy of a screen and to automate repetitive UI interactions, for example, to reach a specific screen before you run other tools.

## Installation

Maestro requires Java 17 or later. Install it with the following command:

```bash
curl -fsSL "https://get.maestro.mobile.dev" | bash
```

See the [official documentation](https://docs.maestro.dev/) for other installation options.

## Usage

Print the UI hierarchy of the current screen as JSON:

```bash
maestro hierarchy
```

Run a flow:

```bash
maestro test flow.yaml
```

!!! note
    On Android, Maestro reads the UI through the accessibility framework, so the hierarchy only includes the attributes the accessibility framework exposes. See @MASTG-TECH-0x01 for what this method reveals compared to other methods.
