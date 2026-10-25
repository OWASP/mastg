---
title: Mobile Application Resilience for Beginners
id: MASTG-WORKSHOP-0001
---

## About

This hands-on workshop introduces the user to the mobile application resilience topic, one of the main topics inside mobile application security.

The practical exercises focus on root detection, emulator detection, and runtime hooking detection.

!!! note
    The examples in this workshop use Android, but the underlying resilience concepts apply to both Android and iOS.
    The MASTG repository also includes analogous iOS tests. For example, @MASTG-TEST-0325 covers runtime root detection on Android, while @MASTG-TEST-0241 covers runtime jailbreak detection on iOS.

## Objective

By the end of this workshop, you will be able to:

- Explain how resilience mechanisms increase the effort required to reverse engineer or tamper with an app.
- Identify the main [OWASP MASVS resilience control categories](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/): platform integrity, anti-tampering, anti-static analysis, and anti-dynamic analysis.
- Explain how a layered approach combines complementary mechanisms instead of relying on a single detection check.
- Distinguish between detecting a specific tool, such as Frida, and detecting its underlying consequences on the app (modifying the app's memory integrity).
- Analyze and bypass detection checks in the demos to understand their limitations and relevance to an app's threat model.
- Explain the cat-and-mouse relationship between detection mechanisms and bypass techniques, and why both evolve over time.

## Setup Guide

!!! note
    The setup guide steps were documented and reproduced in September 2026.
    The setup process may differ with newer tool versions.

This guide explains how to prepare your computer and Android emulator for the workshop.

This workshop was tested on these platforms:

- Linux on x86_64
- macOS on Apple silicon

### Reference Versions

If you encounter issues while following this workshop, compare your tool versions with the reference versions below.

| Component | Version |
| --- | --- |
| Android Studio | `AI-261.26222.65.2614.16204760` |
| Android Emulator | `37.1.11` |
| Android SDK Platform Tools | `37.0.1` |
| Android system image | Android 16 (API level 36), Google Play, `arm64-v8a` or `x86_64` |
| Android system image revision (`arm64-v8a`) | `7` |
| Magisk | `30.6` |
| MagiskFrida | `17.18.0-1` |
| Frida client and server | `17.18.0` |
| `frida-tools` | `14.10.4` |
| Python | `3.9.6` |

### Requirements

- At least **8 GB** of **RAM**
- **32 GB** of free **disk** space
- Intel VT-x or AMD-V virtualization and access to KVM on Linux

### Install Android Studio

1. Open the [official Android Studio](https://developer.android.com/studio)
   download page.
2. Download the installer for your computer and install Android Studio.

### Configure a Rooted Android Virtual Device

#### Create a Virtual Device

1. Return to the Android Studio welcome screen.
2. Select **More Actions** -> **Virtual Device Manager**.
3. Select **Create Device**.
4. Select **Phone**.
5. Select **Pixel 8**.
6. Select **Next**.
7. Find **Android 16 (API level 36)**.
8. Select a Google Play system image for your host architecture:

    1. On Linux x86_64, select **x86_64**.
    2. On macOS with Apple silicon, select **arm64-v8a**.

9. Download the image and accept its license.
10. Select **Next**.
11. Name the device.
12. In the advanced settings, select **Cold boot** if this option is available.
13. Select **Finish**.

#### Prepare the Android SDK Paths

Run only the commands for your platform.

macOS:

```shell
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
```

Linux:

```shell
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
```

Verify that Android Debug Bridge (ADB) detects the device:

```shell
adb devices
```

!!! important
    Connect only the workshop emulator.
    The rootAVD script doesn't support multiple ADB devices at the same time.

#### Clone RootAVD and Select the Image to Modify

1. Keep the emulator running and unlocked.
2. Clone the [rootAVD GitLab repository](https://gitlab.com/newbit/rootAVD).
3. In the terminal, go to the cloned rootAVD directory.
4. List all AVD images:

    ```shell
    bash ./rootAVD.sh ListAllAVDs
    ```

5. Find the entry for Android 16, Google Play, and your host architecture.

#### Start the Installation

Run only the command for your platform.

Linux:

```shell
bash ./rootAVD.sh system-images/android-36/google_apis_playstore/x86_64/ramdisk.img FAKEBOOTIMG
```

macOS:

```shell
bash ./rootAVD.sh system-images/android-36/google_apis_playstore/arm64-v8a/ramdisk.img FAKEBOOTIMG
```

1. When the version menu appears, select Magisk 30.6.

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/rootavd-magisk-version-selection.png" alt="Magisk 30.6 selected in rootAVD" width="50%" />

2. Wait for rootAVD to open Magisk in the emulator.

#### Patch the Image in Magisk

1. In the Magisk section, select **Install**.
2. Select **Select and Patch a File**.
3. Open **Downloads**.
4. Select `fakeboot.img`.
5. Select **Let's Go**.
6. Wait for the patch operation to finish.
7. Return to the terminal.
8. When rootAVD prompts you, press Enter.
9. Wait for rootAVD to copy the modified image.

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/rootavd-installation-success.png" alt="Successful rootAVD image installation" width="50%" />

!!! warning
    Keep the terminal and emulator open while rootAVD modifies the image.
    Closing either application can interrupt the modification.

#### Restart and Check Magisk

1. After rootAVD finishes, stop the emulator if it is still running.
2. In **Device Manager**, open the device menu.
3. Select **Cold Boot** or **Cold Boot Now**.
4. Wait for Android to start.
5. Open Magisk.
6. If Magisk requests additional setup, accept it and permit the restart.
7. Confirm that Magisk displays **Yes** for **Zygisk** and **Ramdisk**.

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magisk-root-status.png" alt="Magisk root status" width="300px" />

### Install Frida

#### Install MagiskFrida on the Android Virtual Device

MagiskFrida is a Magisk module that starts `frida-server` when Android boots.
The workshop pins MagiskFrida to release `17.18.0-1`.

1. Download the
   [MagiskFrida ZIP release](https://github.com/ViRb3/magisk-frida/releases/tag/17.18.0-1).
2. Don't extract the ZIP file.
3. Drag the ZIP file from your computer into the emulator window.
4. Wait for the transfer to finish.
5. Open Magisk, and then select **Modules** -> **Install from storage**.
6. Open **Download** and select the MagiskFrida ZIP file.

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magiskfrida-zip-in-downloads.png" alt="MagiskFrida ZIP file in the device Downloads directory" width="300px" />

7. Wait for the installation to finish.
8. Select **Reboot**.
9. After the restart, open Magisk, and then select **Modules**.
10. Confirm that MagiskFrida is installed, enabled, and active.
11. If **active** doesn't appear, select **Action** or reboot the device.

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magiskfrida-active-module.png" alt="Active MagiskFrida module" width="300px" />

#### Install Frida Client Tools on the Host Computer

The MagiskFrida module installs the server on Android. You must also install the
Frida client tools on your computer.

1. Download and install Python 3.9.6 for your computer.
2. Create and activate a Python virtual environment:

    ```shell
    python3 -m venv "$HOME/mobile-workshop-venv"
    source "$HOME/mobile-workshop-venv/bin/activate"
    ```

3. Install the client tools and the Frida version that matches MagiskFrida:

    ```shell
    python -m pip install frida-tools "frida==17.18.0"
    ```

MagiskFrida release `17.18.0-1` uses Frida `17.18.0`. The `-1` suffix
identifies the module release. The `frida-tools` and `frida` packages use
different version numbers.

#### Check the Frida Connection

With the emulator running and the Python environment active, run:

```shell
frida -U -f com.android.settings
```

Android Settings should start on the device. The Frida command-line interface
(CLI) should appear in the terminal.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/frida-connection-test.png" alt="Successful Frida connection test" width="50%" />

## Content

### What is Mobile App Resilience?

Mobile app resilience aims to make reverse engineering and tampering with mobile applications harder. It increases the time, expertise, and patience needed for activities such as:

- Understanding the app's implementation and proprietary protocols.
- Modifying the app's behavior to bypass security controls.
- Analyzing how the app communicates with its backend.
- Executing the app in an **untrusted** environment (e.g., rooted or emulated devices).

Resilience mechanisms raise the effort required for these activities, but do not make them impossible.

### Why is Mobile App Resilience needed

To understand why these resilience barriers matter, let's use a social network application that pays creators for content views as an example.

If the app does not properly detect emulators or virtual devices, someone could run hundreds of automated mobile device instances to generate fake views and interactions with the objective of earning money without reaching a real audience.

The result is that the platform would end up paying for fake activity, and, at the same time, users expecting to interact with real people would encounter bots instead, potentially damaging the platform's reputation and driving users away.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/device-farm.png" alt="Multiple mobile device sessions controlled through a single interface" width="30%" />

Another example is a competitive online game. Someone could modify the app statically (patching) or modify its runtime behavior (e.g., by hooking or debugging the application) with the purpose of manipulating scores to gain an unfair advantage, for example, or creating a cheat that provides information about the other users in the game (like a wallhack).

Legitimate players would find themselves competing against cheaters rather than other players on equal terms. This could ruin matches, distort leaderboards, and drive players away, damaging both the game's reputation and its revenue.

### OWASP MAS Project and the Resilience topic

Before moving on to the practical exercises, let's look at how resilience fits into the [OWASP Mobile Application Security (MAS) project](https://mas.owasp.org/). The project provides a security standard and supporting resources that serve as a reference for developers building mobile apps and security testers assessing them.

The following overview introduces MASVS, MASWE, and MASTG and explains how security controls, weaknesses, and tests connect, using resilience as an example.

#### MASVS: Security Controls

The [OWASP Mobile Application Security Verification Standard (MASVS)](https://mas.owasp.org/MASVS/) defines security controls for mobile applications. These controls describe the protections an app is expected to provide.

The controls are organized into groups, each covering a different area of mobile application security. For example, `MASVS-STORAGE` covers the protection of data stored on the device, while `MASVS-NETWORK` covers communication between the app and remote endpoints.

There is also a group specifically for resilience: [MASVS-RESILIENCE](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/). It contains the following controls:

1. [MASVS-RESILIENCE-1](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-1/){ .md-tag .md-tag--masvs-resilience }: The app validates the integrity of the platform.

    This control concerns the environment where the app runs. It addresses questions such as: "Is this a rooted device?" Root or jailbreak detection are examples of mechanisms related to this control.

2. [MASVS-RESILIENCE-2](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-2/){ .md-tag .md-tag--masvs-resilience }: The app implements anti-tampering mechanisms.

    This control concerns changes to the app itself, such as modifications to its code or resources. It addresses questions such as: "Has this app been patched or modified?"

3. [MASVS-RESILIENCE-3](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-3/){ .md-tag .md-tag--masvs-resilience }: The app implements anti-static analysis mechanisms.

    This control concerns attempts to understand the app through static analysis. It addresses questions such as: "Can an attacker understand my code without running it?" Code obfuscation is an example of a mechanism that can make this harder.

4. [MASVS-RESILIENCE-4](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-4/){ .md-tag .md-tag--masvs-resilience }: The app implements anti-dynamic analysis techniques.

    This control concerns analysis and manipulation at runtime. It addresses questions such as: "Can someone inspect or manipulate my app while it runs?" Anti-debugging and hooking detection are examples of mechanisms related to this control.

!!! note
    Not every app has the same security requirements (for example, compare a banking application with a calendar application). To address these differences, the OWASP MAS project provides [MAS Testing Profiles](https://mas.owasp.org/Profiles/), which define sets of controls and tests for different protection needs:

    - `MAS-L1`: Essential security controls for mobile apps.
    - `MAS-L2`: Advanced security controls for scenarios where the operating system cannot be trusted or attackers may have physical access to the device.
    - `MAS-R`: Resilience against reverse engineering and tampering, including threats from users who control the device.
    - `MAS-P`: Baseline privacy protections for users' personal data.

    The appropriate profiles, or combination of profiles, depend on the type of app, its functionality, the sensitivity of its data, and its threat model. Check the [MAS Testing Profiles](https://mas.owasp.org/Profiles/) for more information on the topic.

#### MASWE: From Controls to Weaknesses

When a security control required for an app is missing or is not properly enforced, this can introduce a security weakness.

The [OWASP Mobile Application Security Weakness Enumeration (MASWE)](https://mas.owasp.org/MASWE/) provides a taxonomy of mobile security and privacy weaknesses. It connects these weaknesses to the relevant MASVS controls and explains their causes, potential impact, and mitigations.

For example, [MASVS-RESILIENCE-1](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-1/){ .md-tag .md-tag--masvs-resilience } is dedicated to validating the platform's integrity. One of the resulting weaknesses connected to it is @MASWE-0051, which happens when root or jailbreak detection is not implemented effectively.

#### MASTG: From Weaknesses to Tests, Demos, Knowledge Pages and Best Practices

MASTG provides practical testing procedures (**MASTG-TEST**) to verify if the application has such weaknesses, supported by knowledge pages, techniques, and demos.

Continuing with the root detection weakness (@MASWE-0051), MASTG provides different tests to check whether an app implements root detection or not (and therefore has such a weakness):

- @MASTG-TEST-0324 looks for references to root detection mechanisms through static analysis.
- @MASTG-TEST-0325 checks for the use of root detection mechanisms at runtime via hooking.

The MASTG repository contains sample applications (**MASTG-DEMO**) that focus on a certain test and let you follow the test's steps and observe the results. This makes it easier to understand the test and why it is being tested, without the complexity of a production app. @MASTG-DEMO-0087 and @MASTG-DEMO-0088 are the associated DEMOs of the tests above.

The MASTG repository also contains knowledge pages (**MASTG-KNOW**) that explain the concepts and mechanisms behind a topic, providing background for understanding and testing it. For example, @MASTG-KNOW-0027 covers root detection on Android.

Also, the repository includes best-practice pages (**MASTG-BEST**) that provide implementation guidance and explain relevant trade-offs and limitations to avoid having such weaknesses. For example, @MASTG-BEST-0030 covers implementing root detection.

### Root Detection

#### Why Detect a Rooted Device?

A rooted device is not necessarily being used maliciously. However, root access on a device enables certain tooling that requires elevated privileges to work. Examples include eBPF-based tools that need to load eBPF programs into the kernel, such as [stackplz](https://github.com/SeeFlowerX/stackplz), or a tool like @MASTG-TOOL-0075, which captures packets directly from the device's network interfaces.

For some types of apps, detecting signs of root is therefore enough reason to flag the environment as risky, even without detecting a specific tool or technique being performed on the application.

#### Root Detection in the MAS Project

As introduced earlier, the OWASP MAS project connects security controls, weaknesses, and practical testing resources. For root detection, that connection looks like this:

- MASVS defines the relevant security controls. [MASVS-RESILIENCE-1](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-1/){ .md-tag .md-tag--masvs-resilience } covers platform integrity, while [MASVS-RESILIENCE-4](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-4/){ .md-tag .md-tag--masvs-resilience } covers anti-dynamic analysis.
- MASWE describes the related weakness. @MASWE-0051 covers missing or ineffective root or jailbreak detection.
- MASTG knowledge pages explain the topic. @MASTG-KNOW-0027 describes root detection on Android and common detection mechanisms.
- MASTG tests provide ways to investigate the implementation. @MASTG-TEST-0324 looks for references to root detection mechanisms through static analysis, while @MASTG-TEST-0325 checks their use at runtime.
- MASTG demos provide sample implementations and analysis examples for these tests. @MASTG-DEMO-0087 demonstrates the static approach, and @MASTG-DEMO-0088 demonstrates the runtime approach.
- MASTG best-practice pages provide implementation guidance. @MASTG-BEST-0030 covers implementing root detection and understanding its limitations.

#### Inspecting the Demo

Let's inspect an app's code to see how it looks for signs of root. This will help us understand how apps can identify rooted environments.

For this exercise, we will analyze and install the sample app from @MASTG-DEMO-0087 or @MASTG-DEMO-0088. Both demos share the same code, which implements several root-detection checks and displays their results.

1. Open either demo's MASTG page and download its APK using the APK download button.
2. Install the APK on the Android device prepared in the Setup Guide, following @MASTG-TECH-0005.
3. Open the installed app and tap **Start** to run the root-detection checks.

The app displays the result of each check, as shown in the example below.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/root-detection-results.png" alt="Demo app showing individual root detection results and reporting that the device appears to be rooted" width="400px" />

!!! note
    Before reading the explanations below, open either demo and inspect its sample code. Try to identify the root detection mechanisms, what each one checks, and what makes it return a positive result. Remember to inspect the `AndroidManifest.xml` as well.

Once you've inspected the code, let's go through each check (the DEMO page contains the source code) and see what it actually detects.

#### Detection Mechanisms in the Demo

The results below correspond to the example shown in the screenshot. Compare them with the results on your own device. If the setup was followed accordingly, the same results should be achieved.

1. Checking common paths for root-related files

    **Goal:** Root solutions commonly provide an entry point through which applications or users can request elevated privileges. The most common entry point is the `su` command, which is often linked to a binary that provides the requesting process root privileges. If this command is found on the device, it can therefore give an app a useful indication that the device has been rooted.

    The function `checkForSuBinary()` follows this idea by looking for root-related files at predefined locations. Its list includes paths such as `/system/bin/su`, `/system/xbin/su`, and `/data/local/su`.

    ```kotlin
    private fun checkForSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        Log.d(tag, "checkForSuBinary: testing ${paths.size} paths")

        var found = false
        for (path in paths) {
            try {
                val exists = File(path).exists()
                Log.d(tag, "su path check: path=$path, exists=$exists")
                if (exists) found = true
            } catch (se: SecurityException) {
                Log.w(tag, "su path check blocked: path=$path, msg=${se.message}")
            } catch (t: Throwable) {
                Log.w(
                    tag,
                    "su path check error: path=$path, err=${t::class.java.simpleName}, msg=${t.message}"
                )
            }
        }

        Log.i(tag, "checkForSuBinary result: found=$found")
        return found
    }
    ```

    **Detected:** No. In this setup, Magisk places `su` at a different path (`/system_ext/bin/su`), which is outside the function's list. The device is rooted, but the check is looking in different places. You can explore the [Magisk source code](https://github.com/topjohnwu/Magisk) to understand how it makes `su` available and determines its location.

    **Weakness:** This approach depends on knowing where a root solution places its files. Different implementations, installation methods, or Android environments can expose those files at different locations. Adding `/system_ext/bin/su` would help detect this particular setup, but another location could leave the check with the same problem again.

2. Looking for `su` in `PATH`

    **Goal:** Rather than checking individual locations, another approach is to ask the system where it would find the `su` executable.

    The `PATH` environment variable contains the directories a process searches when locating commands. Root solutions commonly make `su` accessible through one of those directories so applications and users can invoke it by name instead of by path.

    The function `checkForWhichSu()` executes `which su` and checks whether the command successfully returns a location.

    ```kotlin
    private fun checkForWhichSu(): Boolean {
        return try {
            Log.d(tag, "checkForWhichSu: executing which su")
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))

            val stdout = process.inputStream.bufferedReader().use { it.readText().trim() }
            val stderr = process.errorStream.bufferedReader().use { it.readText().trim() }
            val exit = try { process.waitFor() } catch (_: Throwable) { -1 }

            val found = stdout.isNotEmpty() && exit == 0
            if (found) {
                Log.i(tag, "su found via which: path=$stdout")
            } else {
                Log.d(
                    tag,
                    "which su not found: exit=$exit, stderr=$stderr"
                )
            }
            found
        } catch (se: SecurityException) {
            Log.w(
                tag,
                "checkForWhichSu blocked: msg=${se.message}"
            )
            false
        } catch (t: Throwable) {
            Log.w(
                tag,
                "checkForWhichSu error: err=${t::class.java.simpleName}, msg=${t.message}"
            )
            false
        }
    }
    ```

    **Detected:** Yes. This gives us an interesting contrast with the previous check: the app can find `su` through `PATH`, even though none of its hardcoded locations produced a match. In the next exercise, we will investigate where the executable is being found and why it is visible to the app.

    **Weakness:** The result depends on the environment visible to the app's process. If the executable is hidden from that process or becomes unavailable through its search path, the result can change while root remains available elsewhere on the device. **We will explore this distinction when examining the bypass.**

3. Looking for known root-management packages

    **Goal:** Root solutions often include a management application. For example, the Magisk app lets users manage their installation, modules, and root permissions. Its presence gives other applications another artifact to look for.

    The function `checkForRootPackages()` queries a list of known package names, including `com.topjohnwu.magisk` (the Magisk management application) and `eu.chainfire.supersu` (the management application of [SuperSU](https://github.com/khadas/supersu), another root solution). The manifest's `<queries>` declarations allow the app to query these packages under Android's [package visibility rules](https://developer.android.com/training/package-visibility/declaring).

    ```kotlin
    private fun checkForRootPackages(): Boolean {
        val packages = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
        )

        Log.d(tag, "checkForRootPackages: testing ${packages.size} package names")

        var foundAny = false
        for (packageName in packages) {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                Log.i(tag, "root package detected: package=$packageName")
                foundAny = true
            } catch (_: PackageManager.NameNotFoundException) {
                Log.d(tag, "root package not present: package=$packageName")
            } catch (se: SecurityException) {
                Log.w(
                    tag,
                    "package check blocked: package=$packageName, msg=${se.message}"
                )
            } catch (t: Throwable) {
                Log.w(
                    tag,
                    "package check error: package=$packageName, err=${t::class.java.simpleName}, msg=${t.message}"
                )
            }
        }

        Log.i(tag, "checkForRootPackages result: found=$foundAny")
        return foundAny
    }
    ```

    **Detected:** Yes. At least one package in the list is visible to the demo app. In the next exercise, we will inspect which package is being identified and how the app obtains that information.

    **Weakness:** The check identifies the management app through its package name. That connection can break if the manager uses a different identifier. Magisk itself supports [repackaging its management app under a generated package name](https://github.com/topjohnwu/Magisk/blob/v30.6/app/core/src/main/java/com/topjohnwu/magisk/core/tasks/AppMigration.kt), so detecting the manager and detecting the underlying root installation are different problems.

4. Checking system properties

    **Goal:** Some root solutions modify system properties to enable settings that make debugging or privileged access easier. An app can inspect these system properties as additional indicators of an unusual execution environment.

    The function `checkForDangerousProps()` looks for two particular system property values: `ro.debuggable=1` and `ro.secure=0`. These properties are involved in debugging and [ADB privilege handling](https://android.googlesource.com/platform/packages/modules/adb/+/refs/heads/main/daemon/main.cpp).

    ```kotlin
    private fun checkForDangerousProps(): Boolean {
        val dangerousProps = mapOf(
            "ro.debuggable" to "1",
            "ro.secure" to "0"
        )

        Log.d(
            tag,
            "checkForDangerousProps: testing ${dangerousProps.size} properties"
        )

        var matchedAny = false
        for ((prop, expected) in dangerousProps) {
            val actual = getSystemProperty(prop)
            val matched = actual == expected
            Log.i(
                tag,
                "system property check: prop=$prop, actual=$actual, expected=$expected, matched=$matched"
            )
            if (matched) matchedAny = true
        }

        Log.i(tag, "checkForDangerousProps result: matched=$matchedAny")
        return matchedAny
    }

    private fun getSystemProperty(key: String): String? {
        return try {
            Log.d(tag, "getSystemProperty exec: key=$key")
            val process = Runtime.getRuntime().exec(arrayOf("getprop", key))

            val stdout = process.inputStream.bufferedReader().use { it.readText().trim() }
            val stderr = process.errorStream.bufferedReader().use { it.readText().trim() }
            val exit = try { process.waitFor() } catch (_: Throwable) { -1 }

            if (stderr.isNotEmpty()) {
                Log.w(
                    tag,
                    "getprop stderr: key=$key, exit=$exit, stderr=$stderr"
                )
            } else {
                Log.d(
                    tag,
                    "getprop ok: key=$key, exit=$exit"
                )
            }

            stdout.ifEmpty { null }
        } catch (se: SecurityException) {
            Log.w(
                tag,
                "getSystemProperty blocked: key=$key, msg=${se.message}"
            )
            null
        } catch (t: Throwable) {
            Log.w(
                tag,
                "getSystemProperty error: key=$key, err=${t::class.java.simpleName}, msg=${t.message}"
            )
            null
        }
    }
    ```

    **Detected:** No. The function found neither of its expected values in this run. This is because Magisk does not change these values in its default installation. Also, this check can result in **false positives**, as these modifications can be present without a rooting tool such as Magisk. This is why it is important not to rely on a single detection check. A check can be bypassed, miss a rooting solution, or incorrectly flag a device as rooted.

    **Weakness:** This check assumes that particular property values reveal the environment's security state. However, software with sufficient privileges can change the values exposed to applications. Magisk's [`resetprop`](https://topjohnwu.github.io/Magisk/details.html#resetprop), for example, can modify these properties. The app may therefore receive ordinary-looking values from a modified environment.

5. Checking for a `test-keys` build tag

    **Goal:** Some development and custom Android images include root capabilities. This makes signs of a custom build interesting to an app that wants to detect a modified environment.

    Android includes publicly available [signing keys for development](https://source.android.com/docs/core/ota/sign_builds#release-keys). A build tagged with `test-keys` can therefore indicate a development or custom Android image, and might contain root capabilities.

    The function `checkForTestKeys()` looks for this tag in `Build.TAGS`. It therefore checks for signs of a development or custom build, rather than directly checking whether root access is available.

    ```kotlin
    private fun checkForTestKeys(): Boolean {
        val buildTags = Build.TAGS
        val isTestKeys = buildTags != null && buildTags.contains("test-keys")
        Log.i(
            tag,
            "checkForTestKeys: buildTags=$buildTags, isTestKeys=$isTestKeys"
        )
        return isTestKeys
    }
    ```

    **Detected:** No. The check found no matching tag. The workshop starts from a Google Play system image, which [Android documents as release-key signed](https://developer.android.com/studio/run/managing-avds#system-image). Adding root through Magisk does not automatically turn that environment into a test-key-signed build.

    **Weakness:** A custom ROM can use test keys without providing root access. If the app interprets that build tag as "root detected", it incorrectly flags the device as rooted: a false positive. Conversely, a rooted device can still use release-key build tags and produce a negative result, as illustrated here. This is why the result should be considered alongside other detection signals rather than used as a verdict on its own. Scattering the checks across different parts of the app also makes it harder to disable all detection by patching one place, instead of leaving a single, easily located check.

#### Bypassing the Detections

An attacker with access to the APK can inspect the app's compiled code, including its detection checks. Their first question is: **Which checks is this app performing?** With enough time, patience, and skill, a determined attacker can work out how these local checks work and then find a way to bypass them.

Obfuscation, described in @MASTG-KNOW-0033, is another resilience mechanism that makes this job harder. It makes the code harder to understand and slows down the analysis. For example, meaningful method names can become short, meaningless identifiers. Finding the root checks then would take more work than searching for a name such as `checkForSuBinary`.

For this exercise, we will assume that the attacker (us) has already found the checks, and now we want to **prevent the app from detecting root while keeping root access available on the device**.

Whenever possible, prefer **app-agnostic bypasses**: methods that don't depend on how a particular app implements its checks. Here, we will focus on changing which root artifacts the app can see, without modifying the app itself.

This approach has practical advantages:

- You don't need to find individual detection functions in each app. Hiding an artifact can work across apps looking for that artifact, regardless of their method names or code structure. Understanding the checks helps explain the results, but **the bypass doesn't depend on finding their exact location**.
- You don't need to patch the APK or change the app's behavior through hooking or debugging. These approaches can introduce extra signs of tampering or instrumentation. Patching the APK can trigger integrity checks. Hooking functions or attaching a debugger can leave traces that other checks detect. **You might bypass root detection but trigger an anti-hooking or anti-debugging check in the process.**

Open the demo app and tap **Start** again. In this setup, two checks detect signs of root: the `which su` check and the check for root-management apps.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/root-detection-results.png" alt="Demo app showing two positive root detection results: su found through the which command and root-management apps found" width="400px" />

We will analyze and bypass these checks one at a time.

#### Bypassing the `which su` Check

Open a terminal on your computer and enter the device's shell with `adb shell`. Then run `which su`:

```console
$ adb shell
device:/ $ which su
/system_ext/bin/su
```

The command finds `su` at `/system_ext/bin/su`. This is the same command that `checkForWhichSu` runs from the app's process environment. Before the bypass, both the app and the ADB shell can find `su`.

Now inspect that path with `ls -la`:

```console
device:/ $ ls -la /system_ext/bin/su
lrwxrwxrwx 1 root root 8 2026-10-24 11:30 /system_ext/bin/su -> ./magisk
```

The arrow shows that `su` is a symbolic link to the `magisk` binary in the same directory. Magisk provides several commands through this same executable, including its [`su` applet](https://github.com/topjohnwu/Magisk/blob/v30.6/native/src/core/applets.cpp). Calling the executable through the name `su` selects that applet and provides "root" access to the asking process.

Magisk exposes its binary and applets through mounted filesystem views, including `tmpfs` overlays, without adding those files directly to the underlying system partition. These mounts make the files visible to processes, allowing commands such as `which su` to find them. You can follow this in Magisk's [binary injection code](https://github.com/topjohnwu/Magisk/blob/v30.6/native/src/core/module.rs).

Android creates app processes through [Zygote](https://source.android.com/docs/core/runtime/zygote), a process that forks itself to create child processes. A child (any spawned application) initially inherits its parent's filesystem view, which can include Magisk's mounts. Android then prepares the child to run an app, including [setting up its mount namespace](https://source.android.com/docs/core/storage). This namespace determines which mounted filesystems the app can see.

Leave the demo app running and inspect its mounts from the same ADB shell:

```console
device:/ $ su -c 'cat /proc/$(pidof org.owasp.mastestapp)/mounts' | grep magisk
magisk /system_ext/bin tmpfs ro,seclabel,relatime,mode=755 0 0
magisk /system_ext/bin/magisk tmpfs ro,seclabel,relatime,mode=755 0 0
magisk /system_ext/bin/magiskpolicy tmpfs ro,seclabel,relatime,mode=755 0 0
magisk /debug_ramdisk tmpfs rw,seclabel,relatime,mode=755 0 0
devpts /debug_ramdisk/.magisk/pts devpts rw,seclabel,relatime,mode=600,ptmxmode=000 0 0
magisk /system/lib64 tmpfs ro,seclabel,relatime,mode=755 0 0
magisk /system/lib64/libzygisk.so tmpfs ro,seclabel,relatime,mode=755 0 0
```

!!! note
    If Magisk requests root permission for the shell process, grant it.

!!! note "About the executed command"
    The `pidof` command finds the app's process ID. Reading [`/proc/<pid>/mounts`](https://man7.org/linux/man-pages/man5/proc_pid_mounts.5.html) shows the mounts visible to that process, and `grep magisk` selects the entries containing `magisk`. These entries explain how Magisk's files become visible inside the app's environment.

Now, Magisk has a setting called **Enforce DenyList** that **[removes Magisk's mounts from the mount namespace of selected app processes](https://github.com/topjohnwu/Magisk/blob/v30.6/native/src/core/mount.rs)**. We will enable this setting in the Magisk manager and add the demo app's processes to the list, and see what happens to the app.

Let's enforce the DenyList for the demo app:

1. Open Magisk and go to **Settings** using the gear icon.
2. Enable **Enforce DenyList**.
3. Open **Configure DenyList**.
4. Find **MASTestApp** (`org.owasp.mastestapp`) and select its processes. Expand the entry to check that its main process is selected.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/magisk-denylist-settings.png" alt="Magisk settings with Enforce DenyList enabled and MASTestApp selected in Configure DenyList" width="90%" />

Now force-stop the demo app from the ADB shell:

```console
device:/ $ am force-stop org.owasp.mastestapp
```

Reopen the app from the launcher and tap **Start**. Force-stopping ensures that Android starts a fresh process, so the DenyList rules apply during startup. Simply returning to the home screen can leave the existing process running.

The `which su` result should now change to **su not found via which command**, as shown below:

<img src="Images/Workshops/MASTG-WORKSHOP-0001/root-detection-after-denylist.png" alt="Demo app no longer finding su through which, while still detecting root-management apps" width="400px" />

With the app running, let's inspect the mounts again and confirm that root is still available in the ADB shell:

```console
device:/ $ su -c 'cat /proc/$(pidof org.owasp.mastestapp)/mounts' | grep magisk
device:/ $ su -c 'id -u'
0
```

The filtered mount listing now has no output: no entries contain `magisk` in the app's mount view.

The `0` from `id -u` confirms that the shell command still runs as root. The DenyList change affects the selected app processes, not the entire device. The ADB shell keeps its own filesystem view and can still access `su`. We can therefore continue running tools with root privileges while hiding these files from the demo app.

The root-management package check still finds the Magisk app, so the overall result remains `true`. This is the next check to bypass.

#### Bypassing the Root Management Package Presence Check

Root solutions often include a management app for changing root settings and permissions; we just used Magisk's management app to configure the DenyList. The manager is an installed Android package, which gives other apps another root artifact to look for.

The function `checkForRootPackages` of the app checks known package names with `PackageManager.getPackageInfo`. Its list includes `com.topjohnwu.magisk`, the standard package name of the Magisk manager. As explained in the [Android package visibility documentation](https://developer.android.com/training/package-visibility), this API returns information about packages visible to the calling app.

In the same ADB shell, list the installed packages and look for Magisk's package name:

```console
device:/ $ pm list packages | grep -F com.topjohnwu.magisk
package:com.topjohnwu.magisk
```

!!! note "About the executed command"
    The [`pm list packages` command](https://developer.android.com/tools/adb#pm) lists installed package names. Here, `grep -F` filters that output for the literal text `com.topjohnwu.magisk`. The result confirms that the package is installed, matching the artifact that the demo app detects.

Magisk has a [setting](https://github.com/topjohnwu/Magisk/blob/v30.6/app/core/src/main/java/com/topjohnwu/magisk/core/tasks/AppMigration.kt) called **Hide the Magisk app** that **installs a replacement manager under a randomly generated package name**. The replacement provides the same management features as the original manager app. After the replacement starts, Magisk removes the original `com.topjohnwu.magisk` package.

Let's use this option:

1. Open Magisk and go to **Settings** using the gear icon.
2. Select **Hide the Magisk app**.
3. Enter a recognizable app name, such as "Workshop Manager", and confirm the dialog. This is the launcher label, not the package ID, which Magisk generates separately.
4. Let Magisk install and open the replacement manager. Complete any setup prompts and accept a launcher shortcut if offered.
5. In the replacement manager, confirm that **Enforce DenyList** is still enabled and the demo app's processes remain selected.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/magisk-hide-manager-setting.png" alt="Magisk settings with Hide the Magisk app highlighted, offering a proxy app with a random package ID and custom label" width="400px" />

Once the replacement manager has opened and the migration has finished, check the package list again. The original identifier should no longer appear:

```console
device:/ $ pm list packages | grep -F com.topjohnwu.magisk
device:/ $ pm list packages -3
package:org.owasp.mastestapp
package:qv.ztmx.lra
```

!!! note "About the executed command"
    `pm list packages -3`, with the `-3` option, lists only third-party packages, excluding system apps.

The manager is still installed, but its new package ID is not in the demo app's list of known packages.

!!! note
    Magisk generates the package ID randomly, so expect a different identifier on your device when inspecting the new package ID.

Force-stop the demo app from the ADB shell:

```console
device:/ $ am force-stop org.owasp.mastestapp
```

Reopen the demo app and tap **Start**. The package result should now show **No root management apps found**. With the previous DenyList bypass still in place, none of the five checks should report a positive result. The final message should be `Device appears to be rooted: false`.

Confirm that root is still available from the ADB shell:

```console
device:/ $ su -c 'id -u'
0
```

The device still provides root access, but the demo app reports it as not rooted. We have bypassed its root detection without patching or instrumenting the demo app.

??? info "About Removing the Manager App"
    Uninstalling only the manager APK, rather than Magisk itself, can also remove this package artifact. However, you lose the interface for managing the DenyList and other root settings. Root requests that need approval can also [fail without a manager](https://github.com/topjohnwu/Magisk/blob/v30.6/native/src/core/su/daemon.rs). You could reinstall the manager when needed and remove it afterward, but installing the original APK makes `com.topjohnwu.magisk` visible again. Keeping a manager with a modified package name is usually more convenient.

#### Root Detection Beyond This Workshop

We have bypassed the root detection in this application while keeping root access on the device. But that does not mean we have won the game: there are more checks and environments to explore.

This application performs a small, representative set of checks to help you learn how root detection works as a resilience mechanism. Many other root-detection methods are not covered here. Bypassing these checks during the workshop does not mean that we have defeated every root detection an app could implement. Another app could still detect this same environment through different checks.

Root detection, like other local environment checks used for resilience, is a cat-and-mouse game. These checks run inside the application, where an attacker can inspect and manipulate them. With enough time and effort, a determined attacker can understand the checks and find ways around them.

Magisk is one of several root solutions. Other root solutions include:

- [KernelSU](https://kernelsu.org/), which provides root capabilities through the Linux kernel.
- [APatch](https://apatch.dev/), another kernel-based root solution for Android.

Different root solutions work differently and can expose different artifacts. A check or bypass designed around Magisk might behave differently with another root solution or configuration.

There are also open-source detector apps, such as [Duck Detector](https://github.com/eltavine/Duck-Detector-Refactoring), that implement additional root and environment checks. Their source code provides further examples to study and experiment with.

You can learn by studying both root solutions and detectors: how root capabilities are implemented, which artifacts they expose, and how applications recognize those artifacts. Keep exploring these repositories and testing what you learn on your lab device.

### Emulator Detection

### Hooking Detection

### Other Resilience mechanisms
