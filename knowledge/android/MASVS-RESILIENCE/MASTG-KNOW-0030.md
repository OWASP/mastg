---
masvs_category: MASVS-RESILIENCE
platform: android
title: Reverse Engineering Tool Detection
---

Reverse engineering and instrumentation tools often leave observable artifacts on the device or inside the app process. These artifacts can include installed packages, binaries, running services, open ports, loaded libraries, memory mappings, thread names, Unix sockets, named pipes, or tool specific strings.

Android apps can inspect some of these indicators to detect whether the app is running in an analysis environment or has been instrumented with common tools. This type of detection is artifact based. It does not prove that the app's code or memory has been modified, but it can provide useful signals that the runtime environment is suspicious.

These checks are usually fragile when used in isolation. Tool names, ports, file paths, strings, and process artifacts can often be changed by an attacker. They are most useful as part of a layered resilience strategy, combined with runtime integrity verification, app integrity checks, anti debugging, and obfuscation.

The examples below focus on detecting @MASTG-TOOL-0031, which is used extensively throughout this guide. Other tools, such as @MASTG-TOOL-0139 or @MASTG-TOOL-0149, can leave similar artifacts and can be detected using the same general approach.

!!! note
    Some tools can be detected both by their artifacts and by the modifications they make to the runtime. This knowledge article focuses on identifying the presence of known tools, while @MASTG-KNOW-0032 focuses on identifying unauthorized runtime modification.

The following sections describe common categories of artifact based detection. The examples use Frida, but the same approach can be applied to other reverse engineering and instrumentation tools that leave observable files, processes, network endpoints, loaded libraries, runtime strings, or IPC artifacts.

