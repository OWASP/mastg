---
masvs_category: MASVS-RESILIENCE
platform: android
title: Detection of Reverse Engineering Tools
---

The presence of tools, frameworks, and apps commonly used by reverse engineers may indicate an attempt to reverse engineer the app. Some of these tools run only on rooted devices, while others force the app into debugging mode or require starting a background service on the mobile phone. Therefore, an app may implement various ways to detect and respond to reverse-engineering attacks, e.g., by terminating itself.

You can detect popular reverse-engineering tools installed in an unmodified form by looking for associated application packages, files, processes, or other tool-specific modifications and artifacts. In the following examples, we'll discuss ways to detect the Frida instrumentation framework, which is used extensively in this guide. Other tools, such as ElleKit, can be detected similarly. Xposed detection is covered in @MASTG-KNOW-0032. Note that DBI/injection/hooking tools can often be detected implicitly through runtime integrity checks, as discussed in @MASTG-KNOW-0032.

For instance, in its default configuration on a rooted device, Frida runs as frida-server. When you explicitly attach to a target app (e.g., via frida-trace or the Frida REPL), Frida injects a frida-agent into the app's memory. Therefore, you may expect to find it there only after attaching to the app (not before). If you check `/proc/<pid>/maps`, you'll find the frida-agent as frida-agent-64.so:

```bash
bullhead:/ # cat /proc/18370/maps | grep -i frida
71b6bd6000-71b7d62000 r-xp  /data/local/tmp/re.frida.server/frida-agent-64.so
71b7d7f000-71b7e06000 r--p  /data/local/tmp/re.frida.server/frida-agent-64.so
71b7e06000-71b7e28000 rw-p  /data/local/tmp/re.frida.server/frida-agent-64.so
```

The other method (which also works on non-rooted devices) involves embedding a [frida-gadget](https://www.frida.re/docs/gadget/ "Frida Gadget") into the APK and _forcing_ the app to load it as a native library. If you inspect the app's memory maps after starting the app (no need to attach explicitly), you'll find the embedded Frida gadget as libfrida-gadget.so.

```bash
bullhead:/ # cat /proc/18370/maps | grep -i frida

71b865a000-71b97f1000 r-xp  /data/app/sg.vp.owasp_mobile.omtg_android-.../lib/arm64/libfrida-gadget.so
71b9802000-71b988a000 r--p  /data/app/sg.vp.owasp_mobile.omtg_android-.../lib/arm64/libfrida-gadget.so
71b988a000-71b98ac000 rw-p  /data/app/sg.vp.owasp_mobile.omtg_android-.../lib/arm64/libfrida-gadget.so
```

Looking at these two _traces_ that Frida _left behind_, you might assume detecting them would be trivial. In fact, bypassing detection will be trivial. But things can get much more complicated. The following table presents a set of typical Frida detection methods and a brief discussion of their effectiveness.

!!! note
    Some of the following detection methods are presented in the article ["The Jiu-Jitsu of Detecting Frida" by Berdhard Mueller](https://web.archive.org/web/20181227120751/http://www.vantagepoint.sg/blog/90-the-jiu-jitsu-of-detecting-frida "The Jiu-Jitsu of Detecting Frida") (archived). Please refer to it for more details, including code snippets.

| Method | Description | Discussion |
| --- | --- | --- |
| **Checking the App Signature** | To embed frida-gadget in the APK, it would need to be repackaged and re-signed. You could check the APK's signature when the app starts (e.g., [GET_SIGNING_CERTIFICATES](https://developer.android.com/reference/android/content/pm/PackageManager#GET_SIGNING_CERTIFICATES "GET_SIGNING_CERTIFICATES") since API level 28) and compare it to the one pinned in your APK. | This is unfortunately too trivial to bypass, e.g., by patching the APK or hooking system calls. |
| **Check The Environment For Related Artifacts** | Artifacts can include package files, binaries, libraries, processes, and temporary files. For Frida, this could be the Frida server running on the target (rooted) system (the daemon that exposes Frida over TCP). Inspect the running services ([`getRunningServices`](https://developer.android.com/reference/android/app/ActivityManager.html#getRunningServices%28int%29 "getRunningServices")) and processes (`ps`) to search for one named "frida-server". You could also walk through the list of loaded libraries and check for suspicious ones (e.g., those including "frida" in their names). | Since Android 7.0 (API level 24), inspecting running services/processes won't show daemons such as the Frida server, as they aren't started by the app itself. Even if it were possible, bypassing this would be as simple as renaming the corresponding Frida artifacts (frida-server/frida-gadget/frida-agent). |
| **Checking For Open TCP Ports** | The frida-server process binds to TCP port 27042 by default. Checking whether this port is open is another way to detect the daemon. | This method detects Frida-server in its default mode, but the listening port can be changed via a command-line argument, making bypassing trivial. |
| **Scanning Process Memory for Known Artifacts** | Scan the memory for artifacts found in Frida's libraries, such as the string "LIBFRIDA," which is present in all versions of frida-gadget and frida-agent. For example, use `Runtime.getRuntime().exec` to iterate through the memory mappings listed in `/proc/self/maps` or `/proc/<pid>/maps` (depending on the Android version), searching for the string. Find the source code on [Berdhard Mueller's GitHub](https://github.com/muellerberndt/frida-detection-demo/blob/master/AntiFrida/app/src/main/cpp/native-lib.cpp "frida-detection-demo"). | This method is somewhat more effective and is difficult to bypass with Frida alone, especially when obfuscation is applied, and multiple artifacts are scanned. However, the chosen artifacts might be patched in the Frida binaries. |

Please remember that this table is far from exhaustive. Additional artifact-based detection includes checking for [named pipes](https://en.wikipedia.org/wiki/Named_pipe "Named Pipes") used by frida-server for external communication, which can be detected by scanning `/proc/self/fd` or similar interfaces. The `procfs` virtual filesystem exposes many other sources of Frida-related indicators of compromise (IoCs) beyond `/proc/self/maps`, for example:

- `/proc/self/task/<tid>/status`: Thread status files list thread names. Frida spawns recognizable threads such as `gdbus` and `gum-js-loop`.
- `/proc/self/net/unix`: Lists Unix-domain sockets in the process network namespace. Frida uses a socket named `frida-zymbiote` for internal communication.
- `/proc/self/smaps`: Provides the same memory-region list as `/proc/self/maps` but with additional memory-usage statistics, and can be used as an alternative or complement to `maps`.

Beyond detecting Frida's presence via artifacts and network signatures, apps can also detect the _modifications_ Frida makes when hooking functions (e.g., GOT/PLT hooks, inline patches, Java method hooks). These techniques are covered in @MASTG-KNOW-0032. Xposed detection is also covered there rather than here: its primary detection vector—looking for the `XposedBridge` class injected into the app's classloader—is a runtime modification rather than a presence artifact, so it cannot be cleanly separated into "present" vs. "modified".

Many more techniques exist, and each depends on whether you're using a rooted device, the specific version of the rooting method, and/or the version of the tool itself. Additionally, the app can make it harder to detect the implemented protection mechanisms by using various obfuscation techniques. In the end, this is part of the cat-and-mouse game of protecting data being processed in an untrusted environment (an app running on a user device).

!!! note
    It is important to note that these controls only increase the complexity of the reverse engineering process. If used, the best approach is to combine the controls effectively rather than use them individually. However, none of them can guarantee 100% effectiveness, as the reverse engineer will always have full access to the device and will therefore always win! You also have to consider that integrating certain controls into your app might increase its complexity and even affect its performance.
