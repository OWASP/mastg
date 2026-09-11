---
title: Reverse Engineering Android Apps
platform: android
---

Android's openness makes it a favorable environment for reverse engineers, offering significant advantages not available on iOS. Because Android is open-source, you can study its source code at the Android Open Source Project (AOSP) and modify the OS and its standard tools any way you want. Even on standard retail devices, it is possible to do things like activating developer mode and sideloading apps without jumping through many hoops. From the powerful tools included with the SDK to the wide range of reverse engineering tools available, there are many features to make your life easier.

However, there are a few Android-specific challenges as well. For example, you'll need to deal with both Java bytecode and native code. Java Native Interface (JNI) is sometimes deliberately used to confuse reverse engineers (to be fair, there are legitimate reasons for using JNI, such as improving performance or supporting legacy code). Developers sometimes use the native layer to "hide" data and functionality, and they may structure their apps such that execution frequently jumps between the two layers.

You'll need at least a working knowledge of both the Java-based Android environment and the Linux OS and Kernel, on which Android is based. You'll also need the right toolset to work with both bytecode running on the Java virtual machine and native code.

To reverse engineer Android apps, consider the following techniques: @MASTG-TECH-0016, @MASTG-TECH-0017, @MASTG-TECH-0018.
