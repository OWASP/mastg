---
title: Static Analysis on iOS
platform: ios
---

The preferred method for statically analyzing iOS apps is to use the original Xcode project files. Ideally, you can compile and debug the app to quickly identify potential issues in the source code.

Black box analysis of iOS apps without access to the source code requires @MASTG-TECH-0065. For example, no decompilers are available for iOS apps (although most commercial and open-source disassemblers can provide a pseudo-source code view of the binary). Hence, a deep inspection requires you to read assembly code.
