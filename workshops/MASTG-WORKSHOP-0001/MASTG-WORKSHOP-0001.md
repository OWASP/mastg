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

MacOS:

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

Mobile app resilience aims to make reverse engineering and tampering mobile applications harder. It increases the time, expertise, and patience needed for activities such as:

- Understanding the app's implementation and proprietary protocols.
- Modifying the app's behavior to bypass security controls.
- Analyzing how the app communicates with its backend.
- Executing the app in an **unstrusted** environment (e.g, rooted or emulated devices).

Resilience mechanisms raise the effort required for these activities, but do not make them impossible.

### Why is Mobile App Resilience needed

To understand why these resilience barriers matter, let's use a social network application that pays creators for content views as an example.

If the app does not properly detect emulators or virtual devices, someone could run hundreds of automated mobile device instances to generate fake views and interactions with th eobjective of earning money without reaching a real audience.

The result is that the platform would end up paying for fake activity, and, at the same time, users expecting to interact with real people would encounter bots instead, potentially damaging the platform's reputation and driving users away.

<img src="Images/Workshops/MASTG-WORKSHOP-0001/device-farm.png" alt="Multiple mobile device sessions controlled through a single interface" width="30%" />

Another example is a competitive online game. Someone could modify the app statically (patching) or modify its runtime behavior (e.g., by hooking or debugging the application) with the purpose of manipulating scores to gain an unfair advantage, for example, or creating a cheat that provides information of the other users in the game (like a wallhack).

Legitimate players would find themselves competing against cheaters rather than other players on equal terms. This could ruin matches, distort leaderboards, and drive players away, damaging both the game's reputation and its revenue.

### OWASP MAS Project and the Resilience topic

Before moving on to the practical exercises, let's look at how resilience fits into the [OWASP Mobile Application Security (MAS) project](https://mas.owasp.org/). The project provides a security standard and supporting resources that serve as a reference for developers building mobile apps and security testers assessing them.

The following overview introduces MASVS, MASWE, and MASTG and explains how security controls, weaknesses, and tests connect, using resilience as an example.

#### MASVS: Security Controls

The [OWASP Mobile Application Security Verification Standard (MASVS)](https://mas.owasp.org/MASVS/) defines security controls for mobile applications. These controls describe the protections an app is expected to provide.

The controls are organized into groups, each covering a different area of mobile application security. For example, `MASVS-STORAGE` covers the protection of data stored on the device, while `MASVS-NETWORK` covers communication between the app and remote endpoints.

There is also a group specifically for resilience: [MASVS-RESILIENCE](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/). It contains the following controls:

1. `MASVS-RESILIENCE-1`: The app validates the integrity of the platform.

    This control concerns the environment where the app runs. It addresses questions such as: "Is this a rooted device?" Root or jailbreak detection are examples of mechanisms related to this control.

2. `MASVS-RESILIENCE-2`: The app implements anti-tampering mechanisms.

    This control concerns changes to the app itself, such as modifications to its code or resources. It addresses questions such as: "Has this app been patched or modified?"

3. `MASVS-RESILIENCE-3`: The app implements anti-static analysis mechanisms.

    This control concerns attempts to understand the app through static analysis. It addresses questions such as: "Can an attacker understand my code without running it?" Code obfuscation is an example of a mechanism that can make this harder.

4. `MASVS-RESILIENCE-4`: The app implements anti-dynamic analysis techniques.

    This control concerns analysis and manipulation at runtime. It addresses questions such as: "Can someone inspect or manipulate my app while it runs?" Anti-debugging and hooking detection are examples of mechanisms related to this control.

!!! note
    Not every app has the same security requirements (for example, compare a banking application with a calendar application). To address these differences, the OWASP MAS project provides [MAS Testing Profiles](https://mas.owasp.org/Profiles/), which define sets of controls and tests for different protection needs:

    - `MAS-L1`: Essential security controls for mobile apps.
    - `MAS-L2`: Advanced security controls for scenarios where the operating system cannot be trusted or attackers may have physical access to the device.
    - `MAS-R`: Resilience against reverse engineering and tampering, including threats from users who control the device.
    - `MAS-P`: Baseline privacy protections for users' personal data.

    The appropriate profiles, or combination of profiles, depend on the type of app, its functionality, the sensitivity of its data, and its threat model. Check the [MAS Testing Profiles](https://mas.owasp.org/Profiles/) for more information on the topic.

#### MASWE: From Controls to Weaknesses

When a control required for an app is missing or is not properly enforced, this can introduce a security weakness.

The [OWASP Mobile Application Security Weakness Enumeration (MASWE)](https://mas.owasp.org/MASWE/) provides a taxonomy of mobile security and privacy weaknesses. It connects these weaknesses to the relevant MASVS controls and explains their causes, potential impact, and mitigations.

For example, `MASVS-RESILIENCE-1` concerns platform integrity. @MASWE-0051 describes the related weakness: root or jailbreak detection is not implemented effectively.

For resilience, this relationship must be considered in the context of the app's threat model. The absence of a resilience mechanism does not, on its own, mean that the app is vulnerable.

#### MASTG: From Weaknesses to Tests, Demos, Knowledge Pages and Best Practices

MASTG provides practical testing procedures to investigate these weaknesses, supported by knowledge articles, techniques, and demos.

Continuing with the root detection example, MASTG provides different ways to check whether an app implements root detection:

- @MASTG-TEST-0324 looks for references to root detection mechanisms through static analysis.
- @MASTG-TEST-0325 checks for the use of root detection mechanisms at runtime.

Demos provide focused examples that let you follow a test's steps and observe the results in a sample app. This makes it easier to understand the behavior being tested without the complexity of a production app. @MASTG-DEMO-0087 and @MASTG-DEMO-0088 are examples associated with the tests above.

Knowledge pages explain the concepts and mechanisms behind a topic, providing background for understanding and testing it. For example, @MASTG-KNOW-0027 covers root detection on Android.

Best-practice pages provide implementation guidance and explain relevant trade-offs and limitations. For example, @MASTG-BEST-0030 covers implementing root detection.

### Root Detection

### Emulator Detection

### Hooking Detection

### Other Resilience mechanisms
