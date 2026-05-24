---
title: debugmepLS
platform: android
source: https://github.com/sgIOlas/debugmepLS
hosts: [android]
---

[debugmepLS](https://github.com/sgIOlas/debugmepLS) is an @MASTG-TOOL-0149 module and companion app that makes selected Android apps appear debuggable at runtime. It hooks framework services in `system_server` to modify `ApplicationInfo` and process start flags, allowing chosen packages to report `FLAG_DEBUGGABLE` without patching or re-signing APKs.

The companion app provides a per-app enable list with search, a system-app toggle, and an LSPosed connection status header. This is useful when you need to attach a JDWP debugger to a non-debuggable app during dynamic testing.

## Requirements and Usage Notes

- Requires Android 13 or later and LSPosed with the libxposed API.
- The LSPosed scope must include System Framework.
- Changes apply after the target app process restarts.
- Apps may still detect root, hooking frameworks, or debugger attachment through other checks.

For installation and usage instructions, refer to the official [debugmepLS repository](https://github.com/sgIOlas/debugmepLS).
