---
masvs_category: MASVS-RESILIENCE
platform: generic
title: Code Injection
---

Code injection is a very powerful technique that allows you to explore and modify processes at runtime. Injection can be implemented in various ways, but you'll get by without knowing all the details thanks to freely available, well-documented tools that automate the process. These tools give you direct access to process memory and important structures such as live objects instantiated by the app. They come with many utility functions that are useful for resolving loaded libraries, hooking methods and native functions, and more. Process memory tampering is more difficult to detect than file patching, so it is the preferred method in most cases.

@MASTG-TOOL-0139, @MASTG-TOOL-0031, and @MASTG-TOOL-0027 are the most widely used hooking and code injection frameworks in the mobile industry. The three frameworks differ in design philosophy and implementation details: ElleKit and Xposed focus on code injection and/or hooking, while Frida aims to be a full-blown "dynamic instrumentation framework", incorporating code injection, language bindings, and an injectable JavaScript VM and console.

We recommend starting with Frida because it is the most versatile of the three (for this reason, we also include more Frida details and examples across the MASTG). Notably, Frida can inject a JavaScript VM into a process on both Android and iOS, while injection with ElleKit only works on iOS and Xposed only works on Android. Ultimately, however, you can of course achieve many of the same goals with either framework.
