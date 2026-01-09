---
title: No Networking in Native Code
alias: no-networking-in-native-code
id: MASTG-BEST-0028
platform: android
knowledge: [MASTG-KNOW-0109]
---

Use the Java/Kotlin layer for networking only.

The @MASTG-KNOW-0109 provide libraries which are secured by the @MASTG-KNOW-0014.

As they come with the Android Operating System, the app developer does not need to provide these libraries. In order to make sure, that the libraries are secure, it is recommended testing if the OS is up to date and not affected by known vulnerabilities (@MASTG-TEST-0245).

## If Native Networking Is Necessary

If you develop native code using the [Android Native Development Kit (NDK)](https://developer.android.com/ndk), you will not have access to high level network libraries.

You, or any networking library you use, can still directly use POSIX sockets. However, @MASTG-KNOW-0014 will not apply automatically.

Some applications, like games or streaming apps, require low latency network connections. Hence they often provide their own native network stack.

In this case, the developer is responsible to make sure the libraries are to date (@MASTG-TEST-0274) and [configured securely](../Document/0x04f-Testing-Network-Communication.md#verifying-the-tls-settings).
