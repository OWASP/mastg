# Android Security Testing

In this chapter, we'll dive into setting up a security testing environment and introduce you to some practical processes and techniques for testing the security of Android apps. These are the building blocks for the MASTG test cases.

## Android Testing Setup

You can set up a fully functioning test environment on almost any machine running Windows, Linux, or macOS.

### Host Device

At the very least, you'll need @MASTG-TOOL-0007 (which comes with the @MASTG-TOOL-0006) platform tools, an emulator, and an app to manage the various SDK versions and framework components. Android Studio also comes with an Android Virtual Device (AVD) Manager application for creating emulator images. Make sure that the newest [SDK tools](https://developer.android.com/studio/releases/sdk-tools) and [platform tools](https://developer.android.com/studio/releases/platform-tools) packages are installed on your system.

In addition, you may want to complete your host setup by installing the @MASTG-TOOL-0005 if you're planning to work with apps containing native libraries.

Sometimes it can be useful to display or control devices from the computer. To achieve this, you can use @MASTG-TOOL-0024.

### Testing Device

For dynamic analysis, you'll need an Android device to run the target app on. In principle, you can test without a real Android device and use only the emulator. However, apps execute quite slowly on a emulator, and simulators may not give realistic results. Testing on a real device makes for a smoother process and a more realistic environment. On the other hand, emulators allow you to easily change SDK versions or create multiple devices. A full overview of the pros and cons of each approach is listed in the table below.

