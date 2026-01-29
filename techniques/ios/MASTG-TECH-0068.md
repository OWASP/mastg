---
title: Disassembling Native Code
platform: ios
---

Because Objective-C and Swift are fundamentally different, the programming language used to write the app affects the feasibility of reverse engineering it. For example, Objective-C allows method invocations to be changed at runtime. This makes hooking into other app functions (a technique heavily used by [Cycript](http://www.cycript.org/ "Cycript") and other reverse engineering tools) easy. This "method swizzling" is implemented differently in Swift, which makes the technique harder to implement in Swift than in Objective-C.

On iOS, all the application code (both Swift and Objective-C) is compiled to machine code (e.g. ARM). Thus, to analyze iOS applications, a disassembler is needed.

If you want to disassemble an application from the App Store, remove the FairPlay DRM first. See @MASTG-TECH-0054 for more information.

In this context, the term "app binary" refers to the Mach-O file in the application bundle, which contains the compiled code and should not be confused with the application bundle - the IPA file. See @MASTG-TECH-0058 for more details on the composition of IPA files.

## Disassembling With IDA Pro

If you have a license for IDA Pro, you can also analyze the app binary with IDA Pro.

> The free version of IDA unfortunately does not support the ARM processor type.

To get started, open the app binary in IDA Pro.

<img src="Images/Chapters/0x06c/ida_macho_import.png" width="100%" />

Upon opening the file, IDA Pro will perform auto-analysis, which can take some time depending on the binary's size. Once the auto-analysis is completed, you can browse the disassembly in the **IDA View** (Disassembly) window and explore functions in the **Functions** window, both shown in the screenshot below.

<img src="Images/Chapters/0x06c/ida_main_window.png" width="100%" />

A regular IDA Pro license does not include a decompiler by default and requires an additional license for the Hex-Rays decompiler, which is expensive. In contrast, Ghidra consists of a highly capable, free built-in decompiler, making it a compelling alternative for reverse engineering.

If you have a regular IDA Pro license and do not want to buy the Hex-Rays decompiler, you can use Ghidra's decompiler by installing the [GhIDA plugin](https://github.com/Cisco-Talos/GhIDA/) for IDA Pro.
