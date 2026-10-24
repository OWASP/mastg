---
title: Mobile Application Resilience for Beginners
id: MASTG-WORKSHOP-0001
---

## About

This hands-on workshop introduces the user to the mobile application resilience topic, one of the main topics inside mobile application security.

It uses examples, demos, and tests from [this repository](https://github.com/OWASP/mastg), alongside workshop-specific bypass exercises.

The practical exercises focus on root detection, emulator detection, and runtime hooking detection.

!!! note
    The examples in this workshop use Android, but the underlying resilience concepts apply to both Android and iOS.
    The MASTG repository also includes analogous iOS tests. For example, @MASTG-TEST-0325 covers runtime root detection on Android, while @MASTG-TEST-0241 covers runtime jailbreak detection on iOS.

## Objective

By the end of this workshop, you will be able to:

- Explain how resilience mechanisms increase the effort required to reverse engineer or tamper with an app.
- Identify the main [OWASP MASVS resilience control categories](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/): platform integrity, anti-tampering, anti-static analysis, and anti-dynamic analysis.
- Explain how a layered approach combines complementary mechanisms instead of relying on a single detection check.
- Distinguish between detecting a specific tool, such as Frida, and detecting the underlying technique, such as hooking.
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
2. Clone the [rootAVD GitLab repository](https://gitlab.com/newbit/rootAVD). **Do not clone the Github repository**.
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

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magisk-install-button.png" alt="Magisk Install button" width="50%" />

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

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magisk-root-status.png" alt="Magisk root status" width="50%" />

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

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magiskfrida-zip-in-downloads.png" alt="MagiskFrida ZIP file in the device Downloads directory" width="50%" />

7. Wait for the installation to finish.
8. Select **Reboot**.
9. After the restart, open Magisk, and then select **Modules**.
10. Confirm that MagiskFrida is installed, enabled, and active.
11. If **active** doesn't appear, select **Action** or reboot the device.

    <img src="Images/Workshops/MASTG-WORKSHOP-0001/magiskfrida-active-module.png" alt="Active MagiskFrida module" width="50%" />

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