| Property | Physical | Emulator/Simulator |
| --- | --- | --- |
| Ability to restore | Softbricks are always possible, but new firmware can typically still be flashed. Hardbricks are very rare. | Emulators can crash or become corrupt, but a new one can be created or a snapshot can be restored. |
| Reset | Can be restored to factory settings or reflashed. | Emulators can be deleted and recreated. |
| Snapshots | Not possible. | Supported, great for malware analysis. |
| Speed | Much faster than emulators. | Typically slow, but improvements are being made. |
| Cost | Typically start at $200 for a usable device. You may require different devices, such as one with or without a biometric sensor. | Both free and commercial solutions exist. |
| Ease of rooting | Highly dependent on the device. | Typically rooted by default. |
| Ease of emulator detection | It's not an emulator, so emulator checks are not applicable. | Many artefacts will exist, making it easy to detect that the app is running in an emulator. |
| Ease of root detection | Easier to hide root, as many root detection algorithms check for emulator properties. With Magisk Systemless root it's nearly impossible to detect. | Emulators will almost always trigger root detection algorithms due to the fact that they are built for testing with many artefacts that can be found. |
| Hardware interaction | Easy interaction through Bluetooth, NFC, 4G, Wi-Fi, biometrics, camera, GPS, gyroscope, ... | Usually fairly limited, with emulated hardware input (e.g. random GPS coordinates) |
| API level support | Depends on the device and the community. Active communities will keep distributing updated versions (e.g. LineageOS), while less popular devices may only receive a few updates. Switching between versions requires flashing the device, a tedious process. | Always supports the latest versions, including beta releases. Emulators containing specific API levels can easily be downloaded and launched. |
| Native library support | Native libraries are usually built for ARM devices, so they will work on a physical device. | Some emulators run on x86 CPUs, so they may not be able to run packaged native libraries (see [ARM Binary Translation on x86 Emulators](#arm-binary-translation-on-x86-emulators) below for available workarounds). |
| Malware danger | Malware samples can infect a device, but if you can clear out the device storage and flash a clean firmware, thereby restoring it to factory settings, this should not be a problem. Be aware that there are malware samples that try to exploit the USB bridge. | Malware samples can infect an emulator, but the emulator can simply be removed and recreated. It is also possible to create snapshots and compare different snapshots to help in malware analysis. Be aware that there are malware proofs of concept which try to attack the hypervisor. |

#### Testing on a Real Device

Almost any physical device can be used for testing, but there are a few considerations to be made. First, the device needs to be rootable. This is typically either done through an exploit, or through an unlocked bootloader. Exploits are not always available, and the bootloader may be locked permanently, or it may only be unlocked once the carrier contract has been terminated.

The best candidates are flagship Google pixel devices built for developers. These devices typically come with an unlockable bootloader, opensource firmware, kernel, radio available online and official OS source code. The developer communities prefer Google devices as the OS is closest to the android open source project. These devices generally have the longest support windows with 2 years of OS updates and 1 year of security updates after that.

Alternatively, Google's [Android One](https://www.android.com/one/ "Android One") project contains devices that will receive the same support windows (2 years of OS updates, 1 year of security updates) and have near-stock experiences. While it was originally started as a project for low-end devices, the program has evolved to include mid-range and high-end smartphones, many of which are actively supported by the modding community.

Devices that are supported by the [LineageOS](https://lineageos.org/ "LineageOS") project are also very good candidates for test devices. They have an active community, easy to follow flashing and rooting instructions and the latest Android versions are typically quickly available as a Lineage installation. LineageOS also continues support for new Android versions long after the OEM has stopped distributing updates.

When working with an Android physical device, you'll want to enable Developer Mode and USB debugging on the device in order to use the @MASTG-TOOL-0004 debugging interface. Since Android 4.2 (API level 16), the **Developer options** sub menu in the Settings app is hidden by default. To activate it, tap the **Build number** section of the **About phone** view seven times. Note that the build number field's location varies slightly by device. For example, on LG Phones, it is under **About phone** -> **Software information**. Once you have done this, **Developer options** will be shown at bottom of the Settings menu. Once developer options are activated, you can enable debugging with the **USB debugging** switch.

#### Testing on an Emulator

Multiple emulators exist, once again with their own strengths and weaknesses:

Free emulators:

- [Android Virtual Device (AVD)](https://developer.android.com/studio/run/managing-avds.html "Create and Manage Virtual Devices") - The official android emulator, distributed with Android Studio.
- [Android X86](https://www.android-x86.org/ "Android X86") - An x86 port of the Android code base

Commercial emulators:

- [Genymotion](https://www.genymotion.com/download/ "Genymotion") - Mature emulator with many features, both as local and cloud-based solution. Free version available for non-commercial use.
- [Corellium](https://corellium.com/ "Corellium") - Offers custom device virtualization through a cloud-based or on-prem solution.

Although there exist several free Android emulators, we recommend using AVD as it provides enhanced features appropriate for testing your app compared to the others. In the remainder of this guide, we will use the official AVD to perform tests.

AVD supports some hardware emulation, such as GPS or SMS through its so-called [Extended Controls](https://developer.android.com/studio/run/advanced-emulator-usage#extended "Extended Controls") as well as [motion sensors](https://developer.android.com/guide/topics/sensors/sensors_overview#test-with-the-android-emulator "Testing motion sensors on emulators").

You can either start an Android Virtual Device (AVD) by using the AVD Manager in Android Studio or start the AVD manager from the command line with the `android` command, which is found in the tools directory of the Android SDK:

```bash
./android avd
```

Several tools and VMs that can be used to test an app within an emulator environment are available:

- @MASTG-TOOL-0035
- [Nathan](https://github.com/mseclab/nathan "Nathan") (not updated since 2016)

##### ARM Binary Translation on x86 Emulators

!!! warning
Running ARM code on x86 through binary translation can change app behavior or cause crashes. When contributing to the OWASP MASTG, state whether you used binary translation and identify the emulator, system image, and translation implementation. Translation can affect security testing results, so confirm relevant tests or demos on an ARM device when possible.
Most Android devices use ARM-based processors, and many Android applications include native libraries compiled for ARM architectures (`armeabi-v7a` or `arm64-v8a`). When testing these applications on an x86 or x86_64 emulator, the emulator must provide a mechanism to execute ARM native code.

**Official Android Emulator (AVD):** Starting with Android 11 (API level 30), Google APIs and Google Play system images include built-in ARM-to-x86 translation. The level of support depends on the system image: the x86 image supports both x86 and ARMv7 (`armeabi-v7a`) ABIs, while the x86_64 image additionally supports ARM64 (`arm64-v8a`). The Android OS and Android Runtime (ART) run natively on x86, while ARM binaries required by an application's process are translated to x86 within that process. This avoids the performance overhead of full-system ARM emulation. For more details, see [Google's announcement on running ARM apps on the Android Emulator](https://android-developers.googleblog.com/2020/03/run-arm-apps-on-android-emulator.html).

**Custom VMs and the Native Bridge:** Custom x86-based environments such as Genymotion typically do not provide ARM translation out of the box. Android provides a Native Bridge mechanism that can be used to load binary translation implementations. The `ro.dalvik.vm.native.bridge` system property is used to configure the native bridge implementation.

Common translation implementations encountered in Android x86 environments include:

- `libhoudini`: A proprietary ARM-to-x86 translation implementation used by some Android x86 environments.
- `libndk_translation`: An ARM-to-x86 translation implementation included in compatible Android Emulator system images.

Community-maintained scripts and flashable archives can be used to add ARM translation support to some custom VMs. However, such packages are version-specific and may not work across different Android system images.

When using ARM binary translation during security testing, consider the following limitations and security aspects:

- **Dynamic instrumentation**: Tools such as @MASTG-TOOL-0031 can experience limitations or fail to hook native ARM functions running through a translation layer.
- **Application compatibility**: Applications that depend on architecture-specific behavior or perform environment checks may behave differently or fail under translation. Therefore, successful execution through a translation layer does not guarantee the same behavior on a physical ARM device.
- **Supply chain risks**: Community translation packages obtained from third-party repositories may require root privileges and modifications to system partitions or system libraries. Only install such packages from trusted sources and use them in isolated testing environments.

#### Getting Privileged Access

_Rooting_ (i.e., modifying the OS so that you can run commands as the root user) is recommended for testing on a real device. This gives you full control over the operating system and allows you to bypass restrictions such as app sandboxing. These privileges in turn allow you to use techniques like code injection and function hooking more easily.

Note that rooting is risky, and three main consequences need to be clarified before you proceed. Rooting can have the following negative effects:

- voiding the device warranty (always check the manufacturer's policy before taking any action)
- "bricking" the device, i.e., rendering it inoperable and unusable
- creating additional security risks (because built-in exploit mitigations are often removed)

You should not root a personal device that you store your private information on. We recommend getting a cheap, dedicated test device instead. Many older devices, such as Google's Nexus series, can run the newest Android versions and are perfectly fine for testing.

**You need to understand that rooting your device is ultimately YOUR decision and that OWASP shall in no way be held responsible for any damage. If you're uncertain, seek expert advice before starting the rooting process.**

##### Which Mobiles Can Be Rooted

Virtually any Android mobile can be rooted. Commercial versions of Android OS (which are Linux OS evolutions at the kernel level) are optimized for the mobile world. Some features have been removed or disabled for these versions, for example, non-privileged users' ability to become the 'root' user (who has elevated privileges). Rooting a phone means allowing users to become the root user, e.g., adding a standard Linux executable called `su`, which is used to change to another user account.

To root a mobile device, first unlock its boot loader. The unlocking procedure depends on the device manufacturer. However, for practical reasons, rooting some mobile devices is more popular than rooting others, particularly when it comes to security testing: devices created by Google and manufactured by companies like Samsung, LG, and Motorola are among the most popular, particularly because they are used by many developers. The device warranty is not nullified when the boot loader is unlocked and Google provides many tools to support the root itself.

##### Rooting with Magisk

Magisk ("Magic Mask") is one way to root your Android device. Its specialty lies in the way the modifications on the system are performed. While other rooting tools alter the actual data on the system partition, Magisk does not (which is called "systemless"). This enables a way to hide the modifications from root-sensitive applications (e.g. for banking or games) and allows using the official Android OTA upgrades without the need to unroot the device beforehand.

You can get familiar with Magisk reading the official [documentation on GitHub](https://topjohnwu.github.io/Magisk/ "Magisk Documentation"). If you don't have Magisk installed, you can find installation instructions in [the documentation](https://topjohnwu.github.io/Magisk/ "Magisk Documentation"). If you use an official Android version and plan to upgrade it, Magisk provides a [tutorial on GitHub](https://topjohnwu.github.io/Magisk/ota.html "OTA Installation").

Furthermore, developers can use the power of Magisk to create custom modules and [submit](https://github.com/Magisk-Modules-Repo/submission "Submission") them to the official [Magisk Modules repository](https://github.com/Magisk-Modules-Repo "Magisk-Modules-Repo"). Submitted modules can then be installed inside the Magisk Manager application. One of these installable modules is a systemless version of @MASTG-TOOL-0027 (available for SDK versions up to 27).

##### Root Detection

An extensive list of root detection methods is presented in the "Testing Anti-Reversing Defenses on Android" chapter.

For a typical mobile app security build, you'll usually want to test a debug build with root detection disabled. If such a build is not available for testing, you can disable root detection in a variety of ways that will be introduced later in this book.
