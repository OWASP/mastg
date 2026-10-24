---
title: LSPosed
platform: android
source: https://github.com/JingMatrix/LSPosed
---

LSPosed is a Zygisk module that allows modifying the system or application aspect and behavior at runtime, without modifying any Android application package (APK) or re-flashing. Technically, it is an extended version of Zygote that exports APIs for running Java code when a new process is started. Running Java code in the context of the newly instantiated app makes it possible to resolve, hook, and override Java methods belonging to the app.