Some of the following detection methods are presented in the article ["The Jiu-Jitsu of Detecting Frida" by Bernhard Mueller](https://web.archive.org/web/20181227120751/http://www.vantagepoint.sg/blog/90-the-jiu-jitsu-of-detecting-frida "The Jiu-Jitsu of Detecting Frida") archived. Please refer to it for more details, including code snippets.

## App Signature Checks

Embedding Frida Gadget into an APK usually requires repackaging and re-signing the app (@MASTG-TECH-0039). The app can check its own signing certificate at startup, for example using [`GET_SIGNING_CERTIFICATES`](https://developer.android.com/reference/android/content/pm/PackageManager#GET_SIGNING_CERTIFICATES "GET_SIGNING_CERTIFICATES") since API level 28, and compare it against an expected certificate pinned in the app.

This can detect basic repackaging, but it is usually easy to bypass by patching the APK, modifying the comparison logic, or hooking the relevant platform APIs. Signature checks are more useful when combined with stronger app integrity controls.

## Loaded Library and Memory Mapping Checks

Apps can inspect `/proc/self/maps` to identify loaded libraries, executable mappings, deleted mappings, and file paths associated with injected code. `/proc/self/smaps` provides similar mapping information with additional memory usage statistics and can be used as an alternative or complement to `maps`.

In its default configuration on a rooted device, Frida runs as `frida-server`. When you explicitly attach to a target app, for example via `frida-trace` or the Frida REPL, Frida injects a Frida agent into the app's memory. Therefore, you should expect to find the agent in the target process only after attaching to the app, not before.

If you inspect `/proc/<pid>/maps` after attaching to the app, you may find the Frida agent mapped as `frida-agent-64.so`:

```bash
bullhead:/ # cat /proc/18370/maps | grep -i frida
71b6bd6000-71b7d62000 r-xp  /data/local/tmp/re.frida.server/frida-agent-64.so
71b7d7f000-71b7e06000 r--p  /data/local/tmp/re.frida.server/frida-agent-64.so
71b7e06000-71b7e28000 rw-p  /data/local/tmp/re.frida.server/frida-agent-64.so
```

Another approach, which also works on non-rooted devices, involves embedding [Frida Gadget](https://www.frida.re/docs/gadget/ "Frida Gadget") into the APK and forcing the app to load it as a native library. In this case, the app loads the Gadget when it starts, so no explicit attach step is required. If you inspect the app's memory maps after startup, you may find the embedded Frida Gadget as `libfrida-gadget.so`:

```bash
bullhead:/ # cat /proc/18370/maps | grep -i frida

71b865a000-71b97f1000 r-xp  /data/app/sg.vp.owasp_mobile.omtg_android-.../lib/arm64/libfrida-gadget.so
71b9802000-71b988a000 r--p  /data/app/sg.vp.owasp_mobile.omtg_android-.../lib/arm64/libfrida-gadget.so
71b988a000-71b98ac000 rw-p  /data/app/sg.vp.owasp_mobile.omtg_android-.../lib/arm64/libfrida-gadget.so
```

These checks can detect default or lightly modified Frida deployments, especially when the tool leaves recognizable library names, paths, or executable mappings. However, these indicators are fragile. Attackers can rename binaries and libraries, change paths, patch Frida builds, hide mappings, or hook the detection code itself.

## Process and Service Artifact Checks

Tool artifacts can include package files, binaries, libraries, processes, services, temporary files, and loaded modules. For Frida, this can include a `frida-server` binary on a rooted system or a Frida daemon exposing a TCP endpoint. Apps may inspect running services with [`getRunningServices`](https://developer.android.com/reference/android/app/ActivityManager.html#getRunningServices%28int%29 "getRunningServices"), execute commands such as `ps`, or inspect known filesystem locations for suspicious binaries and files.

On modern Android versions, apps have limited visibility into other apps, services, and processes. Since Android 7.0, API level 24, process and service visibility is restricted, and app level APIs will not reliably expose unrelated daemons such as `frida-server`. Even when artifacts are visible, simple name based checks can often be bypassed by renaming Frida binaries, libraries, or paths.

## Open TCP Port Checks

`frida-server` listens on TCP port `27042` by default. An app can try to connect to this port on localhost or scan a small set of known Frida ports to detect a listening Frida daemon.

This can detect Frida server in its default configuration, but the port can be changed through a command line argument. Attackers may also use forwarding, custom builds, or other transport configurations. Port checks are therefore useful only as a weak signal.

## Process Memory String Scanning

The app can scan memory regions for strings or byte patterns associated with Frida libraries, such as `LIBFRIDA`, which has historically appeared in Frida Gadget and Frida Agent binaries. A native implementation can iterate through mappings listed in `/proc/self/maps`, read mapped regions, and search for known strings. Example source code is available in [Bernhard Mueller's frida-detection-demo](https://github.com/muellerberndt/frida-detection-demo/blob/master/AntiFrida/app/src/main/cpp/native-lib.cpp "frida-detection-demo").

This is generally stronger than checking names, ports, or package files, especially when implemented natively, obfuscated, and combined with several indicators. However, the selected strings can be patched out of Frida binaries, and the detection logic can itself be patched or hooked.

## Thread Name Checks

Instrumentation frameworks may create recognizable threads inside the process. Frida has historically used thread names such as `gum-js-loop` and `gdbus`. The app can inspect `/proc/self/task/<tid>/status` for each thread and search for suspicious thread names.

This can detect default or lightly modified Frida deployments. It is fragile because thread names can be changed, hidden, or created only after specific instrumentation activity. Thread name checks should not be treated as conclusive by themselves.

## Unix Socket and Named Pipe Checks

Frida and similar tools may use Unix domain sockets or named pipes for communication between components. The app can inspect interfaces such as `/proc/self/net/unix` or `/proc/self/fd` to look for suspicious socket names, pipe targets, or file descriptors.

For example, Frida's Android implementation [uses abstract Unix socket names](https://github.com/frida/frida-core/blob/17.9.7/src/linux/linux-host-session.vala#L1531) with the `frida-zymbiote-` prefix followed by a random identifier. This kind of indicator is highly version and configuration dependent and should not be treated as a stable Frida signature.

These checks can reveal artifacts that are less obvious than process names or ports, but they remain implementation specific. A custom tool build, changed transport mechanism, or future Frida version can remove or rename these indicators.

## Effectiveness and Limitations

Artifact based detection is useful for identifying common reverse engineering and instrumentation setups, especially default or lightly modified tool deployments. It can raise the cost of analysis and provide signals that the app is running in an unusual environment.

However, these techniques should not be treated as strong proof of compromise. Many indicators are easy to rename, remove, delay, or spoof. Some checks may also produce false positives when a device contains security tools, developer tooling, accessibility tooling, enterprise monitoring software, or other legitimate software that exposes similar artifacts.

For this reason, apps should avoid relying on a single artifact or terminating immediately based on one weak signal. A more robust strategy combines several independent indicators, weighs them according to risk, and pairs them with runtime integrity verification as described in @MASTG-KNOW-0032.

In the end, detecting reverse engineering tools is part of the broader cat-and-mouse problem of protecting code and data processed on a user controlled device. These techniques can increase attacker effort, but they cannot guarantee prevention against a determined attacker with sufficient time, tooling, and control over the runtime environment.
