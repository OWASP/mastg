---
title: Native Code Tracing
platform: ios
---

As discussed earlier in this chapter, iOS applications can also contain native code (C/C++) that can be traced with the `frida-trace` CLI. For example, you can trace calls to the `open` function by running the following command:

```bash
frida-trace -U -i "open" sg.vp.UnCrackable1
```

The overall approach and further improvements for tracing native code using Frida are similar to those discussed in @MASTG-TECH-0034.

Unfortunately, there are no tools such as `strace` or `ftrace` available to trace syscalls or function calls of an iOS app. Only `DTrace` exists, a powerful and versatile tracing tool, but it's available only on macOS, not on iOS.
