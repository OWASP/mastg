---
title: Xposed
platform: android
source: https://github.com/ElderDrivers/EdXposed
status: deprecated
deprecation_note: Xposed does not work on Android 9 (API level 28). However, it was forked to EdXposed, which has been abandoned, and then to LSPosed. The original LSPosed has been abandoned, but there are multiple active forks of LSPosed available (see MASTG-TOOL-0149).
covered_by: [MASTG-TOOL-0020, MASTG-TOOL-0025, MASTG-TOOL-0029, MASTG-TOOL-0140]

---

Xposed is a framework that allows modifying the system or application aspect and behavior at runtime, without modifying any Android application package (APK) or re-flashing. Technically, it is an extended version of Zygote that exports APIs for running Java code when a new process is started. Running Java code in the context of the newly instantiated app makes it possible to resolve, hook, and override Java methods belonging to the app. Xposed uses [reflection](https://docs.oracle.com/javase/tutorial/reflect/ "Reflection Tutorial") to examine and modify the running app. Changes are applied in memory and persist only during the process's runtime since the application binaries are not modified.

To use Xposed, you need to first install the Xposed framework on a rooted device. Modules can be installed through the Xposed Installer app, and they can be toggled on and off through the GUI.

Note: given that a plain installation of the Xposed framework is easily detected with SafetyNet, we recommend using Magisk to install Xposed. This way, applications with SafetyNet attestation should have a higher chance of being testable with Xposed modules.

Xposed has been compared to Frida. When you run the Frida server on a rooted device, you will end up with a similarly effective setup. Both frameworks deliver a lot of value when you want to do dynamic instrumentation. When Frida crashes the app, you can try something similar with Xposed. Next, similar to the abundance of Frida scripts, you can easily use one of the many modules that come with Xposed, such as the earlier discussed module to bypass SSL pinning ([JustTrustMe](https://github.com/Fuzion24/JustTrustMe "JustTrustMe") and [SSLUnpinning](https://github.com/ac-pm/SSLUnpinning_Xposed "SSL Unpinning")). Xposed includes other modules, such as [Inspeckage](https://github.com/ac-pm/Inspeckage "Inspeckage"), which allow you to do more in-depth application testing as well. On top of that, you can create your own modules as well to patch often-used security mechanisms of Android applications.
