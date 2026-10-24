---
masvs_category: MASVS-RESILIENCE
platform: generic
title: Emulation-based Dynamic Analysis
---

Emulation is an imitation of a certain computer platform or program being executed in different platform or within another program. The software or hardware performing this imitation is called an _emulator_. Emulators provide a much cheaper alternative to an actual device, where a user can manipulate it without worrying about damaging the device. There are multiple emulators available for Android, but for iOS there are practically no viable emulators available. iOS only has a simulator, shipped within Xcode.

The difference between a simulator and an emulator often causes confusion and leads to use of the two terms interchangeably, but in reality they are different, specially for the iOS use case. An emulator mimics both the software and hardware environment of a targeted platform. On the other hand, a simulator only mimics the software environment.

QEMU based emulators for Android take into consideration the RAM, CPU, battery performance etc (hardware components) while running an application, but in an iOS simulator this hardware component behaviour is not taken into consideration at all. The iOS simulator even lacks the implementation of the iOS kernel, as a result if an application is using syscalls it cannot be executed in this simulator.

In simple words, an emulator is a much closer imitation of the targeted platform, while a simulator mimics only a part of it.

Running an app in the emulator gives you powerful ways to monitor and manipulate its environment. For some reverse engineering tasks, especially those that require low-level instruction tracing, emulation is the best (or only) choice. Unfortunately, this type of analysis is only viable for Android, because no free or open source emulator exists for iOS (the iOS simulator is not an emulator, and apps compiled for an iOS device don't run on it). The only iOS emulator available is a commercial SaaS solution - @MASTG-TOOL-0108.
